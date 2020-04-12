package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IPowderRecipe extends ISSRecipe<IPowderRecipe.Inv> {

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static class PowderRecipe extends SSRecipe<Inv> implements IPowderRecipe {

		public PowderRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t) {
			super(rl, Registrar.RSP_PDR, Registrar.RTP_PDR, ingr, is, t);
		}
	}

	public static final Ser<PowderRecipe, Inv> SERIALIZER = ISSRecipe.getSingleSer(PowderRecipe::new);

}
