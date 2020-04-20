package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface ICentRecipe extends ISMRecipe<ICentRecipe.Inv> {

	public static class CentRecipe extends SMRecipe<Inv> implements ICentRecipe {

		public CentRecipe(ResourceLocation rl, Ingredient ingr, ItemStack[] is, int t) {
			super(rl, MacReg.RSP_CEN, MacReg.RTP_CEN, ingr, is, t);
		}

	}

	public static interface Inv extends ISMRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static final Ser<CentRecipe, Inv> SERIALIZER = ISMRecipe.getMultiSer(CentRecipe::new);
}
