package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.recipe.IPlateRecipe;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PlateST {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int wid, PlayerInventory pl) {
			this(wid, pl, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int wid, PlayerInventory pl, IInventory inv, IIntArray arr) {
			super(MacReg.CTST_PLA, wid, pl, inv, 84, arr);// TODO height
			addSlot(new Slot(inv, 0, 20, 20));
			addSlot(new Slot(inv, 1, 20, 50));
			addSlot(new ResultSlot(inv, 2, 20, 80));
		}

	}

	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/stonetable_plate.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 172);// TODO height
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
			// TODO
		}

		public boolean mouseClicked(double x, double y, int t) {
			if (super.mouseClicked(x, y, t))
				return true;
			if (get(2) > 0)
				send(3, 1);// TODO
			return true;
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
			if (index == 0)
				return prog;
			if (index == 1)
				return max_prog;
			if (index == 2) {
				ItemStack tool = getStackInSlot(1);
				return max_prog == 0 || tool.isEmpty() ? 0 : 1;
			}
			return 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 3) {
				prog += value > 0 ? 1 : -1;
				System.out.println("prog: " + prog);
				updateProg();
			}
		}

		protected void onChange(int index) {
			IPlateRecipe nr = getRecipe();
			if (nr != null) {
				ItemStack is = nr.getCraftingResult(this);
				if (!CTETE.canMerge(is, getStackInSlot(2)))
					nr = null;
			}
			if (nr == null) {
				max_prog = prog = 0;
				rec = null;
			} else {

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
			ItemStack tool = getStackInSlot(1);
			int d = tool.getDamage() + 1;
			if (d < tool.getMaxDamage())
				tool.setDamage(d);
			else
				setInventorySlotContents(1, ItemStack.EMPTY);

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

	private static final int SIZE = 3, LEN = 4;

}
