package com.arthurlcy0x1.quantizedinformatics.recipe;

import net.minecraft.inventory.IInventory;

public interface RedInv extends IInventory {

	public static final int ING_MAIN = 0, ING_SIDE = 1, FUEL = 2, RES_MAIN = 3, RES_SIDE = 4, FUEL_REMAIN = 5;

	public int[] getSlots();

}
