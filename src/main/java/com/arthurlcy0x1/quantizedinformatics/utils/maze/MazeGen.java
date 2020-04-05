package com.arthurlcy0x1.quantizedinformatics.utils.maze;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MazeGen {

	private class Face {

		private final int r, n, m;
		private final Direction d, di, dj;
		private final BlockPos min, max;

		private Face(int R, Direction D) {
			r = R;
			d = D;
			BlockPos cen = o.offset(d, r);
			int dx = 1 - Math.abs(d.getXOffset());
			int dy = 1 - Math.abs(d.getYOffset());
			int dz = 1 - Math.abs(d.getZOffset());
			min = regulate(cen.add(-dx * r, -dy * r, -dz * r));
			max = regulate(cen.add(dx * r, dy * r, dz * r));
			di = x == 0 ? Direction.UP : Direction.EAST;
			dj = z == 0 ? Direction.UP : Direction.SOUTH;
			BlockPos dif = max.subtract(min);
			n = x == 0 ? dif.getY() : dif.getX();
			m = z == 0 ? dif.getY() : dif.getZ();
		}

	}

	public static int[][][] generateMaze(int x, int y, int z, BlockPos o) {
		MazeGen maze = new MazeGen(x, y, z, o);
		return maze.gen();
	}

	private final int[][][] ans, state;

	private final int x, y, z, rad;

	private final BlockPos o;

	private final Map<Integer, Set<BlockPos>> map = new HashMap<>();

	private MazeGen(int X, int Y, int Z, BlockPos O) {
		x = X;
		y = Y;
		z = Z;
		o = O;
		ans = new int[x][y][z];
		state = new int[x][y][z];
		int r = 0;
		r = Math.max(r, o.getX());
		r = Math.max(r, x - o.getX() - 1);
		r = Math.max(r, o.getY());
		r = Math.max(r, y - o.getY() - 1);
		r = Math.max(r, o.getZ());
		r = Math.max(r, z - o.getZ() - 1);
		rad = r;
	}

	private int[][][] gen() {
		set(ans, o, 63);
		set(state, o, 1);
		for (int i = 1; i <= rad; i++)
			genSurface(i);
		return ans;
	}

	private void genSurface(int r) {
		Face[] face = new Face[6];
		for (int i = 0; i < 6; i++)
			face[i] = getFace(r, Direction.byIndex(i));
		// TODO
	}

	private int get(int[][][] arr, BlockPos p) {
		return arr[p.getX()][p.getY()][p.getZ()];
	}

	private Face getFace(int r, Direction d) {
		if (within(o.offset(d, r)))
			return new Face(r, d);
		return null;
	}

	private BlockPos regulate(BlockPos p) {
		int vx = MathHelper.clamp(p.getX(), 0, x - 1);
		int vy = MathHelper.clamp(p.getY(), 0, y - 1);
		int vz = MathHelper.clamp(p.getZ(), 0, z - 1);
		return new BlockPos(vx, vy, vz);
	}

	private void set(int[][][] arr, BlockPos p, int v) {
		arr[p.getX()][p.getY()][p.getZ()] = v;
	}

	private boolean within(BlockPos p) {
		return p.getX() >= 0 && p.getX() < x && p.getY() >= 0 && p.getY() < y && p.getZ() >= 0 && p.getZ() < z;
	}

}
