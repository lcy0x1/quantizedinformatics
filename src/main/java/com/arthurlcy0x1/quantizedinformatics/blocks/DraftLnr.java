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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class DraftLnr extends Block implements DraftIO {

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
	};

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_LNR, INPUT);
		}

		@Override
		public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
				PlayerEntity p_createMenu_3_) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_listener");

	private static final EnumProperty<Mode> PROP = EnumProperty.create("mode", Mode.class, Mode.values());

	public DraftLnr() {
		super(Block.Properties.create(Material.ROCK));
	}

	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(PROP);
	}

	public TE createTileEntity(BlockState bs, IBlockReader w) {
		return new TE();
	}

	@Override
	public boolean hasTileEntity(BlockState bs) {
		return true;
	}

	@Override
	public int ioType(BlockState b, Direction d) {
		return d == Direction.DOWN ? INPUT : NONE;
	}

}
