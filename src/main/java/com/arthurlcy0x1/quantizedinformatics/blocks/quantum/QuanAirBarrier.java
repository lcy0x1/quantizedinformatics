package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class QuanAirBarrier extends QuanAir {

	public QuanAirBarrier() {
		super(Properties.create(Material.AIR).tickRandomly());
	}

	@Override
	public void onRep(World w, BlockState cur, BlockState tar, BlockPos pos) {
		if (tar.getBlock() == Registrar.BQ_BARAIR || tar.getBlock() == Registrar.BQ_BARRIER)
			return;
		w.setBlockState(pos, Registrar.BQ_BARRIER.getDefaultState(), 50);
	}

	@Override
	public void randomTick(BlockState bs, ServerWorld w, BlockPos pos, Random r) {
		Block b0 = w.getBlockState(pos.up()).getBlock();
		Block b1 = w.getBlockState(pos.down()).getBlock();
		if (isValid(b0) || isValid(b1)) {
			w.setBlockState(pos, Registrar.BQ_BARRIER.getDefaultState());
			return;
		}
		Queue<BlockPos> q = new ArrayDeque<>();
		q.add(pos);
		Set<BlockPos> mar = new HashSet<>();
		Set<BlockPos> set = new HashSet<>();
		set.add(pos);
		while (q.size() > 0) {
			BlockPos p0 = q.poll();
			for (Direction d : Direction.values()) {
				BlockPos p1 = p0.offset(d);
				Block b = w.getBlockState(p1).getBlock();
				if ((d == Direction.UP || d == Direction.DOWN) && isValid(b))
					mar.add(p0);
				if (!set.contains(p1)) {
					set.add(p1);
					if (b == Registrar.BQ_BARAIR)
						q.add(p1);
				}
			}
		}
		for (BlockPos pi : mar)
			w.getPendingBlockTicks().scheduleTick(pi, this, w.getRandom().nextInt(30));
	}

	private boolean isValid(Block b) {
		return b == Registrar.BQ_BARRIER || b == Blocks.VOID_AIR;
	}

}
