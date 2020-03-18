package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.PrepChip;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChipRedRec extends RedRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<ChipRedRec> {

		@Override
		public ChipRedRec read(ResourceLocation id, JsonObject json) {
			return new ChipRedRec(id, JSONUtils.getInt(json, "time"));
		}

		@Override
		public ChipRedRec read(ResourceLocation id, PacketBuffer buffer) {
			return new ChipRedRec(id, buffer.readInt());
		}

		@Override
		public void write(PacketBuffer buffer, ChipRedRec recipe) {
			buffer.writeInt(recipe.time);
		}

	}

	public ChipRedRec(ResourceLocation rl, int t) {
		super(rl, new Ingredient[] { null, null, null }, new int[] { 1, 1, 1 }, new ItemStack[] { null, null }, t);

	}

	@Override
	public ItemStack getCraftingResult(RedRecipe.Inv inv) {
		ItemStack im = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_MAIN]);
		ItemStack me = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.MEDIUM]);
		return PrepChip.setProcStat(im.copy(), me.getItem());
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RSC_RED;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	public boolean matches(RedRecipe.Inv inv) {
		ItemStack im = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_MAIN]);
		ItemStack is = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_SIDE]);
		ItemStack me = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.MEDIUM]);

		if (!im.isEmpty() && is.isEmpty() && !me.isEmpty() && im.getItem() == Registrar.IC_PREP)
			return PrepChip.validForDope(im, me.getItem());
		return false;
	}

}
