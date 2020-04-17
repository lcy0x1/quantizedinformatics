package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IPlateRecipe extends ISSRecipe<IPlateRecipe.Inv> {

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static class PlateRecipe extends SSRecipe<Inv> implements IPlateRecipe {

		public PlateRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t) {
			super(rl, Registrar.RSP_PLA, Registrar.RTP_PLA, ingr, is, t);
		}
	}

	public static final Ser<PlateRecipe, Inv> SERIALIZER = ISSRecipe.getSingleSer(PlateRecipe::new);

}
