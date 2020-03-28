package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.SoulItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulExt {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTA_SOUL, id, inv, ent, 92, arr);
			addSlot(new CondsSlot(ent, 0, 58, 17, TE::isValid, 1));
			addSlot(new CondsSlot(ent, 1, 102, 17, TE::isValid, 1));
			addSlot(new ResultSlot(ent, 2, 58, 61));
			addSlot(new CondsSlot(ent, 3, 102, 61, TE::isValid, 1));
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/soul_extracter.png");

		private static final int[][] ARR = { { 60, 39 }, { 80, 19 }, { 78, 37 }, { 78, 37 }, { 80, 63 }, { 103, 39 } };

		private static final int[] SPR = { 2, 0, 3, 1, 0, 2 };
		private static final int[][] SPS = { { 0, 16, 12 }, { 12, 20, 20 }, { 32, 12, 16 }, { 48, 20, 20 } };
		private int hov = 0;

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 174);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			int act = getLocation(x, y);
			if (act > 0)
				send(3, act);
			return super.mouseClicked(x, y, t);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int x = guiLeft;
			int y = guiTop;
			blit(x, y, 0, 0, xSize, ySize);
			int power = container.getData().get(0);
			for (int i = 0; i < 4; i++)
				if ((power & 1 << i) > 0)
					blit(x + 56 + i % 2 * 44, y + 15 + i / 2 * 44, 176, 68, 20, 20);
			int avail = container.getData().get(2);
			int red = 0;
			if (power == 0b0011)
				red = 1;
			if (power == 0b0101)
				red = 2;
			if (power == 0b1001)
				red = 3;
			if (power == 0b0110)
				red = 4;
			if (power == 0b1010)
				red = 5;
			if (power == 0b1100)
				red = 6;
			for (int i = 0; i < 6; i++) {
				int col = red == i + 1 ? 3 : (avail & 1 << i) > 0 ? hov == i + 1 ? 2 : 1 : 0;
				int spr = SPR[i];
				blit(x + ARR[i][0], x + ARR[i][1], 176 + SPS[spr][1] * col, SPS[spr][0], SPS[spr][1], SPS[spr][2]);
			}
		}

		@Override
		protected void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			hov = getLocation(mx, my);
		}

		private int getLocation(double x, double y) {
			int i = guiLeft;
			int j = guiTop;
			if (x > i + 60 && x < i + 72 && y > j + 39 && y < j + 55)
				return 1;
			if (x > i + 80 && x < i + 96 && y > j + 19 && y < j + 31)
				return 2;
			if (x > i + 78 && x < i + 86 && y > j + 37 && y < j + 45)
				return 3;
			if (x > i + 90 && x < i + 98 && y > j + 49 && y < j + 57)
				return 3;
			if (x > i + 78 && x < i + 86 && y > j + 49 && y < j + 57)
				return 4;
			if (x > i + 90 && x < i + 98 && y > j + 37 && y < j + 45)
				return 4;
			if (x > i + 80 && x < i + 96 && y > j + 63 && y < j + 75)
				return 5;
			if (x > i + 103 && x < i + 115 && y > j + 39 && y < j + 55)
				return 6;
			return 0;
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, ITickableTileEntity {

		private static boolean isValid(int index, ItemStack stack) {
			if (index == 0)
				return stack.getItem() == Registrar.IS_COLL;
			if (index == 1)
				return stack.getItem() == Registrar.IS_TRAP;
			if (index == 3)
				return stack.getItem() == Registrar.IS_EXP || stack.getItem() == Items.GLASS_BOTTLE;
			return false;
		}

		private int power = 0, avail = 0, action = 0;

		public TE() {
			super(Registrar.TETA_SOUL, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			if (index == 0)
				return power;
			if (index == 2)
				return avail;
			if (index == 3)
				return action;
			return 0;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("soul_extracter");
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return isValid(index, stack);
		}

		@Override
		public void set(int index, int value) {
			if (index == 3)
				action = value;
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			Direction d0 = getBlockState().get(BaseBlock.HORIZONTAL_FACING);
			Direction d1 = d0.rotateYCCW();
			Direction d2 = d1.rotateYCCW();
			Direction d3 = d2.rotateYCCW();
			boolean p0 = world.isSidePowered(pos, d0);
			boolean p1 = world.isSidePowered(pos, d1);
			boolean p2 = world.isSidePowered(pos, d2);
			boolean p3 = world.isSidePowered(pos, d3);
			power = (p0 ? 1 : 0) + (p1 ? 2 : 0) + (p2 ? 4 : 0) + (p3 ? 8 : 0);
			int count = (p0 ? 1 : 0) + (p1 ? 1 : 0) + (p2 ? 1 : 0) + (p3 ? 1 : 0);
			ItemStack i0 = getStackInSlot(0);
			ItemStack i1 = getStackInSlot(1);
			ItemStack i2 = getStackInSlot(2);
			ItemStack i3 = getStackInSlot(3);
			boolean c0 = !i0.isEmpty();
			boolean c1 = !i1.isEmpty();
			boolean c2 = i2.getCount() < 64;
			boolean c3 = !i3.isEmpty();
			boolean c02 = c0 && c2 && i0.getOrCreateTag().getInt("total") >= 64;
			boolean c12 = c1 && c2 && i1.getOrCreateTag().getString("soul").length() > 0 && i1.getDamage() == 0;
			boolean c03 = c0 && c3 && i0.getOrCreateTag().getInt("total") >= 0;
			boolean c13 = c1 && c3 && i1.getOrCreateTag().getString("soul").length() > 0;
			boolean c23 = c3 && !i2.isEmpty();
			boolean c01 = c0 && c1;
			if (c01) {
				String str = i1.getOrCreateTag().getString("soul");
				if (str.length() == 0) {
					CompoundNBT tag = i0.getOrCreateChildTag("souls");
					boolean found = false;
					for (String key : tag.keySet())
						if (tag.getInt(key) >= 64) {
							found = true;
							break;
						}
					c01 = found;
				} else {
					int cont = i0.getOrCreateChildTag("souls").getInt(str);
					c01 = cont >= i1.getDamage();
				}
			}
			avail = (c01 ? 1 : 0) + (c02 ? 2 : 0) + (c03 ? 4 : 0) + (c12 ? 8 : 0) + (c13 ? 16 : 0) + (c23 ? 32 : 0);
			if (c01 && (action == 1 || count == 2 && p0 && p1)) {
				String str = i1.getOrCreateTag().getString("soul");
				CompoundNBT tag = i0.getOrCreateChildTag("souls");
				if (str.length() == 0) {
					String found = null;
					for (String key : tag.keySet())
						if (tag.getInt(key) >= 64) {
							found = key;
							break;
						}
					i1.getOrCreateTag().putString("soul", found);
				} else {
					int cont = tag.getInt(str);
					if (cont == i1.getDamage())
						tag.remove(str);
					else
						tag.putInt(str, cont - i1.getDamage());
					i1.setDamage(0);

				}
			} else if (c02 && (action == 2 || count == 2 && p0 && p2)) {
				int tot = i0.getOrCreateTag().getInt("total");
				CompoundNBT tag = i0.getOrCreateChildTag("souls");
				ItemStack is = new ItemStack(Registrar.IS_COLL);
				int rem = tot - 64;
				if (rem > 0) {
					is.getOrCreateTag().putInt("total", rem);
					CompoundNBT ntag = is.getOrCreateChildTag("souls");
					for (String key : tag.keySet()) {
						int cur = tag.getInt(key);
						ntag.putInt(key, Math.min(rem, cur));
						rem -= cur;
						if (rem <= 0)
							break;
					}
				}
				setInventorySlotContents(0, is);
				incrOrSet(2, new ItemStack(Registrar.IE_SOUL));
			} else if (c03 && (action == 3 || count == 2 && p0 && p3)) {
				setInventorySlotContents(3, SoulItem.SoulBottle.addExp(i3, i0.getOrCreateTag().getInt("total")));
				setInventorySlotContents(0, new ItemStack(Registrar.IS_COLL));
			} else if (c12 && (action == 4 || count == 2 && p1 && p2)) {
				incrOrSet(2, new ItemStack(Registrar.IE_SOUL));
				setInventorySlotContents(1, new ItemStack(Registrar.IS_TRAP));
			} else if (c13 && (action == 5 || count == 2 && p1 && p3)) {
				setInventorySlotContents(3, SoulItem.SoulBottle.addExp(i3, 64 - i1.getDamage()));
				setInventorySlotContents(1, new ItemStack(Registrar.IS_TRAP));
			} else if (c23 && (action == 6 || count == 2 && p2 && p3)) {
				decrStackSize(2, 1);
				setInventorySlotContents(3, SoulItem.SoulBottle.addExp(i3, 64));
			}
			action = 0;
		}

	}

	private static final int SIZE = 4, LEN = 4;

}
