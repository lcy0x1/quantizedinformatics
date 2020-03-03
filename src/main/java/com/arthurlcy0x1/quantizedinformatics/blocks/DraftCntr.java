package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftTE;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DraftCntr {

	public static class TE extends CTEBlock.CTETE<TE> implements DraftTE, ITickableTileEntity {

		public TE() {
			super(Registrar.TET_CNTR, 1);
			// TODO Auto-generated constructor stub
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

		@Override
		public int getOutputChannel(int ind) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getOutputValue(int ind) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int outputCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void tick() {
			// TODO Auto-generated method stub

		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_center");

}
