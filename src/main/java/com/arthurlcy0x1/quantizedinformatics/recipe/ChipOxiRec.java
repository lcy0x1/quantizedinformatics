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
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChipOxiRec extends OxiRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<ChipOxiRec> {

		@Override
		public ChipOxiRec read(ResourceLocation id, JsonObject json) {
			return new ChipOxiRec(id, JSONUtils.getInt(json, "time"));
		}

		@Override
		public ChipOxiRec read(ResourceLocation id, PacketBuffer buffer) {
			return new ChipOxiRec(id, buffer.readInt());
		}

		@Override
		public void write(PacketBuffer buffer, ChipOxiRec recipe) {
			buffer.writeInt(recipe.time);
		}

	}

	public ChipOxiRec(ResourceLocation rl, int t) {
		super(rl, new Ingredient[] { null, null }, new int[] { 1, 1 }, new ItemStack[] { null, null }, t);

	}

	@Override
	public ItemStack getCraftingResult(OxiRecipe.Inv inv) {
		ItemStack im = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_MAIN]);
		return PrepChip.setProcStat(im.copy(), null);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RSC_OXI;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(OxiRecipe.Inv inv, World worldIn) {
		ItemStack im = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_MAIN]);
		ItemStack is = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_SIDE]);
		if (!im.isEmpty() && is.isEmpty() && im.getItem() == Registrar.IC_PREP)
			return PrepChip.validForDope(im, null);
		return false;
	}
}
