package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.recipe.IForgeRecipe;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTECont;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class ForgeST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_FRG, wid, pl, inv, GUI.getPIH(), arr);
			GUI.getSlot("input", (x, y) -> {
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
						addSlot(new Slot(inv, TE.INPUT + i * 3 + j, x + 18 * j, y + 18 * i));
				return null;
			});
			addSlot(GUI.getSlot("fuel", (x, y) -> new CondSlot(inv, TE.FUEL, x, y, CTECont::isFuel)));
			addSlot(GUI.getSlot("result", (x, y) -> new ResultSlot(inv, TE.RES, x, y)));
		}

	}

	public static class Scr extends StoneTable.FScr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, GUI);
		}

	}

	public static class TE extends StoneTable.FTE<TE, Cont, IForgeRecipe.Inv, IForgeRecipe>
			implements IForgeRecipe.Inv {

		private static final int RES = 0, FUEL = 1, INPUT = 2;

		public TE() {
			super(MacReg.TETST_FRG, Cont::new, SIZE, -1, FUEL, NAME);
		}

		@Override
		public int getStartIndex() {
			return INPUT;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack is) {
			if (index == RES)
				return false;
			if (index == FUEL)
				return CTECont.isFuel(is);
			return true;
		}

		@Override
		protected void addOutput(IForgeRecipe rec) {
			incrOrSet(RES, rec.getCraftingResult(this));
		}

		@Override
		protected boolean checkRecipe(IForgeRecipe nr) {
			return CTETE.canMerge(nr.getCraftingResult(this), getStackInSlot(RES));
		}

		@Override
		protected void doReduce() {
			for (int i = 0; i < 9; i++) {
				int ind = INPUT + i;
				ItemStack is = getStackInSlot(ind);
				if (!is.isEmpty())
					if (is.hasContainerItem())
						this.setInventorySlotContents(ind, is.getContainerItem().copy());
					else
						decrStackSize(ind, 1);
			}
		}

		@Override
		protected IForgeRecipe getRecipe() {
			IForgeRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_FRG, this, world).orElse(null);
			if (r == null || r.getClickCost() <= 0)
				return null;
			return r;
		}

	}

	private static final String NAME = "stonetable_forge";
	private static final SpriteManager GUI = new SpriteManager(Registrar.MODID, NAME);
	private static final int SIZE = 11, LEN = 6;

}
