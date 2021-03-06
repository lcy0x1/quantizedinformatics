package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IElecRecipe extends ISMRecipe<IElecRecipe.Inv> {

	public static class ElecRecipe extends SMRecipe<Inv> implements IElecRecipe {

		public ElecRecipe(ResourceLocation rl, Ingredient ingr, ItemStack[] is, int t) {
			super(rl, MacReg.RSP_ELE, MacReg.RTP_ELE, ingr, is, t);
		}

	}

	public static interface Inv extends ISMRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static final Ser<ElecRecipe, Inv> SERIALIZER = ISMRecipe.getMultiSer(ElecRecipe::new);
}
