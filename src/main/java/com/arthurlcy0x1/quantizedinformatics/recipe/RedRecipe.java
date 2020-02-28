package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RedRecipe implements IRecipe<RedInv> {

	public final ResourceLocation id;
	public final Ingredient[] ing;
	public final ItemStack[] output;
	public final int[] inc;
	public final int time;

	public RedRecipe(ResourceLocation rl, Ingredient[] ingr, int[] in, ItemStack[] is, int t) {
		id = rl;
		ing = ingr;
		output = is;
		inc = in;
		time = t;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= ing.length;
	}

	public void craft(RedInv inv) {
		int ing0 = inv.getSlots()[RedInv.ING_MAIN];
		int ing1 = inv.getSlots()[RedInv.ING_SIDE];
		inv.decrStackSize(ing0, inc[0]);
		if (ing[1] != null)
			inv.decrStackSize(ing1, inc[1]);
		int res0 = inv.getSlots()[RedInv.ING_MAIN];
		int res1 = inv.getSlots()[RedInv.ING_SIDE];
		if (inv.getStackInSlot(res0) == ItemStack.EMPTY)
			inv.setInventorySlotContents(res0, output[0].copy());
		else
			inv.getStackInSlot(res0).grow(output[0].getCount());
		if (output[1] != null)
			if (inv.getStackInSlot(res1) == ItemStack.EMPTY)
				inv.setInventorySlotContents(res1, output[1].copy());
			else
				inv.getStackInSlot(res1).grow(output[1].getCount());
	}

	public int getCraftCost() {
		return time;
	}

	@Override
	public ItemStack getCraftingResult(RedInv inv) {
		return output[0].copy();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output[0];
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_RED;
	}

	@Override
	public IRecipeType<?> getType() {
		return Registrar.RT_RED;
	}

	@Override
	public boolean matches(RedInv inv, World worldIn) {
		int ing0 = inv.getSlots()[RedInv.ING_MAIN];
		int ing1 = inv.getSlots()[RedInv.ING_SIDE];
		ItemStack ig0 = inv.getStackInSlot(ing0);
		ItemStack ig1 = inv.getStackInSlot(ing1);
		if (!ing[0].test(ig0) || ig0.getCount() < inc[0])
			return false;

		if (ing[1] != null) {
			if (!ing[1].test(ig1) || ig1.getCount() < inc[1])
				return false;
		} else if (ig1 != ItemStack.EMPTY)
			return false;
		int res0 = inv.getSlots()[RedInv.ING_MAIN];
		int res1 = inv.getSlots()[RedInv.ING_SIDE];
		ItemStack is0 = inv.getStackInSlot(res0);
		ItemStack is1 = inv.getStackInSlot(res1);
		if (is0.getCount() + output[0].getCount() > is0.getItem().getItemStackLimit(is0))
			return false;
		if (output[1] != null && is1.getCount() + output[1].getCount() > is1.getItem().getItemStackLimit(is1))
			return false;
		if (is0 != ItemStack.EMPTY && output[0].getItem() != is0.getItem())
			return false;
		if (output[1] != null)
			if (is1 != ItemStack.EMPTY && output[1].getItem() != is1.getItem())
				return false;
		return true;
	}

}
