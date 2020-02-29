package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe.Craft3DInv;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Craft3D extends CTEBlock<Craft3D.TE> {

	public static class Cont extends CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CT_CRAFT3D, id, inv, ent);

			// TODO add slots
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements Craft3DInv, INamedContainerProvider {

		private static class Matcher {

			private static final int[][] DIRE = { { 0, 0, 1 }, { 0, 0, -1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 0, 0 },
					{ -1, 0, 0 } };

			private static final int MAX = 8;

			private final Set<BlockPos> set = new HashSet<>();
			private final int x, y, z;
			private int x0, x1, y0, y1, z0, z1;
			private boolean malf = false;

			private Matcher(BlockPos bp) {
				x = bp.getX();
				y = bp.getY();
				z = bp.getZ();
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

			private int[][] getBounds() {
				return check() ? new int[][] { { x0, x1 }, { y0, y1 }, { z0, z1 } } : null;
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

		private int[][] bounds;

		private int[] dim;

		public TE() {
			super(Registrar.TET_CRAFT3D, Cont::new, 1, "quantizedinformatics::container.craft_3d");
		}

		@Override
		public int[][] getBounds() {
			return bounds;
		}

		@Override
		public int[] getDim() {
			return dim;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public int getSlot() {
			return 0;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return false;
		}

		public void react(World w, PlayerEntity pl) {
			checkBounds();
			Optional<C3DRecipe> opt = w.getRecipeManager().getRecipe(Registrar.RT_C3D, this, w);
			if (opt.isPresent()) {
				C3DRecipe r = opt.get();
				r.craft(this, w);
				ItemStack is = r.getCraftingResult(this);
				if (!pl.addItemStackToInventory(is))
					pl.dropItem(is, false);
			}
		}

		private void checkBounds() {
			Matcher mat = new Matcher(pos);
			for (int[] v : Matcher.DIRE)
				mat.rec(world, pos.add(v[0], v[1], v[2]));
			bounds = mat.getBounds();
			if (bounds == null)
				dim = null;
			else {
				dim = new int[3];
				for (int i = 0; i < 3; i++)
					dim[i] = bounds[i][1] - bounds[i][0] - 1;
			}
		}

	}

	public Craft3D() {
		super(TE::new);

	}

	@Override
	public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		TileEntity ent = w.getTileEntity(pos);
		if (ent != null && ent instanceof TE) {
			TE te = (TE) ent;
			te.react(w, pl);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.SUCCESS;
	}

}
