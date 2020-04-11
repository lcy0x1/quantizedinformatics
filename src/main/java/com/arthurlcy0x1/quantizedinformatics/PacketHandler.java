package com.arthurlcy0x1.quantizedinformatics;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftCntr;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

	public interface DataCont {

		public IIntArray getData();

	}

	public static class IntMsg {

		public static IntMsg decode(PacketBuffer packet) {
			return new IntMsg(packet.readInt(), packet.readVarInt(), packet.readVarInt());
		}

		private final int wid, ind, val;

		public IntMsg(int id, int index, int value) {
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
			if (c != null && c.windowId == wid && c instanceof DataCont)
				((DataCont) c).getData().set(ind, val);
		}
	}

	private static final String VER = "1";
	private static final ResourceLocation NAME = new ResourceLocation(Registrar.MODID, "main");
	private static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(NAME, () -> VER, VER::equals, VER::equals);

	private static int id = 0;

	public static void registerPackets() {
		reg(IntMsg.class, IntMsg::encode, IntMsg::decode, IntMsg::handle);
		reg(DraftCntr.Msg.class, DraftCntr.Msg::encode, DraftCntr.Msg::decode, DraftCntr.Msg::handle);
		reg(IFluidTE.Msg.class, IFluidTE.Msg::encode, IFluidTE.Msg::decode, IFluidTE.Msg::handle);
	}

	public static <T> void send(T msg) {
		CH.sendToServer(msg);
	}

	public static <T> void toClient(T msg) {
		NetworkManager manager = Minecraft.getInstance().getConnection().getNetworkManager();
		NetworkDirection dir = NetworkDirection.PLAY_TO_CLIENT;
		CH.sendTo(msg, manager, dir);
	}

	private static <T> void reg(Class<T> cls, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
			BiConsumer<T, Supplier<Context>> handler) {
		CH.registerMessage(id++, cls, encoder, decoder, handler);
	}

}
