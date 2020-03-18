package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.ArrayList;
import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.MaxwellItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.registries.ForgeRegistries;

public class EntMachine {

	protected static abstract class EMTE<T extends EMTE<T>> extends CTEBlock.CTETE<T> implements ITickableTileEntity {

		private final int type;

		private boolean dirty = true;

		private List<EntityType<?>> list;

		public EMTE(TileEntityType<T> tet, int size, int t) {
			super(tet, size);
			type = t;
		}

		public int getLevel(int ind) {
			ItemStack is = getStackInSlot(ind);
			if (!is.isEmpty())
				return MaxwellItem.getLevel(is);
			return 0;
		}

		public int getPower() {
			return Math.max(0, getLevel(type) - getLevel(1 - type));
		}

		public double getRadius() {
			return 1 << Math.min(MAX_RADIUS, Math.min(getLevel(0), getLevel(1)));
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			if (slot == 0)
				return is.getItem() == Registrar.IMU_ATK;
			if (slot == 1)
				return is.getItem() == Registrar.IMU_DEF;
			return false;
		}

		@Override
		public void onChange(int index) {
			dirty = true;
		}

		@Override
		public void tick() {
			if (dirty)
				updateEntityList();
			dirty = false;
			if (!world.isBlockPowered(pos))
				return;
			double x0 = pos.getX() + 0.5;
			double y0 = pos.getY() + 0.5;
			double z0 = pos.getZ() + 0.5;
			double r = getRadius();
			AxisAlignedBB aabb = new AxisAlignedBB(x0 - r, y0 - r, z0 - r, x0 + r, y0 + r, z0 + r);
			List<Entity> le = world.getEntitiesWithinAABBExcludingEntity(null, aabb);
			for (Entity e : le) {
				if (!canHandleEntity(e))
					continue;
				Vec3d dir = e.getPositionVec().add(-x0, -y0, -z0);
				double dis = dir.length();
				if (dis < r)
					handle(e, dir);
			}
		}

		protected abstract void handle(Entity e, Vec3d dir);

		private boolean canHandleEntity(Entity e) {
			return !(e instanceof PlayerEntity);
		}

		private void updateEntityList() {
			ItemStack is = getStackInSlot(2);
			if (is.isEmpty() || !is.getOrCreateTag().contains("entities"))
				list = null;
			else {
				list = new ArrayList<>();
				ListNBT lnbt = is.getOrCreateTag().getList("entities", 8);
				for (int i = 0; i < lnbt.size(); i++)
					list.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(lnbt.getString(i))));
			}
		}

	}

	public static final int MAX_RADIUS = 6;
	public static final int TYPE_ATK = 0, TYPE_DEF = 1;

}
