package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.battle.IMaxwell.IMaxRepairable;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FixMaxItem implements ICraftingRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<FixMaxItem> {

		@Override
		public FixMaxItem read(ResourceLocation id, JsonObject json) {
			return new FixMaxItem(id);
		}

		@Override
		public FixMaxItem read(ResourceLocation id, PacketBuffer buffer) {
			return new FixMaxItem(id);
		}

		@Override
		public void write(PacketBuffer buffer, FixMaxItem recipe) {
		}
	}

	private final ResourceLocation id;

	public FixMaxItem(ResourceLocation ID) {
		id = ID;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory ci) {
		ItemStack w = null, r = null;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty())
				if (is.getItem() instanceof IMaxRepairable)
					w = is;
				else
					r = is;
		}
		IMaxRepairable item = (IMaxRepairable) w.getItem();
		ItemStack ans = w.copy();
		ans.setDamage(Math.max(0, w.getDamage() - item.repair(r)));
		return ans;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RSF_WP;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory ci, World world) {
		ItemStack w = null;
		int count = 0;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty()) {
				if (is.getItem() instanceof IMaxRepairable)
					if (w != null)
						return false;
					else
						w = is;
				count++;
			}
		}
		if (w == null || count != 2)
			return false;
		IMaxRepairable item = (IMaxRepairable) w.getItem();
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty() && item.canUse(is))
				return true;
		}
		return false;
	}

}
