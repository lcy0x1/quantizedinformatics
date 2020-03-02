package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class DraftGate extends CTEBlock<DraftGate.TE> implements DraftBlock {

	public static class Cont extends CTECont {

		private static boolean isChip(ItemStack is) {
			return is.getItem() instanceof LogicDraft;
		}

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1), new IntArray(DATA_LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray d) {
			super(Registrar.CT_GATE, id, inv, ent, 86);
			this.addSlot(new CondSlot(slots, 0, 80, 36, Cont::isChip));
			trackIntArray(data = d);
		}

	}

	public static class Msg {

		public static Msg decode(PacketBuffer packet) {
			return new Msg(packet.readInt(), packet.readInt());
		}

		private final int ind, val;

		private Msg(int index, int value) {
			ind = index;
			val = value;
		}

		public void encode(PacketBuffer packet) {
			packet.writeInt(ind);
			packet.writeInt(val);
		}

		public void handle(Context ctx) {
			Container c = ctx.getSender().openContainer;
			if (c instanceof Cont) {
				Cont cont = (Cont) c;
				cont.data.set(ind, val);
			}
		}

		public void handle(Supplier<Context> sup) {
			Context ctx = sup.get();
			ctx.enqueueWork(() -> this.handle(ctx));
			ctx.setPacketHandled(true);
		}

	}

	public static class Scr extends CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/draft_gate.png");

		private int sele = -1;

		public Scr(DraftGate.Cont cont, PlayerInventory inv, ITextComponent tit) {
			super(cont, inv, tit, 168);
		}

		@Override
		public boolean charTyped(char ch, int t) {
			int bit = -1;
			if (ch >= '0' && ch <= '9')
				bit = ch - '0';
			else if (ch >= 'a' && ch <= 'f')
				bit = ch - 'a' + 10;
			else if (ch >= 'A' && ch <= 'F')
				bit = ch - 'A' + 10;
			else if (ch == 'L' || ch == 'l')
				bit = 16;
			else if (ch == 'H' || ch == 'h')
				bit = 17;
			else if (ch == ' ')
				bit = 18;
			if (sele >= 0) {
				if (bit == -1 || sele >= 16 && bit > 16 || sele < 16 && bit == 18)
					sele = -1;
				else
					PacketHandler.send(new Msg(sele, bit));
				return true;
			}
			return super.charTyped(ch, t);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			sele = getSele(x, y);
			if (sele >= 0 && container.data.get(sele) == 19)
				sele = -1;
			if (sele == -1)
				return super.mouseClicked(x, y, t);
			return true;
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int x = guiLeft;
			int y = guiTop;
			blit(x, y, 0, 0, xSize, ySize);
			Cont cont = container;
			for (int i = 0; i < 16; i++) {
				int x0 = x + 53 - i / 4 * 13;
				int y0 = y + 18 + i % 4 * 13;
				int x1 = x + 110 + i / 4 * 13;
				drawSymbol(x0, y0, i, cont.data.get(i), true);
				drawSymbol(x1, y0, i, cont.data.get(i + 16), true);
			}
			if (sele >= 0)
				if (sele < 16)
					drawSymbol(x + 53 - sele / 4 * 13, y + 18 + sele % 4 * 13, sele, 36, true);
				else
					drawSymbol(x + 58 + sele / 4 * 13, y + 18 + sele % 4 * 13, sele - 16, 36, true);
		}

		private void drawSymbol(int x, int y, int i, int cv, boolean valid) {
			if (cv == 19 && i >= 4)
				return;
			int cx = 176 + cv % 4 * 13;
			int cy = cv / 4 * 13 + (valid ? 0 : 65);
			blit(x, y, cx, cy, 13, 13);
		}

		private int getSele(double x, double y) {
			int xc = guiLeft;
			int yc = guiTop;
			for (int i = 0; i < 16; i++) {
				int x0 = xc + 53 - i / 4 * 13;
				int y0 = yc + 18 + i % 4 * 13;
				int x1 = xc + 110 + i / 4 * 13;
				if (x >= x0 && x < x0 + 13 && y > y0 && y < y0 + 13)
					return i;
				if (x >= x1 && x < x1 + 13 && y > y0 && y < y0 + 13)
					return i + 16;
			}
			return -1;
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> {

		private LogicGate chip;
		private final IntArray data = new IntArray(DATA_LEN);

		public TE() {
			super(Registrar.TET_GATE, 1);
			onChange(-1);
		}

		public TE(BlockState bs, IBlockReader w) {
			this();
		}

		@Override
		public Container createMenu(int type, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(type, pi, this, data);
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return Cont.isChip(stack);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			int[] arr = tag.getIntArray("channels");
			if (arr.length > 0)
				for (int i = 0; i < DATA_LEN; i++)
					data.set(i, arr[i]);
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			int[] arr = new int[DATA_LEN];
			for (int i = 0; i < DATA_LEN; i++)
				arr[i] = data.get(i);
			tag.putIntArray("channels", arr);
			return tag;
		}

		@Override
		protected void onChange(int ind) {
			chip = loadChip();
			int cin = chip == null ? 0 : chip.input;
			int cout = chip == null ? 0 : chip.output;
			for (int i = 0; i < 16; i++)
				if (i >= cin)
					data.set(i, 19);
				else
					data.set(i, 16);
			for (int i = 0; i < 16; i++)
				if (i >= cout)
					data.set(16 + i, 19);
				else
					data.set(16 + i, 18);
		}

		private LogicGate loadChip() {
			ItemStack is = this.getStackInSlot(0);
			if (is.isEmpty() || !(is.getItem() instanceof LogicDraft))
				return null;
			return ((LogicDraft) is.getItem()).getLogicGate(is);
		}

	}

	private static final int DATA_LEN = 32;

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_gate");

	public DraftGate() {
		super(TE::new);
	}

	@Override
	public int type() {
		return GATE;
	}
}
