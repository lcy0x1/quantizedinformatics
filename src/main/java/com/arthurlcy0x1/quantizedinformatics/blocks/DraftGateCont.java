package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class DraftGateCont extends BaseCont {

	private static class ChipSlot extends Slot {

		public ChipSlot(IInventory inv, int index, int x, int y) {
			super(inv, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof LogicDraft;
		}

	}

	public DraftGateCont(int id, PlayerInventory inv) {
		this(id, inv, new Inventory(1));
	}

	protected DraftGateCont(int id, PlayerInventory inv, IInventory ent) {
		super(Registrar.CT_GATE, id, inv, ent);
		this.addSlot(new ChipSlot(slots, 0, 0, 0));

	}

}
