package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface ICutRecipe extends ISSRecipe<ICutRecipe.Inv> {

	public static class CutRecipe extends SSRecipe<Inv> implements ICutRecipe {

		public CutRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t) {
			super(rl, Registrar.RSP_CUT, Registrar.RTP_CUT, ingr, is, t);
		}
	}

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static final Ser<CutRecipe, Inv> SERIALIZER = ISSRecipe.getSingleSer(CutRecipe::new);

}
