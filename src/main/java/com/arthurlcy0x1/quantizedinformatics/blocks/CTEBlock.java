package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CTEBlock<T extends TileEntity> extends HorizontalBlock {

	public static abstract class CTECont extends Container {

		protected static class CondSlot extends Slot {

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

		protected static class ResultSlot extends Slot {

			public ResultSlot(IInventory inv, int ind, int x, int y) {
				super(inv, ind, x, y);
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return false;
			}

		}

		public static boolean canSplit(ItemStack s, ItemStack is) {
			return s.isEmpty() || is.isEmpty()
					|| Container.areItemsAndTagsEqual(s, is) && s.getCount() < s.getMaxStackSize();
		}

		protected static boolean isFuel(ItemStack is) {
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

	public static abstract class CTEScr<T extends CTECont> extends ContainerScreen<T> {

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

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			String s = this.title.getFormattedText();
			this.font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6.0F, 4210752);
			this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, this.ySize - 96 + 2,
					4210752);
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
			if (!itemstack.isEmpty()) {
				onChange(index);
				markDirty();
			}

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

	private final Supplier<T> s;

	public CTEBlock(Supplier<T> sup) {
		super(Block.Properties.create(Material.ROCK));
		s = sup;
	}

	@Override
	public T createTileEntity(BlockState state, IBlockReader world) {
		return s.get();
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	public ActionResultType func_225533_a_(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h,
			BlockRayTraceResult r) {
		return onClick(bs, w, pos, pl, h);
	}

	public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		if (w.isRemote)
			return ActionResultType.SUCCESS;
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof INamedContainerProvider)
			pl.openContainer((INamedContainerProvider) te);
		return ActionResultType.SUCCESS;
	}
}