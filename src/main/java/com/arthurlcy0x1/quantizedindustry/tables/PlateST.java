package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.SpriteManager.ScreenRenderer;
import com.arthurlcy0x1.quantizedindustry.recipe.IPlateRecipe;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class PlateST {

	private static final SpriteManager GUI = new SpriteManager(Registrar.MODID, "stonetable_plate");

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

	public static class Scr extends CTEBlock.CommScr<Cont> {

		private boolean hov = false;

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, GUI.getHeight());
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			if (get(2) > 0)
				if (GUI.within("arrow", x - guiLeft, y - guiTop))
					send(3, 1);
			return super.mouseClicked(x, y, t);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			ScreenRenderer renderer = GUI.getRenderer(this);
			renderer.start();
			int prog = get(0);
			int max_prog = get(1);
			if (max_prog > 0) {
				renderer.draw("arrow", hov ? "arrow_2" : "arrow_1");
				renderer.drawLeftRight("arrow", "arrow_3", 1.0 * prog / max_prog);
			}
		}

		@Override
		protected void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			hov = GUI.within("arrow", mx - guiLeft, my - guiTop);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, IPlateRecipe.Inv, ITickableTileEntity {

		private int prog = 0, max_prog = 0;
		private boolean dirty = true;
		private IPlateRecipe rec;

		public TE() {
			super(MacReg.TETST_PLA, SIZE);
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
		public ITextComponent getDisplayName() {
			return Translator.getCont("stonetable_plate");
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
		public void set(int index, int value) {
			if (index == 3) {
				prog += value > 0 ? 1 : -1;
				markDirty();
				dirty = true;
			}
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			if (dirty) {
				dirty = false;
				updateRecipe();
			}

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("prog", prog);
			tag.putInt("max_prog", max_prog);
			return tag;
		}

		@Override
		protected void onChange(int index) {
			dirty = true;
		}

		private IPlateRecipe getRecipe() {
			IPlateRecipe r = world.getRecipeManager().getRecipe(MacReg.RTP_PLA, this, world).orElse(null);
			if (r == null || r.getClickCost() <= 0)
				return null;
			return r;
		}

		private void refreshRecipe() {
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
				max_prog = rec.getClickCost();
			}
		}

		private void updateRecipe() {
			refreshRecipe();
			if (max_prog > 0 && prog == max_prog) {
				decrStackSize(0, 1);
				incrOrSet(2, rec.getCraftingResult(this));
				prog = 0;
				refreshRecipe();
			}
			ItemStack tool = getStackInSlot(1);
			int d = tool.getDamage() + 1;
			if (d < tool.getMaxDamage())
				tool.setDamage(d);
			else
				setInventorySlotContents(1, ItemStack.EMPTY);
			markDirty();
		}

	}

	private static final int SIZE = 3, LEN = 4;

}
