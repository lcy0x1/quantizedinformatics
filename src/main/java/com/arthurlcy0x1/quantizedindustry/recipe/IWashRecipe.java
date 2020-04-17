package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IWashRecipe extends ISMRecipe<IWashRecipe.Inv> {

	public static interface Inv extends ISMRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static class WashRecipe extends SMRecipe<Inv> implements IWashRecipe {

		public WashRecipe(ResourceLocation rl, Ingredient ingr, ItemStack[] is, int t) {
			super(rl, MacReg.RSP_WSH, MacReg.RTP_WSH, ingr, is, t);
		}

	}

	public static final Ser<WashRecipe, Inv> SERIALIZER = ISMRecipe.getMultiSer(WashRecipe::new);
}
