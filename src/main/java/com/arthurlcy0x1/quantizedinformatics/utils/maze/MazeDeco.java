package com.arthurlcy0x1.quantizedinformatics.utils.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeDeco {

	public static class DecoConfig {

		private static final int LEAF_WEIGHT = 5;
		private static final int PATH_WEIGHT = 1;
		private static final int SIZE_WEIGHT = 2;

		private final int leaf_weight, path_weight, size_weight;

		public DecoConfig() {
			leaf_weight = LEAF_WEIGHT;
			path_weight = PATH_WEIGHT;
			size_weight = SIZE_WEIGHT;
		}

	}

	public static int[][] generate(int[][] map, int count, Random ra, DecoConfig conf) {
		return new MazeDeco(map, count, ra, conf).gen();
	}
	private final List<int[]> list = new ArrayList<>();
	private final int[][] map, dis, size, leaf, weight;
	private final int w, r, count;
	private final Random rand;

	private final DecoConfig config;

	private MazeDeco(int[][] mapin, int countin, Random ra, DecoConfig conf) {
		map = mapin;
		count = countin;
		rand = ra;
		config = conf;
		w = map.length;
		r = w / 2;
		dis = new int[w][w];
		leaf = new int[w][w];
		size = new int[w][w];
		weight = new int[w][w];

	}

	private int[][] gen() {
		makeMap(-1, -1, r, r);
		makeGen(-1, -1, r, r, Math.min(count, leaf[r][r]));
		return list.toArray(new int[0][]);
	}

	private void makeGen(int px, int py, int x, int y, int dist) {
		if (dist == 0)
			return;
		int[] wei = new int[4];
		int[] lfc = new int[4];
		int rc = 0;
		for (int i = 0; i < 4; i++)
			if ((map[x][y] & 1 << i) > 0) {
				int x0 = x + MazeUtil.DIRE[i][0];
				int y0 = y + MazeUtil.DIRE[i][1];
				if (px == x0 && py == y0)
					continue;
				lfc[i] = leaf[x0][y0];
				wei[i] = weight[x0][y0];
				rc++;
			}
		if (rc == 0) {
			list.add(new int[] { x, y });
			return;
		}
		if (rc == 1) {
			for (int i = 0; i < 4; i++)
				if ((map[x][y] & 1 << i) > 0) {
					int x0 = x + MazeUtil.DIRE[i][0];
					int y0 = y + MazeUtil.DIRE[i][1];
					if (px == x0 && py == y0)
						continue;
					makeGen(x, y, x0, y0, dist);
				}
			return;
		}
		int[] dists = new int[4];
		for (int i = 0; i < dist; i++) {
			int ch = MazeUtil.randSel(rand, wei, false, 4);
			lfc[ch]--;
			dists[ch]++;
			if (lfc[ch] == 0)
				wei[ch] = 0;
		}
		for (int i = 0; i < 4; i++)
			if ((map[x][y] & 1 << i) > 0) {
				int x0 = x + MazeUtil.DIRE[i][0];
				int y0 = y + MazeUtil.DIRE[i][1];
				if (px == x0 && py == y0)
					continue;
				makeGen(x, y, x0, y0, dists[i]);
			}
	}

	private void makeMap(int px, int py, int x, int y) {
		int rc = 0, siz = 0, lea = 0, wei = 0;
		for (int i = 0; i < 4; i++)
			if ((map[x][y] & 1 << i) > 0) {
				int x0 = x + MazeUtil.DIRE[i][0];
				int y0 = y + MazeUtil.DIRE[i][1];
				if (px == x0 && py == y0)
					continue;
				dis[x0][y0] = dis[x][y] + 1;
				makeMap(x, y, x0, y0);
				siz += size[x0][y0];
				lea += leaf[x0][y0];
				wei += weight[x0][y0];
				rc++;
			}
		size[x][y] = siz + 1;
		leaf[x][y] = rc == 0 ? 1 : lea;
		wei += lea * config.path_weight + config.size_weight;
		weight[x][y] = rc == 0 ? config.leaf_weight : wei;
	}

}
