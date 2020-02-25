package com.arthurlcy0x1.quantizedinformatics.logic;

public abstract class LogicGate {

	public static final class PreALU extends CompoundLogicGate {

		public static final int ADD = 16, MINUS = 17;

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
							return false; //output mismatch
					}
			}
			return true;
		}

		public static boolean passTest(PreALU gate, int f, boolean cin, boolean m, int testID) {
			int n = gate.Nn;
			for (int i = 0; i < 1 << n; i++)
				for (int j = 0; j < 1 << n; j++)
					if (testCase(i, j, testID) != gate.compute(i, j, f, cin, m))
						return false;
			return true;
		}

		private static int testCase(int testID, int a, int b) {
			return 0;// TODO
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

	}

	public static final class SimpleLogicGate extends LogicGate {

		public final BoolArr map;

		public SimpleLogicGate(int inp, int out, int del, int cos, BoolArr m) {
			super(inp, out, del, cos);
			map = m;
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
		public String toString() {
			return "LogicGate, input: " + input + ", output: " + output + ", cost: " + cost + ", delay: " + delay
					+ "\n\tmap: " + map.toString();
		}

	}

	public static final int MAX = 6;

	public static final int BUFF = 0, NOT = 1, NAND = 2, NOR = 3, AND = 4, OR = 5, XOR = 6, EQ = 7, TOT = 8;
	private static final int[] INP = { 1, 1, -2, -2, -2, -2, 2, 2 };
	private static final int[] COM0 = { 2, 1, 0, 0, 1, 1, 6, 6 };
	private static final int[] COM1 = { 0, 0, 1, 1, 1, 1, 0, 0 };
	private static final int[] DEL = { 2, 1, 1, 1, 2, 2, 2, 2 };
	private static final long[] MAP = { 2, 1, 7, 1, 8, 14, 6, 9 };

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
		return new SimpleLogicGate(inp, 1, DEL[type], cost, BoolArr.wrap(inp, map));
	}

	public final int input, output, delay, cost;

	public LogicGate(int inp, int out, int del, int cos) {
		input = inp;
		output = out;
		delay = del;
		cost = cos;
	}

	public abstract int compute(int in);

}
