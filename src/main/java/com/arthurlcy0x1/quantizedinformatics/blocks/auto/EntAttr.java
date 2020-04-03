package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.battle.SoulItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntAttr extends EntMachine {

	public static class Cont extends CTEBlock.CTECont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(2));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTME_ATR, id, inv, ent, 61);
			for (int i = 0; i < 4; i++)
				addSlot(new CondsSlot(ent, i, 8 + i * 18, 17, EntAttr::isValid, 1));
			for (int i = 4; i < SIZE; i++)
				addSlot(new ResultSlot(ent, i, 8 + i * 18, 17));
			trackIntArray(data = arr);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/ent_attract.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 143);
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
			String s0 = Translator.getContText("ent_machine.radius") + container.data.get(0);
			int spe = container.data.get(1);
			boolean col = spe > MAX_SPEED;
			spe = Math.min(spe, MAX_SPEED);
			String s1 = Translator.getContText("ent_machine.power") + (1 << 3 * spe);
			font.drawString(s0, 8, 36, COLOR);
			font.drawString(s1, ySize / 2, 36, col ? RED : COLOR);
		}
	}

	public static class TE extends EntMachine.EMTE<TE> implements ISidedInventory {

		private static final int[] UP = { 3 }, OTHER = { 4, 5, 6, 7, 8 };

		public TE() {
			super(Registrar.TETME_ATR, SIZE, EntMachine.TYPE_DEF);
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
			return index >= 4;
		}

		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
			return index == 3 && direction == Direction.UP && isValid(3, itemStackIn);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pe) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("ent_attract");
		}

		public double getMaxVec() {
			return SPEED_FAC * (1 << 3 * Math.min(MAX_SPEED, getPower()));
		}

		@Override
		public int[] getSlotsForFace(Direction side) {
			return side == Direction.UP ? UP : OTHER;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack is) {
			return isValid(slot, is);
		}

		@Override
		public void tick() {
			super.tick();
			if (world.isRemote)
				return;
			AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(2);
			for (Entity e : world.getEntitiesInAABBexcluding(null, aabb,
					a -> a instanceof ItemEntity || a instanceof ExperienceOrbEntity)) {
				if (e instanceof ItemEntity) {
					ItemEntity ie = (ItemEntity) e;
					ItemStack is = ie.getItem();
					for (int i = 4; i < SIZE; i++) {
						ItemStack in = getStackInSlot(i);
						if (in.isEmpty()) {
							setInventorySlotContents(i, is);
							is = null;
							break;
						} else if (Container.areItemsAndTagsEqual(is, in)) {
							int diff = in.getMaxStackSize() - in.getCount();
							if (diff >= is.getCount()) {
								in.grow(is.getCount());
								markDirty();
								is = null;
								break;
							} else {
								is.shrink(diff);
								in.grow(diff);
								markDirty();
							}
						}
					}
					if (is == null)
						ie.remove();
					else
						ie.setItem(is);
				} else if (e instanceof ExperienceOrbEntity) {
					ItemStack is = getStackInSlot(3);
					if (!is.isEmpty()) {
						e.remove();
						int exp = ((ExperienceOrbEntity) e).getXpValue();
						setInventorySlotContents(3, SoulItem.SoulBottle.addExp(is, exp));
					}
				}
			}
		}

		@Override
		protected void handle(Entity e, Vec3d dir) {
			double r = getRadius();
			double dis = dir.length();
			if (dis > 0.4) {
				dir = dir.scale(getMaxVec() * (dis - r) / dis / r);
				e.addVelocity(dir.x, dir.y, dir.z);
			}
		}

	}

	private static final int SIZE = 9, MAX_SPEED = 2;

	private static final double SPEED_FAC = 0.1;

	private static boolean isValid(int ind, ItemStack is) {
		if (ind == 0)
			return is.getItem() == Registrar.IMU_ATK;
		if (ind == 1)
			return is.getItem() == Registrar.IMU_DEF;
		if (ind == 2)
			return is.getItem() == Registrar.IS_MARKER;
		if (ind == 3)
			return is.getItem() == Registrar.IS_EXP || is.getItem() == Items.GLASS_BOTTLE;
		return true;
	}

	public EntAttr() {
		super(TE::new);
	}

}
