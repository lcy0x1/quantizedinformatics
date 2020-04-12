package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface ISMRecipe<T extends ISMRecipe.Inv> extends ISIRecipe<T> {

	public static interface Inv extends ISIRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static interface ISMRFactory<T extends SMRecipe<C>, C extends Inv> {

		public T get(ResourceLocation rl, Ingredient in, ItemStack[] is, int time);

	}

	public class SMRecipe<T extends ISMRecipe.Inv> extends SIRecipe<T> implements ISMRecipe<T> {

		public final ItemStack[] out;

		public SMRecipe(ResourceLocation rl, IRecipeSerializer<?> rs, IRecipeType<?> rt, Ingredient ingr,
				ItemStack[] is, int t) {
			super(rl, rs, rt, ingr, t);
			out = is;
		}

		@Override
		public ItemStack getCraftingResult(T inv) {
			return out[0].copy();
		}

		public ItemStack[] getCraftingResults(T inv) {
			return out;
		}

		@Override
		public ItemStack[] getOutput(ItemStack in) {
			ItemStack[] ans = new ItemStack[out.length];
			for (int i = 0; i < out.length; i++)
				ans[i] = out[i].copy();
			return ans;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return out[0];
		}

	}

	public static ItemStack[] decItems(JsonElement je) {
		if (je.isJsonArray()) {
			JsonArray ja = je.getAsJsonArray();
			ItemStack[] is = new ItemStack[ja.size()];
			for (int i = 0; i < ja.size(); i++)
				is[i] = ISSRecipe.decItem(ja.get(i));
			return is;
		}
		return new ItemStack[] { ISSRecipe.decItem(je) };
	}

	public static ItemStack[] decItems(PacketBuffer data) {
		int n = data.readInt();
		ItemStack[] is = new ItemStack[n];
		for (int i = 0; i < n; i++)
			is[i] = data.readItemStack();
		return is;
	}

	public static <T extends SMRecipe<C>, C extends Inv> Ser<T, C> getMultiSer(ISMRFactory<T, C> f) {
		return new Ser<T, C>((id, json, ingr, t) -> f.get(id, ingr, decItems(json), t),
				(id, data, ingr, t) -> f.get(id, ingr, decItems(data), t), (data, r) -> writeItems(data, r.out));
	}

	public static void writeItems(PacketBuffer data, ItemStack[] is) {
		data.writeInt(is.length);
		for (ItemStack i : is)
			data.writeItemStack(i);
	}

	public ItemStack[] getOutput(ItemStack in);

}
