package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class RecMaker extends CTEBlock<RecMaker.TE> {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTA_REC, id, inv, ent, 0);
			// TODO Auto-generated constructor stub
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> {

		public TE() {
			super(Registrar.TETA_REC, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("recipe_maker");
		}

	}

	private static final int SIZE = 12;

	public RecMaker() {
		super(TE::new);
	}

}
