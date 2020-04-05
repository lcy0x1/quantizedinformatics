package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.function.Function;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class QuantumTele implements ITeleporter {

	public Entity placeEntity(Entity ent, ServerWorld cur, ServerWorld dst, float yaw, Function<Boolean, Entity> pos) {
		BlockPos p = ent.getPosition();
		for(int i=-1;i<=1;i++)
			for(int j=-1;j<=1;j++) {
				dst.setBlockState(p.add(i,-1,j), Registrar.BQ_STONE.getDefaultState());
				dst.setBlockState(p.add(i,0,j), Blocks.AIR.getDefaultState());
				dst.setBlockState(p.add(i,1,j), Blocks.AIR.getDefaultState());
				dst.setBlockState(p.add(i,2,j), Blocks.AIR.getDefaultState());
			}
		return pos.apply(false);
	}

}
