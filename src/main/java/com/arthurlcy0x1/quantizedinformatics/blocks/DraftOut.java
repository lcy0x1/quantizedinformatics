package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DraftOut extends DIOTerm<DraftOut.TE> {

	public DraftOut() {
		super(TE::new, INPUT);
	}

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_OUT, INPUT);
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
			"quantizedinformatics:container.draft_out");

}
