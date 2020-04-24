package com.arthurlcy0x1.quantizedinformatics.items.battle;

import static com.arthurlcy0x1.quantizedinformatics.items.battle.Telescope.addInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.ItemUtil;
import com.arthurlcy0x1.quantizedinformatics.items.battle.IMaxwell.IMaxRepairable;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiResult;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiType;
import com.arthurlcy0x1.quantizedinformatics.world.RegWorld;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class EntityCannon extends ShootableItem implements Telescope, IMaxRepairable {

	public static class ArrowEC extends AbArrowEC {

		public ArrowEC(Properties p) {
			super(p);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return e -> e.getItem() instanceof ArrowItem;
		}

		@Override
		protected AbstractArrowEntity getEntity(World w, PlayerEntity pl, ItemStack ammo) {
			ammo = ammo == null ? new ItemStack(Items.ARROW) : ammo;
			return ((ArrowItem) ammo.getItem()).createArrow(w, ammo, pl);
		}

	}

	public static class DefEC extends AbThrowableEC {

		private final Item item;
		private final BiFunction<World, PlayerEntity, ThrowableEntity> gen;

		public DefEC(Properties p, Item i, BiFunction<World, PlayerEntity, ThrowableEntity> bf) {
			super(p, 0.03, 0.01, 128, 240);
			item = i;
			gen = bf;
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return is -> is.getItem() == item;
		}

		@Override
		protected ThrowableEntity getEntity(World w, PlayerEntity pl, ItemStack ammo) {
			return gen.apply(w, pl);
		}

	}

	public static class FogBall extends ProjectileItemEntity {

		public FogBall(EntityType<FogBall> e, World w) {
			super(e, w);
		}

		public FogBall(World w, double x, double y, double z) {
			super(Registrar.ET_FB, w);
			this.setPosition(x, y, z);
			this.prevPosX = x;
			this.prevPosY = y;
			this.prevPosZ = z;
		}

		public FogBall(World w, PlayerEntity pl) {
			this(w, getPlX(pl), getPlY(pl), getPlZ(pl));
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		protected Item getDefaultItem() {
			return Registrar.I_FOGBALL;
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			final BlockPos pos;
			if (result.getType() == Type.BLOCK) {
				BlockRayTraceResult b = (BlockRayTraceResult) result;
				pos = b.getPos().offset(b.getFace(), 3);
			} else if (result.getType() == Type.ENTITY) {
				EntityRayTraceResult b = (EntityRayTraceResult) result;
				pos = new BlockPos(b.getHitVec());
			} else
				pos = null;
			if (pos != null && !world.isRemote)
				addThings(pos);
			remove();
		}

		private void addThings(BlockPos pos) {
			if (RegWorld.isQuantumWorld(world))
				return;
			int max = 3, rad = 4;
			Direction[] ds = Direction.values();
			for (int i = 0; i < 6; i++) {
				for (int j = 1; j <= max; j++)
					set(pos.offset(ds[i], j));
				for (int j = i + 1; j < 6; j++)
					if (ds[j] != ds[i].getOpposite())
						for (int k = 1; k <= max; k++)
							for (int s = 1; s <= max; s++)
								if (k + s <= rad)
									set(pos.offset(ds[i], k).offset(ds[j], s));
			}

			world.setBlockState(pos, Registrar.B_FOGORE.getDefaultState());
		}

		private void set(BlockPos p) {
			if (world.getBlockState(p).getBlock() == Blocks.AIR)
				world.setBlockState(p, Registrar.B_FOG.getDefaultState());
		}

	}

	public static class ItemPicker extends ProjectileItemEntity {

		private static boolean isValid(Entity e) {
			EntityType<?> type = e.getType();
			return type == EntityType.EXPERIENCE_ORB || type == EntityType.ITEM;
		}

		public ItemPicker(EntityType<ItemPicker> e, World w) {
			super(e, w);
		}

		public ItemPicker(World w, double x, double y, double z, PlayerEntity pe) {
			super(Registrar.ET_IP, w);
			this.setPosition(x, y, z);
			this.prevPosX = x;
			this.prevPosY = y;
			this.prevPosZ = z;
			owner = pe;
		}

		public ItemPicker(World w, PlayerEntity pl) {
			this(w, getPlX(pl), getPlY(pl), getPlZ(pl), pl);
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		protected Item getDefaultItem() {
			return Registrar.I_ITEMPICK;
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			Vec3d hit = result.getHitVec();
			double r = getRadius();
			if (!world.isRemote && getThrower() != null && (getThrower() instanceof PlayerEntity)) {
				PlayerEntity pl = (PlayerEntity) getThrower();
				AxisAlignedBB aabb = new AxisAlignedBB(hit.add(-r, -r, -r), hit.add(r, r, r));
				List<Entity> list = world.getEntitiesInAABBexcluding(this, aabb, ItemPicker::isValid);
				for (Entity e : list)
					if (e.getPositionVec().squareDistanceTo(hit) < r * r)
						e.setPosition(getPlX(pl), getPlY(pl), getPlZ(pl));

			}
			remove();
		}

		private double getRadius() {
			return 8;
		}

	}

	public static class PotionEC extends AbThrowableEC {

		public PotionEC(Properties p) {
			super(p, 0.05, 0.01, 128, 120);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return is -> is.getItem() instanceof ThrowablePotionItem;
		}

		@Override
		protected ThrowableEntity getEntity(World w, PlayerEntity pl, ItemStack ammo) {
			if (ammo.isEmpty() || !(ammo.getItem() instanceof ThrowablePotionItem))
				return null;
			PotionEntity pe = new PotionEntity(w, pl);
			pe.setItem(ammo);
			return pe;
		}

	}

	public static class SmartTNT extends TNTEntity {

		private final LivingEntity tntPlacedBy;
		private final float radius;

		public SmartTNT(EntityType<SmartTNT> e, World w) {
			super(e, w);
			tntPlacedBy = null;
			radius = 0;
		}

		public SmartTNT(World w, double x, double y, double z, LivingEntity pl, ItemStack is) {
			super(Registrar.ET_STNT, w);
			this.setPosition(x, y, z);
			this.setFuse(80);
			this.prevPosX = x;
			this.prevPosY = y;
			this.prevPosZ = z;
			this.tntPlacedBy = pl;
			radius = 4 << MaxwellItem.getLevel(is);
		}

		@Override
		public boolean canExplosionDestroyBlock(Explosion e, IBlockReader w, BlockPos p, BlockState s, float f) {
			return false;
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		public void explode() {
			if (getRadius() <= 0)
				return;
			double x = getPosX();
			double y = getPosYHeight(0.625);
			double z = getPosZ();
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
		public LivingEntity getTntPlacedBy() {
			return tntPlacedBy;
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class SmartTNTRender extends EntityRenderer<SmartTNT> {

		public SmartTNTRender(EntityRendererManager renderManagerIn) {
			super(renderManagerIn);
			this.shadowSize = 0.5F;
		}

		@Override
		@SuppressWarnings("deprecation")
		public ResourceLocation getEntityTexture(SmartTNT entity) {
			return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
		}

		@Override
		public void render(SmartTNT p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_,
				IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
			p_225623_4_.push();
			p_225623_4_.translate(0.0D, 0.5D, 0.0D);
			if (p_225623_1_.getFuse() - p_225623_3_ + 1.0F < 10.0F) {
				float f = 1.0F - (p_225623_1_.getFuse() - p_225623_3_ + 1.0F) / 10.0F;
				f = MathHelper.clamp(f, 0.0F, 1.0F);
				f = f * f;
				f = f * f;
				float f1 = 1.0F + f * 0.3F;
				p_225623_4_.scale(f1, f1, f1);
			}

			p_225623_4_.rotate(Vector3f.YP.rotationDegrees(-90.0F));
			p_225623_4_.translate(-0.5D, -0.5D, 0.5D);
			p_225623_4_.rotate(Vector3f.YP.rotationDegrees(90.0F));
			TNTMinecartRenderer.renderTntFlash(Blocks.TNT.getDefaultState(), p_225623_4_, p_225623_5_, p_225623_6_,
					p_225623_1_.getFuse() / 5 % 2 == 0);
			p_225623_4_.pop();
			super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
		}
	}

	public static class TNTEC extends EntityCannon {

		public TNTEC(Properties p) {
			super(p);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return is -> is.getItem() == Items.TNT || is.getItem() == Registrar.IMU_TNT;
		}

		/** gravity: 0.04, resistance: 0.02 */
		@Override
		public float getVelocity(int charge) {
			return MathHelper.clamp(charge / 20f, 0f, 1f) * 3;
		}

		@Override
		protected Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo) {
			final TNTEntity e;
			if (ammo.getItem() == Registrar.IMU_TNT)
				e = new SmartTNT(w, getPlX(pl), getPlY(pl), getPlZ(pl), pl, ammo);
			else
				e = new TNTEntity(w, getPlX(pl), getPlY(pl), getPlZ(pl), pl);
			EstiResult er = setAim(pl, velo, 128, e, 0.04, 0.02, 80);
			if (er.getType() == EstiType.ZERO) {
				e.setMotion(er.getVec());
				e.setFuse((int) Math.round(er.getT()));
			} else if (er.getType() == EstiType.FAIL)
				setDire(pl, velo, e);
			else
				return null;
			return e;
		}

	}

	public static class TridentEC extends AbArrowEC {

		public TridentEC(Properties p) {
			super(p);
		}

		@Override
		public Predicate<ItemStack> getInventoryAmmoPredicate() {
			return e -> e.getItem() == Items.TRIDENT;
		}

		@Override
		protected AbstractArrowEntity getEntity(World w, PlayerEntity pl, ItemStack ammo) {
			return new TridentEntity(w, pl, ammo == null ? new ItemStack(Items.TRIDENT) : ammo);
		}

	}

	private static abstract class AbArrowEC extends EntityCannon {

		public AbArrowEC(Properties p) {
			super(p);
		}

		@Override
		public float getVelocity(int charge) {
			return MathHelper.clamp(charge / 20f, 0f, 1f) * 3;
		}

		@Override
		protected Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo) {
			AbstractArrowEntity e = getEntity(w, pl, ammo);
			if (e == null)
				return null;
			EstiResult er = setAim(pl, velo, 128, e, 0.05, 0.01, 120);
			if (er.getType() == EstiType.ZERO)
				e.setMotion(er.getVec());
			else if (er.getType() == EstiType.FAIL)
				e.shoot(pl, pl.rotationPitch, pl.rotationYaw, 0, velo, 0);
			else
				return null;
			return e;
		}

		protected abstract AbstractArrowEntity getEntity(World w, PlayerEntity pl, ItemStack ammo);

	}

	private static abstract class AbThrowableEC extends EntityCannon {

		private final double g, k;
		private final int r, t;

		public AbThrowableEC(Properties p, double G, double K, int R, int T) {
			super(p);
			g = G;
			k = K;
			r = R;
			t = T;
		}

		@Override
		public float getVelocity(int charge) {
			return MathHelper.clamp(charge / 20f, 0f, 1f) * 3;
		}

		@Override
		protected Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo) {
			ThrowableEntity e = getEntity(w, pl, ammo);
			if (e == null)
				return null;
			EstiResult er = setAim(pl, velo, r, e, g, k, t);
			if (er.getType() == EstiType.ZERO)
				e.setMotion(er.getVec());
			else if (er.getType() == EstiType.FAIL)
				e.shoot(pl, pl.rotationPitch, pl.rotationYaw, 0, velo, 0);
			else
				return null;
			return e;
		}

		protected abstract ThrowableEntity getEntity(World w, PlayerEntity pl, ItemStack ammo);

	}

	public static final int DAMAGE = MaxwellItem.VALUE * 2;

	private static double getPlX(PlayerEntity pl) {
		return pl.getPosX();
	}

	private static double getPlY(PlayerEntity pl) {
		return pl.getPosYEye() - 0.1;
	}

	private static double getPlZ(PlayerEntity pl) {
		return pl.getPosZ();
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

		for (Entity e : world.getEntitiesWithinAABBExcludingEntity(pl, box)) {
			AxisAlignedBB aabb = e.getBoundingBox().grow(e.getCollisionBorderSize());
			Optional<Vec3d> optional = aabb.rayTrace(pos, end);
			if (aabb.contains(pos)) {
				if (d0 >= 0.0D) {
					entity = e;
					vec3d = optional.orElse(pos);
					d0 = 0.0D;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d1 = optional.get();
				double d1 = pos.squareDistanceTo(vec3d1);
				if (d1 < d0 || d0 == 0.0D) {
					if (e.getLowestRidingEntity() == pl.getLowestRidingEntity() && !e.canRiderInteract()) {
						if (d0 == 0.0D) {
							entity = e;
							vec3d = vec3d1;
						}
					} else {
						entity = e;
						vec3d = vec3d1;
						d0 = d1;
					}
				}
			}
		}

		return entity == null ? null : new EntityRayTraceResult(entity, vec3d);
	}

	private static EstiResult setAim(PlayerEntity pl, double velo, int reach, Entity e, double g, double k, int maxt) {
		EntityRayTraceResult ertr = rayTraceEntity(pl, reach);
		if (ertr != null && ertr.getType() == Type.ENTITY) {
			if (ertr.getHitVec().distanceTo(pl.getPositionVec()) < velo)
				return EstiType.CLOSE;
			LogManager.getLogger().info("targeting entity: " + ertr.getEntity().getEntityString());
			Vec3d mot = ertr.getEntity().getMotion();
			Vec3d tar = ertr.getHitVec();
			Vec3d pos = e.getPositionVec();
			Estimator.EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, mot).getAnswer();
			LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
			if (er.getType() == EstiType.ZERO)
				return er;
		}
		BlockRayTraceResult brtr = rayTraceBlock(pl.world, pl, reach);
		if (brtr != null && brtr.getType() == Type.BLOCK) {
			if (brtr.getHitVec().distanceTo(pl.getPositionVec()) < velo)
				return EstiType.CLOSE;
			LogManager.getLogger().info("targeting block: " + brtr.getPos());
			Vec3d tar = brtr.getHitVec();
			Vec3d pos = e.getPositionVec();
			Estimator.EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, Vec3d.ZERO).getAnswer();
			LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
			if (er.getType() == EstiType.ZERO)
				return er;
		}
		return EstiType.FAIL;
	}

	private static void setDire(PlayerEntity pl, float velo, Entity ent) {
		float yaw = pl.rotationYaw;
		float pitch = pl.rotationPitch;
		float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		float f1 = -MathHelper.sin(pitch * ((float) Math.PI / 180F));
		float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
		Vec3d vec3d = new Vec3d(f, f1, f2).normalize().scale(velo);
		ent.setMotion(vec3d);
		float f3 = MathHelper.sqrt(Entity.horizontalMag(vec3d));
		ent.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180F / (float) Math.PI));
		ent.rotationPitch = (float) (MathHelper.atan2(vec3d.y, f3) * (180F / (float) Math.PI));
		ent.prevRotationYaw = ent.rotationYaw;
		ent.prevRotationPitch = ent.rotationPitch;
	}

	public EntityCannon(Properties p) {
		super(p.maxDamage(DAMAGE));
	}

	@Override
	public boolean canUse(ItemStack is) {
		return is.getItem() == Registrar.IMU_FIX || is.getItem() == Registrar.IMU_ATK && MaxwellItem.getLevel(is) == 2;
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return super.getDisplayName(stack).deepCopy().appendText(", ").appendSibling(addInfo(stack));
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
			return ActionResult.resultSuccess(is);
		}
		return ActionResult.resultFail(is);
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
					Entity e = getEntity(w, ammo, pl, f);
					if (!w.isRemote && e != null) {
						w.addEntity(e);
						ItemUtil.damageItem(self, pl);
					}
					if (!flag && e != null)
						ItemUtil.consume(self, ammo, 1, pl);
				}

			}
		}
	}

	@Override
	public int repair(ItemStack is) {
		return canUse(is) ? MaxwellItem.VALUE : 0;
	}

	protected abstract Entity getEntity(World w, ItemStack ammo, PlayerEntity pl, float velo);

}
