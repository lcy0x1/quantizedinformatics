package com.arthurlcy0x1.quantizedindustry.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface ICSRecipe<T extends ICSRecipe.Inv> extends ICIRecipe<T> {

	public class CSRecipe<T extends ICSRecipe.Inv> extends CIRecipe<T> implements ICSRecipe<T> {

		public final ItemStack out;

		public CSRecipe(ResourceLocation rl, IRecipeSerializer<?> rs, IRecipeType<?> rt, Ingredient[][] ingr,
				ItemStack is, int t) {
			super(rl, rs, rt, ingr, t);
			out = is;
		}

		@Override
		public ItemStack getOutput() {
			return out.copy();
		}

		@Override
		public ItemStack getRecipeOutput() {
			return out;
		}

	}

	public static interface ICSRFactory<T extends CSRecipe<C>, C extends Inv> {

		public T get(ResourceLocation rl, Ingredient[][] in, ItemStack is, int time);

	}

	public static <T extends CSRecipe<C>, C extends Inv> Ser<T, C> getSingleSer(ICSRFactory<T, C> f) {
		return new Ser<T, C>((id, json, ingr, t) -> f.get(id, ingr, ISSRecipe.decItem(json.get("out")), t),
				(id, data, ingr, t) -> f.get(id, ingr, data.readItemStack(), t),
				(data, r) -> data.writeItemStack(r.getRecipeOutput()));
	}

}
