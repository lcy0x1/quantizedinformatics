package com.arthurlcy0x1.quantizedinformatics.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

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

		@Override
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

		private static class Triplet {

			private final GateContainer[] larr;
			private final int[] index;
			private final List<LogicGate> pset;

			private Triplet(ParentDiagram pd) {
				larr = pd.list.toArray(new GateContainer[0]);
				index = new int[larr.length];
				pset = new ArrayList<>();
				for (int i = 0; i < larr.length; i++) {
					index[i] = -1;
					for (int j = 0; j < pset.size(); j++)
						if (pset.get(j).equals(larr[i].gate)) {
							index[i] = j;
							break;
						}
					if (index[i] == -1) {
						index[i] = pset.size();
						pset.add(larr[i].gate);
					}
				}
			}

			private int find(GateContainer src) {
				if (src != null)
					for (int k = 0; k < larr.length; k++)
						if (larr[k] == src)
							return k;
				return -1;
			}

			private void put(CompoundNBT sub, GatePin[] p) {
				int[] inds = new int[p.length];
				int[] srcs = new int[p.length];
				for (int i = 0; i < p.length; i++) {
					inds[i] = p[i].pin;
					srcs[i] = find(p[i].src);
				}
				sub.putIntArray("inds", inds);
				sub.putIntArray("srcs", srcs);
			}

		}

		public static ParentDiagram decode(CompoundNBT tag) {
			int ver = tag.getByte("version");
			switch (ver) {
			case 1: {
				int in = tag.getInt("in");
				int out = tag.getInt("out");
				int[] inds = tag.getIntArray("inds");
				int[] srcs = tag.getIntArray("srcs");
				ListNBT ltag = tag.getList("set", 10);
				int sl = ltag.size();
				LogicGate[] lgs = new LogicGate[sl];
				for (int i = 0; i < sl; i++)
					lgs[i] = LogicGate.decode(ltag.getCompound(i));
				ListNBT gate = tag.getList("gate", 10);
				int gl = gate.size();
				GateContainer[] list = new GateContainer[gl];
				int[][][] values = new int[gl][2][];
				ParentDiagram ans = new ParentDiagram(in, out);
				for (int i = 0; i < gl; i++) {
					CompoundNBT sub = gate.getCompound(i);
					int ind = sub.getInt("gate");
					LogicGate lga = lgs[ind];
					list[i] = new GateContainer(ans, lga);
					values[i][0] = sub.getIntArray("inds");
					values[i][1] = sub.getIntArray("srcs");
				}
				for (int i = 0; i < out; i++) {
					int ind = srcs[i];
					GateContainer gc = ind == -1 ? null : list[ind];
					ans.setInput(i, gc, inds[i]);
				}
				for (int i = 0; i < gl; i++)
					for (int j = 0; j < list[i].gate.input; j++) {
						int ind = values[i][1][j];
						GateContainer gc = ind == -1 ? null : list[ind];
						list[i].setInput(j, gc, values[i][0][j]);
					}
				ans.list.addAll(Arrays.asList(list));
				return ans;
			}
			default:
				throw new LogicRE("unsupported version");
			}
		}

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

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ParentDiagram))
				return false;
			ParentDiagram d = (ParentDiagram) o;
			return toTag().equals(d.toTag()); // TODO better algorithm?
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

		@Override
		public void setInput(int i, GateContainer gc, int j) {
			output[i].src = gc;
			output[i].pin = j;
		}

		public LogicGate toGate() {
			if (inlen > 6 || outlen > 6)
				return new LogicGate.CompoundLogicGate(inlen, outlen, getDelay(), getCost(), this);
			return new LogicGate.SimpleLogicGate(inlen, outlen, getDelay(), getCost(), LogicGate.SIMP, getMap());
		}

		public CompoundNBT toTag() {
			CompoundNBT ans = new CompoundNBT();
			ans.putByte("version", (byte) 1);
			Triplet t = new Triplet(this);
			List<LogicGate> pset = t.pset;
			GateContainer[] larr = t.larr;
			int[] index = t.index;
			ans.putInt("in", inlen);
			ans.putInt("out", outlen);
			t.put(ans, output);
			ListNBT ltag = new ListNBT();
			for (int i = 0; i < pset.size(); i++)
				ltag.add(LogicGate.encode(pset.get(i)));
			ans.put("set", ltag);
			ListNBT gate = new ListNBT();
			for (int i = 0; i < larr.length; i++) {
				CompoundNBT sub = new CompoundNBT();
				sub.putInt("gate", index[i]);
				t.put(sub, larr[i].pins);
				gate.add(sub);
			}
			ans.put("gate", gate);
			return ans;
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

	public static final int FALSE = -1, TRUE = -2;

	private int delay = -2;
	private BoolArr map;
	private int val = -1;

	public abstract void setInput(int i, GateContainer gc, int j);

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
