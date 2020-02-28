package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.DraftLogicGate;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class DraftGateCont extends Container {

	private static class ChipSlot extends Slot {

		public ChipSlot(IInventory inv, int index, int x, int y) {
			super(inv, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof LogicDraft;
		}

	}

	private final IInventory chipslot;

	public DraftGateCont(int id, PlayerInventory inv) {
		this(id, inv, new Inventory(1));
	}

	protected DraftGateCont(int id, PlayerInventory inv, IInventory ent) {
		super(Registrar.CT_GATE, id);
		chipslot = ent;
		this.addSlot(new ChipSlot(chipslot, 0, 0, 0));
		for (int r = 0; r < 3; ++r)
			for (int c = 0; c < 9; ++c)
				this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 103 + r * 18));
		for (int c = 0; c < 9; ++c)
			this.addSlot(new Slot(inv, c, 8 + c * 18, 161));

	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return chipslot.isUsableByPlayer(player);
	}

}
