package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.entities.QuanStand;

import net.minecraft.block.BlockState;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StoneStand extends QuanBlock {

	public static class SRep implements IRep {

		private final int dir;

		public SRep(int h) {
			dir = h;
		}

		@Override
		public void onReplaced(BlockState state, World w, BlockPos pos, BlockState newState, boolean isMoving) {
			w.destroyBlock(pos.add(0, dir, 0), false);
		}

	}

	private static void spawn(BlockState state, World w, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!w.isRemote && newState.getBlock() != Registrar.BQ_AIR) {
			QuanStand qs = Registrar.ETM_QS.create(w);
			Direction d = state.get(BaseBlock.HORIZONTAL_FACING);
			d.getHorizontalAngle();
			qs.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, d.getHorizontalAngle(), 0);
			qs.rotationYawHead = d.getHorizontalAngle();
			w.addEntity(qs);
		}
	}

	public StoneStand() {
		super(construct(BlockProp.STONE_STAND).addImpls(HOR, new SRep(1), (IRep) StoneStand::spawn));
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState bs, IBlockReader w, BlockPos pos) {
		return false;
	}

	@Override
	public void onBlockAdded(BlockState bs, World w, BlockPos p, BlockState old, boolean isMoving) {
		if (!w.isRemote) {
			w.setBlockState(p.up(), Registrar.B_STANDHEAD.getDefaultState());
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

}
