package com.arthurlcy0x1.quantizedinformatics.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EncItem extends Item {

	public EncItem(Properties prop) {
		super(prop);
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return true;
	}

}
