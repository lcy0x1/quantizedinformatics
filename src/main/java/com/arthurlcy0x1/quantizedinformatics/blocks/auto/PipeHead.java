package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.ArrayList;
import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import com.arthurlcy0x1.quantizedinformatics.PacketHandler.DataCont;
import com.arthurlcy0x1.quantizedinformatics.PacketHandler.IntMsg;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PipeHead extends BaseBlock implements WireConnect {

	public static class Cont extends CTEBlock.CTECont implements DataCont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(2));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTAP_HEAD, id, inv, ent, 92);
			for (int i = 0; i < SIZE; i++)
				this.addSlot(new SizeSlot(ent, i, 44 + i % 5 * 18, 30 + i / 5 * 31, 1));
			trackIntArray(data = arr);
		}

		@Override
		public IIntArray getData() {
			return data;
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pipe_head.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 174);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			int i = guiLeft + 8;
			int j = guiTop + 61;
			if (x >= i && x <= i + 18 && y >= j && y <= j + 18) {
				PacketHandler.send(new IntMsg(container.windowId, 0, (container.data.get(0) + 1) % 5));
			} else
				return super.mouseClicked(x, y, t);
			return true;
		}

		@Override
		public void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			int i = guiLeft + 8;
			int j = guiTop + 61;
			if (mx >= i && mx <= i + 18 && my >= j && my <= j + 18) {
				List<String> list = new ArrayList<>();
				list.add(Translator.getTooltipText("max"));
				int c = container.data.get(0);
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
			int count = container.data.get(0);
			int loc = count == 0 ? 7 : count - 1;
			blit(i + 8, j + 61, 176 + loc % 4 * 18, loc / 4 * 18, 18, 18);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			super.drawGuiContainerForegroundLayer(mouseX, mouseY);
			String s;
			s = Translator.getContText("pipe_head.extract");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 19.0F, COLOR);
			s = Translator.getContText("pipe_head.insert");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 50.0F, COLOR);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray {

		public static final int INSERT_ALL = 1, INSERT_TAG = 2, EXTRACT_ALL = 4, EXTRACT_TAG = 8;

		private int insertCount = 0;
		private int flags = 0;

		public TE() {
			super(Registrar.TETAP_HEAD, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			return index == 0 ? insertCount : flags;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("pipe_head");
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			insertCount = tag.getInt("input");
			flags = tag.getInt("filter");
		}

		@Override
		public void set(int index, int value) {
			if (index == 0)
				insertCount = value;
			else
				flags = value;
			markDirty();
		}

		@Override
		public int size() {
			return 2;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("input", insertCount);
			tag.putInt("filter", flags);
			return tag;
		}

		protected boolean canExtract(ItemStack is) {
			if (is.isEmpty())
				return false;
			if ((flags & EXTRACT_ALL) != 0)
				return true;
			for (int i = 0; i < 5; i++) {
				ItemStack in = getStackInSlot(i);
				if (!in.isEmpty())
					if ((flags & EXTRACT_TAG) == 0 && is.getItem() == in.getItem())
						return true;
					else if (Container.areItemsAndTagsEqual(is, in))
						return true;
			}
			return false;
		}

		protected int canInsert(ItemStack is, IInventory id, Direction dir) {
			if (is.isEmpty())
				return 0;
			if ((flags & INSERT_ALL) != 0)
				return -1;
			for (int i = 5; i < 10; i++) {
				ItemStack in = getStackInSlot(i);
				if (!in.isEmpty() && checkEqual(is, in)) {
					int ans = checkCount(is, id, dir);
					if (ans != 0)
						return ans;
				}
			}
			return 0;
		}

		private int checkCount(ItemStack is, IInventory id, Direction dir) {
			if (insertCount == 0)
				return -1;
			int count = 0;
			if (id instanceof ISidedInventory) {
				ISidedInventory isi = (ISidedInventory) id;
				for (int i : isi.getSlotsForFace(dir)) {
					ItemStack it = isi.getStackInSlot(i);
					if (checkEqual(is, it) && isi.canInsertItem(i, is, dir))
						count += it.getCount();
				}
			} else
				for (int i = 0; i < id.getSizeInventory(); i++) {
					ItemStack it = id.getStackInSlot(i);
					if (checkEqual(is, it))
						count += it.getCount();
				}
			return (1 << 2 * (insertCount - 1)) - count;
		}

		private boolean checkEqual(ItemStack i0, ItemStack i1) {
			return (flags & INSERT_TAG) == 0 ? i0.getItem() == i1.getItem() : Container.areItemsAndTagsEqual(i0, i1);
		}

	}

	private static final int SIZE = 10;

	public PipeHead() {
		super(construct(Material.ROCK).addImpls((STE) TE::new, ALD));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == SPIPE && b.get(FACING) == d || type == PIPE && b.get(FACING) == d.getOpposite();
	}

}
