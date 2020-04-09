package com.arthurlcy0x1.quantizedinformatics.utils.logic;

import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram.ParentDiagram;

import net.minecraft.nbt.CompoundNBT;

public abstract class LogicGate {

	public static class CompoundLogicGate extends LogicGate {

		private final LogicDiagram.ParentDiagram diag;

		public CompoundLogicGate(int inp, int out, int del, int cost, LogicDiagram.ParentDiagram ld) {
			super(inp, out, del, cost);
			diag = ld;
		}

		@Override
		public int compute(int in) {
			return diag.compute(in);
		}

		@Override
		public boolean equals(Object gate) {
			if (gate == this)
				return true;
			if (!super.equals(gate))
				return false;
			if (!(gate instanceof CompoundLogicGate))
				return false;
			CompoundLogicGate g = (CompoundLogicGate) gate;
			return g.diag.equals(g.diag);
		}

	}

	public static final class PreALU extends CompoundLogicGate {

		public static final int ADD = 16, MINUS = 17, SHIFT = 18;

		public static boolean logicTest(PreALU gate) {
			int pass = 0;
			int[] funcmap = new int[16];
			int n = gate.Nn;
			// generate map
			for (int i = 0; i < 16; i++) {
				int e0 = gate.compute(0, 0, i, false, true);
				int e1 = gate.compute(0, 1, i, false, true);
				int e2 = gate.compute(1, 0, i, false, true);
				int e3 = gate.compute(1, 1, i, false, true);
				if (e1 < 0 || e1 < 0 || e2 < 0 || e3 < 0 || e0 > 1 || e1 > 1 || e2 > 1 || e3 > 1)
					return false; // not singular
				int func = e0 | e1 << 1 | e2 << 2 | e3 << 3;
				if ((pass & 1 << func) != 0)
					return false; // repeated
				pass |= 1 << func;
				funcmap[i] = func;
			}

			for (int i = 0; i < 16; i++) {
				for (int a = 0; a < 1 << n; a++)
					for (int b = 0; b < 1 << n; b++) {
						int e0 = gate.compute(a, b, i, false, true);
						int e1 = gate.compute(a, b, i, true, true);
						int cor = 0;
						for (int j = 0; j < n; j++) {
							int cj = (b << 1 >> j) & 2 | (a >> j) & 1;
							if ((funcmap[i] & 1 << cj) != 0)
								cor |= 1 << j;
						}
						if (cor != e0 || cor != e1)
							return false; // output mismatch
					}
			}
			return true;
		}

		public static boolean passTest(PreALU gate, int f, boolean cin, int testID) {
			int n = gate.Nn;
			for (int i = 0; i < 1 << n; i++)
				for (int j = 0; j < 1 << n; j++)
					for (int m = 0; m < 1; m++)
						if ((testCase(testID, i, j, m) & 1 << n - 1) != gate.compute(i, j, f, cin, m == 1))
							return false;
			return true;
		}

		private static int testCase(int testID, int a, int b, int m) {
			if (testID == ADD)
				return a + b + m;
			if (testID == MINUS)
				return a - b - 1 + m;
			if (testID == SHIFT)
				return a * 2 + m;
			return 0;// TODO add test case for ALU
		}

		private final int[] A, B, S, F;

		private final int Nn, Ns, Ci, Co, M;

		public PreALU(CompoundLogicGate gate, int a[], int b[], int s[], int cin, int m, int f[], int cout) {
			super(gate.input, gate.output, gate.delay, gate.cost, gate.diag);
			A = a;
			B = b;
			S = s;
			F = f;
			Ci = cin;
			Co = cout;
			M = m;
			Nn = A.length;
			Ns = S.length;
		}

		private int compute(int a, int b, int s, boolean cin, boolean m) {
			int input = 0;
			for (int i = 0; i < Nn; i++) {
				if ((a & 1 << i) != 0)
					input |= 1 << A[i];
				if ((b & 1 << i) != 0)
					input |= 1 << B[i];
			}
			for (int i = 0; i < Ns; i++)
				if ((s & 1 << i) != 0)
					input |= 1 << S[i];
			if (cin)
				input |= 1 << Ci;
			if (m)
				input |= 1 << M;
			int output = compute(input);
			int ans = 0;
			for (int i = 0; i < Nn; i++)
				if ((output & 1 << F[i]) != 0)
					ans |= 1 << i;
			if ((output & 1 << Co) != 0)
				ans |= 1 << Nn;
			return ans;
		}

	}

	public static final class SimpleLogicGate extends LogicGate {

		public final BoolArr map;

		private final int type;

		public SimpleLogicGate(int inp, int out, int del, int cos, int t, BoolArr m) {
			super(inp, out, del, cos);
			map = m;
			type = t;
		}

		@Override
		public int compute(int in) {
			int ans = 0;
			for (int i = 0; i < output; i++)
				if (map.get(i, (byte) in))
					ans |= 1 << i;
			return ans;
		}

