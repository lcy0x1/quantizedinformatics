package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DraftWire extends SixWayBlock implements WireConnect {

	/**
	 * return 2 arrays of BlockPos. <br>
	 * Array 0 is an array of Blocks that reads from the wire <br>
	 * Array 1 is an array of Blocks that writes to the wire
	 */
	public static BlockPos[][] queryGate(World w, BlockPos pb) {
		List<BlockPos> ans = new ArrayList<>();
		List<BlockPos> ins = new ArrayList<>();
		Queue<BlockPos> q = new ArrayDeque<>();
		Set<BlockPos> set = new HashSet<>();
		q.add(pb);
		set.add(pb);
		while (q.size() > 0) {
			BlockPos p0 = q.poll();
			for (Direction d : Direction.values()) {
				BlockPos p1 = p0.offset(d);
				if (set.contains(p1))
					continue;
				BlockState st = w.getBlockState(p1);
				if (st.getBlock() instanceof DraftWire)
					q.add(p1);
				else if (st.getBlock() instanceof WireConnect.DraftIO) {
					WireConnect.DraftIO io = (WireConnect.DraftIO) st.getBlock();
					int type = io.ioType(st, d.getOpposite());
					if (type == WireConnect.OUTPUT)
						ans.add(p1);
					else if (type == WireConnect.INPUT)
						ins.add(p1);
					continue;
				}
				set.add(p1);
			}
		}
		BlockPos[][] ret = { ins.toArray(new BlockPos[0]), ans.toArray(new BlockPos[0]) };
		return ret;
	}

	public static BlockPos[] queryPoint(World w, BlockPos c) {
		Set<BlockPos> set = new HashSet<>();
		List<BlockPos> ans = new ArrayList<>();
		Queue<BlockPos> q = new ArrayDeque<>();
		set.add(c);
		q.add(c);
		while (q.size() > 0) {
			BlockPos p0 = q.poll();
			BlockState s = w.getBlockState(p0);
			WireConnect cn = (WireConnect) s.getBlock();
			for (Direction d : Direction.values())
				if (cn.canConnectFrom(CRAFT, s, d)) {
					BlockPos p = p0.offset(d);
					if (set.contains(p))
						continue;
					set.add(p);
					BlockState bs = w.getBlockState(p);
					if (bs.getBlock() instanceof WireConnect) {
						WireConnect wc = (WireConnect) bs.getBlock();
						if (!wc.canConnectFrom(CRAFT, bs, d.getOpposite()))
							continue;
						if (wc instanceof DraftIO)
							ans.add(p);
						q.add(p);
					}

				}
		}
		return ans.toArray(new BlockPos[0]);
	}

	private final int type;

	public DraftWire(int n) {
		super(0.25f, Block.Properties.create(Material.EARTH));
		type = n;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public boolean canConnectFrom(int t, BlockState b, Direction d) {
		return t == type;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return makeConnections(context.getWorld(), context.getPos());
	}

	@Override
	public BlockState updatePostPlacement(BlockState os, Direction f, BlockState fs, IWorld w, BlockPos op,
			BlockPos fp) {
		return os.with(FACING_TO_PROPERTY_MAP.get(f), connectable(type, fs, f));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	private BlockState makeConnections(IBlockReader world, BlockPos pos) {
		BlockState s = this.getDefaultState();
		for (Direction d : Direction.values()) {
			BlockState bl = world.getBlockState(pos.offset(d));
			s = s.with(FACING_TO_PROPERTY_MAP.get(d), connectable(type, bl, d));
		}
		return s;
	}

}
