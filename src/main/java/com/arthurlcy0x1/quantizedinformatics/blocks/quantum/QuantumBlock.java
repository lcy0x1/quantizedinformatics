package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface QuantumBlock {

	public void onRep(World w, BlockState bs, BlockState tar, BlockPos pos);

}
