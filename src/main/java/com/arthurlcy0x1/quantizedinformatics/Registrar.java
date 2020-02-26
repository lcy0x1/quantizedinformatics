package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftWire;
import com.arthurlcy0x1.quantizedinformatics.items.DraftGateChip;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Registrar extends ItemGroup {

	public static final String MODID = "quantizedinformatics";

	public static final Block B_FOG = generate("quantumfog", Material.EARTH);
	public static final Block BD_WIRE = addName(new DraftWire(), "draftwire");
	public static final Block BD_GATE = addName(new DraftGate(), "draftgate");

	public static final ItemGroup ITEM_GROUP = new Registrar();

	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBD_GATE = convert(BD_GATE);

	public static final Item ID_GATECHIP = addName(new DraftGateChip(), "draftgatechip");

	public static final ContainerType<DraftGateCont> CT_GATE = getCT(DraftGateCont::new, "draftgate_c");

	public static final TileEntityType<DraftGateTE> TET_GATE = getTET(DraftGateTE::new, BD_GATE, "draftgate_te");

	@SuppressWarnings("unchecked")
	protected static <T extends IForgeRegistryEntry<T>> void getList(RegistryEvent.Register<T> event, Class<T> cls) {
		try {
			IForgeRegistry<T> reg = event.getRegistry();
			Field[] fs = Registrar.class.getDeclaredFields();
			for (Field f : fs)
				if (cls.isAssignableFrom(f.getType()))
					reg.register((T) f.get(null));
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

	protected static void registerContainerType(RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().registerAll(CT_GATE);
	}

	protected static void registerTileEntityType(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(TET_GATE);
	}

	private static <T extends ForgeRegistryEntry<T>> T addName(T t, String name) {
		return t.setRegistryName(MODID, name);
	}

	private static Item convert(Block block) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		return new BlockItem(block, p).setRegistryName(block.getRegistryName());
	}

	private static Block generate(String str, Material mat) {
		Block.Properties p = Block.Properties.create(mat);
		return addName(new Block(p), str);
	}

	private static <T extends Container> ContainerType<T> getCT(ContainerType.IFactory<T> fact, String str) {
		ContainerType<T> ans = new ContainerType<>(fact);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	private static <T extends TileEntity> TileEntityType<T> getTET(Supplier<T> fact, Block b, String str) {
		TileEntityType<T> ans = TileEntityType.Builder.create(fact, b).build(null);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	private Registrar() {
		super(MODID);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(B_FOG);
	}

}
