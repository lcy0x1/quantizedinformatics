package com.arthurlcy0x1.quantizedinformatics.recipe;

import net.minecraft.inventory.IInventory;

public interface OxiInv extends IInventory {

	public static final int ING_MAIN = 0, ING_SIDE = 1, MEDIUM = 2, FUEL = 3, RES_MAIN = 4, RES_SIDE = 5,
			FUEL_REMAIN = 6;

	public int[] getSlots();

}
