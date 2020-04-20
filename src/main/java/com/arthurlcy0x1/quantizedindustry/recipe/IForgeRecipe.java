package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IForgeRecipe extends ICSRecipe<IForgeRecipe.Inv>, IClickableRecipe<IForgeRecipe.Inv> {

	public static class ForgeRecipe extends CSRecipe<Inv> implements IForgeRecipe {

		private final int click;

		public ForgeRecipe(ResourceLocation rl, Ingredient[][] ingr, ItemStack is, int t, int c) {
			super(rl, MacReg.RSP_FRG, MacReg.RTP_FRG, ingr, is, t);
			click = c;
		}

		@Override
		public int getClickCost() {
			return click;
		}
	}

	public static interface Inv extends ICSRecipe.Inv {

	}

	public static final Ser<ForgeRecipe, Inv> SERIALIZER = new Ser<>(
			(id, json, ingr, t) -> new ForgeRecipe(id, ingr, ISSRecipe.decItem(json.get("out")), t,
					IClickableRecipe.readClickFromJson(json)),
			(id, data, ingr, t) -> new ForgeRecipe(id, ingr, data.readItemStack(), t,
					IClickableRecipe.readClickFromData(data)),
			(data, r) -> {
				data.writeItemStack(r.getRecipeOutput());
				IClickableRecipe.writeClickToData(data, r);
			});

}
