package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.Random;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.MaxwellItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class EntSpawn extends EntMachine {

	public static class Cont extends CTEBlock.CTECont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(7));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTME_SPA, id, inv, ent, 74);
			addSlot(new CondsSlot(ent, 0, 62, 17, EntSpawn::isValid, 1));
			addSlot(new CondsSlot(ent, 1, 80, 17, EntSpawn::isValid, 1));
			addSlot(new CondsSlot(ent, 2, 98, 17, EntSpawn::isValid, 1));
			addSlot(new CondsSlot(ent, 3, 134, 17, EntSpawn::isValid, 1));
			trackIntArray(data = arr);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/ent_spawn.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 156);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			super.drawGuiContainerForegroundLayer(mouseX, mouseY);
			if (!container.getSlot(36).getHasStack() || !container.getSlot(37).getHasStack())
				return;
			IIntArray data = container.data;
			String s0 = Translator.getContText("ent_machine.progress") + data.get(0) + "/" + data.get(1);
			String s1 = Translator.getContText("ent_machine.radius") + container.data.get(2);
			String s2 = Translator.getContText("ent_machine.limit") + container.data.get(3);
			String s3 = Translator.getContText("ent_machine.success") + container.data.get(5) + '/' + data.get(6);
			font.drawString(s0, 8, 36, COLOR);
			font.drawString(s2, ySize / 2 + 20, 36, COLOR);
			font.drawString(s3, 8, 49, COLOR);
			font.drawString(s1, ySize / 2 + 20, 49, COLOR);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, ITickableTileEntity {

		private boolean dirty = true;

		private int prog = 0, max_prog = 0, range = 0, max = 0, exc = 0;
		private int success = 0, count = 0, in_range = 0;

		public TE() {
			super(Registrar.TETME_SPA, SIZE);
		}

		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getInt("te.progress");
			success = tag.getInt("te.success");
			count = tag.getInt("te.count");
		}

		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("te.progress", prog);
			tag.putInt("te.count", count);
			tag.putInt("te.success", success);
			return tag;
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pe) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("ent_spawn");
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return isValid(slot, is);
		}

		@Override
		public void tick() {
			boolean lit = getBlockState().get(LIT);
			if (world.isRemote)
				return;
			ItemStack is0 = getStackInSlot(0);
			ItemStack is1 = getStackInSlot(1);
			if (dirty) {
				max_prog = range = max = -1;
				exc = 0;
				if (!is0.isEmpty() && !is1.isEmpty()) {
					int atk = MaxwellItem.getLevel(is0);
					int def = MaxwellItem.getLevel(is1);
					int min = Math.min(atk, def);
					if (min >= 2) {
						min = Math.min(min, MAX_TPOW) - 2;
						max_prog = 1 << MAX_TIME - 3 * min;
					}
					int nax = Math.max(0, atk - def) + Math.max(0, MAX_TPOW - min);
					int rng = Math.max(0, def - atk) + Math.max(0, MAX_TPOW - min);
					max = 1 << Math.min(nax, MAX_MPOW);
					if (nax > MAX_MPOW)
						rng += nax - MAX_MPOW;
					range = 1 << MAX_RANGE - Math.min(rng, MAX_RPOW);
					if (rng > MAX_RPOW)
						exc = rng - MAX_RPOW;
				}
			}
			dirty = false;
			boolean pow = world.isBlockPowered(pos);
			pow &= !is0.isEmpty() && !is1.isEmpty() && max > 0 && max_prog > 0 && range > 0;
			pow &= !getStackInSlot(2).isEmpty();
			String str = getStackInSlot(2).getOrCreateTag().getString("soul");
			EntityType<?> et = str == "" ? null : ForgeRegistries.ENTITIES.getValue(new ResourceLocation(str));
			pow &= et != null;
			if (pow != lit)
				world.setBlockState(pos, getBlockState().with(LIT, pow));
			lit = pow;
			if (!lit)
				return;
			if (this.prog < max_prog) {
				prog++;
				return;
			}
			prog -= prog / 8;
			count++;
			in_range = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).grow(range)).size();
			if (in_range >= max)
				return;
			Random r = world.getRandom();
			SpawnReason reason = SpawnReason.SPAWNER;
			double d0 = pos.getX() + 0.5 + (r.nextDouble() * 2 - 1) * range;
			double d1 = pos.getY() + 0.5 + (r.nextDouble() * 2 - 1) * range;
			double d2 = pos.getZ() + 0.5 + (r.nextDouble() * 2 - 1) * range;
			if (!world.func_226664_a_(et.func_220328_a(d0, d1, d2)))
				return;
			if (!EntitySpawnPlacementRegistry.func_223515_a(et, world, reason, new BlockPos(d0, d1, d2), r))
				return;
			Entity ent = et.create(world);
			ent.setLocationAndAngles(d0, d1, d2, world.rand.nextFloat() * 360, 0);
			if (ent instanceof MobEntity) {
				MobEntity mob = (MobEntity) ent;
				mob.onInitialSpawn(world, world.getDifficultyForLocation(pos), reason, null, null);
			}
			world.addEntity(ent);
			world.playEvent(2004, pos, 0);
			prog = 0;
			success++;
		}

		@Override
		public int get(int index) {
			if (index == 0)
				return prog;
			if (index == 1)
				return max_prog;
			if (index == 2)
				return range;
			if (index == 3)
				return max;
			if (index == 4)
				return exc;
			if (index == 5)
				return count;
			if (index == 6)
				return success;
			return 0;
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return 7;
		}

		protected void onChange(int ind) {
			dirty = true;
		}

	}

	private static final int SIZE = 4, MAX_TIME = 10, MAX_RANGE = 4, MAX_TPOW = 5, MAX_MPOW = 4, MAX_RPOW = 2;

	private static boolean isValid(int ind, ItemStack is) {
		if (ind == 0)
			return is.getItem() == Registrar.IMU_ATK;
		if (ind == 1)
			return is.getItem() == Registrar.IMU_DEF;
		if (ind == 2)
			return is.getItem() == Registrar.IS_TRAP;
		return false;
	}

	public EntSpawn() {
		super(TE::new);
	}

}
