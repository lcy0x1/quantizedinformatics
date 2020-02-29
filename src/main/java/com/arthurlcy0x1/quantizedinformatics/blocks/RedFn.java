package com.arthurlcy0x1.quantizedinformatics.blocks;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTECont;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecipe;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class RedFn {

	public static class Cont extends CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(6));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CT_REDFN, id, inv, ent);

			// TODO add slots
		}

	}

	public static class TE extends CTETE<TE> implements RedRecipe.Inv {

		private static final int[] SLOTS = { 0, 1, 2, 3, 4, 5, 6 };

		public TE() {
			super(Registrar.TET_REDFN, Cont::new, 7, "quantizedinformatics::container.reduction_furance");
			LogManager.getLogger().warn("redution furance entity active");
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public int[] getSlots() {
			return SLOTS;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return true;// TODO add validation
		}

	}

}
