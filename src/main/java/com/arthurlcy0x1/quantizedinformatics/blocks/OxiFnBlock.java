package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.world.IBlockReader;

public class OxiFnBlock extends HorizontalBlock {

	public OxiFnBlock() {
		super(Block.Properties.create(Material.ROCK));
	}

	@Override
	public OxiFnTE createTileEntity(BlockState state, IBlockReader world) {
		return new OxiFnTE();
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
