package com.arthurlcy0x1.quantizedinformatics.utils.maze;

import java.util.Random;

import com.arthurlcy0x1.quantizedinformatics.utils.maze.MazeDeco.DecoConfig;
import com.arthurlcy0x1.quantizedinformatics.utils.maze.MazeGen.MazeConfig;

public class MazeUtil {

	public static int[][] generate(int r, int count, Random rand) {
		int[][] map = MazeGen.generate(r, rand, new MazeConfig());
		int[][] ans = MazeDeco.generate(map, count, rand, new DecoConfig());
		for (int[] arr : ans) {
			int x = arr[0];
			int y = arr[1];
			int v = (int) Math.round(Math.log(map[x][y]) / Math.log(2));
			int tx = x + DIRE[v][0];
			int ty = y + DIRE[v][1];
			map[x][y] |= 1 << v + 4;
			map[tx][ty] |= 1 << (v ^ 1) + 4;
		}
		return map;
	}

	public static int randSel(Random r, int[] arr, boolean beg, int len) {
		int a = 0, b = 0;
		for (int i = 0; i < arr.length; i++)
			b += arr[i];
		if (beg)
			a += arr[0];
		for (int i = len + 1; i < arr.length; i++)
			b -= arr[i];
		int v = a + r.nextInt(b - a);
		for (int i = 0; i < arr.length; i++) {
			if (v < arr[i])
				return i;
			v -= arr[i];
		}
		return arr.length - 1;
	}

	public static int[] randArray(int n, Random r) {
		int[] ans = new int[n];
		if (n <= 1)
			return ans;
		for (int i = 0; i < n; i++)
			ans[i] = i;
		for (int i = 0; i < n; i++) {
			int x = r.nextInt(n - 1);
			if (x >= i)
				x++;
			int temp = ans[i];
			ans[i] = ans[x];
			ans[x] = temp;
		}
		return ans;
	}

	public static final int[][] DIRE = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

}
