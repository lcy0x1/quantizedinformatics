package com.arthurlcy0x1.quantizedinformatics.logic;

public class LogicGate {

	public static final int MAX = 6;

	public static final int BUFF = 0, NOT = 1, NAND = 2, NOR = 3, AND = 4, OR = 5, XOR = 6, EQ = 7, TOT = 8;
	private static final int[] INP = { 1, 1, -2, -2, -2, -2, 2, 2 };
	private static final int[] COM0 = { 2, 1, 0, 0, 1, 1, 6, 6 };
	private static final int[] COM1 = { 0, 0, 1, 1, 1, 1, 0, 0 };
	private static final int[] DEL = { 2, 1, 1, 1, 2, 2, 2, 2 };
	private static final long[] MAP = { 2, 1, 7, 1, 8, 14, 6, 9 };// 0111 0001 1000 1110

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
		return new LogicGate(inp, 1, DEL[type], cost, BoolArr.wrap(inp, map));
	}

	public final int input, output, delay, cost;
	public final BoolArr map;

	public LogicGate(int inp, int out, int del, int cos, BoolArr m) {
		input = inp;
		output = out;
		delay = del;
		cost = cos;
		map = m;
	}

	public byte compute(byte in) {
		byte ans = 0;
		for (int i = 0; i < output; i++)
			if (map.get(i, in))
				ans |= 1 << i;
		return ans;
	}

	@Override
	public String toString() {
		return "LogicGate, input: " + input + ", output: " + output + ", cost: " + cost + ", delay: " + delay;
	}

}
