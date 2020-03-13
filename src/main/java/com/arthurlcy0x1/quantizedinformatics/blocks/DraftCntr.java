package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTEScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import static com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftIO.*;

import java.util.function.Supplier;

public class DraftCntr {

	public static class Cont extends CTEBlock.CTECont implements DraftCont {

		private static class TerminalWriter extends MsgWriter {

			public TerminalWriter(IIntArray arr) {
				super(arr);
			}

			@Override
			protected boolean allowed(int sele, int bit) {
				return bit == S_LOW | bit == S_HIGH | bit == S_FLOAT;
			}

			@Override
			protected int translate(int bit) {
				if (bit == C_FLOAT)
					return S_FLOAT;
				if (bit == C_LOW)
					return S_LOW;
				if (bit == C_HIGH)
					return S_HIGH;
				return -1;
			}
		}

		private final TerminalWriter data;
		private final InfoCont cont;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1), new IntArray(CNUM), new InfoCont(null));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr, InfoCont c) {
			super(Registrar.CTD_CNTR, id, inv, ent, 104);
			addSlot(new ResultSlot(ent, 0, 51, 71));
			trackIntArray(arr);
			data = new TerminalWriter(arr);
			cont = c;
		}

		@Override
		public void detectAndSendChanges() {
			super.detectAndSendChanges();
			if (cont.te != null && cont.te.cir != null)
				PacketHandler.send(new Msg(cont.te.cir.getInfo()));
		}

		@Override
		public MsgWriter getSignal() {
			return data;
		}

	}

	public static class Msg {

		public static Msg decode(PacketBuffer packet) {
			return new Msg(packet.readVarIntArray());
		}

		private final int[] val;

		public Msg(int[] value) {
			val = value;
		}

		public void encode(PacketBuffer packet) {
			packet.writeVarIntArray(val);
		}

		public void handle(Supplier<Context> sup) {
			Context ctx = sup.get();
			ctx.enqueueWork(() -> this.handle(ctx));
			ctx.setPacketHandled(true);
		}

		private void handle(Context ctx) {
			Container c = ctx.getSender().openContainer;
			if (c instanceof Cont)
				((Cont) c).cont.data = val;
		}

	}

	public static class Scr extends CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/draft_center.png");

		private int sele = -1, scroll = 0;

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 186);
		}

		@Override
		public boolean charTyped(char ch, int t) {
			if (sele >= 0) {
				sele = container.data.updateSele(sele, ch);
				return true;
			}
			return super.charTyped(ch, t);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			sele = getSele(x, y);
			if (sele >= 0 && container.data.get(sele) == C_FLOAT)
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
				int x0 = x + 16 + i % 4 * 13;
				int y0 = y + 16 + i / 4 * 13;
				drawSymbol(x0, y0, i, cont.data.get(i));
			}
			if (sele >= 0)
				drawSymbol(x + 16 + sele % 4 * 13, y + 16 + sele / 4 * 13, 0, -1);
			renderData();
		}

		private void drawSymbol(int x, int y, int i, int id) {
			if (id == -1)
				id = 36;
			else {
				int v = id & S_MASK;
				int m = id - v;
				if (v > 0)
					id = 15 + v + m * 2 / SC_ERR;
				else
					id = i + (m == SC_ERR ? 20 : m == SC_FLOAT ? 40 : 0);
			}
			blit(x, y, 176 + id % 4 * 13, id / 4 * 13, 13, 13);
		}

		private int getSele(double x, double y) {
			int xc = guiLeft;
			int yc = guiTop;
			for (int i = 0; i < CNUM; i++) {
				int x0 = xc + 16 + i % 4 * 13;
				int y0 = yc + 16 + i / 4 * 13;
				if (x >= x0 && x < x0 + 13 && y > y0 && y < y0 + 13)
					return i;
			}
			return -1;
		}

		private void renderData() {
			int[] data = container.cont.data;
			if (data == null)
				return;
			int x = guiLeft;
			int y = guiTop;
			int fx = x + 71;
			int fy = y + 17;
			for (int i = 0; i < data.length / 6; i++) {
				int iy = i * 20 - scroll;
				if (iy < -20 || iy >= 70)
					continue;
				int stat = data[i * 6];
				int id = stat & 15;
				int err0 = stat << 4 & 4;
				int err1 = stat << 6 & 4;
				int err2 = stat << 8 & 1;
				int err = 0;
				if (err0 > 0 || err1 > 0)
					err = 2;
				else if (err2 > 0)
					err = 1;
				else
					err = 3;
				blit(err % 2 * 88, 186 + err / 2 * 20, fx, fy, 88, 20);
				if (iy < -18 || iy >= 68)
					continue;
				ItemStack is = new ItemStack(Registrar.BDS.get(id).asItem());
				itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, is, fx + 2, fy + 2 + iy);
				String sin = Translator.getCont("draft_center.in").getFormattedText();
				String sout = Translator.getCont("draft_center.out").getFormattedText();
				String text = sin + data[i * 6 + 4] + sout + data[i * 6 + 5];
				this.font.drawString(text, fx, fy + iy + 10 - font.FONT_HEIGHT / 2, COLOR);
			}
			blit(70, 0, x + 70, y, 90, 17);
			blit(70, 87, x + 70, y + 87, 90, 17);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements DraftTE, ITickableTileEntity {

		private static class TerminalSignal implements ISignalManager {

			private int[] data = new int[CNUM];

			@Override
			public int get(int index) {
				return data[index];
			}

			@Override
			public int getInput(int i) {
				return 0;
			}

			@Override
			public int getOutput(int i) {
				return getSignal(i) == S_FLOAT ? C_FLOAT : i;
			}

			@Override
			public int getSignal(int ch) {
				return data[ch] & S_MASK;
			}

			@Override
			public int inputCount() {
				return 0;
			}

			@Override
			public int outputCount() {
				return 16;
			}

			@Override
			public void post() {
			}

			public void read(CompoundNBT tag) {
				int[] arr = tag.getIntArray("channels");
				for (int i = 0; i < Math.min(arr.length, data.length); i++)
					data[i] = arr[i];
			}

			@Override
			public void set(int index, int value) {
				data[index] = value;
			}

			@Override
			public int size() {
				return CNUM;
			}

			@Override
			public void updateSignal(int[] signal) {
			}

			@Override
			public void updateValidity(boolean isInput, int[] vali) {
				if (isInput)
					return;
				for (int i = 0; i < CNUM; i++)
					data[i] = data[i] & S_MASK | (vali[i] == 0 ? SC_FLOAT : vali[i] > 1 ? SC_ERR : 0);
			}

			public void write(CompoundNBT tag) {
				tag.putIntArray("channels", data);
			}

		}

		private Circuit cir;

		private final TerminalSignal data = new TerminalSignal();

		public TE() {
			super(Registrar.TET_CNTR, 1);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, pi, this, data, new InfoCont(this));
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("draft_center");
		}

		@Override
		public ISignalManager getSignal() {
			return data;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			data.read(tag);
		}

		@Override
		public void tick() {
			cir = null;
			if (world.isRemote)
				return;
			cir = new Circuit(world, pos);
			cir.updateSignal();
		}

		@Override
		public int[] update(int[] vals) {
			return data.data;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			data.write(tag);
			return tag;
		}

	}

	private static class InfoCont {

		private int[] data;

		private TE te;

		private InfoCont(TE ent) {
			te = ent;
		}

	}
}
