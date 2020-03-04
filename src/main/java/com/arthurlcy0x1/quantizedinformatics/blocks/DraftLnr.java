package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftIO;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class DraftLnr extends Block implements DraftIO {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(1));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_LNR, id, new SignalWriter(1, 0, arr));
		}

	}

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_LNR, INPUT);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, getSignal());
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

		@Override
		public int[] update(int[] vals) {
			return vals.clone();
		}

	};

	private static enum Mode implements IStringSerializable {
		FLOAT("float"), ERROR("error"), HIGH("high"), LOW("low");

		private final String name;

		private Mode(String str) {
			name = str;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_listener");

	private static final EnumProperty<Mode> PROP = EnumProperty.create("mode", Mode.class, Mode.values());

	public DraftLnr() {
		super(Block.Properties.create(Material.ROCK));
	}

	@Override
	public TE createTileEntity(BlockState bs, IBlockReader w) {
		return new TE();
	}

	@Override
	public Direction getInDire(BlockState b) {
		return Direction.DOWN;
	}

	@Override
	public Direction getOutDire(BlockState b) {
		return null;
	}

	@Override
	public boolean hasTileEntity(BlockState bs) {
		return true;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(PROP);
	}

}
