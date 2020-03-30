package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.battle.MaxwellItem;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MaxwellRecipe extends ShapedRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<MaxwellRecipe> {

		@Override
		public MaxwellRecipe read(ResourceLocation id, JsonObject json) {
			String sc = JSONUtils.getString(json, "center");
			String sr = JSONUtils.getString(json, "surround");
			String so = JSONUtils.getString(json, "output");
			Item c = sc.length() == 0 ? Items.AIR : ForgeRegistries.ITEMS.getValue(new ResourceLocation(sc));
			Item r = ForgeRegistries.ITEMS.getValue(new ResourceLocation(sr));
			Item o = ForgeRegistries.ITEMS.getValue(new ResourceLocation(so));
			return new MaxwellRecipe(id, c, r, o);
		}

		@Override
		public MaxwellRecipe read(ResourceLocation id, PacketBuffer buffer) {
			Item c = buffer.readRegistryId();
			Item r = buffer.readRegistryId();
			Item o = buffer.readRegistryId();
			return new MaxwellRecipe(id, c, r, o);
		}

		@Override
		public void write(PacketBuffer buffer, MaxwellRecipe recipe) {
			buffer.writeRegistryId(recipe.c);
			buffer.writeRegistryId(recipe.r);
			buffer.writeRegistryId(recipe.o);

		}
	}

	private static final NonNullList<Ingredient> translate(Item c, Item r) {
		Ingredient i2 = c == Items.AIR ? Ingredient.EMPTY : Ingredient.fromItems(c);
		Ingredient i1 = Ingredient.fromItems(r);
		return NonNullList.from(Ingredient.EMPTY, i1, i1, i1, i1, i2, i1, i1, i1, i1);
	}

	private final Item c, r, o;

	public MaxwellRecipe(ResourceLocation id, Item mc, Item mr, Item item) {
		super(id, "", 3, 3, translate(mc, mr), new ItemStack(item));
		c = mc;
		r = mr;
		o = item;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory ci) {
		int lv = MaxwellItem.getLevel(ci.getStackInSlot(0));
		ItemStack is = getRecipeOutput().copy();
		MaxwellItem.setLevel(is, lv + 1);
		return is;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_MAX;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory ci, World w) {
		int clv = -1;
		for (int i = 0; i < 9; i++) {
			ItemStack is = ci.getStackInSlot(i);
			if (is.isEmpty() && c == Items.AIR && i == 4)
				continue;
			if (i == 4 && !(c instanceof MaxwellItem) && is.getItem() == c)
				continue;
			if (is.isEmpty() || !(is.getItem() instanceof MaxwellItem))
				return false;
			if (i == 4 && is.getItem() != c)
				return false;
			int lv = MaxwellItem.getLevel(is);
			Item cr = r;
			if (lv == 0 && r == Registrar.IMW_ELEC)
				cr = Registrar.IM_ELEC;
			if (lv == 0 && r == Registrar.IMW_MAGN)
				cr = Registrar.IM_MAGN;
			if (i != 4 && is.getItem() != cr)
				return false;
			if (clv == -1)
				clv = lv;
			else if (i != 4 && clv != lv || i == 4 && clv != lv - 1)
				return false;
		}
		return true;

	}

}
