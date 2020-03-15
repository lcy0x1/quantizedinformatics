package com.arthurlcy0x1.quantizedinformatics.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ALUItem extends Item {

	public static int getSpeed(ItemStack is) {
		if (is.isEmpty())
			return 0;
		return 0;// TODO
	}

	public ALUItem(Properties properties) {
		super(properties);
	}

}
