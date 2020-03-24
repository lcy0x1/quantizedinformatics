package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.Telescope;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class TeleRecipe implements ICraftingRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<TeleRecipe> {

		@Override
		public TeleRecipe read(ResourceLocation id, JsonObject json) {
			return new TeleRecipe(id);
		}

		@Override
		public TeleRecipe read(ResourceLocation id, PacketBuffer buffer) {
			return new TeleRecipe(id);
		}

		@Override
		public void write(PacketBuffer buffer, TeleRecipe recipe) {
		}
	}

	private final ResourceLocation id;

	public TeleRecipe(ResourceLocation ID) {
		id = ID;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory ci) {
		int lv = 0;
		ItemStack tool = null;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty() && is.getItem() instanceof Telescope) {
				if (is.getItem() != Registrar.IW_TELE)
					tool = is;
				lv += Telescope.getLv(is);
			}
		}
		return Telescope.setLv(tool == null ? getRecipeOutput() : tool.copy(), lv);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(Registrar.IW_TELE);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_TELE;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory ci, World w) {
		int count = 0;
		ItemStack tool = null;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty() && is.getItem() instanceof Telescope) {
				if (is.getItem() != Registrar.IW_TELE)
					if (tool != null)
						return false;
					else
						tool = is;
				count++;
			}
		}
		return count > 1;
	}

}
