package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;

public interface IMachineRecipe<T extends IInventory> extends IRecipe<T> {

	public int getCraftCost();

}
