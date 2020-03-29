package com.arthurlcy0x1.quantizedinformatics.items;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MaxArmor extends ArmorItem {

	public static final MaxMat LV2 = new MaxMat(2);

	public static class MaxMat implements IArmorMaterial {

		private static final int[] REDUCTION = { 3, 6, 8, 3 };

		private int lv;

		private MaxMat(int level) {
			lv = level;
		}

		@Override
		public int getDurability(EquipmentSlotType slotIn) {
			return 160 * (1 << 2 * lv);
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn) {
			return REDUCTION[slotIn.getIndex()];
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public SoundEvent getSoundEvent() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairMaterial() {
			return null;
		}

		@Override
		public String getName() {
			return Registrar.MODID + ":maxwell";
		}

		@Override
		public float getToughness() {
			return 1 << (lv - 1);
		}

	}

	private static final int HEAL_FACTOR = 4, ROAD_LEN = 4, DIR_FAC = 10, ENT_RAD = 8;
	private static final double MAX_SPEED = 0.5;

	public MaxArmor(Properties p, EquipmentSlotType s, MaxMat mat) {
		super(mat, s, p);
	}

	private static boolean canFunction(ItemStack is) {
		return is.getDamage() < is.getMaxDamage() / 2;
	}

	private static void setFog(ItemStack is, World w, PlayerEntity pl, BlockPos pos) {
		if (w.getBlockState(pos).getBlock() == Blocks.AIR) {
			w.setBlockState(pos, Registrar.B_FOG.getDefaultState());
			ItemUtil.damageItem(is, pl);
		}
	}

	private static void breakBlock(ItemStack is, World w, PlayerEntity pl, BlockPos pos, Block held) {
		if (held == Blocks.AIR)
			return;
		BlockState bs = w.getBlockState(pos);
		if (bs.getBlock() != held)
			return;
		float hard = bs.getBlockHardness(w, pos);
		if (hard < 0 || hard >= 20)
			return;
		w.destroyBlock(pos, true);
		ItemUtil.damageItem(is, pl);
	}

	public void onArmorTick(ItemStack is, World w, PlayerEntity pl) {
		ItemStack off = pl.getHeldItemOffhand();
		if (slot == EquipmentSlotType.FEET) {
			if (!w.isRemote && pl.fallDistance > 2 && !off.isEmpty()) {
				BlockPos pos = pl.getPosition().offset(Direction.DOWN);
				setFog(is, w, pl, pos);
			}
		}
		if (slot == EquipmentSlotType.CHEST) {
			int fac = HEAL_FACTOR;
			if (pl.getHealth() < pl.getMaxHealth() - fac && canFunction(is)) {
				if (!off.isEmpty() && off.getItem() == Registrar.IE_SOUL) {
					off.shrink(1);
					pl.heal(fac);
					ItemUtil.damageItem(is, pl, fac);
				}
			}
		}
		if (slot == EquipmentSlotType.LEGS) {
			if (pl.isSprinting()) {
				if (w.isRemote) {
					pl.setMotionMultiplier(Registrar.B_FOG.getDefaultState(), new Vec3d(4, 1, 4));
				} else {
					BlockPos pos = pl.getPosition().offset(Direction.DOWN);
					Vec3d vec = pl.getPositionVec();
					vec = new Vec3d(vec.x, pos.getY(), vec.z);
					float yaw = pl.prevRotationYaw;
					float cy = -MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
					float sy = -MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
					int max = cy * cy * sy * sy * DIR_FAC > 1 ? 2 : 1;
					for (int i = -max; i <= max; i++)
						for (int j = 0; j < ROAD_LEN; j++)
							if (canFunction(is)) {
								Vec3d tar = vec.add(j * cy + i * sy, 0, j * sy + i * cy);
								if (off.getItem() == Registrar.I_FOGBALL)
									setFog(is, w, pl, new BlockPos(tar));
								else if (off.getItem() instanceof BlockItem) {
									Block b = ((BlockItem) off.getItem()).getBlock();
									breakBlock(is, w, pl, new BlockPos(tar.add(0, 1, 0)), b);
									breakBlock(is, w, pl, new BlockPos(tar.add(0, 2, 0)), b);
								}
							}
				}
			}
		}
		if (slot == EquipmentSlotType.HEAD) {
			Vec3d vec = pl.getPositionVec();
			AxisAlignedBB aabb = new AxisAlignedBB(vec, vec).grow(ENT_RAD);
			for (Entity ie : w.getEntitiesInAABBexcluding(null, aabb,
					a -> a instanceof ItemEntity || a instanceof ExperienceOrbEntity)) {
				Vec3d ipos = ie.getPositionVec();
				Vec3d dif = ipos.subtract(vec);
				dif.scale(-MAX_SPEED / ENT_RAD);
				ie.addVelocity(dif.x, dif.y, dif.z);
			}
		}
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}
}
