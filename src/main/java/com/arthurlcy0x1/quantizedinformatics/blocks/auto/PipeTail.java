package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.blocks.AllDireBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;

public class PipeTail extends AllDireBlock implements WireConnect {

	public PipeTail() {
		super(Block.Properties.create(Material.ROCK));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == PIPE && b.get(FACING) == d.getOpposite();
	}

}
