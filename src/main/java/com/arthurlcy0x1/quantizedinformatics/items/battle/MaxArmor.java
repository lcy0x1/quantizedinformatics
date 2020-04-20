package com.arthurlcy0x1.quantizedinformatics.items.battle;

import com.arthurlcy0x1.quantizedinformatics.AbReg;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.ItemUtil;
import com.arthurlcy0x1.quantizedinformatics.world.RegWorld;

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
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MaxArmor extends ArmorItem {

	public static class MaxMat implements IArmorMaterial {

		private static final int[] REDUCTION = { 1, 2, 3, 1 };

		private int lv;

		private MaxMat(int level) {
			lv = level;
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn) {
			return REDUCTION[slotIn.getIndex()] * (lv - 1);
		}

		@Override
		public int getDurability(EquipmentSlotType slotIn) {
			return DURABILITY * 2 * (1 << lv - 2);
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public String getName() {
			return AbReg.MODID + ":maxwell";
		}

		@Override
		public Ingredient getRepairMaterial() {
			return null;
		}

		@Override
		public SoundEvent getSoundEvent() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public float getToughness() {
			return 1 << (lv - 1);
		}

	}

	public static final int DURABILITY = MaxwellItem.VALUE / 10;

	public static final MaxMat LV2 = new MaxMat(2);
	public static final MaxMat LV3 = new MaxMat(3);
	public static final MaxMat LV4 = new MaxMat(4);

	private static final int HEAL_FACTOR = 4, ROAD_LEN = 5, DIR_FAC = 20, ENT_RAD = 8, MIN_LEN = 3;
	private static final int EFF_RE = 201, EFF_MAX = 219;
	private static final double MAX_SPEED = 0.5, DUR_CHANCE = 0.01;

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
		ItemUtil.damageItem(is, pl, w, DUR_CHANCE);
	}

	private static boolean canFunction(ItemStack is) {
		return is.getDamage() < is.getMaxDamage() * 3 / 4;
	}

	private static boolean canHavePotion(PlayerEntity pl) {
		for (ItemStack is : pl.getArmorInventoryList())
			if (is.getItem() instanceof MaxArmor
					&& ((MaxArmor) is.getItem()).getEquipmentSlot() == EquipmentSlotType.HEAD)
				return true;
		return false;
	}

	private static void setFog(ItemStack is, World w, PlayerEntity pl, BlockPos pos) {
		Items.CHORUS_FRUIT.asItem();
		if (RegWorld.isQuantumWorld(w))
			return;
		if (w.getBlockState(pos).isAir(w, pos)) {
			w.setBlockState(pos, Registrar.B_FOG.getDefaultState());
			ItemUtil.damageItem(is, pl, w, DUR_CHANCE);
		}
	}

	public MaxArmor(Properties p, EquipmentSlotType s, MaxMat mat) {
		super(mat, s, p);
	}

	public boolean canUse(ItemStack is) {
		return is.getItem() == Registrar.IMU_FIX || is.getItem() == Registrar.IMU_DEF && MaxwellItem.getLevel(is) == 2;
	}

	@Override
	public MaxMat getArmorMaterial() {
		return (MaxMat) super.getArmorMaterial();
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	public int getLv() {
		return getArmorMaterial().lv;
	}

	@Override
	public void onArmorTick(ItemStack is, World w, PlayerEntity pl) {
		if (slot == EquipmentSlotType.FEET)
			tickBoots(is, w, pl);
		else if (slot == EquipmentSlotType.CHEST)
			tickChestplate(is, w, pl);
		else if (slot == EquipmentSlotType.LEGS)
			tickLeggings(is, w, pl);
		else if (slot == EquipmentSlotType.HEAD)
			tickHelmet(is, w, pl);
	}

	public int repair(ItemStack is) {
		return canUse(is) ? DURABILITY : 0;
	}

	private boolean addEffect(int lv, PlayerEntity pl, Effect eff, int amp) {
		if (!canHavePotion(pl))
			return false;
		EffectInstance ei = pl.getActivePotionEffect(eff);
		int re = EFF_RE;
		int max = EFF_MAX;
		if (ei == null || ei.getDuration() < re)
			while (amp > 0)
				if (getLv() >= lv)
					return pl.addPotionEffect(new EffectInstance(eff, max, amp - 1));
				else {
					amp--;
					lv--;
				}
		return false;
	}

	private void tickBoots(ItemStack is, World w, PlayerEntity pl) {
		ItemStack off = pl.getHeldItemOffhand();
		addEffect(4, pl, Effects.JUMP_BOOST, 2);
		if (!w.isRemote && pl.fallDistance > 2 && !off.isEmpty()) {
			BlockPos pos = pl.getPosition().offset(Direction.DOWN);
			setFog(is, w, pl, pos);
		}
	}

	private void tickChestplate(ItemStack is, World w, PlayerEntity pl) {
		ItemStack off = pl.getHeldItemOffhand();
		addEffect(2, pl, Effects.FIRE_RESISTANCE, 1);
		addEffect(4, pl, Effects.RESISTANCE, 2);
		int fac = HEAL_FACTOR;
		if (pl.getHealth() < pl.getMaxHealth() - fac && canFunction(is)) {
			if (!off.isEmpty() && off.getItem() == Registrar.IE_SOUL) {
				off.shrink(1);
				pl.heal(fac);
				ItemUtil.damageItem(is, pl, fac);
			}
		}
		for (ItemStack ais : pl.getArmorInventoryList())
			if (!ais.isEmpty() && ais.getItem() instanceof MaxArmor) {
				MaxArmor ma = (MaxArmor) ais.getItem();
				int dam = ais.getDamage();
				if (dam >= DURABILITY)
					if (pl.inventory.clearMatchingItems(ma::canUse, 1) > 0)
						ais.setDamage(dam - DURABILITY);
			}
	}

	private void tickHelmet(ItemStack is, World w, PlayerEntity pl) {
		addEffect(2, pl, Effects.NIGHT_VISION, 1);
		addEffect(3, pl, Effects.WATER_BREATHING, 1);
		addEffect(4, pl, Effects.SATURATION, 1);
		Vec3d vec = pl.getPositionVec();
		AxisAlignedBB aabb = new AxisAlignedBB(vec, vec).grow(ENT_RAD);
		for (Entity ie : w.getEntitiesInAABBexcluding(null, aabb,
				a -> a instanceof ItemEntity || a instanceof ExperienceOrbEntity)) {
			Vec3d ipos = ie.getPositionVec();
			if (ipos.length() < MIN_LEN)
				ie.setPosition(vec.getX(), vec.getY(), vec.getZ());
			else {
				Vec3d dif = ipos.subtract(vec).scale(-MAX_SPEED / ENT_RAD);
				ie.addVelocity(dif.getX(), dif.getY(), dif.getZ());
			}
		}
	}

	private void tickLeggings(ItemStack is, World w, PlayerEntity pl) {
		ItemStack off = pl.getHeldItemOffhand();
		addEffect(4, pl, Effects.SPEED, 2);
		if (pl.isSprinting()) {
			if (w.isRemote) {
				if (pl.onGround)
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
							Vec3d tar = vec.add(j * cy - i * sy, 0, j * sy + i * cy);
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

}
