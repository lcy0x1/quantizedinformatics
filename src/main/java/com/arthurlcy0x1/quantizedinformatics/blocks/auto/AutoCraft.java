package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.AutoRecipe;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AutoCraft extends CTEBlock<AutoCraft.TE> {

	public static class Cont extends CTEBlock.CTECont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(1));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTA_CRAFT, id, inv, ent, 120);
			for (int i = 0; i < 15; i++)
				addSlot(new Slot(ent, i, 31 + i % 3 * 18, 17 + i / 3 * 18));
			for (int i = 0; i < 15; i++)
				addSlot(new ResultSlot(ent, 15 + i, 115 + i % 3 * 18, 17 + i / 3 * 18));
			addSlot(new CondsSlot(ent, 30, 91, 35, TE::isValid, 1));
			for (int i = 0; i < 5; i++)
				addSlot(new CondsSlot(ent, 31 + i, 9, 17 + i * 18, TE::isValid, 1));
			trackIntArray(data = arr);
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/auto_craft.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 202);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
			if (container.data.get(0) > 0)
				blit(i + 88, j + 60, 176, 0, container.data.get(0), 15);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements ITickableTileEntity, IIntArray {

		private static final double DEF_SPEED = 0.04;

		private static final boolean isValid(int ind, ItemStack is) {
			if (ind < 15)
				return true;
			if (ind < 30)
				return false;
			if (ind == 30)
				return is.getItem() instanceof AutoRecipe;
			return false;// TODO is ALU
		}

		private double prog;

		private NonNullList<ItemStack> list;
		private ItemStack res;

		public TE() {
			super(Registrar.TETA_CRAFT, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			return (int) (prog * 22);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("auto_craft");
		}

		public int getSpeed() {
			return 1;// TODO
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getDouble("progress");
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return 1;
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
					if (Container.areItemsAndTagsEqual(is, in)) {
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
				} else if (Container.areItemsAndTagsEqual(res, in)) {
					int c = Math.min(has, in.getMaxStackSize() - in.getCount());
					has -= c;
					in.grow(c);
					markDirty();
				}

			}

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putDouble("progress", prog);
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
					if (Container.areItemsAndTagsEqual(is, in))
						has += in.getCount();
				}
				avi = Math.min(avi, has / is.getCount());
			}
			return avi;
		}

		private int getOutputCap() {
			int cap = 0;
			for (int i = 15; i < 30; i++) {
				ItemStack in = getStackInSlot(i);
				if (in.isEmpty())
					cap += res.getMaxStackSize();
				else if (Container.areItemsAndTagsEqual(in, res))
					cap += in.getMaxStackSize() - in.getCount();
			}
			return cap / res.getCount();
		}

	}

	private static final int SIZE = 36;

	public AutoCraft() {
		super(TE::new);
	}

}
