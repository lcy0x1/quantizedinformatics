package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.world.RegWorld;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class QTeleBlock extends QuanBlock {

	public QTeleBlock() {
		super(construct(BlockProp.PORTAL));
	}

	@Override
	public void onEntityCollision(BlockState state, World w, BlockPos pos, Entity ent) {
		if (!w.isRemote && ent instanceof PlayerEntity) {
			if (ent.dimension == DimensionType.OVERWORLD) {
				DimensionType dimType = RegWorld.getDT("quantum_world", RegWorld.MD_Q);
				ent.changeDimension(dimType, RegWorld.TELE_Q);
			} else {
				ent.changeDimension(DimensionType.OVERWORLD, RegWorld.TELE_Q);
			}

		}
	}

}
