package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.ArrayList;
import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.logic.ALUItem;
import com.arthurlcy0x1.quantizedinformatics.items.logic.AutoRecipe;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AutoCraft {

	public static class Cont extends CTEBlock.CommCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(2));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTA_CRAFT, id, inv, ent, 120, arr);
			for (int i = 0; i < 15; i++)
				addSlot(new Slot(ent, i, 31 + i % 3 * 18, 17 + i / 3 * 18));
			for (int i = 0; i < 15; i++)
				addSlot(new ResultSlot(ent, 15 + i, 115 + i % 3 * 18, 17 + i / 3 * 18));
			addSlot(new CondsSlot(ent, 30, 91, 35, TE::isValid, 1));
			for (int i = 0; i < 5; i++)
				addSlot(new CondsSlot(ent, 31 + i, 9, 17 + i * 18, TE::isValid, 1));
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/auto_craft.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 202);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			int i = guiLeft + 91;
			int j = guiTop + 89;
			if (x >= i && x <= i + 18 && y >= j && y <= j + 18)
				send(1, (get(1) + 1) % 5);
			else
				return super.mouseClicked(x, y, t);
			return true;
		}

		@Override
		public void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			int i = guiLeft + 91;
			int j = guiTop + 89;
			if (mx >= i && mx <= i + 18 && my >= j && my <= j + 18) {
				List<String> list = new ArrayList<>();
				list.add(Translator.getTooltipText("max"));
				int c = get(1);
				list.add(c > 0 ? "" + (1 << 2 * (c - 1)) : Translator.getTooltipText("infinity"));
				renderTooltip(list, mx, my);
			}
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
			if (get(0) > 0)
				blit(i + 88, j + 60, 176, 0, get(0), 15);
			int count = get(1);
			int loc = count == 0 ? 7 : count - 1;
			blit(i + 91, j + 89, 176 + loc % 4 * 18, 15 + loc / 4 * 18, 18, 18);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements ITickableTileEntity, IIntArray, ISidedInventory {

		private static final double DEF_SPEED = 0.04;

		private static final int[] SLOTS;

		static {
			SLOTS = new int[30];
			for (int i = 0; i < 30; i++)
				SLOTS[i] = i;
		}

		private static boolean eq(ItemStack i0, ItemStack i1) {
			if (Container.areItemsAndTagsEqual(i0, i1))
				return true;
			if (i0.getItem() == Registrar.IA_RECIPE && i1.getItem() == Registrar.IA_RECIPE)
				return true;
			if (i0.getItem() == Registrar.I_ALU && i1.getItem() == Registrar.I_ALU)
				return true;
			return false;
		}

		private static final boolean isValid(int ind, ItemStack is) {
			if (ind < 15)
				return true;
			if (ind < 30)
				return false;
			if (ind == 30)
				return is.getItem() instanceof AutoRecipe;
			return is.getItem() instanceof ALUItem;
		}

		private double prog;

		private int outputCount;

		private NonNullList<ItemStack> list;

		private ItemStack res;

		public TE() {
			super(Registrar.TETA_CRAFT, SIZE);
		}

		/** it can extract the left slots from the sides */
		@Override
		public boolean canExtractItem(int index, ItemStack stack, Direction d) {
			return (d != Direction.UP || index >= 15) && index < 30;
		}

		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
			return index < 15;
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			return index == 0 ? (int) (prog * 22) : outputCount;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("auto_craft");
		}

		@Override
		public int[] getSlotsForFace(Direction side) {
			return SLOTS;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getDouble("progress");
			outputCount = tag.getInt("output");
		}

		@Override
		public void set(int index, int value) {
			if (index == 1)
				outputCount = value;
			markDirty();
		}

		@Override
		public int size() {
			return 2;
		}

		@Override
		public void tick() {
			if (world.isRemote || res == null || res.isEmpty())
				return;
			int outc = getOutputCap();
			if (outc == 0)
				return;
			int avi = getCapability();
			if (avi == 0) {
				prog = 0;
				return;
			}
			markDirty();
			prog += DEF_SPEED * getSpeed();
			int cap = (int) prog;
			prog -= cap;
			if (cap == 0)
				return;
			int cra = Math.min(cap, Math.min(outc, avi));

			for (ItemStack is : list) {
				int needs = cra * is.getCount();
				for (int i = 0; i < 15; i++) {
					if (needs == 0)
						break;
					ItemStack in = getStackInSlot(i);
					if (eq(is, in)) {
						int c = in.getCount();
						if (c <= needs) {
							this.setInventorySlotContents(i, ItemStack.EMPTY);
							needs -= c;
						} else {
							this.decrStackSize(i, needs);
							needs = 0;
						}
					}
				}
				if (is.hasContainerItem()) {
					ItemStack cont = is.getContainerItem();
					int ext = cra * is.getCount() * cont.getCount();

					for (int i = 0; i < 15; i++) {
						ItemStack in = getStackInSlot(i);
						if (in.isEmpty()) {
							int c = Math.min(ext, cont.getMaxStackSize());
							ext -= c;
							ItemStack toSet = cont.copy();
							toSet.setCount(c);
							setInventorySlotContents(i, toSet);
						} else if (eq(cont, in)) {
							int c = Math.min(ext, in.getMaxStackSize() - in.getCount());
							ext -= c;
							in.grow(c);
						}

					}
				}
			}

			int has = cra * res.getCount();
			for (int i = 15; i < 30; i++) {
				ItemStack in = getStackInSlot(i);
				if (in.isEmpty()) {
					int c = Math.min(has, res.getMaxStackSize());
					has -= c;
					ItemStack toSet = res.copy();
					toSet.setCount(c);
					setInventorySlotContents(i, toSet);
				} else if (eq(res, in)) {
					int c = Math.min(has, in.getMaxStackSize() - in.getCount());
					has -= c;
					in.grow(c);
				}

			}

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putDouble("progress", prog);
			tag.putInt("output", outputCount);
			return tag;
		}

		@Override
		protected void onChange(int index) {
			if (index != -1 && index != 30)
				return;
			ItemStack is = this.getStackInSlot(30);
			if (is.isEmpty()) {
				list = null;
				res = null;
				prog = 0;
				return;
			}
			CompoundNBT tag = is.getChildTag("recipe");
			if (tag == null) {
				list = null;
				res = null;
				prog = 0;
				return;
			}
			int len = tag.getInt("length");
			list = NonNullList.withSize(len, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(tag, list);
			res = ItemStack.read(tag);
		}

		private int getCapability() {
			int avi = 64;
			for (ItemStack is : list) {
				int has = 0;
				for (int i = 0; i < 15; i++) {
					ItemStack in = getStackInSlot(i);
					if (eq(is, in))
						has += in.getCount();
				}
				avi = Math.min(avi, has / is.getCount());
			}
			return avi;
		}

		private int getOutputCap() {
			int cap = 0;
			int has = 0;
			for (int i = 15; i < 30; i++) {
				ItemStack in = getStackInSlot(i);
				if (in.isEmpty())
					cap += res.getMaxStackSize();
				else if (eq(in, res)) {
					has += in.getCount();
					cap += in.getMaxStackSize() - in.getCount();
				}
			}
			if (outputCount != 0) {
				int max = 1 << 2 * (outputCount - 1);
				cap = Math.min(cap, Math.max(0, max - has));
			}
			if (res.hasContainerItem()) {
				ItemStack is = res.getContainerItem();
				if (res.getMaxStackSize() == 1)
					return cap / res.getCount();
				int cont = 0;
				for (int i = 0; i < 15; i++) {
					ItemStack in = getStackInSlot(i);
					if (in.isEmpty())
						cont += is.getMaxStackSize();
					else if (eq(in, is))
						cont += in.getMaxStackSize() - in.getCount();
				}
				cap = Math.min(cont / is.getCount(), cap);
			}

			return cap / res.getCount();
		}

		private int getSpeed() {
			int ans = 1;
			for (int i = 31; i < 36; i++)
				ans += ALUItem.getSpeed(getStackInSlot(i));
			return ans;
		}

	}

	private static final int SIZE = 36;

}