		@Override
		public boolean equals(Object gate) {
			if (gate == this)
				return true;
			if (!super.equals(gate))
				return false;
			if (!(gate instanceof SimpleLogicGate))
				return false;
			SimpleLogicGate g = (SimpleLogicGate) gate;
			if (type != SIMP)
				return type == g.type;
			if (type != g.type)
				return false;
			return map.equals(g.map);
		}

		@Override
		public String toString() {
			return "LogicGate, input: " + input + ", output: " + output + ", cost: " + cost + ", delay: " + delay
					+ "\n\tmap: " + map.toString();
		}

	}

	public static final int MAX = 6;

	public static final int BUFF = 0, NOT = 1, NAND = 2, NOR = 3, AND = 4, OR = 5, XOR = 6, EQ = 7, TOT = 8, SIMP = 16,
			CPLX = 17;
	private static final int[] INP = { 1, 1, -2, -2, -2, -2, 2, 2 };
	private static final int[] COM0 = { 2, 1, 0, 0, 1, 1, 6, 6 };
	private static final int[] COM1 = { 0, 0, 1, 1, 1, 1, 0, 0 };
	private static final int[] DEL = { 2, 1, 1, 1, 2, 2, 2, 2 };
	private static final long[] MAP = { 2, 1, 7, 1, 8, 14, 6, 9 };

	public static final LogicGate decode(CompoundNBT tag) {
		if (!tag.contains("version"))
			throw new LogicRE("wrong or corrupt tag");
		switch (tag.getByte("version")) {
		case 1: {
			int type = tag.getByte("type") - 1;
			if (type == CPLX) {
				int in = tag.getInt("input");
				int ou = tag.getInt("output");
				int de = tag.getInt("delay");
				int co = tag.getInt("cost");
				ParentDiagram p = ParentDiagram.decode(tag.getCompound("data"));
				return new CompoundLogicGate(in, ou, de, co, p);
			}
			if (type == SIMP) {
				int in = tag.getByte("input");
				int ou = tag.getByte("output");
				int de = tag.getInt("delay");
				int co = tag.getInt("cost");
				BoolArr arr = BoolArr.decode(tag.getCompound("data"));
				return new SimpleLogicGate(in, ou, de, co, SIMP, arr);
			}
			int num = tag.getByte("input");
			if (num == 0 || num > MAX || type < 0 || type >= TOT)
				throw new LogicRE("illegal type or gate num");
			return getPrimeGate(type, num);
		}
		default: {
			throw new LogicRE("cannot identify version");
		}
		}
	}

	public static final CompoundNBT encode(LogicGate gate) {
		CompoundNBT ans = new CompoundNBT();
		ans.putByte("version", (byte) 1);
		if (gate instanceof SimpleLogicGate) {
			SimpleLogicGate slg = (SimpleLogicGate) gate;
			ans.putByte("type", (byte) (slg.type + 1));
			ans.putByte("input", (byte) slg.input);
			if (slg.type == SIMP) {
				ans.putByte("output", (byte) slg.output);
				ans.putInt("delay", slg.delay);
				ans.putInt("cost", slg.cost);
				ans.put("data", slg.map.toTag());
			}
		} else if (gate instanceof CompoundLogicGate) {
			CompoundLogicGate clg = (CompoundLogicGate) gate;
			ans.putByte("type", (byte) (CPLX + 1));
			ans.putInt("input", clg.input);
			ans.putInt("output", clg.output);
			ans.putInt("delay", clg.delay);
			ans.putInt("cost", clg.cost);
			ans.put("data", clg.diag.toTag());
		}
		return ans;
	}

	public static final LogicGate getPrimeGate(int type, int inp) {
		if (type < 0 || type >= TOT)
			throw new LogicRE("gate type " + type + " not found");
		if (INP[type] < 0 && inp < -INP[type])
			throw new LogicRE("gate type " + type + " needs " + -INP[type] + " gates, " + inp + " provided");
		if (INP[type] >= 0)
			inp = INP[type];
		long map = 0;
		if (INP[type] >= 0)
			map = MAP[type];
		else if (type == NAND)
			map = (1 << (1 << inp) - 1) - 1;
		else if (type == NOR)
			map = 1;
		else if (type == AND)
			map = 1 << (1 << inp) - 1;
		else if (type == OR)
			map = (1 << (1 << inp)) - 2;
		int cost = COM0[type] + COM1[type] * inp;
		return new SimpleLogicGate(inp, 1, DEL[type], cost, type, BoolArr.wrap(inp, map));
	}

	public final int input, output, delay, cost;

	public LogicGate(int inp, int out, int del, int cos) {
		input = inp;
		output = out;
		delay = del;
		cost = cos;
	}

	public abstract int compute(int in);

	@Override
	public boolean equals(Object gate) {
		if (!(gate instanceof LogicGate))
			return false;
		LogicGate g = (LogicGate) gate;
		return input == g.input && output == g.output && cost == g.cost && delay == g.delay;
	}

}
