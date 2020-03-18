package com.arthurlcy0x1.quantizedinformatics;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.AutoCraft;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntAttack;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntRepel;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.PipeCore;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.PipeHead;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.RecMaker;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.SPipeHead;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOTerm;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Craft3D;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.OxiFn;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.QuantumOre;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.RedFn;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.DraftIO;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftCntr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftIn;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftLnr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftOut;
import com.arthurlcy0x1.quantizedinformatics.items.ALUItem;
import com.arthurlcy0x1.quantizedinformatics.items.AutoRecipe;
import com.arthurlcy0x1.quantizedinformatics.items.DraftGateItem;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.items.MaxwellItem;
import com.arthurlcy0x1.quantizedinformatics.items.PrepChip;
import com.arthurlcy0x1.quantizedinformatics.items.SoulItem;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.ChipOxiRec;
import com.arthurlcy0x1.quantizedinformatics.recipe.ChipRedRec;
import com.arthurlcy0x1.quantizedinformatics.recipe.MaxwellRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecipe;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
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
	public static final Block B_FOGORE = addName(new QuantumOre(), "quantum_ore");
	public static final Block B_FOG = generate("quantum_fog", BlockProp.QUANTUM_FOG);
	public static final Block BC_FRAME = addName(new Wire(WireConnect.CRAFT), "craft_frame");
	public static final Block B_CRAFT3D = addName(new Craft3D(), "craft_3d");
	public static final Block B_OXIFN = addName(new CTEBlock(BlockProp.FURNACE, OxiFn.TE::new), "oxidation_furnace");
	public static final Block B_REDFN = addName(new CTEBlock(BlockProp.FURNACE, RedFn.TE::new), "reduction_furnace");
	public static final Block BD_WIRE = addName(new Wire(WireConnect.GATE), "draft_wire");
	public static final Block BD_CNTR = addName(new DIOTerm(DraftCntr.TE::new, DraftIO.OUTPUT), "draft_center");
	public static final Block BD_GATE = addName(new DraftGate(), "draft_gate");
	public static final Block BD_IN = addName(new DIOTerm(DraftIn.TE::new, DraftIO.OUTPUT), "draft_in");
	public static final Block BD_OUT = addName(new DraftOut(), "draft_out");
	public static final Block BD_LNR = addName(new DraftLnr(), "draft_listener");
	public static final Block BA_CRAFT = addName(new CTEBlock(BlockProp.M_CRAFT, AutoCraft.TE::new), "auto_craft");
	public static final Block BA_REC = addName(new CTEBlock(BlockProp.M_CRAFT, RecMaker.TE::new), "recipe_maker");
	public static final Block BAP_HEAD = addName(new PipeHead(), "pipe_head");
	public static final Block BAP_BODY = addName(new Wire(WireConnect.PIPE), "pipe_body");
	public static final Block BAP_CORE = addName(new PipeCore(), "pipe_core");
	public static final Block BAP_SUB = addName(new Wire(WireConnect.SPIPE), "pipe_sub_body");
	public static final Block BAP_SHEAD = addName(new SPipeHead(), "pipe_sub_head");
	public static final Block BAME_ATK = addName(new EntAttack(), "ent_attack");
	public static final Block BAME_REP = addName(new EntRepel(), "ent_repel");

	public static final List<Block> BDS = Arrays.asList(BD_CNTR, BD_GATE, BD_IN, BD_OUT, BD_LNR);

	public static final ItemGroup ITEM_GROUP = new Registrar();

	// block items
	public static final Item IB_FOGORE = convert(B_FOGORE);
	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBC_FRAME = convert(BC_FRAME);
	public static final Item IB_CRAFT3D = convert(B_CRAFT3D);
	public static final Item IB_OXIFN = convert(B_OXIFN);
	public static final Item IB_REDFN = convert(B_REDFN);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBD_CNTR = convert(BD_CNTR);
	public static final Item IBD_GATE = convert(BD_GATE);
	public static final Item IBD_IN = convert(BD_IN);
	public static final Item IBD_OUT = convert(BD_OUT);
	public static final Item IBD_LNR = convert(BD_LNR);
	public static final Item IBA_CRAFT = convert(BA_CRAFT);
	public static final Item IBA_REC = convert(BA_REC);
	public static final Item IBAP_HEAD = convert(BAP_HEAD);
	public static final Item IBAP_BODY = convert(BAP_BODY);
	public static final Item IBAP_CORE = convert(BAP_CORE);
	public static final Item IBAP_SUB = convert(BAP_SUB);
	public static final Item IBAP_SHEAD = convert(BAP_SHEAD);
	public static final Item IBME_ATK = convert(BAME_ATK);
	public static final Item IBME_REP = convert(BAME_REP);

	// items
	public static final Item IE_P = generate("elem_p", 64);
	public static final Item IE_B = generate("elem_b", 64);
	public static final Item IE_PO = generate("elem_po", 64);
	public static final Item IE_BO = generate("elem_bo", 64);
	public static final Item IE_SI = generate("elem_si", 64);
	public static final Item IE_FEO = generate("elem_feo", 64);
	public static final Item IE_DARK = generate("elem_dark", 64);
	public static final Item IE_SOUL = generate("elem_soul", 64);
	public static final Item IE_SPACE = generate("elem_space", 64);
	public static final Item IA_RECIPE = generate("auto_recipe", 1, AutoRecipe::new);
	public static final Item IC_PREP = generate("prep_chip", 1, PrepChip::new);
	public static final Item I_ALU = generate("alu", 1, ALUItem::new);
	public static final Item IM_ELEC = generate("maxwell_electric", 1, MaxwellItem::new);
	public static final Item IM_MAGN = generate("maxwell_magnetic", 1, MaxwellItem::new);
	public static final Item IMW_ELEC = generate("maxwell_wrap_electric", 1, MaxwellItem::new);
	public static final Item IMW_MAGN = generate("maxwell_wrap_magnetic", 1, MaxwellItem::new);
	public static final Item IMU_ATK = generate("maxwell_attack", 1, MaxwellItem::new);
	public static final Item IMU_DEF = generate("maxwell_defense", 1, MaxwellItem::new);
	public static final Item IS_TRAP = generate("soul_trap", 1, SoulItem.SoulTrap::new);
	public static final Item IS_COLL = generate("soul_collector", 1, SoulItem.SoulCollector::new);

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

	public static final ContainerType<OxiFn.Cont> CT_OXIFN = getCT(OxiFn.Cont::new, "oxidation_furnace_c");
	public static final ContainerType<RedFn.Cont> CT_REDFN = getCT(RedFn.Cont::new, "reduction_furnace_c");
	public static final ContainerType<DraftCntr.Cont> CTD_CNTR = getCT(DraftCntr.Cont::new, "draft_cntr_c");
	public static final ContainerType<DraftGate.Cont> CTD_GATE = getCT(DraftGate.Cont::new, "draft_gate_c");
	public static final ContainerType<DraftIn.Cont> CTD_IN = getCT(DraftIn.Cont::new, "draft_in_c");
	public static final ContainerType<DraftOut.Cont> CTD_OUT = getCT(DraftOut.Cont::new, "draft_out_c");
	public static final ContainerType<DraftLnr.Cont> CTD_LNR = getCT(DraftLnr.Cont::new, "draft_listener_c");
	public static final ContainerType<AutoCraft.Cont> CTA_CRAFT = getCT(AutoCraft.Cont::new, "auto_craft_c");
	public static final ContainerType<RecMaker.Cont> CTA_REC = getCT(RecMaker.Cont::new, "recipe_maker_c");
	public static final ContainerType<PipeHead.Cont> CTAP_HEAD = getCT(PipeHead.Cont::new, "pipe_head_c");
	public static final ContainerType<PipeCore.Cont> CTAP_CORE = getCT(PipeCore.Cont::new, "pipe_core_c");
	public static final ContainerType<EntAttack.Cont> CTME_ATK = getCT(EntAttack.Cont::new, "ent_attack_c");
	public static final ContainerType<EntRepel.Cont> CTME_REP = getCT(EntRepel.Cont::new, "ent_repel_c");

	public static final TileEntityType<OxiFn.TE> TET_OXIFN = getTET(OxiFn.TE::new, B_OXIFN, "oxidation_furnace_te");
	public static final TileEntityType<RedFn.TE> TET_REDFN = getTET(RedFn.TE::new, B_REDFN, "reduction_furnace_te");
	public static final TileEntityType<DraftCntr.TE> TETD_CNTR = getTET(DraftCntr.TE::new, BD_CNTR, "draft_center_te");
	public static final TileEntityType<DraftGate.TE> TETD_GATE = getTET(DraftGate.TE::new, BD_GATE, "draft_gate_te");
	public static final TileEntityType<DraftIn.TE> TETD_IN = getTET(DraftIn.TE::new, BD_IN, "draft_in_te");
	public static final TileEntityType<DraftOut.TE> TETD_OUT = getTET(DraftOut.TE::new, BD_OUT, "draft_out_te");
	public static final TileEntityType<DraftLnr.TE> TETD_LNR = getTET(DraftLnr.TE::new, BD_LNR, "draft_listener_te");
	public static final TileEntityType<AutoCraft.TE> TETA_CRAFT = getTET(AutoCraft.TE::new, BA_CRAFT, "auto_craft_te");
	public static final TileEntityType<RecMaker.TE> TETA_REC = getTET(RecMaker.TE::new, BA_REC, "recipe_maker_te");
	public static final TileEntityType<PipeHead.TE> TETAP_HEAD = getTET(PipeHead.TE::new, BAP_HEAD, "pipe_head_te");
	public static final TileEntityType<PipeCore.TE> TETAP_CORE = getTET(PipeCore.TE::new, BAP_CORE, "pipe_core_te");
	public static final TileEntityType<EntAttack.TE> TETME_ATK = getTET(EntAttack.TE::new, BAME_ATK, "ent_attack_te");
	public static final TileEntityType<EntRepel.TE> TETME_REP = getTET(EntRepel.TE::new, BAME_REP, "ent_repel_te");

	public static final IRecipeType<OxiRecipe> RT_OXI = IRecipeType.register("quantizedinformatics:oxidation");
	public static final IRecipeType<RedRecipe> RT_RED = IRecipeType.register("quantizedinformatics:reduction");
	public static final IRecipeType<C3DRecipe> RT_C3D = IRecipeType.register("quantizedinformatics:craft_3d");
	public static final IRecipeSerializer<?> RS_OXI = new OxiRecipe.Serializer().setRegistryName(MODID, "oxidation");
	public static final IRecipeSerializer<?> RS_RED = new RedRecipe.Serializer().setRegistryName(MODID, "reduction");
	public static final IRecipeSerializer<?> RS_C3D = new C3DRecipe.Serializer().setRegistryName(MODID, "craft_3d");
	public static final IRecipeSerializer<?> RS_MAX = new MaxwellRecipe.Serializer().setRegistryName(MODID, "maxwell");
	public static final IRecipeSerializer<?> RSC_OXI = new ChipOxiRec.Serializer().setRegistryName(MODID, "chip_oxi");
	public static final IRecipeSerializer<?> RSC_RED = new ChipRedRec.Serializer().setRegistryName(MODID, "chip_red");

	static {
		ScreenManager.registerFactory(CT_OXIFN, OxiFn.Scr::new);
		ScreenManager.registerFactory(CT_REDFN, RedFn.Scr::new);
		ScreenManager.registerFactory(CTD_CNTR, DraftCntr.Scr::new);
		ScreenManager.registerFactory(CTD_GATE, DraftGate.Scr::new);
		ScreenManager.registerFactory(CTD_IN, DIOScr<DraftIn.Cont>::new);
		ScreenManager.registerFactory(CTD_OUT, DIOScr<DraftOut.Cont>::new);
		ScreenManager.registerFactory(CTD_LNR, DIOScr<DraftLnr.Cont>::new);
		ScreenManager.registerFactory(CTAP_HEAD, PipeHead.Scr::new);
		ScreenManager.registerFactory(CTAP_CORE, PipeCore.Scr::new);
		ScreenManager.registerFactory(CTA_REC, RecMaker.Scr::new);
		ScreenManager.registerFactory(CTA_CRAFT, AutoCraft.Scr::new);
		ScreenManager.registerFactory(CTME_ATK, EntAttack.Scr::new);
		ScreenManager.registerFactory(CTME_REP, EntRepel.Scr::new);
	}

	public static final ContainerType<?>[] CTS = { CT_OXIFN, CT_REDFN, CTD_CNTR, CTD_GATE, CTD_IN, CTD_OUT, CTD_LNR,
			CTA_CRAFT, CTA_REC, CTAP_HEAD, CTAP_CORE, CTME_ATK, CTME_REP };
	public static final TileEntityType<?>[] TETS = { TET_OXIFN, TET_REDFN, TETD_CNTR, TETD_GATE, TETD_IN, TETD_OUT,
			TETD_LNR, TETA_CRAFT, TETA_REC, TETAP_HEAD, TETAP_CORE, TETME_ATK, TETME_REP };
	public static final IRecipeSerializer<?>[] RSS = { RS_OXI, RS_RED, RS_C3D, RS_MAX, RSC_OXI, RSC_RED };

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

	private static Block generate(String str, BlockProp mat) {
		return addName(new Block(mat.getProps()), str);
	}

	private static Item generate(String str, int size) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(size);
		return addName(new Item(p), str);
	}

	private static Item generate(String str, int size, Function<Item.Properties, Item> f) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(size);
		return addName(f.apply(p), str);
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
