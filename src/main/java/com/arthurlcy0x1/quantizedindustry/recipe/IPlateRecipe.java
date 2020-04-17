package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IPlateRecipe extends ISSRecipe<IPlateRecipe.Inv>, IClickableRecipe {

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static class PlateRecipe extends SSRecipe<Inv> implements IPlateRecipe {

		private final int click;

		public PlateRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t, int c) {
			super(rl, MacReg.RSP_PLA, MacReg.RTP_PLA, ingr, is, t);
			click = c;
		}

		@Override
		public int getClickCost() {
			return click;
		}
	}

	public static final Ser<PlateRecipe, Inv> SERIALIZER = new Ser<>(
			(id, json, ingr, t) -> new PlateRecipe(id, ingr, ISSRecipe.decItem(json), t,
					IClickableRecipe.readClickFromJson(json)),
			(id, data, ingr, t) -> new PlateRecipe(id, ingr, data.readItemStack(), t,
					IClickableRecipe.readClickFromData(data)),
			(data, r) -> {
				data.writeItemStack(r.getRecipeOutput());
				IClickableRecipe.writeClickToData(data, r);
			});

}
