package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MazeWall extends QuanBlock {

	@Override
	public void onRep(World w, BlockState cur, BlockState tar, BlockPos pos) {
		w.setBlockState(pos, Registrar.BQ_MAZEWALL.getDefaultState(), 50);
	}

}
