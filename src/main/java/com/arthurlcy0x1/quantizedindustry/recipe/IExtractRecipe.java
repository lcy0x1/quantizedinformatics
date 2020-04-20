package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IExtractRecipe extends ISSRecipe<IExtractRecipe.Inv>, IClickableRecipe<IExtractRecipe.Inv> {

	public static class ExtractRecipe extends SSRecipe<Inv> implements IExtractRecipe {

		private final int click;

		public ExtractRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t, int c) {
			super(rl, MacReg.RSP_EXT, MacReg.RTP_EXT, ingr, is, t);
			click = c;
		}

		@Override
		public int getClickCost() {
			return click;
		}
	}

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static final Ser<ExtractRecipe, Inv> SERIALIZER = new Ser<>(
			(id, json, ingr, t) -> new ExtractRecipe(id, ingr, ISSRecipe.decItem(json.get("out")), t,
					IClickableRecipe.readClickFromJson(json)),
			(id, data, ingr, t) -> new ExtractRecipe(id, ingr, data.readItemStack(), t,
					IClickableRecipe.readClickFromData(data)),
			(data, r) -> {
				data.writeItemStack(r.getRecipeOutput());
				IClickableRecipe.writeClickToData(data, r);
			});

}
