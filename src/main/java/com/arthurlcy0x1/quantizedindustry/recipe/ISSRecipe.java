package com.arthurlcy0x1.quantizedindustry.recipe;

import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public interface ISSRecipe<T extends ISSRecipe.Inv> extends ISIRecipe<T> {

	public static interface Inv extends ISIRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static interface ISSRFactory<T extends SSRecipe<C>, C extends Inv> {

		public T get(ResourceLocation rl, Ingredient in, ItemStack is, int time);

	}

	public class SSRecipe<T extends ISSRecipe.Inv> extends SIRecipe<T> implements ISSRecipe<T> {

		public final ItemStack out;

		public SSRecipe(ResourceLocation rl, IRecipeSerializer<?> rs, IRecipeType<?> rt, Ingredient ingr, ItemStack is,
				int t) {
			super(rl, rs, rt, ingr, t);
			out = is;
		}

		@Override
		public ItemStack getOutput(ItemStack in) {
			return out.copy();
		}

		@Override
		public ItemStack getRecipeOutput() {
			return out;
		}

	}

	public static ItemStack decItem(JsonElement je) {
		if (je.isJsonObject())
			return ShapedRecipe.deserializeItem(je.getAsJsonObject());
		String s1 = je.getAsString();
		ResourceLocation rl = new ResourceLocation(s1);
		return new ItemStack(ForgeRegistries.ITEMS.getValue(rl));
	}

	public static <T extends SSRecipe<C>, C extends Inv> Ser<T, C> getSingleSer(ISSRFactory<T, C> f) {
		return new Ser<T, C>((id, json, ingr, t) -> f.get(id, ingr, decItem(json.get("out")), t),
				(id, data, ingr, t) -> f.get(id, ingr, data.readItemStack(), t),
				(data, r) -> data.writeItemStack(r.getRecipeOutput()));
	}

	@Override
	public default ItemStack getCraftingResult(T inv) {
		return getOutput(inv.getIngredient());
	}

	public ItemStack getOutput(ItemStack in);
}
