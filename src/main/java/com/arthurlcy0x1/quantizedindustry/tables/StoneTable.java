package com.arthurlcy0x1.quantizedindustry.tables;

import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;

public class StoneTable extends BaseBlock {

	public StoneTable(STE ste) {
		super(construct(BlockProp.M_POWER).addImpls(HOR, ste));
	}

}
