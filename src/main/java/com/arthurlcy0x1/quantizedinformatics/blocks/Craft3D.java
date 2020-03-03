package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe.Craft3DInv;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class Craft3D extends HorizontalBlock implements WireConnect {

	public static class Handler extends Inventory implements Craft3DInv {

		private static final int[][] DIRE = { { 0, 0, 1 }, { 0, 0, -1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 0, 0 },
				{ -1, 0, 0 } };

		private static final int MAX = 8;

		private final Set<BlockPos> set = new HashSet<>();
		private final World w;
		private final BlockPos pos;
		private final int x, y, z;

		private int x0, x1, y0, y1, z0, z1;
		private boolean malf = false;
		private int[][] bounds;
		private int[] dim;

		private Handler(World world, BlockPos bp) {
			super(0);
			w = world;
			pos = bp;
			x = bp.getX();
			y = bp.getY();
			z = bp.getZ();
		}

		@Override
		public int[][] getBounds() {
			return bounds;
		}

		@Override
		public int[] getDim() {
			return dim;
		}

		public void react(PlayerEntity pl) {
			for (int[] v : DIRE)
				rec(w, pos.add(v[0], v[1], v[2]));
			if (!check()) {
				bounds = null;
				dim = null;
				return;
			}
			bounds = new int[][] { { x0, x1 }, { y0, y1 }, { z0, z1 } };
			dim = new int[3];
			for (int i = 0; i < 3; i++)
				dim[i] = bounds[i][1] - bounds[i][0] - 1;
			Optional<C3DRecipe> opt = w.getRecipeManager().getRecipe(Registrar.RT_C3D, this, w);
			if (opt.isPresent()) {
				C3DRecipe r = opt.get();
				r.craft(this, w);
				ItemStack is = r.getCraftingResult(this);
				if (!pl.addItemStackToInventory(is))
					pl.dropItem(is, false);
			}
		}

		private boolean addNew(BlockPos bp) {
			if (set.isEmpty()) {
				x0 = x1 = bp.getX();
				y0 = y1 = bp.getY();
				z0 = z1 = bp.getZ();
			} else {
				x0 = Math.min(x0, bp.getX());
				x1 = Math.max(x1, bp.getX());
				y0 = Math.min(y0, bp.getY());
				y1 = Math.max(y1, bp.getY());
				z0 = Math.min(z0, bp.getZ());
				z1 = Math.max(z1, bp.getZ());
			}
			if (x1 - x0 > MAX || y1 - y0 > MAX || z1 - z0 > MAX)
				malf = true;
			return set.add(bp);
		}

		private boolean check() {
			if (malf)
				return false;
			if (x > x0 && x < x1 && y > y0 && y < y1 && z > z0 && z < z1)
				return false;

			if (x1 - x0 < 2 || y1 - y0 < 2 || z1 - z0 < 2)
				return false;
			int exp = (x1 - x0 + y1 - y0 + z1 - z0) * 4 - 4;

			if (set.size() != exp)
				return false;
			for (int i = x0; i <= x1; i++) {
				if (!set.contains(new BlockPos(i, y0, z0)))
					return false;
				if (!set.contains(new BlockPos(i, y1, z0)))
					return false;
				if (!set.contains(new BlockPos(i, y0, z1)))
					return false;
				if (!set.contains(new BlockPos(i, y1, z1)))
					return false;
			}
			for (int i = y0 + 1; i < y1; i++) {
				if (!set.contains(new BlockPos(x0, i, z0)))
					return false;
				if (!set.contains(new BlockPos(x1, i, z0)))
					return false;
				if (!set.contains(new BlockPos(x0, i, z1)))
					return false;
				if (!set.contains(new BlockPos(x1, i, z1)))
					return false;
			}
			for (int i = z0 + 1; i < z1; i++) {
				if (!set.contains(new BlockPos(x0, y0, i)))
					return false;
				if (!set.contains(new BlockPos(x0, y1, i)))
					return false;
				if (!set.contains(new BlockPos(x1, y0, i)))
					return false;
				if (!set.contains(new BlockPos(x1, y1, i)))
					return false;
			}
			return true;
		}

		private void rec(World w, BlockPos p) {
			if (malf)
				return;
			if (w.getBlockState(p).getBlock() != Registrar.BC_FRAME)
				return;
			if (!addNew(p))
				return;
			for (int[] v : DIRE)
				rec(w, p.add(v[0], v[1], v[2]));
		}

	}

	public Craft3D() {
		super(Block.Properties.create(Material.ROCK));

	}

	@Override
	public boolean canConnectFrom(BlockState bs, Direction d) {
		Direction self = bs.get(HORIZONTAL_FACING);
		return d != self && d != Direction.UP && d != Direction.DOWN;
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public ActionResultType func_225533_a_(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h,
			BlockRayTraceResult r) {
		new Handler(w, pos).react(pl);
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public int type() {
		return GATE;
	}

}
