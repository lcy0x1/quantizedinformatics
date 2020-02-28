package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

public class BaseCont extends Container {

	protected final IInventory slots;

	protected BaseCont(ContainerType<? extends BaseCont> type, int id, PlayerInventory inv, IInventory ent) {
		super(type, id);
		slots = ent;
		for (int r = 0; r < 3; ++r)
			for (int c = 0; c < 9; ++c)
				this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 103 + r * 18));
		for (int c = 0; c < 9; ++c)
			this.addSlot(new Slot(inv, c, 8 + c * 18, 161));// FIXME layout

	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return slots.isUsableByPlayer(player);
	}

}
