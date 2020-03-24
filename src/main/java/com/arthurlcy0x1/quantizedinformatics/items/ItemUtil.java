package com.arthurlcy0x1.quantizedinformatics.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemUtil {

	public static void consume(ItemStack tool, ItemStack ammo, int count, PlayerEntity pl) {
		if (!pl.abilities.isCreativeMode) {
			ammo.shrink(count);
			if (ammo.isEmpty())
				pl.inventory.deleteStack(ammo);
		}
	}

	public static void damageItem(ItemStack is, PlayerEntity pl) {
		is.damageItem(1, pl, (player) -> player.sendBreakAnimation(pl.getActiveHand()));
	}

	public static void drop(ItemStack is, PlayerEntity pl) {
		if (!pl.addItemStackToInventory(is))
			pl.dropItem(is, false);
	}

}
