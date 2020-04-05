package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuanAir extends AirBlock implements QuantumBlock {

	public QuanAir() {
		super(Block.Properties.create(Material.AIR).doesNotBlockMovement().noDrops());
	}

	@Override
	public void onReplaced(BlockState cur, World w, BlockPos pos, BlockState tar, boolean isMoving) {
		QuanBlock.REP.onReplaced(cur, w, pos, tar, isMoving);
	}

}
