package com.arthurlcy0x1.quantizedinformatics.entities;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiResult;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class QuanFly extends FlyingEntity implements IMob, QuanMob {

	public static class Model extends EntityModel<QuanFly> {
		private final ModelRenderer bone;
		private final ModelRenderer tail;
		private final ModelRenderer feet1;
		private final ModelRenderer feet2;
		private final ModelRenderer wings1;
		private final ModelRenderer wings2;
		private final ModelRenderer bone2;

		public Model() {
			textureWidth = 128;
			textureHeight = 128;

			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 24.0F, 0.0F);
			add(bone, 0, 0, -6.0F, -11.0F, 0.0F, 12, 9, 10, 0.0F, false);
			add(bone, 3, 7, -5.0F, -2.0F, 1.0F, 10, 1, 8, 0.0F, false);
			add(bone, 28, 28, -4.0F, -9.0F, 6.0F, 8, 7, 10, 0.0F, false);
			add(bone, 0, 38, -4.0F, -10.0F, -5.0F, 8, 7, 5, 0.0F, false);

			tail = new ModelRenderer(this);
			tail.setRotationPoint(0.0F, 24.0F, 0.0F);

			setRotationAngle(tail, -0.2618F, 0.0F, 0.0F);
			add(tail, 0, 19, -2.0F, -10.0F, 4.0F, 4, 4, 15, 0.0F, false);

			feet1 = new ModelRenderer(this);
			feet1.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(feet1, 0.0F, 0.0F, -0.4363F);
			add(feet1, 0, 0, 5.0F, -1.0F, 4.0F, 2, 4, 2, 0.0F, false);
			add(feet1, 0, 0, 5.0F, -1.0F, 7.0F, 2, 4, 2, 0.0F, false);
			add(feet1, 0, 0, 5.0F, -1.0F, 1.0F, 2, 4, 2, 0.0F, false);

			feet2 = new ModelRenderer(this);
			feet2.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(feet2, 0.0F, 0.0F, 0.4363F);

			add(feet2, 0, 0, -7.0F, -1.0F, 4.0F, 2, 4, 2, 0.0F, false);
			add(feet2, 0, 0, -7.0F, -1.0F, 7.0F, 2, 4, 2, 0.0F, false);
			add(feet2, 0, 0, -7.0F, -1.0F, 1.0F, 2, 4, 2, 0.0F, false);

			wings1 = new ModelRenderer(this);
			wings1.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(wings1, 0.0F, 0.0F, -0.4363F);
			add(wings1, 34, 0, 3.0F, -9.0F, 1.0F, 15, 1, 5, 0.0F, false);
			add(wings1, 34, 4, 3.0F, -9.0F, 6.0F, 11, 1, 3, 0.0F, false);

			wings2 = new ModelRenderer(this);
			wings2.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(wings2, 0.0F, 0.0F, 0.4363F);
			add(wings2, 34, 0, -18.0F, -9.0F, 1.0F, 15, 1, 5, 0.0F, false);
			add(wings2, 34, 5, -14.0F, -9.0F, 6.0F, 11, 1, 3, 0.0F, false);

			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
			add(bone2, 0, 19, -3.0F, -2.0F, -7.0F, 2, 4, 2, 0.0F, false);
			add(bone2, 0, 19, 1.0F, -2.0F, -7.0F, 2, 4, 2, 0.0F, false);
		}

		@Override
		public void render(MatrixStack mat, IVertexBuilder ver, int i0, int i1, float f0, float f1, float f2,
				float f3) {
			bone.render(mat, ver, i0, i1, f0, f1, f2, f3);
			tail.render(mat, ver, i0, i1, f0, f1, f2, f3);
			feet1.render(mat, ver, i0, i1, f0, f1, f2, f3);
			feet2.render(mat, ver, i0, i1, f0, f1, f2, f3);
			wings1.render(mat, ver, i0, i1, f0, f1, f2, f3);
			wings2.render(mat, ver, i0, i1, f0, f1, f2, f3);
			bone2.render(mat, ver, i0, i1, f0, f1, f2, f3);

		}

		@Override
		public void setRotationAngles(QuanFly ent, float swing, float amp, float age, float yaw, float pitch) {
			double freq = 1;
			wings1.rotateAngleZ = (float) (-0.4363f - 0.1f * Math.sin(age * freq));
			wings2.rotateAngleZ = (float) (0.4363f + 0.1f * Math.sin(age * freq));

		}

		private void add(ModelRenderer r, int u, int v, float x, float y, float z, int dx, int dy, int dz, float delta,
				boolean mirror) {
			r.addBox("", x, y, z, dx, dy, dz, delta, u, v);
		}

		private void setRotationAngle(ModelRenderer r, float x, float y, float z) {
			r.rotateAngleX = x;
			r.rotateAngleY = y;
			r.rotateAngleZ = z;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Renderer extends MobRenderer<QuanFly, Model> {

		private static final ResourceLocation TEXTURE = new ResourceLocation(Registrar.MODID,
				"textures/entity/quantum_fly.png");

		public Renderer(EntityRendererManager erm) {
			super(erm, new Model(), 0.5f);
		}

		@Override
		public ResourceLocation getEntityTexture(QuanFly entity) {
			return TEXTURE;
		}

	}

	private static class LookAroundGoal extends Goal {
		private final QuanFly parent;

		public LookAroundGoal(QuanFly mob) {
			this.parent = mob;
			this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		@Override
		public boolean shouldExecute() {
			return true;
		}

		@Override
		public void tick() {
			if (parent.getAttackTarget() == null) {
				Vec3d vec3d = parent.getMotion();
				parent.rotationYaw = (float) (-MathHelper.atan2(vec3d.x, vec3d.z) * 180 / Math.PI);
				parent.renderYawOffset = parent.rotationYaw;
			} else {
				LivingEntity livingentity = parent.getAttackTarget();
				if (livingentity.getDistanceSq(parent) < 4096.0D) {
					double d1 = livingentity.getPosX() - parent.getPosX();
					double d2 = livingentity.getPosZ() - parent.getPosZ();
					parent.rotationYaw = (float) (-MathHelper.atan2(d1, d2) * 180 / Math.PI);
					parent.renderYawOffset = parent.rotationYaw;
				}
			}

		}
	}

	private static class MoveCtrl extends MovementController {

		private QuanFly parent;
		private int courseChangeCooldown;

		public MoveCtrl(QuanFly mob) {
			super(mob);
			parent = mob;
		}

		@Override
		public void tick() {
			if (action == MovementController.Action.MOVE_TO) {
				if (courseChangeCooldown-- <= 0) {
					courseChangeCooldown += parent.getRNG().nextInt(5) + 2;
					Vec3d vec3d = new Vec3d(posX - parent.getPosX(), posY - parent.getPosY(), posZ - parent.getPosZ());
					double d0 = vec3d.length();
					vec3d = vec3d.normalize();
					if (func_220673_a(vec3d, MathHelper.ceil(d0)))
						parent.setMotion(parent.getMotion().add(vec3d.scale(0.5D)));
					else
						action = MovementController.Action.WAIT;
				}

			}
		}

		private boolean func_220673_a(Vec3d pos, int len) {
			AxisAlignedBB aabb = parent.getBoundingBox();
			for (int i = 1; i < len; ++i) {
				aabb = aabb.offset(pos);
				if (!parent.world.hasNoCollisions(parent, aabb))
					return false;
			}
			return true;
		}

	}

	private static class NearGoal<T extends LivingEntity> extends NearestGoal<T> {

		public NearGoal(QuanFly mob, Class<T> cls) {
			super(mob, cls);
		}

		public NearGoal(QuanFly mob, Class<T> cls, Predicate<LivingEntity> pred) {
			super(mob, cls, pred);
		}

		@Override
		protected AxisAlignedBB getTargetableArea(double tar) {
			return goalOwner.getBoundingBox().grow(tar, tar, tar);
		}

	}

	private static class RandomFlyGoal extends Goal {
		private final QuanFly parent;

		public RandomFlyGoal(QuanFly mob) {
			parent = mob;
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public boolean shouldExecute() {
			MovementController ctrl = parent.getMoveHelper();
			if (!ctrl.isUpdating()) {
				return true;
			} else {
				double d0 = ctrl.getX() - parent.getPosX();
				double d1 = ctrl.getY() - parent.getPosY();
				double d2 = ctrl.getZ() - parent.getPosZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				return d3 < 1 || d3 > 3600;
			}
		}

		@Override
		public void startExecuting() {
			Random random = parent.getRNG();
			LivingEntity e = parent.getAttackTarget();
			Vec3d vec = new Vec3d(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5);
			vec = vec.normalize();
			if (e != null) {
				Vec3d p0 = e.getPositionVec().subtract(parent.getPositionVec());
				if (p0.length() < 20)
					vec = vec.add(p0.normalize());
				else if (p0.length() > 40)
					vec = vec.subtract(p0.normalize());
				vec = vec.normalize();
			}
			double d0 = parent.getPosX() + vec.getX() * 10;
			double d1 = parent.getPosY() + vec.getY() * 10;
			double d2 = parent.getPosZ() + vec.getZ() * 10;
			parent.getMoveHelper().setMoveTo(d0, d1, d2, 1);
		}
	}

	private static class TNTAttackGoal extends Goal {
		private final QuanFly parent;
		public int attackTimer;

		public TNTAttackGoal(QuanFly ent) {
			parent = ent;
		}

		@Override
		public void resetTask() {
			// TODO parent.setAttacking(false);
		}

		@Override
		public boolean shouldExecute() {
			return parent.getAttackTarget() != null;
		}

		@Override
		public void startExecuting() {
			this.attackTimer = 0;
		}

		@Override
		public void tick() {
			LivingEntity pl = parent.getAttackTarget();
			if (pl.getDistanceSq(parent) < 4096 && parent.canEntityBeSeen(pl)) {
				World world = parent.world;
				++this.attackTimer;
				if (this.attackTimer == 20) {
					Vec3d vec3d = parent.getLook(1.0F);
					double d2 = parent.getPosX() + vec3d.x * 1;
					double d3 = parent.getPosYHeight(0.5);
					double d4 = parent.getPosZ() + vec3d.z * 1;
					TNTEntity te = new TNTEntity(world, d2, d3, d4, parent);
					Vec3d p0 = te.getPositionVec();
					Vec3d p1 = pl.getPositionVec();
					Vec3d pv = pl.getMotion();
					EstiResult er = new Estimator(0.04, 0.02, p0, 3, 80, p1, pv).getAnswer();
					if (er.getType() == EstiType.ZERO) {
						te.setMotion(er.getVec());
						te.setFuse((int) Math.round(er.getT()));
					} else {
						te.setMotion(p1.subtract(p0).normalize().scale(3));
						te.setFuse(80);
					}
					world.addEntity(te);
					this.attackTimer = -40;
				}
			} else if (this.attackTimer > 0) {
				--this.attackTimer;
			}
		}
	}

	public QuanFly(EntityType<? extends QuanFly> type, World w) {
		super(type, w);
		moveController = new MoveCtrl(this);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source))
			return false;
		if (amount < 5)
			return false;
		return super.attackEntityFrom(source, amount);

	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource ds) {
		if (ds.isExplosion())
			return true;
		if (ds.getTrueSource() instanceof QuanMob)
			return true;
		return super.isInvulnerableTo(ds);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(5, new RandomFlyGoal(this));
		goalSelector.addGoal(7, new LookAroundGoal(this));
		goalSelector.addGoal(7, new TNTAttackGoal(this));
		targetSelector.addGoal(1, new NearGoal<>(this, PlayerEntity.class));
		targetSelector.addGoal(2, new NearGoal<>(this, LivingEntity.class, e -> e instanceof IMob));
		targetSelector.addGoal(3, new NearGoal<>(this, LivingEntity.class));
	}

}
