package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.Craft3D;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftWire;
import com.arthurlcy0x1.quantizedinformatics.blocks.OxiFn;
import com.arthurlcy0x1.quantizedinformatics.blocks.RedFn;
import com.arthurlcy0x1.quantizedinformatics.items.DraftGateItem;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecipe;

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
	public static final Block B_OXIFN = addName(new CTEBlock<OxiFn.TE>(OxiFn.TE::new), "oxidation_furnace");
	public static final Block B_REDFN = addName(new CTEBlock<RedFn.TE>(RedFn.TE::new), "reduction_furnace");
	public static final Block B_CRAFT3D = addName(new Craft3D(), "craft_3d");

	public static final ItemGroup ITEM_GROUP = new Registrar();

	// block items
	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBC_FRAME = convert(BC_FRAME);
	public static final Item IBD_GATE = convert(BD_GATE);
	public static final Item IB_OXIFN = convert(B_OXIFN);
	public static final Item IB_REDFN = convert(B_REDFN);
	public static final Item IB_CRAFT3D = convert(B_CRAFT3D);

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

	public static final ContainerType<DraftGate.Cont> CT_GATE = getCT(DraftGate.Cont::new, "draft_gate_c");
	public static final ContainerType<OxiFn.Cont> CT_OXIFN = getCT(OxiFn.Cont::new, "oxidation_furnace_c");
	public static final ContainerType<RedFn.Cont> CT_REDFN = getCT(RedFn.Cont::new, "reduction_furnace_c");
	public static final ContainerType<Craft3D.Cont> CT_CRAFT3D = getCT(Craft3D.Cont::new, "craft_3d_c");

	public static final TileEntityType<DraftGate.TE> TET_GATE = getTET(DraftGate.TE::new, BD_GATE, "draft_gate_te");
	public static final TileEntityType<OxiFn.TE> TET_OXIFN = getTET(OxiFn.TE::new, B_OXIFN, "oxidation_furnace_te");
	public static final TileEntityType<RedFn.TE> TET_REDFN = getTET(RedFn.TE::new, B_REDFN, "reduction_furnace_te");
	public static final TileEntityType<Craft3D.TE> TET_CRAFT3D = getTET(Craft3D.TE::new, B_CRAFT3D, "craft_3d_te");

	public static final IRecipeType<OxiRecipe> RT_OXI = IRecipeType.register("quantizedinformatics:oxidation");
	public static final IRecipeType<RedRecipe> RT_RED = IRecipeType.register("quantizedinformatics:reduction");
	public static final IRecipeType<C3DRecipe> RT_C3D = IRecipeType.register("quantizedinformatics:craft_3d");
	public static final IRecipeSerializer<?> RS_OXI = new OxiRecipe.Serializer().setRegistryName(MODID, "oxidation");
	public static final IRecipeSerializer<?> RS_RED = new RedRecipe.Serializer().setRegistryName(MODID, "reduction");
	public static final IRecipeSerializer<?> RS_C3D = new C3DRecipe.Serializer().setRegistryName(MODID, "craft_3d");

	public static final ContainerType<?>[] CTS = { CT_GATE, CT_OXIFN, CT_REDFN, CT_CRAFT3D };
	public static final TileEntityType<?>[] TETS = { TET_GATE, TET_OXIFN, TET_REDFN, TET_CRAFT3D };
	public static final IRecipeSerializer<?>[] RSS = { RS_OXI, RS_RED, RS_C3D };

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
