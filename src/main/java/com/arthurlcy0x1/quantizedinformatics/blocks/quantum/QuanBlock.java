package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;

public class QuanBlock extends BaseBlock implements QuantumBlock {

	public QuanBlock() {
		super(construct(BlockProp.QUANTUM_FOG));
	}

}
