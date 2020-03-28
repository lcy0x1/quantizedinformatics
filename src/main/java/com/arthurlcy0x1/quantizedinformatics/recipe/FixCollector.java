package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FixCollector implements ICraftingRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<FixCollector> {

		@Override
		public FixCollector read(ResourceLocation id, JsonObject json) {
			return new FixCollector(id);
		}

		@Override
		public FixCollector read(ResourceLocation id, PacketBuffer buffer) {
			return new FixCollector(id);
		}

		@Override
		public void write(PacketBuffer buffer, FixCollector recipe) {
		}
	}

	private final ResourceLocation id;

	public FixCollector(ResourceLocation ID) {
		id = ID;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory ci) {
		ItemStack tool = null;
		int count = 0;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty())
				if (is.getItem() == Registrar.I_OREC)
					tool = is;
				else if (is.getItem() == Registrar.IBC_FRAME)
					count++;
		}
		ItemStack ans = tool.copy();
		ans.setDamage(Math.max(0, tool.getDamage() - count * 10));
		return ans;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(Registrar.I_OREC);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RSF_OC;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory ci, World w) {
		ItemStack tool = null;
		int count = 0;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty())
				if (is.getItem() == Registrar.I_OREC)
					if (tool != null)
						return false;
					else
						tool = is;
				else if (is.getItem() == Registrar.IBC_FRAME)
					count++;
				else
					return false;
		}
		return tool != null && count > 0;
	}

}
