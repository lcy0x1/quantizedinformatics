package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MaxwellItem extends Item {

	public MaxwellItem(Properties properties) {
		super(properties);
	}

	@Override
	public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
		int level = is.getOrCreateTag().getInt("maxwell_level");
		list.add(Translator.getTooltip("level").shallowCopy().appendText("" + level));

	}
	
	public static int getLevel(ItemStack is) {
		return is.getOrCreateTag().getInt("maxwell_level");
	}

}
