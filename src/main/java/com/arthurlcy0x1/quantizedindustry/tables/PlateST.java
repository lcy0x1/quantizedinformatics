package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.recipe.IPlateRecipe;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class PlateST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_PLA, wid, pl, inv, 0, arr);// TODO height
		}

	}

	public static class Scr extends CTEBlock.CommScr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 0);// TODO height
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			// TODO Auto-generated method stub

		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, IPlateRecipe.Inv {

		private int prog = 0, max_prog = 0;
		private IPlateRecipe rec;

		public TE() {
			super(MacReg.TETST_PLA, SIZE);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("stonetable_plate");
		}

		@Override
		public Container createMenu(int wid, PlayerInventory pl, PlayerEntity pe) {
			return new Cont(wid, pl, this, this);
		}

		@Override
		public int get(int index) {
			if(index == 0)
				return prog;
			if(index == 1)
				return max_prog;
			return 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 2) {
				prog += value > 0 ? 1 : -1;
				updateProg();
			}
		}

		protected void onChange(int index) {
			IPlateRecipe nr = getRecipe();
			if (nr == null) {
				max_prog = prog = 0;
				rec = null;
			} else {
				ItemStack is = nr.getCraftingResult(this);
				if (nr != rec) {
					prog = 0;
					rec = nr;
				}
				max_prog = rec.getCraftCost();
			}
		}

		private IPlateRecipe getRecipe() {
			IPlateRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_PLA, this, world).orElse(null);
			if (r.getClickCost() <= 0)
				return null;
			return r;
		}

		private void updateProg() {
			if (prog == max_prog) {
				decrStackSize(0, 1);
				incrOrSet(2, rec.getCraftingResult(this));
			}
			getStackInSlot(1);
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public ItemStack getIngredient() {
			return getStackInSlot(0);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getInt("prog");
			max_prog = tag.getInt("max_prog");
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("prog", prog);
			tag.putInt("max_prog", max_prog);
			return tag;
		}

	}

	private static final int SIZE = 3, LEN = 3;

}
