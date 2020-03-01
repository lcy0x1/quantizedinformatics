package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CTEBlock<T extends TileEntity> extends HorizontalBlock {

	public static class CTECont extends Container {

		protected static class FuelSlot extends Slot {

			public FuelSlot(IInventory inv, int ind, int x, int y) {
				super(inv, ind, x, y);
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return AbstractFurnaceTileEntity.isFuel(is);
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
			this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F,
					this.ySize - 96 + 2, 4210752);
		}

	}

	public static abstract class CTETE<T extends CTETE<T>> extends TileEntity
			implements IInventory, INamedContainerProvider {

		public static boolean canMerge(ItemStack is0, ItemStack is1) {
			return is0 == null || is1 == null || is0.isEmpty() || is1.isEmpty()
					|| Container.areItemsAndTagsEqual(is0, is1)
							&& is0.getCount() + is1.getCount() <= is1.getMaxStackSize();
		}

		private final Inventory inv;

		public CTETE(TileEntityType<T> type, int size) {
			super(type);
			inv = new Inventory(size);
		}

		@Override
		public void clear() {
			inv.clear();
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			return inv.decrStackSize(index, count);
		}

		@Override
		public int getSizeInventory() {
			return inv.getSizeInventory();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return inv.getStackInSlot(index);
		}

		public void incrOrSet(int index, ItemStack is) {
			if (inv.getStackInSlot(index).isEmpty())
				inv.setInventorySlotContents(index, is);
			else
				inv.getStackInSlot(index).grow(is.getCount());
			markDirty();
		}

		@Override
		public boolean isEmpty() {
			return inv.isEmpty();
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity player) {
			return inv.isUsableByPlayer(player);// TODO add limitation to usability
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return inv.removeStackFromSlot(index);
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			inv.setInventorySlotContents(index, stack);
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
	public ActionResultType func_225533_a_(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h,
			BlockRayTraceResult r) {
		return onClick(bs, w, pos, pl, h);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	protected ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		if (w.isRemote)
			return ActionResultType.SUCCESS;
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof CTETE<?>)
			pl.openContainer((CTETE<?>) te);
		return ActionResultType.SUCCESS;
	}
}
