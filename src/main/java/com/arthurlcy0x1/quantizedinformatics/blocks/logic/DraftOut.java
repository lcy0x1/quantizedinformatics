package com.arthurlcy0x1.quantizedinformatics.blocks.logic;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import static net.minecraft.state.properties.BlockStateProperties.POWER_0_15;

public class DraftOut extends DIOTerm {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(17));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_OUT, id, arr);
		}

	}

	public static class TE extends DTETerm<TE> {

		public TE() {
			super(Registrar.TETD_OUT, INPUT);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, getSignal());
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("draft_out");
		}

		@Override
		public int[] update(int[] vals) {
			int val = vals[getSignal().getInput(0)];
			int power = val == S_HIGH ? 15 : 0;
			world.setBlockState(pos, getBlockState().with(POWER_0_15, power));
			return vals;
		}

	}

	public DraftOut() {
		super(TE::new, INPUT);
	}

	@Override
	protected void addImpls(BlockImplementor impl) {
		impl.addImpl(POW);
	}

}
