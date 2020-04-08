package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class StandHead extends QuanBlock {

	public StandHead() {
		super(construct(BlockProp.STONE_STAND).addImpl(new StoneStand.SRep(-1)));
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isNormalCube(BlockState bs, IBlockReader w, BlockPos pos) {
		return false;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

}
