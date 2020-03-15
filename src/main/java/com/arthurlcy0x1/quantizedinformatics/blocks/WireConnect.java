package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface WireConnect {

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
			return d == null ? NONE : d == getInDire(b) ? INPUT : d == getOutDire(b) ? OUTPUT : NONE;
		}

	}

	public static interface DraftTE extends INamedContainerProvider {

		public ISignalManager getSignal();

		public int[] update(int[] vals);

	}

	public static interface ISignalManager extends IIntArray {

		/** warning: be sure to check range before use */
		public int getInput(int i);

		/** warning: be sure to check range before use */
		public int getOutput(int i);

		public int getSignal(int ch);

		public int inputCount();

		public int outputCount();

		public void post();

		public void updateSignal(int[] signal);

		public void updateValidity(boolean isInput, int[] vali);

	}

	public static class Msg {

		public static Msg decode(PacketBuffer packet) {
			return new Msg(packet.readInt(), packet.readVarInt(), packet.readVarInt());
		}

		private final int wid, ind, val;

		public Msg(int id, int index, int value) {
			wid = id;
			ind = index;
			val = value;
		}

		public void encode(PacketBuffer packet) {
			packet.writeInt(wid);
			packet.writeVarInt(ind);
			packet.writeVarInt(val);
		}

		public void handle(Supplier<Context> sup) {
			Context ctx = sup.get();
			ctx.enqueueWork(() -> this.handle(ctx));
			ctx.setPacketHandled(true);
		}

		private void handle(Context ctx) {
			Container c = ctx.getSender().openContainer;
			if (c != null && c.windowId == wid && c instanceof DraftCont) {
				DraftCont cont = (DraftCont) c;
				cont.getSignal().set(ind, val);
			}
		}

	}

	public static abstract class MsgWriter {

		private final int wid;
		private final IIntArray data;

		public MsgWriter(int id, IIntArray arr) {
			wid = id;
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
			bit = translate(bit);
			if (!allowed(sele, bit))
				sele = -1;
			else
				PacketHandler.send(new Msg(wid, sele, bit));
			return sele;
		}

		protected abstract boolean allowed(int sele, int bit);

		protected int translate(int bit) {
			return bit;
		}

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
			return getRaw(index);
		}

		@Override
		public int getInput(int i) {
			return channels[i] & C_MASK;
		}

		@Override
		public int getOutput(int i) {
			return channels[i + inps] & C_MASK;
		}

		@Override
		public int getSignal(int ch) {
			return signals[ch];
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
			if (temp != null)
				signals = temp;
			temp = null;
		}

		public void read(CompoundNBT tag) {
			int[] arr = tag.getIntArray("channels");
			for (int i = 0; i < Math.min(arr.length, channels.length); i++)
				channels[i] = arr[i];
			arr = tag.getIntArray("signals");
			for (int i = 0; i < Math.min(arr.length, signals.length); i++)
				signals[i] = arr[i];
		}

		@Deprecated
		@Override
		public void set(int index, int value) {
			setRaw(index, value);
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

		@Override
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
			tag.putIntArray("signals", signals);
		}

		protected int getRaw(int i) {
			return channels[i];
		}

		protected void setRaw(int i, int v) {
			channels[i] = v;
		}

	}

	public static class SignalWriter extends MsgWriter {

		private final int inps, outs;

		public SignalWriter(int wid, int input, int output, IIntArray arr) {
			super(wid, arr);
			inps = input;
			outs = output;
		}

		public int getInputData(int i) {
			return get(i);
		}

		public int getOutputData(int i) {
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
			return bit < CNUM || sele < inps && bit >= C_LOW && bit <= C_HIGH || sele >= inps && bit == C_FLOAT;
		}

	}

	public static final int CNUM = 16;

	/** connection type */
	public static final int GATE = 0, CRAFT = 1, PIPE = 2, SPIPE = 3;

	/** face type */
	public static final int NONE = 0, INPUT = 1, OUTPUT = 2;

	/** signal ID and signal masks */
	public static final int S_FLOAT = 0, S_LOW = 1, S_HIGH = 2, S_ERR = 3, S_MASK = 15, SC_FLOAT = 16, SC_ERR = 32;

	/** channel ID and channel masks */
	public static final int C_LOW = 16, C_HIGH = 17, C_FLOAT = 18, C_FORBID = 19, C_ERR = 32, C_MASK = 31;

	public boolean canConnectFrom(int type, BlockState b, Direction d);

	public default boolean connectable(int type, BlockState b, Direction d) {
		Block bl = b.getBlock();
		if (!(bl instanceof WireConnect))
			return false;
		WireConnect wc = (WireConnect) bl;
		return wc.canConnectFrom(type, b, d.getOpposite());
	}

}
