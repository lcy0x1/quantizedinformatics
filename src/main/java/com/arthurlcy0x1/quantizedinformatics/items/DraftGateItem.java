package com.arthurlcy0x1.quantizedinformatics.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DraftGateItem extends Item {

	private final Item cont;

	public DraftGateItem(Properties prop, Item c) {
		super(prop);
		cont = c;
	}

	@Override
	public ItemStack getContainerItem(ItemStack is) {
		return new ItemStack(cont);
	}

	@Override
	public boolean hasContainerItem(ItemStack is) {
		return true;
	}

}
