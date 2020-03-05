package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface WireConnect {

	public static class DIOCont extends Container implements DraftCont {

		private final SignalWriter data;

		protected DIOCont(ContainerType<?> type, int id, SignalWriter arr) {
			super(type, id);
			data = arr;
			trackIntArray(data.getData());
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}

		@Override
		public SignalWriter getSignal() {
			return data;
		}

	}

	public static class DIOScr<T extends Container & DraftCont> extends ContainerScreen<T> {

		public DIOScr(T cont, PlayerInventory inv, ITextComponent title) {
			super(cont, inv, title);
			// TODO reset gui size
		}

		@Override
		public void render(int x, int y, float t) {
			renderBackground();
			super.render(x, y, t);
			renderHoveredToolTip(x, y);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			// TODO draw gui

		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			String s = this.title.getFormattedText();
			this.font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6.0F, 4210752);
		}

	}

	public static class DIOTerm<T extends TileEntity> extends CTEBlock<T> implements DraftIO {

		private final int type;

		public DIOTerm(Supplier<T> sup, int t) {
			super(sup);
			type = t;
		}

		@Override
		public boolean canConnectFrom(int type, BlockState b, Direction d) {
			if (type == CRAFT) {
				Direction dire = b.get(HorizontalBlock.HORIZONTAL_FACING);
				return d != dire && d != dire.getOpposite();
			} else
				return WireConnect.DraftIO.super.canConnectFrom(type, b, d);
		}

		@Override
		public Direction getInDire(BlockState bs) {
			return type == INPUT ? bs.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite() : null;
		}

		@Override
		public Direction getOutDire(BlockState bs) {
			return type == OUTPUT ? bs.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite() : null;
		}

		@Override
		public int ioType(BlockState b, Direction d) {
			return b.get(HorizontalBlock.HORIZONTAL_FACING) == d.getOpposite() ? type : NONE;
		}

	}

	public static interface DraftCont {

		public MsgWriter getSignal();

	}

	public static interface DraftIO extends WireConnect {

		@Override
		public default boolean canConnectFrom(int type, BlockState b, Direction d) {
			return type == GATE && ioType(b, d) != NONE;
		}

		public Direction getInDire(BlockState b);

		public Direction getOutDire(BlockState b);

		public default int ioType(BlockState b, Direction d) {
			return d == null ? null : d == getInDire(b) ? INPUT : d == getOutDire(b) ? OUTPUT : null;
		}

	}

	public static interface DraftTE {

		public ISignalManager getSignal();

		public int[] update(int[] vals);

	}

	public abstract static class DTETerm<T extends DTETerm<T>> extends TileEntity implements DraftTE {

		private final SignalManager data;

		public DTETerm(TileEntityType<T> t, int type) {
			super(t);
			data = new SignalManager(this, 2 - type, type - 1);
		}

		@Override
		public SignalManager getSignal() {
			return data;
		}

	}

	public static interface ISignalManager extends IIntArray {

		/** warning: be sure to check range before use */
		public int getInput(int i);

		/** warning: be sure to check range before use */
		public int getOutput(int i);

		public int[] getSignal();

		public int inputCount();

		public int outputCount();

		public void post();

		public void updateSignal(int[] signal);

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

		public void handle(Supplier<Context> sup) {
			Context ctx = sup.get();
			ctx.enqueueWork(() -> this.handle(ctx));
			ctx.setPacketHandled(true);
		}

		private void handle(Context ctx) {
			Container c = ctx.getSender().openContainer;
			if (c instanceof DraftCont) {
				DraftCont cont = (DraftCont) c;
				cont.getSignal().set(ind, val);
			}
		}

	}

	public static abstract class MsgWriter {

		private final IIntArray data;

		public MsgWriter(IIntArray arr) {
			data = arr;
		}

		public int get(int i) {
			return data.get(i);
		}

		public IIntArray getData() {
			return data;
		}

		public void set(int i, int val) {
			data.set(i, val);
		}

		public int updateSele(int sele, char ch) {
			int bit = -1;
			if (ch >= '0' && ch <= '9')
				bit = ch - '0';
			else if (ch >= 'a' && ch <= 'f')
				bit = ch - 'a' + 10;
			else if (ch >= 'A' && ch <= 'F')
				bit = ch - 'A' + 10;
			else if (ch == 'L' || ch == 'l')
				bit = C_LOW;
			else if (ch == 'H' || ch == 'h')
				bit = C_HIGH;
			else if (ch == ' ')
				bit = C_FLOAT;
			if (!allowed(sele, bit))
				sele = -1;
			else
				PacketHandler.send(new Msg(sele, bit));
			return sele;
		}

		protected abstract boolean allowed(int sele, int bit);

	}

	public static class SignalManager implements ISignalManager {

		private final DraftTE ent;
		private final int[] channels;
		private final int inps, outs;

		private int[] signals = new int[CNUM];
		private int[] temp;

		public SignalManager(DraftTE te, int input, int output) {
			ent = te;
			channels = new int[input + output];
			inps = input;
			outs = output;
		}

		@Deprecated
		@Override
		public int get(int index) {
			return channels[index];
		}

		@Override
		public int getInput(int i) {
			return channels[i];
		}

		@Override
		public int getOutput(int i) {
			return channels[i + inps];
		}

		@Override
		public int[] getSignal() {
			return signals;
		}

		@Override
		public int inputCount() {
			return inps;
		}

		@Override
		public int outputCount() {
			return outs;
		}

		@Override
		public void post() {
			signals = temp;
			temp = null;
		}

		public void read(CompoundNBT tag) {
			int[] arr = tag.getIntArray("channels");
			for (int i = 0; i < Math.min(arr.length, channels.length); i++)
				channels[i] = arr[i];
		}

		@Deprecated
		@Override
		public void set(int index, int value) {
			channels[index] = value;
		}

		public void setInput(int i, int val) {
			channels[i] = val;
		}

		public void setOutput(int i, int val) {
			channels[i + inps] = val;
		}

		@Override
		public int size() {
			return channels.length;
		}

		@Override
		public void updateSignal(int[] vals) {
			temp = ent.update(vals);
		}

		public void updateValidity(boolean isInput, int[] vali) {
			int i0 = isInput ? 0 : inps;
			int i1 = isInput ? inps : inps + outs;
			for (int i = i0; i < i1; i++) {
				int ch = channels[i] & C_MASK;
				if (ch < 16)
					channels[i] = ch | (vali[ch] == 1 ? 0 : C_ERR);
			}
		}

		public void write(CompoundNBT tag) {
			tag.putIntArray("channels", channels);
		}

	}

	public static class SignalWriter extends MsgWriter {

		private final int inps, outs;

		public SignalWriter(int input, int output, IIntArray arr) {
			super(arr);
			inps = input;
			outs = output;
		}

		public int getInput(int i) {
			return get(i);
		}

		public int getOutput(int i) {
			return get(i + inps);
		}

		public int inputCount() {
			return inps;
		}

		public int outputCount() {
			return outs;
		}

		public void setInput(int i, int val) {
			set(i, val);
		}

		public void setOutput(int i, int val) {
			set(i + inps, val);
		}

		@Override
		protected boolean allowed(int sele, int bit) {
			return bit == -1 || sele >= inps && bit >= C_LOW && bit != C_FLOAT || sele < inps && bit == C_FLOAT;
		}

	}

	public static final int CNUM = 16;
	public static final int NONE = 0, INPUT = 1, OUTPUT = 2;
	public static final int S_FLOAT = 0, S_LOW = 1, S_HIGH = 2, S_ERR = 3;
	public static final int C_LOW = 16, C_HIGH = 17, C_FLOAT = 18, C_FORBID = 19, C_ERR = 32, C_MASK = 31;

	public static final int GATE = 0, CRAFT = 1;

	public boolean canConnectFrom(int type, BlockState b, Direction d);

	public default boolean connectable(int type, BlockState b, Direction d) {
		Block bl = b.getBlock();
		if (!(bl instanceof WireConnect))
			return false;
		WireConnect wc = (WireConnect) bl;
		return wc.canConnectFrom(type, b, d.getOpposite());
	}

}
