package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class SPipeHead extends BaseBlock implements WireConnect {

	public SPipeHead() {
		super(construct(BlockProp.M_PIPE).addImpls(ALD));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == SPIPE && b.get(FACING) == d.getOpposite();
	}

}
