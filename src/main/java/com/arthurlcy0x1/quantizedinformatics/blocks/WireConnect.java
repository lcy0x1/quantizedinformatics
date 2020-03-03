package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public interface WireConnect {

	public static class DIOTerm<T extends TileEntity> extends CTEBlock<T> implements DraftIO {

		private final int type;

		public DIOTerm(Supplier<T> sup, int t) {
			super(sup);
			type = t;
		}

		@Override
		public boolean canConnectFrom(int type, BlockState b, Direction d) {
			if (type == CRAFT) {
				Direction dire = b.get(HorizontalBlock.HORIZONTAL_FACING);
				return d != dire && d != dire.getOpposite();
			} else
				return WireConnect.DraftIO.super.canConnectFrom(type, b, d);
		}

		@Override
		public int ioType(BlockState b, Direction d) {
			return b.get(HorizontalBlock.HORIZONTAL_FACING) == d.getOpposite() ? type : NONE;
		}

	}

	public static class DTETerm<T extends DTETerm<T>> extends TileEntity implements DraftTE {

		private final int type;

		private int channel;

		public DTETerm(TileEntityType<T> t, int typ) {
			super(t);
			type = typ;
		}

		@Override
		public int getOutputChannel(int ind) {
			return type == DraftIO.OUTPUT ? channel : 0;
		}

		@Override
		public int getOutputValue(int ind) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int outputCount() {
			return type == DraftIO.OUTPUT ? 1 : 0;
		}

	}

	public static interface DraftIO extends WireConnect {

		public static final int NONE = 0, INPUT = 1, OUTPUT = 2;

		@Override
		public default boolean canConnectFrom(int type, BlockState b, Direction d) {
			return type == GATE && ioType(b, d) != NONE;
		}

		public int ioType(BlockState b, Direction d);

	}

	public static interface DraftTE {

		public static final int LOW = 0, HIGH = 1, FLOAT = 2, ARBI = 3;

		public int getOutputChannel(int ind);

		public int getOutputValue(int ind);

		public int outputCount();

	}

	public static final int GATE = 0, CRAFT = 1;

	public boolean canConnectFrom(int type, BlockState b, Direction d);

	public default boolean connectable(int type, BlockState b, Direction d) {
		Block bl = b.getBlock();
		if (!(bl instanceof WireConnect))
			return false;
		WireConnect wc = (WireConnect) bl;
		return wc.canConnectFrom(type, b, d.getOpposite());
	}

}
