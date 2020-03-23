package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.logic.Estimator;
import com.arthurlcy0x1.quantizedinformatics.logic.Estimator.EstiType;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class EntityCannon extends ShootableItem {

	public static class PotionEC extends EntityCannon {

		private static boolean isValid(ItemStack is) {
			return is.getItem() instanceof ThrowablePotionItem;

		}

		public PotionEC(Properties p) {
			super(p, 256);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return PotionEC::isValid;
		}

		/** gravity: 0.05, resistance: 0.01 */
		@Override
		public float getVelocity(int charge) {
			return MathHelper.clamp(charge / 20f, 0f, 1f) * 3;
		}

		@Override
		protected Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo) {
			PotionEntity e = new PotionEntity(w, pl);
			e.setItem(ammo);
			e.shoot(pl, pl.rotationPitch, pl.rotationYaw, 0, velo, 0);
			return null;
		}

	}

	public static class SmartTNT extends TNTEntity {

		private final PlayerEntity tntPlacedBy;
		private final float radius;

		public SmartTNT(World w, double x, double y, double z, PlayerEntity pl, ItemStack is) {
			super(Registrar.ET_STNT, w);
			this.setPosition(x, y, z);
			this.setFuse(80);
			this.prevPosX = x;
			this.prevPosY = y;
			this.prevPosZ = z;
			this.tntPlacedBy = pl;
			radius = 1 << MaxwellItem.getLevel(is);
		}

		@Override
		public void explode() {
			if (getRadius() <= 0)
				return;
			double x = func_226277_ct_();
			double y = func_226283_e_(0.0625);
			double z = func_226281_cx_();
			world.createExplosion(this, getSource(), x, y, z, getRadius(), false, Explosion.Mode.BREAK);
		}

		public double getGravity() {
			return 0.04;
		}

		public float getRadius() {
			return radius;
		}

		public DamageSource getSource() {
			return DamageSource.causeExplosionDamage(getTntPlacedBy());
		}

		@Override
		public PlayerEntity getTntPlacedBy() {
			return tntPlacedBy;
		}

		@Override
		public void tick() {
			if (!hasNoGravity())
				setMotion(getMotion().add(0, 0.04 - getGravity(), 0));
			super.tick();
			if (!onGround)
				setMotion(getMotion().scale(1 / 0.98));
		}

	}

	public static class SmartTNTRender extends TNTRenderer {

		public SmartTNTRender(EntityRendererManager erm) {
			super(erm);
		}

	}

	public static class TNTEC extends EntityCannon {

		private static boolean isValid(ItemStack is) {
			return is.getItem() == Items.TNT || is.getItem() == Registrar.IMU_TNT;
		}

		public TNTEC(Properties p) {
			super(p, 256);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return TNTEC::isValid;
		}

		/** gravity: 0.04, resistance: 0.02 */
		@Override
		public float getVelocity(int charge) {
			return MathHelper.clamp(charge / 20f, 0f, 1f) * 3;
		}

		@Override
		protected Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo) {
			TNTEntity e = new TNTEntity(w, getPlX(pl), getPlY(pl), getPlZ(pl), pl);
			EntityRayTraceResult ertr = rayTraceEntity(pl, velo * 40);
			if (ertr != null && ertr.getType() == Type.ENTITY) {
				LogManager.getLogger().warn("targeting entity: " + ertr.getEntity().getEntityString());
				Vec3d mot = ertr.getEntity().getMotion();
				Vec3d tar = ertr.getHitVec();
				Vec3d pos = e.getPositionVec();
				Estimator.EstiResult er = new Estimator(0.04, 0.02, pos, velo, 80, tar, mot).getAnswer();
				LogManager.getLogger().warn("aim status success: " + (er.getType() == EstiType.ZERO));
				if (er.getType() == EstiType.ZERO) {
					e.setMotion(er.getVec());
					e.setFuse((int) Math.round(er.getT()));
					return e;
				}

			}
			BlockRayTraceResult brtr = rayTraceBlock(pl.world, pl, velo * 40);
			if (brtr != null && brtr.getType() == Type.BLOCK) {
				LogManager.getLogger().warn("targeting block: " + brtr.getPos());
				Vec3d tar = brtr.getHitVec();
				Vec3d pos = e.getPositionVec();
				Estimator.EstiResult er = new Estimator(0.04, 0.02, pos, velo, 80, tar, Vec3d.ZERO).getAnswer();
				LogManager.getLogger().warn("aim status success: " + (er.getType() == EstiType.ZERO));
				if (er.getType() == EstiType.ZERO) {
					e.setMotion(er.getVec());
					e.setFuse((int) Math.round(er.getT()));
					return e;
				}
			}
			setDire(pl, velo, e);
			return e;
		}

	}

	private static double getPlX(PlayerEntity pl) {
		return pl.func_226277_ct_();
	}

	private static double getPlY(PlayerEntity pl) {
		return pl.func_226280_cw_() - 0.1;
	}

	private static double getPlZ(PlayerEntity pl) {
		return pl.func_226281_cx_();
	}

	private static Vec3d getRayTerm(Vec3d pos, float pitch, float yaw, double reach) {
		float f2 = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
		float f5 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		return pos.add(f6 * reach, f5 * reach, f7 * reach);
	}

	private static BlockRayTraceResult rayTraceBlock(World worldIn, PlayerEntity player, double reach) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		Vec3d vec3d = player.getEyePosition(1.0F);
		Vec3d vec3d1 = getRayTerm(vec3d, f, f1, reach);
		return worldIn.rayTraceBlocks(
				new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, FluidMode.NONE, player));
	}

	private static EntityRayTraceResult rayTraceEntity(PlayerEntity pl, double reach) {
		World world = pl.world;
		Vec3d pos = pl.getEyePosition(1);
		Vec3d end = getRayTerm(pos, pl.rotationPitch, pl.rotationYaw, reach);
		AxisAlignedBB box = new AxisAlignedBB(pos, end).grow(1);

		double d0 = reach * reach;
		Entity entity = null;
		Vec3d vec3d = null;

		for (Entity entity1 : world.getEntitiesInAABBexcluding(pl, box, e -> e instanceof LivingEntity)) {
			AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
			Optional<Vec3d> optional = axisalignedbb.rayTrace(pos, end);
			if (axisalignedbb.contains(pos)) {
				if (d0 >= 0.0D) {
					entity = entity1;
					vec3d = optional.orElse(pos);
					d0 = 0.0D;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d1 = optional.get();
				double d1 = pos.squareDistanceTo(vec3d1);
				if (d1 < d0 || d0 == 0.0D) {
					if (entity1.getLowestRidingEntity() == pl.getLowestRidingEntity() && !entity1.canRiderInteract()) {
						if (d0 == 0.0D) {
							entity = entity1;
							vec3d = vec3d1;
						}
					} else {
						entity = entity1;
						vec3d = vec3d1;
						d0 = d1;
					}
				}
			}
		}

		return entity == null ? null : new EntityRayTraceResult(entity, vec3d);
	}

	private static void setDire(PlayerEntity pl, float velo, Entity ent) {
		float yaw = pl.rotationYaw;
		float pitch = pl.rotationPitch;
		float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin(pitch * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		Vec3d vec3d = new Vec3d(f, f1, f2).normalize().scale(velo);
		ent.setMotion(vec3d);
		float f3 = MathHelper.sqrt(Entity.func_213296_b(vec3d));
		ent.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180F / (float) Math.PI));
		ent.rotationPitch = (float) (MathHelper.atan2(vec3d.y, f3) * (180F / (float) Math.PI));
		ent.prevRotationYaw = ent.rotationYaw;
		ent.prevRotationPitch = ent.rotationPitch;
	}

	public EntityCannon(Properties p, int damage) {
		super(p.maxDamage(damage));
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	public abstract float getVelocity(int charge);

	@Override
	public ActionResult<ItemStack> onItemRightClick(World w, PlayerEntity pl, Hand hand) {
		ItemStack is = pl.getHeldItem(hand);
		boolean flag = !pl.findAmmo(is).isEmpty() || pl.abilities.isCreativeMode;
		if (flag) {
			pl.setActiveHand(hand);
			return ActionResult.func_226249_b_(is);
		}
		return ActionResult.func_226251_d_(is);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack self, World w, LivingEntity user, int timeLeft) {
		if (user instanceof PlayerEntity) {
			PlayerEntity pl = (PlayerEntity) user;
			boolean flag = pl.abilities.isCreativeMode;
			ItemStack ammo = pl.findAmmo(self);
			int i = this.getUseDuration(self) - timeLeft;
			if (!ammo.isEmpty() || flag) {
				float f = getVelocity(i);
				if (f > 0.1) {
					if (!w.isRemote) {
						Entity e = getEntity(w, ammo, pl, f);
						if (e != null)
							w.addEntity(e);
						self.damageItem(1, pl, (player) -> player.sendBreakAnimation(pl.getActiveHand()));
					}
				}

				if (!pl.abilities.isCreativeMode) {
					ammo.shrink(1);
					if (ammo.isEmpty())
						pl.inventory.deleteStack(ammo);
				}

			}
		}
	}

	protected abstract Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo);

}
