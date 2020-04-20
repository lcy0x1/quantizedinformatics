package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.IFluidTE;
import com.arthurlcy0x1.quantizedindustry.IFluidTE.FluidManager.VarFluidTank;
import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.recipe.IExtractRecipe;
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

public class ExtST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_EXT, wid, pl, inv, GUI.getPIH(), arr);
			addSlot(GUI.getSlot("input", (x, y) -> new Slot(inv, 0, x, y)));
			addSlot(GUI.getSlot("tool", (x, y) -> new CondsSlot(inv, 1, x, y, TE::isValid, 1)));
			addSlot(GUI.getSlot("result", (x, y) -> new ResultSlot(inv, 2, x, y)));
			addSlot(GUI.getSlot("fuel", (x, y) -> new CondSlot(inv, 3, x, y, CTECont::isFuel)));
			addSlot(GUI.getSlot("fluid", (x, y) -> new CondsSlot(inv, 4, x, y + 54, TE::isValid, 1)));
		}

	}

	public static class Scr extends StoneTable.FScr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, GUI);
		}

	}

	public static class TE extends StoneTable.FTE<TE, Cont, IExtractRecipe.Inv, IExtractRecipe>
			implements IExtractRecipe.Inv, IFluidTE.FluidManagerTE {

		private static final int TOOL = 1, RES = 2, INPUT = 0, FUEL = 3, FCONT = 4;

		public static boolean isValid(int index, ItemStack is) {
			if (index == RES)
				return false;
			if (index == TOOL)
				return true;// TODO
			if (index == FUEL)
				return CTECont.isFuel(is);
			if (index == FCONT)
				return IFluidTE.isFluidCont(is);
			return true;
		}

		private final VarFluidTank tank = new VarFluidTank(Priority.PRODUCER, x -> true);

		private final FluidManager manager = new FluidManager(tank);

		public TE() {
			super(MacReg.TETST_EXT, Cont::new, SIZE, TOOL, FUEL, NAME);
		}

		@Override
		public ItemStack getIngredient() {
			return getStackInSlot(INPUT);
		}

		@Override
		public FluidManager getManager() {
			return manager;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack is) {
			return isValid(index, is);
		}

		@Override
		protected void addOutput(IExtractRecipe rec) {
			incrOrSet(RES, rec.getCraftingResult(this));// TODO fluid check
		}

		@Override
		protected boolean checkRecipe(IExtractRecipe nr) {
			return CTETE.canMerge(nr.getCraftingResult(this), getStackInSlot(RES));// TODO fluid check
		}

		@Override
		protected void doReduce() {
			ItemStack is = decrStackSize(INPUT, 1);
			if (is.hasContainerItem())
				this.setInventorySlotContents(INPUT, is.getContainerItem().copy());
		}

		@Override
		protected IExtractRecipe getRecipe() {
			IExtractRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_EXT, this, world).orElse(null);
			if (r == null || r.getClickCost() <= 0)
				return null;
			return r;
		}

	}

	private static final String NAME = "stonetable_extract";
	private static final SpriteManager GUI = new SpriteManager(Registrar.MODID, NAME);
	private static final int SIZE = 5, LEN = 6;

}
