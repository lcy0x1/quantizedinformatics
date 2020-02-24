package com.arthurlcy0x1.quantizedinformatics.logic;

import java.util.ArrayList;
import java.util.List;

public abstract class LogicDiagram {

	public static final class ParentDiagram extends LogicDiagram {

		private final List<GateContainer> list = new ArrayList<>();
		private final GatePin[] output = new GatePin[LogicGate.MAX];

		public ParentDiagram() {
			for (int i = 0; i < LogicGate.MAX; i++)
				output[i] = new GatePin();
		}

		public void addGate(LogicGate g) {
			list.add(new GateContainer(g));
		}

		public int getCost() {
			int cost = 0;
			for (GateContainer gc : list)
				cost += gc.gate.cost;
			return cost;
		}

		@Override
		protected GatePin[] getPins() {
			return output;
		}

		@Override
		protected int getSelfDelay() {
			return 0;
		}

		public int getDelay() {
			for (GateContainer gc : list)
				gc.resetDelay();
			resetDelay();
			return super.getDelay();
		}

		public long[] getMap() {
			for (GateContainer gc : list)
				gc.resetMap();
			resetMap();
			return super.getMap();
		}

		@Override
		protected long[] getGateMap() {
			return null;
		}
	}

	private static final class GateContainer extends LogicDiagram {

		private final LogicGate gate;

		private final GatePin[] pins;

		private GateContainer(LogicGate g) {
			gate = g;
			pins = new GatePin[g.input];
			for (int i = 0; i < g.input; i++)
				pins[i] = new GatePin();
		}

		@Override
		protected GatePin[] getPins() {
			return pins;
		}

		@Override
		protected int getSelfDelay() {
			return gate.delay;
		}

		private boolean canSetOutput(GateContainer gc) {
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

		private void setInput(int i, GateContainer gc, int j) {
			if (!gc.canSetOutput(this))
				throw new LogicRE("circular diagram");
			pins[i].src = gc;
			pins[i].pin = j;
		}

		@Override
		protected long[] getGateMap() {
			return gate.map;
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
	private long[] map;

	protected void resetDelay() {
		delay = -2;
	}

	protected void resetMap() {
		map = null;
	}

	protected abstract GatePin[] getPins();

	protected abstract int getSelfDelay();

	protected abstract long[] getGateMap();

	protected int getDelay() {
		if (delay != -2)
			return delay;
		delay = -1;
		for (GatePin p : getPins())
			if (p.src != null)
				delay = Math.max(delay, p.src.getDelay());
			else if (p.pin >= 0)
				delay = Math.max(delay, 0);
		if (delay >= 0)
			delay += getSelfDelay();
		return delay;
	}

	protected long[] getMap() {
		if (map != null)
			return map;
		long[] pinmap = new long[LogicGate.MAX];
		for (int i = 0; i < LogicGate.MAX; i++) {
			GatePin p = getPins()[i];
			if (p.src == null)
				if (p.pin == FALSE)
					pinmap[i] = 0;
				else if (p.pin == TRUE)
					pinmap[i] = -1;
				else
					for (int j = 0; j < 1 << LogicGate.MAX - 1; j++) {
						// i=2, j = abcde, left = abc000, right = 0000de
						int left = j >> i << i + 1;
						int right = j & (1 << i) - 1;
						int base = left | 1 << i | right;
						pinmap[i] |= 1l << base;
					}
		}
		long[] gateMap = getGateMap();
		if (gateMap == null)
			return pinmap;
		map = new long[LogicGate.MAX];
		byte[] pinval = new byte[1 << LogicGate.MAX];
		for (int i = 0; i < 1 << LogicGate.MAX; i++)
			for (int j = 0; j < LogicGate.MAX; j++)
				if ((pinmap[j] & 1 << i) != 0)
					pinval[i] |= 1 << j;

		for (int i = 0; i < LogicGate.MAX; i++) {
			map[i] = 0;
			for (int j = 0; j < 1 << LogicGate.MAX; j++)
				if ((gateMap[i] & 1 << pinmap[j]) != 0)
					map[i] |= 1l << j;
		}
		return map;
	}

}
