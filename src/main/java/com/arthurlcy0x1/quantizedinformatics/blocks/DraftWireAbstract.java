package com.arthurlcy0x1.quantizedinformatics.blocks;

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

public abstract class DraftWireAbstract extends SixWayBlock implements WireConnect {

	public DraftWireAbstract() {
		super(0.125f, Block.Properties.create(Material.EARTH));
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.makeConnections(context.getWorld(), context.getPos());
	}

	@Override
	public BlockState updatePostPlacement(BlockState os, Direction f, BlockState fs, IWorld w, BlockPos op,
			BlockPos fp) {
		return makeConnections(w, op);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	private BlockState makeConnections(IBlockReader world, BlockPos pos) {
		BlockState s = this.getDefaultState();
		for (Direction d : Direction.values()) {
			Block bl = world.getBlockState(pos.offset(d)).getBlock();
			s = s.with(FACING_TO_PROPERTY_MAP.get(d), bl instanceof WireConnect);
		}
		return s;
	}

}
