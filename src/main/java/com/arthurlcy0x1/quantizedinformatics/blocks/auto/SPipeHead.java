package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;

public class SPipeHead extends BaseBlock implements WireConnect {

	public SPipeHead() {
		super(construct(Material.ROCK).addImpls(ALD));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == SPIPE && b.get(FACING) == d.getOpposite();
	}

}
