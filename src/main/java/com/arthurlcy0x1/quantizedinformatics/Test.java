package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

public class Test {

	public static void main(String[] args) {
		allGate(2);
	}

	private static void allGate(int i) {
		//long = bool[64] = bool[bool[6]]
		for (int j = 0; j < 1 << LogicGate.MAX - 1; j++) {
			// i=2, j = abcde, left = abc000, right = 0000de
			int left = j >> i << i + 1;
			int right = j & (1 << i) - 1;
			int base = left | 1 << i | right;
			printBin(base);
			if (j % 4 == 3)
				System.out.println();
			else
				System.out.print("\t");
		}
	}

	private static void printBin(int n) {
		for (int i = 5; i >= 0; i--)
			System.out.print((n & 1 << i) != 0 ? 1 : 0);
	}

}
