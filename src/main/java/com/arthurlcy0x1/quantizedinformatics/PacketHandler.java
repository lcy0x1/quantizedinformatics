package com.arthurlcy0x1.quantizedinformatics;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String VER = "1";
	private static final ResourceLocation NAME = new ResourceLocation(Registrar.MODID, "main");
	private static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(NAME, () -> VER, VER::equals, VER::equals);

	private static int id = 0;

	public static void registerPackets() {
		reg(WireConnect.Msg.class, WireConnect.Msg::encode, WireConnect.Msg::decode, WireConnect.Msg::handle);
	}

	public static <T> void send(T msg) {
		CH.sendToServer(msg);
	}

	private static <T> void reg(Class<T> cls, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
			BiConsumer<T, Supplier<Context>> handler) {
		CH.registerMessage(id++, cls, encoder, decoder, handler);
	}

}
