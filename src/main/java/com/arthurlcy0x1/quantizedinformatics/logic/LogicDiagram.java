package com.arthurlcy0x1.quantizedinformatics.logic;

import java.util.ArrayList;
import java.util.List;

public abstract class LogicDiagram {

	public static final class GateContainer extends LogicDiagram {

		private final ParentDiagram env;
		private final LogicGate gate;
		private final GatePin[] pins;

		private GateContainer(ParentDiagram par, LogicGate g) {
			env = par;
			gate = g;
			pins = new GatePin[g.input];
			for (int i = 0; i < g.input; i++)
				pins[i] = new GatePin();
		}

		public boolean canSetOutput(GateContainer gc) {
			for (GatePin p : pins) {
				if (p.src == null)
					continue;
				if (p.src == gc)
					return false;
				if (!p.src.canSetOutput(gc))
					return false;
			}
			return true;
		}

		public void setInput(int i, GateContainer gc, int j) {
			if (i < 0 || i >= gate.input)
				throw new LogicRE("input index out of range: " + i + ", range: " + gate.input);
			if (gc != null && !gc.canSetOutput(this))
				throw new LogicRE("circular diagram");
			if (gc != null && (j < 0 || j >= gc.gate.output))
				throw new LogicRE("output index out of range: " + j + ", range: " + gc.gate.output);
			if (gc == null && (j < -2 || j >= env.inlen))
				throw new LogicRE("target index out of range: " + j + ", range:" + -2 + "~" + env.inlen);

			pins[i].src = gc;
			pins[i].pin = j;
		}

		@Override
		protected int dstlen() {
			return gate.output;
		}

		@Override
		protected int envlen() {
			return env.inlen;
		}

		@Override
		protected LogicGate getGateMap() {
			return gate;
		}

		@Override
		protected GatePin[] getPins() {
			return pins;
		}

		@Override
		protected int srclen() {
			return gate.input;
		}

	}

	public static final class ParentDiagram extends LogicDiagram {

		private final List<GateContainer> list = new ArrayList<>();
		private final GatePin[] output;
		private final int inlen, outlen;

		private int compCount = 0;

		public ParentDiagram() {
			this(LogicGate.MAX, LogicGate.MAX);
		}

		public ParentDiagram(int in, int out) {
			inlen = in;
			outlen = out;
			output = new GatePin[outlen];
			for (int i = 0; i < outlen; i++)
				output[i] = new GatePin();
		}

		public GateContainer addGate(LogicGate g) {
			GateContainer cont = new GateContainer(this, g);
			list.add(cont);
			if (!(g instanceof LogicGate.SimpleLogicGate))
				compCount++;
			return cont;
		}

		@Override
		public int compute(int in) {
			for (GateContainer gc : list)
				gc.resetVal();
			resetVal();
			return super.compute(in);
		}

		public int getCost() {
			int cost = 0;
			for (GateContainer gc : list)
				cost += gc.gate.cost;
			return cost;
		}

		@Override
		public int getDelay() {
			for (GateContainer gc : list)
				gc.resetDelay();
			resetDelay();
			return super.getDelay();
		}

		@Override
		public BoolArr getMap() {
			if (compCount > 0)
				throw new LogicRE("contains complicated component, not going to calculate map");
			if (inlen > 6 || outlen > 6)
				throw new LogicRE("inlen or outlen larger than 6, not going to calculate map");
			for (GateContainer gc : list)
				gc.resetMap();
			resetMap();
			return super.getMap();
		}

		public void setInput(int i, GateContainer gc, int j) {
			output[i].src = gc;
			output[i].pin = j;
		}

		public LogicGate toGate() {
			if (inlen > 6 || outlen > 6)
				throw new LogicRE("inlen or outlen slarger than 6, not going to calculate map");
			return new LogicGate.SimpleLogicGate(inlen, outlen, getDelay(), getCost(), getMap());
		}

		@Override
		protected int dstlen() {
			return 0;
		}

		@Override
		protected int envlen() {
			return inlen;
		}

		@Override
		protected LogicGate getGateMap() {
			return null;
		}

		@Override
		protected GatePin[] getPins() {
			return output;
		}

		@Override
		protected int srclen() {
			return outlen;
		}
	}

	private static final class GatePin {

		private GateContainer src;
		private int pin;

		private GatePin() {
			src = null;
			pin = FALSE;
		}
	}

	private static final int FALSE = -1, TRUE = -2;

	private int delay = -2;
	private BoolArr map;
	private int val = -1;

	protected int compute(int in) {
		if (val >= 0)
			return val;
		int srclen = srclen();
		int input = 0;
		for (int i = 0; i < srclen; i++) {
			GatePin p = getPins()[i];
			int pinin = p.src.compute(in);
			if ((pinin & 1 << p.pin) != 0)
				input |= 1 << i;
		}
		LogicGate gate = getGateMap();
		if (gate == null)
			return val;
		val = gate.compute(input);
		return val;
	}

	protected abstract int dstlen();

	protected abstract int envlen();

	protected int getDelay() {
		if (delay != -2)
			return delay;
		delay = -1;
		for (GatePin p : getPins())
			if (p.src != null)
				delay = Math.max(delay, p.src.getDelay());
			else if (p.pin >= 0)
				delay = Math.max(delay, 0);
		if (delay >= 0 && getGateMap() != null)
			delay += getGateMap().delay;
		return delay;
	}

	protected abstract LogicGate getGateMap();

	protected BoolArr getMap() {
		if (map != null)
			return map;
		int envlen = envlen();
		int srclen = srclen();
		BoolArr pinmap = BoolArr.getNew(srclen, envlen);
		for (int i = 0; i < srclen; i++) {
			GatePin p = getPins()[i];
			if (p.src == null) {
				if (p.pin == FALSE)
					pinmap.setAll(i, false);
				else if (p.pin == TRUE)
					pinmap.setAll(i, true);
				else
					for (byte j = 0; j < 1 << envlen - 1; j++) {
						// i=2, j = abcde, left = abc000, right = 0000de
						int left = j >> p.pin << p.pin + 1;
						int right = j & (1 << p.pin) - 1;
						int base = left | 1 << p.pin | right;
						pinmap.set(i, (byte) base, true);
					}
			} else
				pinmap.copyFrom(p.src.getMap(), i, p.pin);

		}

		LogicGate gate = getGateMap();
		if (gate == null)
			return pinmap;
		BoolArr gateMap = ((LogicGate.SimpleLogicGate) gate).map;
		byte[] pinval = new byte[1 << envlen];
		for (byte i = 0; i < 1 << envlen; i++)
			for (int j = 0; j < srclen; j++)
				if (pinmap.get(j, i))
					pinval[i] |= 1 << j;

		int outlen = dstlen();
		map = BoolArr.getNew(outlen, envlen);
		for (int i = 0; i < outlen; i++)
			for (byte j = 0; j < 1 << envlen; j++)
				if (gateMap.get(i, pinval[j]))
					map.set(i, j, true);
		return map;
	}

	protected abstract GatePin[] getPins();

	protected void resetDelay() {
		delay = -2;
	}

	protected void resetMap() {
		map = null;
	}

	protected void resetVal() {
		val = -1;
	}

	protected abstract int srclen();

}
