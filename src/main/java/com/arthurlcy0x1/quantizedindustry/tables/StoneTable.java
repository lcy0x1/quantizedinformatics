package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedindustry.SpriteManager;
import com.arthurlcy0x1.quantizedindustry.SpriteManager.ScreenRenderer;
import com.arthurlcy0x1.quantizedindustry.recipe.IClickableRecipe;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;

public class StoneTable extends BaseBlock {

	public static class FScr<C extends CTEBlock.CommCont> extends Scr<C> {

		public FScr(C cont, PlayerInventory inv, ITextComponent text, SpriteManager gui) {
			super(cont, inv, text, gui);
		}

		@Override
		protected void drawBackground(float pt, int x, int y, ScreenRenderer renderer) {
			super.drawBackground(pt, x, y, renderer);
			int fuel = get(4);
			int max_fuel = get(5);
			if (fuel > 0)
				renderer.drawBottomUp("fire", "fire_1", 1.0 * fuel / max_fuel);
		}

	}

	public static abstract class FTE<T extends TE<T, C, I, R>, C extends CTEBlock.CommCont, I extends IInventory, R extends IClickableRecipe<I>>
			extends TE<T, C, I, R> {

		private final int fuelSlot;

		private int fuel = 0, max_fuel = 0;

		public FTE(TileEntityType<T> type, ISTContFac<C> fac, int size, int tool, int f, String str) {
			super(type, fac, size, tool, str);
			fuelSlot = f;
		}

		@Override
		public int get(int index) {
			if (index < 4)
				return super.get(index);
			if (index == 4)
				return fuel;
			if (index == 5)
				return max_fuel;
			return 0;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			fuel = tag.getInt("fuel");
			max_fuel = tag.getInt("max_fuel");
		}

		@Override
		public int size() {
			return 6;
		}

		@Override
		public void tick() {
			super.tick();
			if (world.isRemote)
				return;
			if (fuel > 0)
				fuel--;
			if (fuel == 0) {
				ItemStack is = getStackInSlot(fuelSlot);
				if (CTEBlock.CTECont.isFuel(is)) {
					max_fuel = fuel = ForgeHooks.getBurnTime(is);
					if (is.hasContainerItem())
						setInventorySlotContents(fuelSlot, is.getContainerItem().copy());
					else
						decrStackSize(fuelSlot, 1);
				}
			}
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("fuel", fuel);
			tag.putInt("max_fuel", max_fuel);
			return tag;
		}

		@Override
		protected boolean canClick() {
			return super.canClick() && fuel > 0;
		}

	}

	public static interface ISTContFac<C> {

		public C get(int wid, PlayerInventory pi, IInventory inv, IIntArray arr);

	}

	public static class Scr<C extends CTEBlock.CommCont> extends CTEBlock.CommScr<C> {

		private boolean hov = false;
		protected final SpriteManager manager;

		public Scr(C cont, PlayerInventory inv, ITextComponent text, SpriteManager gui) {
			super(cont, inv, text, gui.getHeight());
			manager = gui;
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			if (get(2) > 0)
				if (manager.within("arrow", x - guiLeft, y - guiTop))
					send(3, 1);
			return super.mouseClicked(x, y, t);
		}

		protected void drawBackground(float pt, int x, int y, ScreenRenderer renderer) {
			renderer.start();
			int prog = get(0);
			int max_prog = get(1);
			if (max_prog > 0) {
				renderer.draw("arrow", get(2) > 0 && hov ? "arrow_2" : "arrow_1");
				renderer.drawLeftRight("arrow", "arrow_3", 1.0 * prog / max_prog);
			}
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float pt, int x, int y) {
			ScreenRenderer renderer = manager.getRenderer(this);
			drawBackground(pt, x, y, renderer);
		}

		@Override
		protected void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			hov = manager.within("arrow", mx - guiLeft, my - guiTop);
		}

	}

	public static abstract class TE<T extends TE<T, C, I, R>, C extends CTEBlock.CommCont, I extends IInventory, R extends IClickableRecipe<I>>
			extends CTEBlock.CTETE<T> implements IIntArray, ITickableTileEntity {

		private final ISTContFac<C> contFac;
		private final int toolSlot;
		private final String name;

		private int prog = 0, max_prog = 0;
		private boolean dirty = true;
		private R rec;

		public TE(TileEntityType<T> type, ISTContFac<C> fac, int size, int tool, String str) {
			super(type, size);
			contFac = fac;
			toolSlot = tool;
			name = str;
		}

		@Override
		public Container createMenu(int wid, PlayerInventory pi, PlayerEntity pl) {
			return contFac.get(wid, pi, this, this);
		}

		@Override
		public int get(int index) {
			if (index == 0)
				return prog;
			if (index == 1)
				return max_prog;
			if (index == 2)
				return canClick() ? 1 : 0;
			return 0;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont(name);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getInt("prog");
			max_prog = tag.getInt("max_prog");
		}

		@Override
		public void set(int index, int value) {
			if (index == 3)
				proceed();
		}

		@Override
		public int size() {
			return 4;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			if (dirty) {
				dirty = false;
				update();
			}

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("prog", prog);
			tag.putInt("max_prog", max_prog);
			return tag;
		}

		protected abstract void addOutput(R rec);

		protected boolean canClick() {
			return max_prog > 0 && (toolSlot < 0 || !getStackInSlot(toolSlot).isEmpty());
		}

		protected abstract boolean checkRecipe(R nr);

		protected abstract void doReduce();

		protected abstract R getRecipe();

		@Override
		protected void onChange(int index) {
			dirty = true;
		}

		protected void proceed() {
			prog++;
			if (toolSlot >= 0) {
				ItemStack tool = getStackInSlot(toolSlot);
				int d = tool.getDamage() + 1;
				if (d < tool.getMaxDamage())
					tool.setDamage(d);
				else
					setInventorySlotContents(toolSlot, ItemStack.EMPTY);
			}
			markDirty();
			dirty = true;
		}

		protected void update() {
			refreshRecipe();
			if (max_prog > 0 && prog == max_prog) {
				doReduce();
				addOutput(rec);
				prog = 0;
				refreshRecipe();
			}
			markDirty();
		}

		private void refreshRecipe() {
			R nr = getRecipe();
			if (nr != null) {
				if (!checkRecipe(nr))
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
	}

	public StoneTable(STE ste) {
		super(construct(BlockProp.M_POWER).addImpls(HOR, ste));
	}

}
