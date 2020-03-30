package com.arthurlcy0x1.quantizedinformatics.items.logic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ALUItem extends Item {

	public static int getSpeed(ItemStack is) {
		if (is.isEmpty())
			return 0;
		if (is.getItem() instanceof ALUItem)
			return 64;
		return 0;// TODO
	}

	public ALUItem(Properties properties) {
		super(properties);
	}

}
