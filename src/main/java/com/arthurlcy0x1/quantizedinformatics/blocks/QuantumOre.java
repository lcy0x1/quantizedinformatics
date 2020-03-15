package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuantumOre extends BaseBlock {

	public QuantumOre() {
		super(construct(Material.ROCK));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState bs, World w, BlockPos pos, Block b, BlockPos bp, boolean m) {
		checkAir(w, pos);
		super.neighborChanged(bs, w, pos, b, bp, m);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onBlockAdded(BlockState ns, World w, BlockPos p, BlockState os, boolean m) {
		checkAir(w, p);
		super.onBlockAdded(ns, w, p, os, m);
	}

	private void checkAir(World w, BlockPos pos) {
		for (Direction d : Direction.values()) {
			BlockPos p = pos.offset(d);
			BlockState bs = w.getBlockState(p);
			if (!bs.isSolid()) {
				w.destroyBlock(p, bs.getBlock() != Registrar.B_FOG);
				w.setBlockState(p, Registrar.B_FOG.getDefaultState());
			}
		}
	}

}
