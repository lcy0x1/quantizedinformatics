package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;

import static net.minecraft.state.StateContainer.Builder;

public class DraftGate extends HorizontalBlock {

	public DraftGate() {
		super(Block.Properties.create(Material.EARTH));
	
	}

	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

}
