package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arthurlcy0x1.quantizedindustry.MacReg;
import com.arthurlcy0x1.quantizedindustry.OreReg;
import com.arthurlcy0x1.quantizedindustry.QuanFluid;
import com.arthurlcy0x1.quantizedinformatics.world.RegWorld;
import com.arthurlcy0x1.quantizedinformatics.world.WorldGen;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("quantizedinformatics")
public class QuantizedInformatics {

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {

		@SubscribeEvent
		public static void newReg(final RegistryEvent.NewRegistry event) {
			new RegistryBuilder<QuanFluid>().setName(new ResourceLocation(MODID, "fluid")).setType(QuanFluid.class)
					.create();
		}

		@SubscribeEvent
		public static void onModDimensionRegister(final RegisterDimensionsEvent event) {
			LOGGER.warn("dimension type registered");// TODO
			RegWorld.regDimType();
		}

		@SubscribeEvent
		public static void regBiome(final RegistryEvent.Register<Biome> event) {
			getList(RegWorld.class, event, Biome.class);
		}

		@SubscribeEvent
		public static void regBlock(final RegistryEvent.Register<Block> event) {
			getList(Registrar.class, event, Block.class);
			getList(OreReg.class, event, Block.class);
			getList(MacReg.class, event, Block.class);
		}

		@SubscribeEvent
		public static void regChunkGeneratorType(final RegistryEvent.Register<ChunkGeneratorType<?, ?>> event) {
			event.getRegistry().registerAll(RegWorld.CGTS);
		}

		@SubscribeEvent
		public static void regContainerType(final RegistryEvent.Register<ContainerType<?>> event) {
			event.getRegistry().registerAll(Registrar.CTS);
			event.getRegistry().registerAll(MacReg.CTS);
		}

		@SubscribeEvent
		public static void regEntityType(final RegistryEvent.Register<EntityType<?>> event) {
			event.getRegistry().registerAll(Registrar.ETS);
		}

		@SubscribeEvent
		public static void regFeature(final RegistryEvent.Register<Feature<?>> event) {
			event.getRegistry().registerAll(RegWorld.FS);
		}

		@SubscribeEvent
		public static void regIRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
			event.getRegistry().registerAll(Registrar.RSS);
			event.getRegistry().registerAll(MacReg.RSS);
		}

		@SubscribeEvent
		public static void regItem(final RegistryEvent.Register<Item> event) {
			getList(Registrar.class, event, Item.class);
			getList(OreReg.class, event, Item.class);
			getList(MacReg.class, event, Item.class);
			Item[][][] items = { Registrar.IDS, OreReg.IOPS, OreReg.IMPS, OreReg.IMAS };
			for (Item[][] ites : items)
				for (Item[] its : ites)
					event.getRegistry().registerAll(its);

		}

		@SubscribeEvent
		public static void regModDimension(final RegistryEvent.Register<ModDimension> event) {
			getList(RegWorld.class, event, ModDimension.class);
		}

		@SubscribeEvent
		public static void regPlacement(final RegistryEvent.Register<Placement<?>> event) {
			event.getRegistry().registerAll(RegWorld.PS);
		}

		@SubscribeEvent
		public static void regTileEntityType(final RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry().registerAll(Registrar.TETS);
			event.getRegistry().registerAll(MacReg.TETS);
		}

	}

	public static final String MODID = "quantizedinformatics";

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	@SuppressWarnings("unchecked")
	private static <T extends IForgeRegistryEntry<T>> void getList(Class<?> holder, RegistryEvent.Register<T> event,
			Class<T> cls) {
		try {
			IForgeRegistry<T> reg = event.getRegistry();
			Field[] fs = holder.getDeclaredFields();
			for (Field f : fs)
				if (cls.isAssignableFrom(f.getType()))
					reg.register((T) f.get(null));
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	public QuantizedInformatics() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		PacketHandler.registerPackets();

	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		LOGGER.info("HELLO from server starting");
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Registrar.registerRender();
		MacReg.registerRender();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		InterModComms.sendTo("quantizedinformatics", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event) {
		LOGGER.info("Got IMC {}",
				event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
	}

	private void setup(final FMLCommonSetupEvent event) {
		WorldGen.addOres();
	}

}
