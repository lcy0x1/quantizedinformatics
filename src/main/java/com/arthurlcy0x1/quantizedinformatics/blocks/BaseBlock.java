package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class BaseBlock extends Block {

	public static interface IHorizontalBlock {

		public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

		public default BlockState i$rotate(BlockState state, Rotation rot) {
			return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
		}

		public default BlockState i$mirror(BlockState state, Mirror mirrorIn) {
			return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
		}
	}

	public BaseBlock(Properties properties) {
		super(properties);
	}

	public final BlockState rotate(BlockState state, Rotation rot) {
		return i$rotate(state, rot);
	}

	public final BlockState mirror(BlockState state, Mirror mirrorIn) {
		return i$mirror(state, mirrorIn);
	}

	public abstract BlockState i$rotate(BlockState state, Rotation rot);

	public abstract BlockState i$mirror(BlockState state, Mirror mirrorIn);

}
