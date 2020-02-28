package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGateTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftWire;
import com.arthurlcy0x1.quantizedinformatics.blocks.OxiFnBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.OxiFnCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.OxiFnTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.RedFnBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.RedFnCont;
import com.arthurlcy0x1.quantizedinformatics.blocks.RedFnTE;
import com.arthurlcy0x1.quantizedinformatics.items.DraftGateItem;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecSerializer;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecSerializer;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Registrar extends ItemGroup {

	public static final String MODID = "quantizedinformatics";

	// blocks
	public static final Block B_FOG = generate("quantum_fog", Material.EARTH);
	public static final Block BD_WIRE = addName(new DraftWire(DraftGate.GATE), "draft_wire");
	public static final Block BC_FRAME = addName(new DraftWire(DraftGate.CRAFT), "craft_frame");
	public static final Block BD_GATE = addName(new DraftGate(), "draft_gate");
	public static final Block B_OXIFN = addName(new OxiFnBlock(), "oxidation_furnace");
	public static final Block B_REDFN = addName(new RedFnBlock(), "reduction_furance");

	public static final ItemGroup ITEM_GROUP = new Registrar();

	// block items
	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBC_FRAME = convert(BC_FRAME);
	public static final Item IBD_GATE = convert(BD_GATE);

	// items
	public static final Item I_GATECHIP = generate("chip", 1);
	public static final Item IE_P = generate("elem_p", 64);
	public static final Item IE_B = generate("elem_b", 64);
	public static final Item IE_SI = generate("elem_si", 64);
	public static final Item ID_N = generate("gate_dope_n", 64);
	public static final Item ID_P = generate("gate_dope_p", 64);
	public static final Item ID_CAP = generate("gate_cap", 64);
	public static final Item ID_WIRE = generate("gate_wire", 64);

	// draft related
	public static final Item IDR_EMPTY = generate("gate_red_empty", 64);
	public static final Item IDR_DIRTY = generate("gate_red_dirty", 64);
	public static final Item IDM_EMPTY = generate("gate_mos_empty", 64);
	public static final Item IDM_DIRTY = generate("gate_mos_dirty", 64);
	public static final Item ID_NMOS = genDraftGate("gate_nmos", IDM_DIRTY);
	public static final Item ID_PMOS = genDraftGate("gate_pmos", IDM_DIRTY);
	public static final Item IDI_CPLX = generate("gate_imp_cplx", 1);
	public static final Item[][] IDS;

	static {
		String[] type = { "red", "mos", "imp" };
		String[] gate = { "not", "nand", "nor", "buff", "and", "or", "xor" };
		String[] gmul = { "nand", "nor", "and", "or" };
		String[] nmul = { "_3", "_4", "_5", "_6" };
		Item[] cont = new Item[] { IDR_DIRTY, IDM_DIRTY, null };
		IDS = new Item[type.length][gate.length + gmul.length * nmul.length];
		for (int i = 0; i < type.length; i++) {
			for (int j = 0; j < gate.length; j++) {
				String name = "gate_" + type[i] + "_" + gate[j];
				IDS[i][j] = genDraftGate(name, cont[i]);
			}
			for (int j = 0; j < nmul.length; j++)
				for (int k = 0; k < gmul.length; k++) {
					String name = "gate_" + type[i] + "_" + gmul[k] + nmul[j];
					IDS[i][gate.length + k * nmul.length + j] = genDraftGate(name, cont[i]);
				}
		}
	}

	public static final ContainerType<DraftGateCont> CT_GATE = getCT(DraftGateCont::new, "draft_gate_c");
	public static final ContainerType<OxiFnCont> CT_OXIFN = getCT(OxiFnCont::new, "oxidation_furnace_c");
	public static final ContainerType<RedFnCont> CT_REDFN = getCT(RedFnCont::new, "reduction_furance_c");

	public static final TileEntityType<DraftGateTE> TET_GATE = getTET(DraftGateTE::new, BD_GATE, "draft_gate_te");
	public static final TileEntityType<OxiFnTE> TET_OXIFN = getTET(OxiFnTE::new, B_OXIFN, "oxidation_furnace_te");
	public static final TileEntityType<RedFnTE> TET_REDFN = getTET(RedFnTE::new, B_REDFN, "reduction_furance_te");

	public static final IRecipeType<OxiRecipe> RT_OXI = IRecipeType.register("oxidation");
	public static final IRecipeType<RedRecipe> RT_RED = IRecipeType.register("reduction");
	public static final OxiRecSerializer RS_OXI = new OxiRecSerializer();
	public static final RedRecSerializer RS_RED = new RedRecSerializer();

	public static final ContainerType<?>[] CTS = { CT_GATE, CT_OXIFN, CT_REDFN };
	public static final TileEntityType<?>[] TETS = { TET_GATE, TET_OXIFN, TET_REDFN };
	public static final IRecipeSerializer<?>[] RSS = { RS_OXI, RS_RED };

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

	private static Item genDraftGate(String str, Item c) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(1);
		if (c == null)
			return addName(LogicDraft.getSimple(p, str), str);
		return addName(new DraftGateItem(p, c), str);
	}

	private static Item generate(String str, int size) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(size);
		return addName(new Item(p), str);
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
