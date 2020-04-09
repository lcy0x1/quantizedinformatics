package com.arthurlcy0x1.quantizedinformatics.utils.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGen {

	public static class MazeConfig {

		private static final int[] PATH = { 5, 20, 5, 4, 3, 2, 1 };
		private static final int[] LOOP = { 10, 20, 5, 4, 3, 2, 1 };
		private static final double PATH_FAC = 0.5;
		private static final double LOOP_FAC = 0.5;
		private static final double CONN_PRI = 0.1;
		private static final double CONN_SEC = 0.3;
		private final int[] path, loop;
		private final double path_fac, loop_fac, conn_pri, conn_sec;

		public MazeConfig() {
			path = PATH;
			loop = LOOP;
			path_fac = PATH_FAC;
			loop_fac = LOOP_FAC;
			conn_pri = CONN_PRI;
			conn_sec = CONN_SEC;
		}

		public MazeConfig(int[] p, int[] l, double pf, double lf, double c0, double c1) {
			path = p;
			loop = l;
			path_fac = pf;
			loop_fac = lf;
			conn_pri = c0;
			conn_sec = c1;
		}

		public boolean testConn(Random r, boolean b) {
			return b ? r.nextDouble() < conn_pri : r.nextDouble() < conn_sec;
		}

		private int randLoop(StateRim rim, Random r) {
			int len = (int) Math.ceil(rim.aviLoop() * loop_fac);
			return MazeUtil.randSel(r, loop, rim.path == 0, len);
		}

		private int randPath(StateRim rim, Random r, int c) {
			int len = (int) Math.ceil(rim.aviPath() * path_fac);
			return MazeUtil.randSel(r, path, c == 1 || !rim.state.isRoot(), len);
		}

	}

	private class PostRim {

		private int x0, x1;
		private State state;

		private PostRim(int i, int j0, State val) {
			x0 = j0;
			state = val;
		}

	}

	private class State {

		private int ind;
		private State parent;

		private State() {
			ind = STATE_LIST.size();
			STATE_LIST.add(this);
		}

		private boolean equals(State st) {
			return getInd() == st.getInd();
		}

		private int getInd() {
			return getRoot().ind;
		}

		private State getRoot() {
			if (parent != null)
				return parent.getRoot();
			return this;
		}

		private boolean isRoot() {
			return getInd() == 0;
		}

		private void set(State st) {
			getRoot().parent = st;
		}

	}

	private class StateRim {

		private int r, x0, x1;
		private State state;

		private int path, loop;
		private int[] paths;

		private StateRim(int i, int j0, State val) {
			r = i;
			x0 = j0;
			state = val;
		}

		private int aviLoop() {
			return len() - path;
		}

		private int aviPath() {
			return len() - cornerCount();
		}

		private int cornerCount() {
			int a0 = x0;
			int a1 = x1;
			int ans = 0;
			if (x1 < x0)
				x1 += r * 8;
			for (int i = 0; i < 4; i++) {
				int c = r * 2 * i;
				if (c < x0)
					c += r * 8;
				if (c >= a0 && c <= a1)
					ans++;
			}
			return ans;
		}

		private int len() {
			if (x1 >= x0)
				return x1 - x0 + 1;
			return x1 + r * 8 - x0 + 1;
		}

		private void seg() {
			paths = new int[len()];
			if (loop == 0 && path == 0) {
				System.out.println("ERROR: all zero");
			}
			// debug.showRim(this);
			int[] rarr = MazeUtil.randArray(paths.length, rand);
			State[] sts = new State[loop];
			for (int i = 0; i < loop; i++)
				sts[i] = new State();
			State[] sta = new State[paths.length];
			int i = 0;
			while (path > 0) {
				int ind = rarr[i++];
				if ((ind + x0) % (r * 2) == 0)
					continue;
				paths[ind] = 1;
				sta[ind] = state;
				path--;
			}
			i = 0;
			rarr = MazeUtil.randArray(paths.length, rand);
			while (loop > 0) {
				int ind = rarr[i++];
				if (paths[ind] > 0)
					continue;
				paths[ind] = 2;
				sta[ind] = sts[sts.length - loop];
				loop--;
			}
			rarr = MazeUtil.randArray(paths.length, rand);
			for (i = 0; i < paths.length; i++) {
				int ind = rarr[i];
				if (paths[ind] == 0) {
					int dir = rand.nextInt(2);
					int off = dir * 2 - 1;
					while (ind >= 0 && ind < paths.length && paths[ind] == 0) {
						paths[ind] = dir + 3;
						ind += off;
					}
				}
			}
			if (paths[0] == 3) {
				i = 0;
				while (paths[i] == 3) {
					paths[i] = 4;
					i++;
				}
			}
			if (paths[paths.length - 1] == 4) {
				i = paths.length - 1;
				while (paths[i] == 4) {
					paths[i] = 3;
					i--;
				}
			}
			for (i = 0; i < paths.length; i++) {
				if (sta[i] != null) {
					if (i > 0) {
						int ind = i - 1;
						while (ind >= 0 && paths[ind] == 4)
							sta[ind--] = sta[i];
					}
					if (i < paths.length - 1) {
						int ind = i + 1;
						while (ind < paths.length && paths[ind] == 3)
							sta[ind++] = sta[i];
					}
				}
			}
			for (i = 0; i < paths.length; i++) {
				int trans = rim(r, x0 + i);
				int val = 0;
				if (paths[i] == 3)
					val |= 1;
				if (paths[i] == 4)
					val |= 2;
				if (i > 0 && paths[i - 1] == 4)
					val |= 1;
				if (i < paths.length - 1 && paths[i + 1] == 3)
					val |= 2;
				if (paths[i] == 1)
					val |= 4;
				set(conn, trans, val);
				set(states, trans, sta[i]);
			}
			// debug.breakpoint("end rim segmentation");
		}

	}

	public static int[][] generate(int rad, Random r, MazeConfig mc) {
		MazeGen maze = new MazeGen(rad, r, mc);
		maze.gen();
		return maze.ans;
	}

	private static int sign(int x) {
		return x < 0 ? -1 : x > 0 ? 1 : 0;
	}

	private final List<State> STATE_LIST = new ArrayList<>();
	private final MazeConfig config;
	// private final Debugger debug;
	private final int[][] ans, conn;

	private final State[][] states;
	private final int r, w;

	private final Random rand;

	public MazeGen(int rad, Random ra, MazeConfig conf) {
		config = conf;
		// debug = deb;
		r = rad;
		w = r * 2 + 1;
		rand = ra;
		ans = new int[w][w];
		conn = new int[w][w];
		states = new State[w][w];
	}

	private void fill(int i, int j) {
		int con = conn[i][j];
		int trans = inner(i + j * w);
		int ai = trans % w - i;
		int aj = trans / w - j;
		int val = 0;
		if ((con & 2) > 0)
			if (trans == -1) {
				if (i < r && j < r)
					val |= 2;
				if (i < r && j > r)
					val |= 4;
				if (i > r && j < r)
					val |= 8;
				if (i > r && j > r)
					val |= 1;
			} else
				val |= getMask(aj, -ai);

		if ((con & 1) > 0)
			if (trans == -1) {
				if (i < r && j < r)
					val |= 8;
				if (i < r && j > r)
					val |= 2;
				if (i > r && j < r)
					val |= 1;
				if (i > r && j > r)
					val |= 4;
			} else
				val |= getMask(-aj, ai);

		if ((con & 4) > 0) {
			val |= getMask(ai, aj);
			ans[i + ai][j + aj] |= getMask(-ai, -aj);
		}
		ans[i][j] |= val;
	}

	private void gen() {
		// debug.begin(this);
		set(states, trans(0, 0), new State());
		for (int i = 1; i <= r; i++) {
			int stcount = STATE_LIST.size();
			StateRim[] rim = getStateRim(i);
			// debug.setRim(rim);
			int rootCount = 0;
			for (int j = 0; j < rim.length; j++)
				if (rim[j].state.isRoot())
					rootCount++;
			if (i == r)
				rootCount = 1;
			int[] rarr = MazeUtil.randArray(rim.length, rand);
			for (int j = 0; j < rim.length; j++) {
				int ind = rarr[j];
				int path = config.randPath(rim[ind], rand, rootCount);
				if (path == 0)
					rootCount--;
				rim[ind].path = path;
				if (i < r)
					rim[ind].loop = config.randLoop(rim[ind], rand);
				rim[ind].seg();
			}
			// debug.breakpoint("rim " + i + " formed");
			PostRim[] post = getPostRim(i);
			for (int j = 0; j < post.length; j++)
				if (!post[j].state.isRoot())
					if (i == r || config.testConn(rand, stcount <= post[j].state.getInd())) {
						State s0 = post[j].state;
						State s1 = post[(j + 1) % post.length].state;
						if (!s0.equals(s1)) {
							// System.out.println("connect " + s0.getInd() + " to " + s1.getInd());
							int t0 = rim(i, post[j].x1);
							int t1 = rim(i, post[(j + 1) % post.length].x0);
							set(conn, t0, get(conn, t0) | 2);
							set(conn, t1, get(conn, t1) | 1);
							s0.set(s1);
						}
					}
			// debug.breakpoint("rim " + i + " done");
		}
		for (int i = 0; i < w; i++)
			for (int j = 0; j < w; j++)
				fill(i, j);
		// debug.breakpoint("finish");
	}

	private int get(int[][] arr, int code) {
		return arr[code % w][code / w];
	}

	private <T> T get(T[][] arr, int code) {
		return arr[code % w][code / w];
	}

	private int getMask(int dx, int dy) {
		if (dx < 0)
			return 1;
		if (dx > 0)
			return 2;
		if (dy < 0)
			return 4;
		if (dy > 0)
			return 8;
		return 0;
	}

	private PostRim[] getPostRim(int i) {
		List<PostRim> list = new ArrayList<>();
		int first = rim(i, 0);
		PostRim cur = new PostRim(i, 0, get(states, first));
		for (int j = 1; j < i * 8; j++) {
			int code = rim(i, j);
			State st = get(states, code);
			if (!cur.state.equals(st)) {
				cur.x1 = j - 1;
				list.add(cur);
				cur = new PostRim(i, j, st);
			}
		}
		cur.x1 = i * 8 - 1;
		if (list.size() == 0)
			list.add(cur);
		else if (cur.state.equals(list.get(0).state))
			list.get(0).x0 = cur.x0;
		else
			list.add(cur);
		return list.toArray(new PostRim[0]);
	}

	private StateRim[] getStateRim(int i) {
		List<StateRim> list = new ArrayList<>();
		int first = inner(rim(i, 1));
		StateRim cur = new StateRim(i, 1, get(states, first));
		int prev = first;
		for (int j = 2; j <= i * 8; j++) {
			int code = inner(rim(i, j));
			if (code == -1)
				continue;
			State st = get(states, code);
			int con = get(conn, code);
			if (prev != code && (con & 1) == 0 || !cur.state.equals(st)) {
				cur.x1 = j - 1;
				list.add(cur);
				cur = new StateRim(i, j, st);
			}
			prev = code;
		}
		if (list.size() == 0)
			list.add(cur);
		else if (cur.state.equals(list.get(0).state))
			list.get(0).x0 = cur.x0;
		else
			list.add(cur);
		return list.toArray(new StateRim[0]);
	}

	private int inner(int trans) {
		int x = trans % w - r;
		int y = trans / w - r;
		if (Math.abs(x) == Math.abs(y))
			return -1;
		if (Math.abs(x) > Math.abs(y))
			return trans(x - sign(x), y);
		else
			return trans(x, y - sign(y));
	}

	private int rim(int i, int j) {
		if (i == 0)
			return trans(0, 0);
		int step = j % (i * 2);
		int rot = j / (i * 2) % 4;
		int dx, dy;
		if (rot == 0) {
			dx = -i + step;
			dy = -i;
		} else if (rot == 1) {
			dx = i;
			dy = -i + step;
		} else if (rot == 2) {
			dx = i - step;
			dy = i;
		} else {
			dx = -i;
			dy = i - step;
		}
		return trans(dx, dy);
	}

	private void set(int[][] arr, int code, int val) {
		arr[code % w][code / w] = val;
	}

	private <T> void set(T[][] arr, int code, T val) {
		arr[code % w][code / w] = val;
	}

	private int trans(int x, int y) {
		return (x + r) + (y + r) * w;
	}

}
