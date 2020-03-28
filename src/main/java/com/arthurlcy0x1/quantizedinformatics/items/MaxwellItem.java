package com.arthurlcy0x1.quantizedinformatics.items;

import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class MaxwellItem extends Item {

	public static int getLevel(ItemStack is) {
		if (!(is.getItem() instanceof MaxwellItem))
			return 0;
		MaxwellItem i = (MaxwellItem) is.getItem();
		return MathHelper.clamp(is.getOrCreateTag().getInt("maxwell_level"), 0, i.max);
	}

	public static void setLevel(ItemStack is, int lv) {
		if (!(is.getItem() instanceof MaxwellItem))
			return;
		MaxwellItem i = (MaxwellItem) is.getItem();
		is.getOrCreateTag().putInt("maxwell_level", MathHelper.clamp(lv, 0, i.max));
	}

	private final int max;

	public MaxwellItem(Properties properties, int max_level) {
		super(properties);
		max = max_level;
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		ITextComponent c = Translator.getTooltip("level").deepCopy().appendText("" + getLevel(stack));
		ITextComponent d = super.getDisplayName(stack).deepCopy().appendText(", ").appendSibling(c);
		return d;
	}

}
