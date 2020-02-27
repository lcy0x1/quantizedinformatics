package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftWire;
import com.arthurlcy0x1.quantizedinformatics.items.DraftGateItem;

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

	public static final Block B_FOG = generate("quantum_fog", Material.EARTH);
	public static final Block BD_WIRE = addName(new DraftWire(), "draft_wire");
	public static final Block BD_GATE = addName(new DraftGate(), "draft_gate");

	public static final ItemGroup ITEM_GROUP = new Registrar();

	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBD_GATE = convert(BD_GATE);

	private static final String[] STR_TYPE = { "red", "mos", "imp" };
	private static final String[] STR_GATE = { "buff", "not", "nand", "nor", "and", "or", "xor" };

	public static final Item IE_P = generate("elem_p", 64);
	public static final Item IE_B = generate("elem_b", 64);
	public static final Item IE_SI = generate("elem_si", 64);
	public static final Item ID_N = generate("gate_dope_n", 64);
	public static final Item ID_P = generate("gate_dope_p", 64);
	public static final Item ID_CAP = generate("gate_cap", 64);
	public static final Item ID_WIRE = generate("gate_wire", 64);

	public static final Item IDR_EMPTY = generate("gate_red_empty", 64);
	public static final Item IDR_DIRTY = generate("gate_red_dirty", 64);
	public static final Item IDM_EMPTY = generate("gate_mos_empty", 64);
	public static final Item IDM_DIRTY = generate("gate_mos_dirty", 64);
	public static final Item IDI_DIRTY = generate("gate_imp_dirty", 64);

	public static final Item ID_NMOS = generate("gate_nmos", IDM_DIRTY);
	public static final Item ID_PMOS = generate("gate_pmos", IDM_DIRTY);
	public static final Item[][] IDS;

	static {
		Item[] cont = new Item[] { IDR_DIRTY, IDM_DIRTY, IDI_DIRTY };
		IDS = new Item[STR_TYPE.length][STR_GATE.length];
		for (int i = 0; i < STR_TYPE.length; i++)
			for (int j = 0; j < STR_GATE.length; j++) {
				String name = "gate_" + STR_TYPE[i] + "_" + STR_GATE[j];
				IDS[i][j] = generate(name, cont[i]);
			}

	}

	public static final Item ID_GATECHIP = generate("draft_gate_chip", 1);

	public static final ContainerType<DraftGateCont> CT_GATE = getCT(DraftGateCont::new, "draft_gate_c");

	public static final TileEntityType<DraftGateTE> TET_GATE = getTET(DraftGateTE::new, BD_GATE, "draft_gate_te");

	public static final ContainerType<?>[] CTS = { CT_GATE };
	public static final TileEntityType<?>[] TETS = { TET_GATE };

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

	private static <T extends ForgeRegistryEntry<T>> T addName(T t, String name) {
		return t.setRegistryName(MODID, name);
	}

	private static Item convert(Block block) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		return new BlockItem(block, p).setRegistryName(block.getRegistryName());
	}

	private static Item generate(String str, int size) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(size);
		return addName(new Item(p), str);
	}

	private static Item generate(String str, Item c) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(1);
		return addName(new DraftGateItem(p, c), str);
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
