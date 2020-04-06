package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

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

	protected QuanAir(Properties p) {
		super(p.doesNotBlockMovement().noDrops());
	}

	@Override
	public boolean isAir(BlockState state) {
		return false;
	}

	@Override
	public void onRep(World w, BlockState cur, BlockState tar, BlockPos pos) {
		if (tar.getBlock() instanceof QuantumBlock)
			return;
		w.setBlockState(pos, Registrar.BQ_AIR.getDefaultState(), 50);
	}

	@Override
	public void onReplaced(BlockState cur, World w, BlockPos pos, BlockState tar, boolean isMoving) {
		QuanBlock.REP.onReplaced(cur, w, pos, tar, isMoving);
	}

}
