package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuanKey extends QuanBlock {

	public static ActionResultType keyOnClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		if (w.isRemote)
			return ActionResultType.SUCCESS;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				Block b = w.getBlockState(pos.add(i, 0, j)).getBlock();
				if (b != Registrar.BQ_KEY)
					return ActionResultType.SUCCESS;
				b = w.getBlockState(pos.add(i, 1, j)).getBlock();
				if (b != Registrar.BQ_MAZEWALL)
					return ActionResultType.SUCCESS;
			}
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				w.setBlockState(pos.add(i, 0, j), Registrar.BQ_AIR.getDefaultState());
				w.setBlockState(pos.add(i, 1, j), Registrar.BQ_AIR.getDefaultState());
			}
		return ActionResultType.SUCCESS;
	}

	public QuanKey() {
		super(construct(BlockProp.QW_BREAK).addImpl((IClick) QuanKey::keyOnClick));
	}

}
