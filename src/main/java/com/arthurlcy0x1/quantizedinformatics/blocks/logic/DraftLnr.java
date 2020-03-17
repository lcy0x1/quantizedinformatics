package com.arthurlcy0x1.quantizedinformatics.blocks.logic;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DTETerm;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.DraftIO;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;

public class DraftLnr extends BaseBlock implements DraftIO {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(17));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_LNR, id, arr);
		}

	}

	public static class TE extends DTETerm<TE> {

		public TE() {
			super(Registrar.TETD_LNR, INPUT);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, getSignal());
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("draft_listener");
		}

		@Override
		public int[] update(int[] vals) {
			Mode m = Mode.get(vals[getSignal().getInput(0)]);
			world.setBlockState(pos, getBlockState().with(PROP, m));
			return vals;
		}

	};

	private static enum Mode implements IStringSerializable {
		FLOAT("float", 0), ERROR("error", 15), HIGH("high", 15), LOW("low", 0);

		private static Mode get(int type) {
			return type == S_FLOAT ? FLOAT : type == S_HIGH ? HIGH : type == S_LOW ? LOW : ERROR;
		}

		private final String name;
		private final int light;

		private Mode(String str, int l) {
			name = str;
			light = l;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static final EnumProperty<Mode> PROP = EnumProperty.create("mode", Mode.class, Mode.values());

	public DraftLnr() {
		super(construct(BlockProp.M_DRAFT).addImpls((STE) TE::new, (IState) (b) -> b.add(PROP),
				(ILight) b -> b.get(PROP).light));
	}

	@Override
	public Direction getInDire(BlockState b) {
		return Direction.DOWN;
	}

	@Override
	public Direction getOutDire(BlockState b) {
		return null;
	}

}
