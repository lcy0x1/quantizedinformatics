package com.arthurlcy0x1.quantizedinformatics.blocks.logic;

import static com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.CNUM;
import static com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.OUTPUT;
import static com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.S_HIGH;
import static com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.S_LOW;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DTETerm;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class DraftIn {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(17));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_IN, id, arr);
		}

	}

	public static class TE extends DTETerm<TE> {

		public TE() {
			super(Registrar.TETD_IN, OUTPUT);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, getSignal());
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("draft_in");
		}

		@Override
		public int[] update(int[] vals) {
			int[] ans = new int[CNUM];
			ans[getSignal().getInput(0)] = world.isBlockPowered(pos) ? S_HIGH : S_LOW;
			return ans;
		}

	}

}
