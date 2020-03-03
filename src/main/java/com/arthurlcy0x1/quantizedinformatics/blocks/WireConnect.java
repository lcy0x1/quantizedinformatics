package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public interface WireConnect {

	public static final int GATE = 1, CRAFT = 2;

	public default boolean canConnectFrom(BlockState b, Direction d) {
		return true;
	}

	public default boolean connectable(BlockState b, Direction d) {
		Block bl = b.getBlock();
		if (!(bl instanceof WireConnect))
			return false;
		WireConnect wc = (WireConnect) bl;
		if (type() != wc.type())
			return false;
		return wc.canConnectFrom(b, d.getOpposite());
	}

	public int type();

}
