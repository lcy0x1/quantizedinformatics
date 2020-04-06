package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.world.RegWorld;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuanBlock extends BaseBlock implements QuantumBlock {

	public static class QRep implements IRep {

		@Override
		public void onReplaced(BlockState cur, World w, BlockPos pos, BlockState tar, boolean isMoving) {
			if (!RegWorld.isQuantumWorld(w))
				return;
			if (tar.getBlock() instanceof QuantumBlock)
				return;
			w.setBlockState(pos, Registrar.BQ_AIR.getDefaultState(), 48);
		}

	}

	public static final QRep REP = new QRep();

	public QuanBlock() {
		this(construct(BlockProp.QUANTUM_WORLD));
	}

	public QuanBlock(BlockImplementor bi) {
		super(bi.addImpl(REP));
	}

}
