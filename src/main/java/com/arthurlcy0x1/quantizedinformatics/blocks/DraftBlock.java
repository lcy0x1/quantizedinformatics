package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;

public interface DraftBlock {

	public static final int GATE = 1, CRAFT = 2;

	public default boolean connectable(Block b) {
		return b instanceof DraftBlock && ((DraftBlock) b).type() == type();
	}

	public int type();

}
