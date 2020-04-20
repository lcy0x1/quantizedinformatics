package com.arthurlcy0x1.quantizedindustry.recipe;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface ICutRecipe extends ICSRecipe<ICutRecipe.Inv>, IClickableRecipe {

	public static class CutRecipe extends CSRecipe<Inv> implements ICutRecipe {

		private final int click;

		public CutRecipe(ResourceLocation rl, Ingredient[][] ingr, ItemStack is, int t, int c) {
			super(rl, MacReg.RSP_CUT, MacReg.RTP_CUT, ingr, is, t);
			click = c;
		}

		@Override
		public int getClickCost() {
			return click;
		}
	}

	public static interface Inv extends ICSRecipe.Inv {

	}

	public static final Ser<CutRecipe, Inv> SERIALIZER = new Ser<>(
			(id, json, ingr, t) -> new CutRecipe(id, ingr, ISSRecipe.decItem(json.get("out")), t,
					IClickableRecipe.readClickFromJson(json)),
			(id, data, ingr, t) -> new CutRecipe(id, ingr, data.readItemStack(), t,
					IClickableRecipe.readClickFromData(data)),
			(data, r) -> {
				data.writeItemStack(r.getRecipeOutput());
				IClickableRecipe.writeClickToData(data, r);
			});

}
