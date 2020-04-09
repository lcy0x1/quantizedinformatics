package com.arthurlcy0x1.quantizedinformatics.items.battle;

import net.minecraft.item.ItemStack;

public interface IMaxwell {

	public static interface IMaxRepairable {

		public boolean canUse(ItemStack is);

		public int repair(ItemStack is);

	}

	public int getMax();

}
