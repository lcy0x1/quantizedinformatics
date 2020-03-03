package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DraftIn extends DIOTerm<DraftIn.TE> {

	public DraftIn() {
		super(TE::new, OUTPUT);
	}

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_IN,OUTPUT);
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
			return 1;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent("quantizedinformatics:container.draft_in");

}
