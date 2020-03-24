package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import com.arthurlcy0x1.quantizedinformatics.PacketHandler.DataCont;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CTEBlock extends BaseBlock {

	public static class CommCont extends CTECont implements DataCont {

		private final IIntArray data;

		protected CommCont(ContainerType<? extends CTECont> type, int id, PlayerInventory inv, IInventory ent, int h,
				IIntArray arr) {
			super(type, id, inv, ent, h);
			trackIntArray(data = arr);
		}

		@Override
		public IIntArray getData() {
			return data;
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static abstract class CommScr<T extends CommCont> extends CTEScr<T> {

		public CommScr(T cont, PlayerInventory inv, ITextComponent text, int h) {
			super(cont, inv, text, h);
		}

		public final int get(int index) {
			return container.getData().get(index);
		}

		public final void send(int index, int value) {
			PacketHandler.send(new PacketHandler.IntMsg(container.windowId, index, value));
		}

	}

	public static class CTECont extends Container {

		public static class CondSlot extends Slot {

			private final Predicate<ItemStack> p;

			public CondSlot(IInventory inv, int ind, int x, int y, Predicate<ItemStack> pred) {
				super(inv, ind, x, y);
				p = pred;
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return p.test(is);
			}

		}

		public static class CondsSlot extends SizeSlot {

			private final BiPredicate<Integer, ItemStack> p;
			private final int i;

			public CondsSlot(IInventory inv, int ind, int x, int y, BiPredicate<Integer, ItemStack> pred, int siz) {
				super(inv, ind, x, y, siz);
				p = pred;
				i = ind;
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return p.test(i, is);
			}

		}

		public static class ResultSlot extends Slot {

			public ResultSlot(IInventory inv, int ind, int x, int y) {
				super(inv, ind, x, y);
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return false;
			}

		}

		public static class SizeSlot extends Slot {

			private final int size;

			public SizeSlot(IInventory inv, int ind, int x, int y, int siz) {
				super(inv, ind, x, y);
				size = siz;
			}

			@Override
			public int getSlotStackLimit() {
				return size;
			}

		}

		public static boolean canSplit(ItemStack s, ItemStack is) {
			return s.isEmpty() || is.isEmpty()
					|| Container.areItemsAndTagsEqual(s, is) && s.getCount() < s.getMaxStackSize();
		}

		public static boolean isFuel(ItemStack is) {
			return AbstractFurnaceTileEntity.isFuel(is);
		}

		protected final IInventory slots;

		protected CTECont(ContainerType<? extends CTECont> type, int id, PlayerInventory inv, IInventory ent, int h) {
			super(type, id);
			slots = ent;
			for (int r = 0; r < 3; ++r)
				for (int c = 0; c < 9; ++c)
					this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, h + r * 18));
			for (int c = 0; c < 9; ++c)
				this.addSlot(new Slot(inv, c, 8 + c * 18, h + 58));
		}

		@Override
		public boolean canInteractWith(PlayerEntity player) {
			return slots.isUsableByPlayer(player);
		}

		@Override
		public ItemStack transferStackInSlot(PlayerEntity pl, int index) {
			ItemStack ret = ItemStack.EMPTY;
			Slot slot = inventorySlots.get(index);
			if (slot != null && slot.getHasStack()) {
				ItemStack is = slot.getStack();
				ret = is.copy();
				if (slot instanceof ResultSlot) {
					if (!mergeItemStack(is, 0, 36, true))
						return ItemStack.EMPTY;
					slot.onSlotChange(is, ret);
				} else if (index < 36) {
					if (canTransfer(is)) {
						if (!transfer(is))
							return ItemStack.EMPTY;
					}
					if (index < 27) {
						if (!mergeItemStack(is, 27, 36, false))
							return ItemStack.EMPTY;
					} else if (!mergeItemStack(is, 0, 27, false))
						return ItemStack.EMPTY;
				} else if (!mergeItemStack(is, 0, 36, false))
					return ItemStack.EMPTY;
				if (is.isEmpty())
					slot.putStack(ItemStack.EMPTY);
				else
					slot.onSlotChanged();
				if (is.getCount() == ret.getCount())
					return ItemStack.EMPTY;
				slot.onTake(pl, is);
			}
			return ret;
		}

		protected boolean canTransfer(ItemStack is) {
			for (int i = 36; i < this.inventorySlots.size(); i++) {
				Slot s = getSlot(i);
				if (s.isItemValid(is) && canSplit(s.getStack(), is))
					return true;
			}
			return false;
		}

		protected boolean transfer(ItemStack is) {
			for (int i = 36; i < this.inventorySlots.size(); i++) {
				Slot s = getSlot(i);
				if (s.isItemValid(is) && canSplit(s.getStack(), is))
					return mergeItemStack(is, i, i + 1, false);
			}
			return false;
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static abstract class CTEScr<T extends CTECont> extends ContainerScreen<T> {

		public static final int COLOR = 4210752;

		public CTEScr(T cont, PlayerInventory inv, ITextComponent text, int h) {
			super(cont, inv, text);
			ySize = h;
		}

		@Override
		public void render(int x, int y, float t) {
			renderBackground();
			super.render(x, y, t);
			renderHoveredToolTip(x, y);
		}

		protected void cstr(String str, int y) {
			font.drawString(str, xSize / 2 - font.getStringWidth(str) / 2, y, COLOR);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			String s = title.getFormattedText();
			cstr(s, 6);
			font.drawString(playerInventory.getDisplayName().getFormattedText(), 8.0F, ySize - 96 + 2, COLOR);
		}

	}

	public static abstract class CTETE<T extends CTETE<T>> extends TileEntity
			implements IInventory, INamedContainerProvider {

		public static boolean canMerge(ItemStack is0, ItemStack is1) {
			return is0 == null || is1 == null || is0.isEmpty() || is1.isEmpty()
					|| Container.areItemsAndTagsEqual(is0, is1)
							&& is0.getCount() + is1.getCount() <= is1.getMaxStackSize();
		}

		private final NonNullList<ItemStack> inv;

		public CTETE(TileEntityType<T> type, int size) {
			super(type);
			inv = NonNullList.withSize(size, ItemStack.EMPTY);
		}

		@Override
		public void clear() {
			inv.clear();
			onChange(-1);
			this.markDirty();
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			ItemStack itemstack = ItemStackHelper.getAndSplit(inv, index, count);
			onChange(index);
			markDirty();
			return itemstack;
		}

		@Override
		public int getSizeInventory() {
			return inv.size();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return index >= 0 && index < inv.size() ? inv.get(index) : ItemStack.EMPTY;
		}

		public void incrOrSet(int index, ItemStack is) {
			if (is.isEmpty())
				return;
			if (getStackInSlot(index).isEmpty())
				setInventorySlotContents(index, is);
			else {
				getStackInSlot(index).grow(is.getCount());
				onChange(index);
			}
			markDirty();
		}

		@Override
		public boolean isEmpty() {
			for (ItemStack itemstack : inv)
				if (!itemstack.isEmpty())
					return false;

			return true;
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity player) {
			return true;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			ItemStackHelper.loadAllItems(tag, inv);
			onChange(-1);
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			ItemStack itemstack = inv.get(index);
			if (itemstack.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				inv.set(index, ItemStack.EMPTY);
				onChange(index);
				return itemstack;
			}
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			inv.set(index, stack);
			if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
				stack.setCount(getInventoryStackLimit());
			onChange(index);
			markDirty();
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			ItemStackHelper.saveAllItems(tag, inv, true);
			return tag;
		}

		protected void onChange(int ind) {
		}

	}

	public CTEBlock(BlockProp bb, STE sup) {
		super(construct(bb).addImpls(sup, HOR));
	}

}