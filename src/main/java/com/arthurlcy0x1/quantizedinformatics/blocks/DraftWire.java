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

	public static interface DraftIO extends WireConnect {

		public static final int NONE = 0, INPUT = 1, OUTPUT = 2;

		@Override
		public default boolean canConnectFrom(BlockState b, Direction d) {
			return ioType(b, d) != NONE;
		}

		public int ioType(BlockState b, Direction d);

		@Override
		public default int type() {
			return GATE;
		}

	}

	public static interface DraftTE {

		public int outputCount();

		public int getOutput(int ind);

	}

	public static BlockPos[][] query(World w, BlockPos pb) {
		List<BlockPos> ans = new ArrayList<>();
		List<BlockPos> ins = new ArrayList<>();
		Queue<BlockPos> q = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		q.add(pb);
		visited.add(pb);
		while (q.size() > 0) {
			BlockPos p0 = q.poll();
			for (Direction d : Direction.values()) {
				BlockPos p1 = p0.offset(d);
				if (visited.contains(p1))
					continue;
				visited.add(p1);
				BlockState st = w.getBlockState(p1);
				if (st.getBlock() instanceof DraftWire)
					q.add(p1);
				else if (st.getBlock() instanceof DraftIO) {
					DraftIO io = (DraftIO) st.getBlock();
					int type = io.ioType(st, d.getOpposite());
					if (type == DraftIO.OUTPUT)
						ans.add(p1);
					else if (type == DraftIO.INPUT)
						ins.add(p1);

				}
			}
		}
		BlockPos[][] ret = { ins.toArray(new BlockPos[0]), ans.toArray(new BlockPos[0]) };
		return ret;
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
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return makeConnections(context.getWorld(), context.getPos());
	}

	@Override
	public int type() {
		return type;
	}

	@Override
	public BlockState updatePostPlacement(BlockState os, Direction f, BlockState fs, IWorld w, BlockPos op,
			BlockPos fp) {
		return os.with(FACING_TO_PROPERTY_MAP.get(f), connectable(fs, f));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	private BlockState makeConnections(IBlockReader world, BlockPos pos) {
		BlockState s = this.getDefaultState();
		for (Direction d : Direction.values()) {
			BlockState bl = world.getBlockState(pos.offset(d));
			s = s.with(FACING_TO_PROPERTY_MAP.get(d), connectable(bl, d));
		}
		return s;
	}

}
