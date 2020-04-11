package com.arthurlcy0x1.quantizedinformatics.power.blocks;

import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.IPowerBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class PMBlock extends BaseBlock implements WireConnect, IPowerBlock {

	private final boolean hasFluid;

	public PMBlock(STE ste, boolean f) {
		super(construct(BlockProp.M_POWER).addImpls(HOR, ste));
		hasFluid = f;
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return (type == POWER || hasFluid && type == FLUID) && b.get(HORIZONTAL_FACING) != d;
	}

}
