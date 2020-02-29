package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CTEBlock<T extends TileEntity> extends HorizontalBlock {

	public static class CTECont extends Container {

		protected final IInventory slots;

		protected CTECont(ContainerType<? extends CTECont> type, int id, PlayerInventory inv, IInventory ent) {
			super(type, id);
			slots = ent;
			for (int r = 0; r < 3; ++r)
				for (int c = 0; c < 9; ++c)
					this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 103 + r * 18));
			for (int c = 0; c < 9; ++c)
				this.addSlot(new Slot(inv, c, 8 + c * 18, 161));// FIXME layout

		}

		@Override
		public boolean canInteractWith(PlayerEntity player) {
			return slots.isUsableByPlayer(player);
		}

	}

	public static abstract class CTETE<T extends CTETE<T>> extends TileEntity
			implements IInventory, INamedContainerProvider {

		public static interface IFactory {

			public Container create(int type, PlayerInventory inv, IInventory ent);

		}

		private final IFactory fac;

		private final Inventory inv;

		private final ITextComponent title;

		public CTETE(TileEntityType<T> type, IFactory f, int size, String tit) {
			super(type);
			fac = f;
			inv = new Inventory();
			title = new TranslationTextComponent(tit);
		}

		@Override
		public void clear() {
			inv.clear();
		}

		@Override
		public Container createMenu(int type, PlayerInventory inv, PlayerEntity ent) {
			return fac.create(type, inv, this);
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			return inv.decrStackSize(index, count);
		}

		@Override
		public ITextComponent getDisplayName() {
			return title;
		}

		@Override
		public int getSizeInventory() {
			return inv.getSizeInventory();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return inv.getStackInSlot(index);
		}

		@Override
		public boolean isEmpty() {
			return inv.isEmpty();
		}

		@Override
		public abstract boolean isItemValidForSlot(int index, ItemStack is);

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
		return ActionResultType.PASS;
	}
}
