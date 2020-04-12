package com.arthurlcy0x1.quantizedinformatics.power.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IWireRecipe extends ISSRecipe<IWireRecipe.Inv> {

	public static interface Inv extends ISSRecipe.Inv {

		@Override
		public ItemStack getIngredient();

	}

	public static class WireRecipe extends SSRecipe<Inv> implements IWireRecipe {

		public WireRecipe(ResourceLocation rl, Ingredient ingr, ItemStack is, int t) {
			super(rl, Registrar.RSP_WIR, Registrar.RTP_WIR, ingr, is, t);
		}
	}

	public static final Ser<WireRecipe, Inv> SERIALIZER = ISSRecipe.getSingleSer(WireRecipe::new);

}
