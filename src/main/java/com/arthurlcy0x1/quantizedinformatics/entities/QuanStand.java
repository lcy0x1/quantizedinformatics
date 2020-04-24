package com.arthurlcy0x1.quantizedinformatics.entities;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.EnumSet;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class QuanStand extends MonsterEntity implements IMob, QuanMob {

	public static class Model extends EntityModel<QuanStand> {
		private final ModelRenderer head;
		private final ModelRenderer leftarm;
		private final ModelRenderer body;
		private final ModelRenderer tail;
		private final ModelRenderer rightarm;

		public Model() {
			textureWidth = 64;
			textureHeight = 64;

			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, 4.0F, 0.0F);
			add(head, 0, 0, -4.0F, -7.0F, -4.0F, 8, 8, 8, 0.0F, false);

			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 24.0F, 0.0F);
			add(body, 0, 16, -4.0F, -22.0F, -2.0F, 8, 9, 4, 0.0F, false);

			tail = new ModelRenderer(this);
			tail.setRotationPoint(0.0F, 24.0F, 0.0F);
			setRotationAngle(tail, 0.4363F, 0.0F, 0.0F);
			add(tail, 0, 17, -3.0F, -13.0F, 4.0F, 6, 8, 4, 0.0F, false);

			rightarm = new ModelRenderer(this);
			rightarm.setRotationPoint(6.0F, 5.0F, 1.0F);
			add(rightarm, 16, 16, -2.0F, -3.0F, -2.0F, 4, 22, 4, 0.0F, false);

			leftarm = new ModelRenderer(this);
			leftarm.setRotationPoint(-6.0F, 4.0F, 0.0F);
			add(leftarm, 0, 16, -2.0F, -2.0F, -2.0F, 4, 22, 4, 0.0F, false);
		}

		@Override
		public void render(MatrixStack mat, IVertexBuilder ver, int i0, int i1, float f0, float f1, float f2,
				float f3) {
			body.render(mat, ver, i0, i1, f0, f1, f2, f3);
			head.render(mat, ver, i0, i1, f0, f1, f2, f3);
			tail.render(mat, ver, i0, i1, f0, f1, f2, f3);
			leftarm.render(mat, ver, i0, i1, f0, f1, f2, f3);
			rightarm.render(mat, ver, i0, i1, f0, f1, f2, f3);

		}

		@Override
		public void setRotationAngles(QuanStand ent, float swing, float amp, float age, float yaw, float pitch) {
			head.rotateAngleZ = (float) (amp * Math.sin(swing));
			leftarm.rotateAngleX = (float) (amp * Math.sin(swing));
			rightarm.rotateAngleX = (float) (-amp * Math.sin(swing));
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
	public static class Renderer extends MobRenderer<QuanStand, Model> {

		private static final ResourceLocation TEXTURE = new ResourceLocation(Registrar.MODID,
				"textures/entity/quantum_stand.png");

		public Renderer(EntityRendererManager erm) {
			super(erm, new Model(), 0.3f);
		}

		@Override
		public ResourceLocation getEntityTexture(QuanStand entity) {
			return TEXTURE;
		}

	}

	private static class AttackGoal extends MeleeAttackGoal {

		public AttackGoal(QuanStand mob) {
			super(mob, 1, false);
		}

		@Override
		protected double getAttackReachSqr(LivingEntity tar) {
			return 4 + tar.getWidth();
		}

	}

	private static class LookGoal extends Goal {
		private final QuanStand parent;

		public LookGoal(QuanStand mob) {
			parent = mob;
			setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		@Override
		public boolean shouldExecute() {
			return true;
		}

		@Override
		public void tick() {
			Vec3d vec3d = parent.getMotion().mul(1, 0, 1);
			LivingEntity tar = parent.getAttackTarget();
			if (tar != null && parent.onGround && vec3d.length() < 0.01) {
				double d1 = tar.getPosX() - parent.getPosX();
				double d2 = tar.getPosZ() - parent.getPosZ();
				parent.rotationYaw = (float) (-MathHelper.atan2(d1, d2) * 180 / Math.PI);
			}
			parent.renderYawOffset = parent.rotationYaw;
		}

	}

	private boolean genDrop = false;

	public QuanStand(EntityType<? extends QuanStand> type, World w) {
		super(type, w);
	}

	@Override
	public boolean attackEntityAsMob(Entity e) {
		if (e instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e;
			le.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 600, 4));
		}
		return super.attackEntityAsMob(e);
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
	public boolean isInvulnerableTo(DamageSource ds) {
		if (ds.isMagicDamage())
			return true;
		if (ds.getTrueSource() instanceof QuanMob)
			return true;
		return super.isInvulnerableTo(ds);
	}

	@Override
	public void onDeathUpdate() {
		super.onDeathUpdate();
		if (!genDrop && !isAlive() && !world.isRemote) {
			BlockPos p = QuanMob.genDrop(world, getPosition(), 2, 1);
			if (p == null)
				p = getPosition();
			Direction fac = Direction.fromAngle(rotationYaw);
			BlockState bs = Registrar.B_STAND.getDefaultState().with(BaseBlock.HORIZONTAL_FACING, fac);
			world.setBlockState(p, bs);
			genDrop = true;
		}
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80);
		getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10);
		getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(2);
		getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2);
		getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20);
		getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(8);
		getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
		getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new AttackGoal(this));
		goalSelector.addGoal(2, new LookGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestGoal<>(this, PlayerEntity.class));
		targetSelector.addGoal(3, new NearestGoal<>(this, LivingEntity.class, e -> e instanceof IMob));
		targetSelector.addGoal(4, new NearestGoal<>(this, LivingEntity.class));
	}

}
