package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.recipe.ICutRecipe;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class CutST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_CUT, wid, pl, inv, GUI.getPIH(), arr);
			GUI.getSlot("input", (x, y) -> {
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++)
						addSlot(new Slot(inv, TE.INPUT + i * 3 + j, x + 18 * j, y + 18 * i));
				return null;
			});
			addSlot(GUI.getSlot("tool", (x, y) -> new Slot(inv, TE.TOOL, x, y)));
			addSlot(GUI.getSlot("result", (x, y) -> new ResultSlot(inv, TE.RES, x, y)));
		}

	}

	public static class Scr extends StoneTable.Scr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, GUI);
		}

	}

	public static class TE extends StoneTable.TE<TE, Cont, ICutRecipe.Inv, ICutRecipe> implements ICutRecipe.Inv {

		private static final int TOOL = 0, RES = 1, INPUT = 2;

		public TE() {
			super(MacReg.TETST_CUT, Cont::new, SIZE, TOOL, "stonetable_cut");
		}

		@Override
		public int getStartIndex() {
			return INPUT;
		}

		@Override
		protected void addOutput(ICutRecipe rec) {
			incrOrSet(RES, rec.getCraftingResult(this));
		}

		@Override
		protected boolean checkRecipe(ICutRecipe nr) {
			return CTETE.canMerge(nr.getCraftingResult(this), getStackInSlot(RES));
		}

		@Override
		protected void doReduce() {
			for (int i = 0; i < 9; i++) {
				int ind = INPUT + i;
				ItemStack is = getStackInSlot(ind);
				if (!is.isEmpty())
					if (is.hasContainerItem())
						this.setInventorySlotContents(ind, is.getContainerItem());
					else
						decrStackSize(ind, 1);
			}
		}

		@Override
		protected ICutRecipe getRecipe() {
			ICutRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_CUT, this, world).orElse(null);
			if (r == null || r.getClickCost() <= 0)
				return null;
			return r;
		}

	}

	private static final SpriteManager GUI = new SpriteManager(Registrar.MODID, "stonetable_cut");

	private static final int SIZE = 11, LEN = 4;

}
