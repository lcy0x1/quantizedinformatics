package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DraftIn extends DIOTerm<DraftIn.TE> {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(1));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_IN, id, new SignalWriter(0, 1, arr));
		}

	}

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_IN, OUTPUT);
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
			int[] ans = new int[CNUM];
			// TODO add inputs
			return ans;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent("quantizedinformatics:container.draft_in");

	public DraftIn() {
		super(TE::new, OUTPUT);
	}

}
