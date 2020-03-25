package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.EntityCannon;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FixWeapon implements ICraftingRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<FixWeapon> {

		@Override
		public FixWeapon read(ResourceLocation id, JsonObject json) {
			return new FixWeapon(id);
		}

		@Override
		public FixWeapon read(ResourceLocation id, PacketBuffer buffer) {
			return new FixWeapon(id);
		}

		@Override
		public void write(PacketBuffer buffer, FixWeapon recipe) {
		}
	}

	private final ResourceLocation id;

	public FixWeapon(ResourceLocation ID) {
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
				if (is.getItem() instanceof EntityCannon)
					tool = is;
				else if (is.getItem() == Registrar.IMU_ATK || is.getItem() == Registrar.IMU_DEF)
					count++;
		}
		ItemStack ans = tool.copy();
		ans.setDamage(Math.max(0, tool.getDamage() - count * 128));
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
	public boolean matches(CraftingInventory ci, World w) {
		ItemStack tool = null;
		int count = 0;
		for (int i = 0; i < ci.getSizeInventory(); i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (!is.isEmpty())
				if (is.getItem() instanceof EntityCannon)
					if (tool != null)
						return false;
					else
						tool = is;
				else if (is.getItem() == Registrar.IMU_ATK || is.getItem() == Registrar.IMU_DEF)
					count++;
				else
					return false;
		}
		return tool != null && count > 0;
	}

}
