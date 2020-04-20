package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.recipe.IPlateRecipe;
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

public class PlateST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_PLA, wid, pl, inv, GUI.getPIH(), arr);
			addSlot(GUI.getSlot("input", (x, y) -> new Slot(inv, 0, x, y)));
			addSlot(GUI.getSlot("tool", (x, y) -> new Slot(inv, 1, x, y)));
			addSlot(GUI.getSlot("result", (x, y) -> new ResultSlot(inv, 2, x, y)));
		}

	}

	public static class Scr extends StoneTable.Scr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, GUI);
		}

	}

	public static class TE extends StoneTable.TE<TE, Cont, IPlateRecipe.Inv, IPlateRecipe> implements IPlateRecipe.Inv {

		public TE() {
			super(MacReg.TETST_PLA, Cont::new, SIZE, 1, "stonetable_plate");
		}

		@Override
		public ItemStack getIngredient() {
			return getStackInSlot(0);
		}

		@Override
		protected void addOutput(IPlateRecipe rec) {
			incrOrSet(2, rec.getCraftingResult(this));
		}

		@Override
		protected boolean checkRecipe(IPlateRecipe nr) {
			return CTETE.canMerge(nr.getCraftingResult(this), getStackInSlot(2));
		}

		@Override
		protected void doReduce() {
			decrStackSize(0, 1);
		}

		@Override
		protected IPlateRecipe getRecipe() {
			IPlateRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_PLA, this, world).orElse(null);
			if (r == null || r.getClickCost() <= 0)
				return null;
			return r;
		}

	}

	private static final SpriteManager GUI = new SpriteManager(Registrar.MODID, "stonetable_plate");

	private static final int SIZE = 3, LEN = 4;

}
