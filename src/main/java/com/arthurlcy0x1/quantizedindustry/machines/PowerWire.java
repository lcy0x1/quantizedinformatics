package com.arthurlcy0x1.quantizedindustry.machines;

import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class PowerWire extends Wire {

	public PowerWire() {
		super(POWER);
	}

	@Override
	public boolean canConnectFrom(int type, BlockState bs, Direction dire) {
		if (bs.getBlock() instanceof PowerWire)
			return bs.getBlock() == this;
		return type == POWER;
	}

}
