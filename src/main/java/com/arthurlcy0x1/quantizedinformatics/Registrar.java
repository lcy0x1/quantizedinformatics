package com.arthurlcy0x1.quantizedinformatics;

import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.AutoCraft;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntAttack;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntAttr;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntRepel;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.EntSpawn;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.PipeCore;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.PipeHead;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.RecMaker;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.SPipeHead;
import com.arthurlcy0x1.quantizedinformatics.blocks.auto.SoulExt;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DIOBlock.DIOTerm;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftCntr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftIn;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftLnr;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftOut;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Craft3D;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.OxiFn;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.QuantumOre;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.RedFn;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect.DraftIO;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.MazeWall;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QTeleBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanAir;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanAirBarrier;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanBarrier;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanChest;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.QuanKey;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.StandHead;
import com.arthurlcy0x1.quantizedinformatics.blocks.quantum.StoneStand;
import com.arthurlcy0x1.quantizedinformatics.entities.QuanFly;
import com.arthurlcy0x1.quantizedinformatics.entities.QuanStand;
import com.arthurlcy0x1.quantizedinformatics.items.EncItem;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.ArrowEC;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.DefEC;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.FogBall;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.ItemPicker;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.PotionEC;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.SmartTNT;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.SmartTNTRender;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.TNTEC;
import com.arthurlcy0x1.quantizedinformatics.items.battle.EntityCannon.TridentEC;
import com.arthurlcy0x1.quantizedinformatics.items.battle.MaxArmor;
import com.arthurlcy0x1.quantizedinformatics.items.battle.MaxArmor.MaxMat;
import com.arthurlcy0x1.quantizedinformatics.items.battle.MaxwellItem;
import com.arthurlcy0x1.quantizedinformatics.items.battle.OreCollect;
import com.arthurlcy0x1.quantizedinformatics.items.battle.SoulItem.SoulBottle;
import com.arthurlcy0x1.quantizedinformatics.items.battle.SoulItem.SoulCollector;
import com.arthurlcy0x1.quantizedinformatics.items.battle.SoulItem.SoulMarker;
import com.arthurlcy0x1.quantizedinformatics.items.battle.SoulItem.SoulTrap;
import com.arthurlcy0x1.quantizedinformatics.items.battle.Telescope;
import com.arthurlcy0x1.quantizedinformatics.items.logic.ALUItem;
import com.arthurlcy0x1.quantizedinformatics.items.logic.AutoRecipe;
import com.arthurlcy0x1.quantizedinformatics.items.logic.DraftGateItem;
import com.arthurlcy0x1.quantizedinformatics.items.logic.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.items.logic.PrepChip;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.ConFurnace;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.ConPump;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.GenThermal;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.PMBlock;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.PowerWire;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.Transistor;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.ICutRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.IElecRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.IPowderRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.IPlateRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.ICentRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.IWashRecipe;
import com.arthurlcy0x1.quantizedinformatics.power.recipe.IWireRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.C3DRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.ChipOxiRec;
import com.arthurlcy0x1.quantizedinformatics.recipe.ChipRedRec;
import com.arthurlcy0x1.quantizedinformatics.recipe.EnchantOxiRec;
import com.arthurlcy0x1.quantizedinformatics.recipe.FixCollector;
import com.arthurlcy0x1.quantizedinformatics.recipe.FixMaxItem;
import com.arthurlcy0x1.quantizedinformatics.recipe.MaxwellRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.RedRecipe;
import com.arthurlcy0x1.quantizedinformatics.recipe.TeleRecipe;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Registrar extends ItemGroup {

	public static final String MODID = "quantizedinformatics";

	// blocks
	public static final Block BO_CU = generate("copper_ore", ORE_1);
	public static final Block BO_SN = generate("tin_ore", ORE_1);
	public static final Block BO_AG = generate("silver_ore", ORE_1);
	public static final Block BO_PB = generate("lead_ore", ORE_1);
	public static final Block BO_U = generate("uranium_ore", ORE_2);
	public static final Block BO_AL = generate("aluminum_ore", ORE_0);
	public static final Block BO_W = generate("tungsten_ore", ORE_2);
	public static final Block BO_GRAPHITE = generate("graphite_ore", ORE_0);
	public static final Block BO_BORAX = generate("borax_ore", ORE_0);
	public static final Block BO_BE = generate("beryllium_ore", ORE_0);
	public static final Block BO_NI = generate("nickel_ore", ORE_1);
	public static final Block BO_MN = generate("manganese_ore", ORE_1);
	public static final Block BO_TI = generate("titanium_ore", ORE_2);

	public static final Block B_FOGORE = addName(new QuantumOre(), "quantum_ore");
	public static final Block B_FOG = generate("quantum_fog", QUANTUM_FOG);
	public static final Block BC_FRAME = addName(new Wire(WireConnect.CRAFT), "craft_frame");
	public static final Block B_CRAFT3D = addName(new Craft3D(), "craft_3d");
	public static final Block B_OXIFN = addName(new CTEBlock(FURNACE, OxiFn.TE::new), "oxidation_furnace");
	public static final Block B_REDFN = addName(new CTEBlock(FURNACE, RedFn.TE::new), "reduction_furnace");
	public static final Block BD_WIRE = addName(new Wire(WireConnect.GATE), "draft_wire");
	public static final Block BD_CNTR = addName(new DIOTerm(DraftCntr.TE::new, DraftIO.OUTPUT), "draft_center");
	public static final Block BD_GATE = addName(new DraftGate(), "draft_gate");
	public static final Block BD_IN = addName(new DIOTerm(DraftIn.TE::new, DraftIO.OUTPUT), "draft_in");
	public static final Block BD_OUT = addName(new DraftOut(), "draft_out");
	public static final Block BD_LNR = addName(new DraftLnr(), "draft_listener");
	public static final Block BA_CRAFT = addName(new CTEBlock(M_CRAFT, AutoCraft.TE::new), "auto_craft");
	public static final Block BA_REC = addName(new CTEBlock(M_CRAFT, RecMaker.TE::new), "recipe_maker");
	public static final Block BA_SOUL = addName(new CTEBlock(M_CRAFT, SoulExt.TE::new), "soul_extracter");
	public static final Block BAP_HEAD = addName(new PipeHead(), "pipe_head");
	public static final Block BAP_BODY = addName(new Wire(WireConnect.PIPE), "pipe_body");
	public static final Block BAP_CORE = addName(new PipeCore(), "pipe_core");
	public static final Block BAP_SUB = addName(new Wire(WireConnect.SPIPE), "pipe_sub_body");
	public static final Block BAP_SHEAD = addName(new SPipeHead(), "pipe_sub_head");
	public static final Block BAME_ATK = addName(new EntAttack(), "ent_attack");
	public static final Block BAME_REP = addName(new EntRepel(), "ent_repel");
	public static final Block BAME_ATR = addName(new EntAttr(), "ent_attract");
	public static final Block BAME_SPA = addName(new EntSpawn(), "ent_spawn");

	public static final Block BQ_AIR = generate("quantum_world_air", QuanAir::new);
	public static final Block BQ_PORTAL = generate("quantum_world_portal", QTeleBlock::new);
	public static final Block BQ_STONE = generate("quantum_world_stone", QuanBlock::new);
	public static final Block BQ_CORE = generate("quantum_world_core", QuanBlock::new);
	public static final Block BQ_BARRIER = generate("quantum_world_barrier", QuanBarrier::new);
	public static final Block BQ_BARAIR = generate("quantum_world_barrier_air", QuanAirBarrier::new);
	public static final Block BQ_MAZEWALL = generate("quantum_world_maze_wall", MazeWall::new);
	public static final Block BQ_KEY = generate("quantum_world_key", QuanKey::new);
	public static final Block BQ_CHEST = generate("quantum_world_chest", QuanChest::new);
	public static final Block B_STAND = addName(new StoneStand(), "stone_stand");
	public static final Block B_STANDHEAD = addName(new StandHead(), "stone_stand_head");

	public static final Block BPW_FE = addName(new PowerWire(), "iron_wire");
	public static final Block BPW_CU = addName(new PowerWire(), "copper_wire");
	public static final Block BPW_AG = addName(new PowerWire(), "silver_wire");
	public static final Block BPW_AL = addName(new PowerWire(), "aluminum_wire");
	public static final Block BP_TR = addName(new PMBlock(Transistor.TE::new, false), "pm_transistor");
	public static final Block BPG_TH = addName(new PMBlock(GenThermal.TE::new, true), "pmg_thermal");
	public static final Block BPC_FN = addName(new PMBlock(ConFurnace.TE::new, false), "pmc_furnace");
	public static final Block BPC_PU = addName(new PMBlock(ConPump.TE::new, true), "pmc_pump");

	public static final List<Block> BDS = Arrays.asList(BD_CNTR, BD_GATE, BD_IN, BD_OUT, BD_LNR);

	public static final ItemGroup ITEM_GROUP = new Registrar();

	// block items
	public static final Item IBO_CU = convert(BO_CU);
	public static final Item IBO_SN = convert(BO_SN);
	public static final Item IBO_AG = convert(BO_AG);
	public static final Item IBO_PB = convert(BO_PB);
	public static final Item IBO_U = convert(BO_U);
	public static final Item IBO_AL = convert(BO_AL);
	public static final Item IBO_W = convert(BO_W);
	public static final Item IBO_GRAPHITE = convert(BO_GRAPHITE);
	public static final Item IBO_BORAX = convert(BO_BORAX);
	public static final Item IBO_BE = convert(BO_BE);
	public static final Item IBO_NI = convert(BO_NI);
	public static final Item IBO_MN = convert(BO_MN);
	public static final Item IBO_TI = convert(BO_TI);

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
	public static final Item IBA_SOUL = convert(BA_SOUL);
	public static final Item IBAP_HEAD = convert(BAP_HEAD);
	public static final Item IBAP_BODY = convert(BAP_BODY);
	public static final Item IBAP_CORE = convert(BAP_CORE);
	public static final Item IBAP_SUB = convert(BAP_SUB);
	public static final Item IBAP_SHEAD = convert(BAP_SHEAD);
	public static final Item IBME_ATK = convert(BAME_ATK);
	public static final Item IBME_REP = convert(BAME_REP);
	public static final Item IBME_ATR = convert(BAME_ATR);
	public static final Item IBME_SPA = convert(BAME_SPA);

	public static final Item IBQ_PORTAL = convert(BQ_PORTAL);
	public static final Item IBQ_STONE = convert(BQ_STONE);
	public static final Item IBQ_CORE = convert(BQ_CORE);
	public static final Item IBQ_BARRIER = convert(BQ_BARRIER);
	public static final Item IBQ_MAZEWALL = convert(BQ_MAZEWALL);
	public static final Item IBQ_KEY = convert(BQ_KEY);
	public static final Item IBQ_CHEST = convert(BQ_CHEST);
	public static final Item IB_STAND = convert(B_STAND);

	public static final Item IBPW_FE = convert(BPW_FE);
	public static final Item IBPW_CU = convert(BPW_CU);
	public static final Item IBPW_AG = convert(BPW_AG);
	public static final Item IBPW_AL = convert(BPW_AL);
	public static final Item IBP_TR = convert(BP_TR);
	public static final Item IBPG_TH = convert(BPG_TH);
	public static final Item IBPC_FN = convert(BPC_FN);
	public static final Item IBPC_PU = convert(BPC_PU);

	// items
	public static final Item IOP_FE = generate("iron_ore_powder", 64);
	public static final Item IOP_AU = generate("gold_ore_powder", 64);
	public static final Item IOP_CU = generate("copper_ore_powder", 64);
	public static final Item IOP_SN = generate("tin_ore_powder", 64);
	public static final Item IOP_AG = generate("silver_ore_powder", 64);
	public static final Item IOP_PB = generate("lead_ore_powder", 64);
	public static final Item IOP_U = generate("uranium_ore_powder", 64);
	public static final Item IOP_AL = generate("aluminum_ore_powder", 64);
	public static final Item IOP_W = generate("tungsten_ore_powder", 64);
	public static final Item IOP_GRAPHITE = generate("graphite_ore_powder", 64);
	public static final Item IOP_BORAX = generate("borax_ore_powder", 64);
	public static final Item IOP_BE = generate("beryllium_ore_powder", 64);
	public static final Item IOP_NI = generate("nickel_ore_powder", 64);
	public static final Item IOP_MN = generate("manganese_ore_powder", 64);
	public static final Item IOP_TI = generate("titanium_ore_powder", 64);

	public static final Item IOPC_FE = generate("iron_ore_powder_clean", 64);
	public static final Item IOPC_AU = generate("gold_ore_powder_clean", 64);
	public static final Item IOPC_CU = generate("copper_ore_powder_clean", 64);
	public static final Item IOPC_SN = generate("tin_ore_powder_clean", 64);
	public static final Item IOPC_AG = generate("silver_ore_powder_clean", 64);
	public static final Item IOPC_PB = generate("lead_ore_powder_clean", 64);
	public static final Item IOPC_U = generate("uranium_ore_powder_clean", 64);
	public static final Item IOPC_AL = generate("aluminum_ore_powder_clean", 64);
	public static final Item IOPC_W = generate("tungsten_ore_powder_clean", 64);
	public static final Item IOPC_GRAPHITE = generate("graphite_ore_powder_clean", 64);
	public static final Item IOPC_BORAX = generate("borax_ore_powder_clean", 64);
	public static final Item IOPC_BE = generate("beryllium_ore_powder_clean", 64);
	public static final Item IOP_NIC = generate("nickel_ore_powder_clean", 64);
	public static final Item IOP_MNC = generate("manganese_ore_powder_clean", 64);
	public static final Item IOP_TIC = generate("titanium_ore_powder_clean", 64);

	public static final Item IOPCT_FE = generate("iron_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_AU = generate("gold_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_CU = generate("copper_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_SN = generate("tin_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_AG = generate("silver_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_PB = generate("lead_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_U = generate("uranium_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_AL = generate("aluminum_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_W = generate("tungsten_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_GRAPHITE = generate("graphite_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_BORAX = generate("borax_ore_powder_clean_tiny", 64);
	public static final Item IOPCT_BE = generate("beryllium_ore_powder_clean_tiny", 64);
	public static final Item IOPT_NIC = generate("nickel_ore_powder_clean_tiny", 64);
	public static final Item IOPT_MNC = generate("manganese_ore_powder_clean_tiny", 64);
	public static final Item IOPT_TIC = generate("titanium_ore_powder_clean_tiny", 64);

	public static final Item IMI_CU = generate("copper_ingot", 64);
	public static final Item IMI_AG = generate("silver_ingot", 64);
	public static final Item IMI_SN = generate("tin_ingot", 64);
	public static final Item IMI_PB = generate("lead_ingot", 64);
	public static final Item IMI_W = generate("tungsten_ingot", 64);
	public static final Item IMI_AL = generate("aluminum_ingot", 64);
	public static final Item IMI_STEEL = generate("steel_ingot", 64);
	public static final Item IMI_BRONZE = generate("bronze_ingot", 64);
	public static final Item IMI_ALALLOY = generate("al_alloy_ingot", 64);
	public static final Item IMI_NI = generate("nickel_ingot", 64);
	public static final Item IMI_CO = generate("cobalt_ingot", 64);
	public static final Item IMI_MN = generate("manganese_ingot", 64);
	public static final Item IMI_TI = generate("titanium_ingot", 64);
	public static final Item IMI_PT = generate("platinum_ingot", 64);

	public static final Item IMN_CU = generate("copper_nugget", 64);
	public static final Item IMN_AG = generate("silver_nugget", 64);
	public static final Item IMN_SN = generate("tin_nugget", 64);
	public static final Item IMN_PB = generate("lead_nugget", 64);
	public static final Item IMN_W = generate("tungsten_nugget", 64);
	public static final Item IMN_AL = generate("aluminum_nugget", 64);
	public static final Item IMN_STEEL = generate("steel_nugget", 64);
	public static final Item IMN_BRONZE = generate("bronze_nugget", 64);
	public static final Item IMN_ALALLOY = generate("al_alloy_nugget", 64);
	public static final Item IMN_NI = generate("nickel_nugget", 64);
	public static final Item IMN_CO = generate("cobalt_nugget", 64);
	public static final Item IMN_MN = generate("manganese_nugget", 64);
	public static final Item IMN_TI = generate("titanium_nugget", 64);
	public static final Item IMN_PT = generate("platinum_nugget", 64);

	public static final Item IMPD_FE = generate("iron_powder", 64);
	public static final Item IMPD_AU = generate("gold_powder", 64);
	public static final Item IMPD_CU = generate("copper_powder", 64);
	public static final Item IMPD_AG = generate("silver_powder", 64);
	public static final Item IMPD_SN = generate("tin_powder", 64);
	public static final Item IMPD_PB = generate("lead_powder", 64);
	public static final Item IMPD_W = generate("tungsten_powder", 64);
	public static final Item IMPD_AL = generate("aluminum_powder", 64);
	public static final Item IMPD_BRONZE = generate("bronze_powder", 64);
	public static final Item IMPD_ALALLOW = generate("al_alloy_powder", 64);
	public static final Item IMPD_NI = generate("nickel_powder", 64);
	public static final Item IMPD_CO = generate("cobalt_powder", 64);
	public static final Item IMPD_MN = generate("manganese_powder", 64);
	public static final Item IMPD_TI = generate("titanium_powder", 64);
	public static final Item IMPD_PT = generate("platinum_powder", 64);

	public static final Item IE_P = generate("elem_p", 64);
	public static final Item IE_B = generate("elem_b", 64);
	public static final Item IE_PO = generate("elem_po", 64);
	public static final Item IE_BO = generate("elem_bo", 64);
	public static final Item IE_SI = generate("elem_si", 64);
	public static final Item IE_FEO = generate("elem_feo", 64);
	public static final Item IE_CAO = generate("elem_cao", 64);
	public static final Item IE_CACO3 = generate("elem_caco3", 64);

	public static final Item IE_S = generate("elem_s", 64);
	public static final Item IE_AS = generate("elem_as", 64);
	public static final Item IE_ASO = generate("elem_aso", 64);
	public static final Item IE_ALO = generate("elem_alo", 64);
	public static final Item IE_WO = generate("elem_wo", 64);
	public static final Item IE_TIO = generate("elem_tio", 64);
	public static final Item IE_UO = generate("elem_uo", 64);
	public static final Item IE_BEO = generate("elem_beo", 64);

	public static final Item IMPT_FE = generate("iron_powder_tiny", 64);
	public static final Item IMPT_AU = generate("gold_powder_tiny", 64);
	public static final Item IMPT_CU = generate("copper_powder_tiny", 64);
	public static final Item IMPT_AG = generate("silver_powder_tiny", 64);
	public static final Item IMPT_SN = generate("tin_powder_tiny", 64);
	public static final Item IMPT_PB = generate("lead_powder_tiny", 64);
	public static final Item IMPT_W = generate("tungsten_powder_tiny", 64);
	public static final Item IMPT_AL = generate("aluminum_powder_tiny", 64);
	public static final Item IMPT_BRONZE = generate("bronze_powder_tiny", 64);
	public static final Item IMPT_ALALLOW = generate("al_alloy_powder_tiny", 64);
	public static final Item IMPT_NI = generate("nickel_powder_tiny", 64);
	public static final Item IMPT_CO = generate("cobalt_powder_tiny", 64);
	public static final Item IMPT_MN = generate("manganese_powder_tiny", 64);
	public static final Item IMPT_TI = generate("titanium_powder_tiny", 64);
	public static final Item IMPT_PT = generate("platinum_powder_tiny", 64);

	public static final Item IET_S = generate("elem_s_tiny", 64);
	public static final Item IET_ASO = generate("elem_aso_tiny", 64);
	public static final Item IET_WO = generate("elem_wo_tiny", 64);
	public static final Item IET_UO = generate("elem_uo_tiny", 64);
	public static final Item IET_BEO = generate("elem_beo_tiny", 64);
	public static final Item IET_TIO = generate("elem_tio_tiny", 64);
	public static final Item IET_ALO = generate("elem_alo_tiny", 64);

	public static final Item IMPL_FE = generate("iron_plate", 64);
	public static final Item IMPL_AU = generate("gold_plate", 64);
	public static final Item IMPL_CU = generate("copper_plate", 64);
	public static final Item IMPL_AG = generate("silver_plate", 64);
	public static final Item IMPL_SN = generate("tin_plate", 64);
	public static final Item IMPL_PB = generate("lead_plate", 64);
	public static final Item IMPL_W = generate("tungsten_plate", 64);
	public static final Item IMPL_AL = generate("aluminum_plate", 64);
	public static final Item IMPL_STEEL = generate("steel_plate", 64);
	public static final Item IMPL_BRONZE = generate("bronze_plate", 64);
	public static final Item IMPL_ALALLOW = generate("al_alloy_plate", 64);
	public static final Item IMPL_NI = generate("nickel_plate", 64);
	public static final Item IMPL_CO = generate("cobalt_plate", 64);
	public static final Item IMPL_MN = generate("manganese_plate", 64);
	public static final Item IMPL_TI = generate("titanium_plate", 64);
	public static final Item IMPL_PT = generate("platinum_plate", 64);

	public static final Item IMW_AU = generate("gold_wire", 64);
	public static final Item IMW_W = generate("tungsten_wire", 64);

	public static final Item I_RUBBER = generate("rubber", 64);
	public static final Item IE_DARK = generate("elem_dark", 64, EncItem::new);
	public static final Item IE_SOUL = generate("elem_soul", 64, EncItem::new);
	public static final Item IE_SPACE = generate("elem_space", 64, EncItem::new);
	public static final Item IEI_STEEL = generate("enchanted_steel_ingot", 64, EncItem::new);
	public static final Item IEI_GOLD = generate("enchanted_gold_ingot", 64, EncItem::new);
	public static final Item IEN_STEEL = generate("enchanted_steel_nugget", 64, EncItem::new);
	public static final Item IEN_GOLD = generate("enchanted_gold_nugget", 64, EncItem::new);
	public static final Item IA_RECIPE = generate("auto_recipe", 1, AutoRecipe::new);
	public static final Item IC_PREP = generate("prep_chip", 1, PrepChip::new);
	public static final Item I_ALU = generate("alu", 1, ALUItem::new);
	public static final Item I_FOGBALL = generate("fog_ball", 64);
	public static final Item I_ITEMPICK = generate("item_picker", 64);
	public static final Item IM_FOG = genMaxwell("maxwell_fog", 64);
	public static final Item IM_ELEC = genMaxwell("maxwell_electric", 7);
	public static final Item IM_MAGN = genMaxwell("maxwell_magnetic", 7);
	public static final Item IMW_ELEC = genMaxwell("maxwell_wrap_electric", 7);
	public static final Item IMW_MAGN = genMaxwell("maxwell_wrap_magnetic", 7);
	public static final Item IMU_ATK = genMaxwell("maxwell_attack", 8);
	public static final Item IMU_DEF = genMaxwell("maxwell_defense", 8);
	public static final Item IMU_FIX = generate("maxwell_fix", 64);
	public static final Item IMU_TNT = genMaxwell("maxwell_tnt", 2);
	public static final Item IS_MARKER = generate("soul_marker", 1, SoulMarker::new);
	public static final Item IS_TRAP = generate("soul_trap", 1, SoulTrap::new);
	public static final Item IS_COLL = generate("soul_collector", 1, SoulCollector::new);
	public static final Item IS_EXP = generate("soul_bottle", 1, SoulBottle::new);

	public static final Item IW_OREC = generate("ore_collector", 1, OreCollect::new);
	public static final Item IW_TNT = generate("weapon_tnt", 1, TNTEC::new);
	public static final Item IW_POTION = generate("weapon_potion", 1, PotionEC::new);
	public static final Item IW_TRIDENT = generate("weapon_trident", 1, TridentEC::new);
	public static final Item IW_ARROW = generate("weapon_arrow", 1, ArrowEC::new);
	public static final Item IW_ENDER = genDefEC("weapon_ender", Items.ENDER_PEARL, EnderPearlEntity::new);
	public static final Item IW_FOG = genDefEC("weapon_fog", I_FOGBALL, FogBall::new);
	public static final Item IW_IP = genDefEC("weapon_picker", I_ITEMPICK, ItemPicker::new);
	public static final Item IW_TELE = generate("telescope", 1, Telescope.TelescopeItem::new);

	public static final Item IM_H2 = genArmor("maxwell_2_helmet", MaxArmor.LV2, 3);
	public static final Item IM_C2 = genArmor("maxwell_2_chestplate", MaxArmor.LV2, 2);
	public static final Item IM_L2 = genArmor("maxwell_2_leggings", MaxArmor.LV2, 1);
	public static final Item IM_B2 = genArmor("maxwell_2_boots", MaxArmor.LV2, 0);
	public static final Item IM_H3 = genArmor("maxwell_3_helmet", MaxArmor.LV3, 3);
	public static final Item IM_C3 = genArmor("maxwell_3_chestplate", MaxArmor.LV3, 2);
	public static final Item IM_L3 = genArmor("maxwell_3_leggings", MaxArmor.LV3, 1);
	public static final Item IM_B3 = genArmor("maxwell_3_boots", MaxArmor.LV3, 0);
	public static final Item IM_H4 = genArmor("maxwell_4_helmet", MaxArmor.LV4, 3);
	public static final Item IM_C4 = genArmor("maxwell_4_chestplate", MaxArmor.LV4, 2);
	public static final Item IM_L4 = genArmor("maxwell_4_leggings", MaxArmor.LV4, 1);
	public static final Item IM_B4 = genArmor("maxwell_4_boots", MaxArmor.LV4, 0);

	// draft related
	public static final Item ID_N = generate("gate_dope_n", 64);
	public static final Item ID_P = generate("gate_dope_p", 64);
	public static final Item ID_CAP = generate("gate_cap", 64);
	public static final Item ID_WIRE = generate("gate_wire", 64);
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
	public static final ContainerType<SoulExt.Cont> CTA_SOUL = getCT(SoulExt.Cont::new, "soul_extracter_c");
	public static final ContainerType<PipeHead.Cont> CTAP_HEAD = getCT(PipeHead.Cont::new, "pipe_head_c");
	public static final ContainerType<PipeCore.Cont> CTAP_CORE = getCT(PipeCore.Cont::new, "pipe_core_c");
	public static final ContainerType<EntAttack.Cont> CTME_ATK = getCT(EntAttack.Cont::new, "ent_attack_c");
	public static final ContainerType<EntRepel.Cont> CTME_REP = getCT(EntRepel.Cont::new, "ent_repel_c");
	public static final ContainerType<EntAttr.Cont> CTME_ATR = getCT(EntAttr.Cont::new, "ent_attract_c");
	public static final ContainerType<EntSpawn.Cont> CTME_SPA = getCT(EntSpawn.Cont::new, "ent_spawn_c");
	public static final ContainerType<QuanChest.Cont> CTQ_CHEST = getCT(QuanChest.Cont::new, "qw_chest_c");
	public static final ContainerType<Transistor.Cont> CTP_TR = getCT(Transistor.Cont::new, "pm_transistor_c");
	public static final ContainerType<GenThermal.Cont> CTPG_TH = getCT(GenThermal.Cont::new, "pmg_thermal_c");
	public static final ContainerType<ConFurnace.Cont> CTPC_FN = getCT(ConFurnace.Cont::new, "pmc_furnace_c");
	public static final ContainerType<ConPump.Cont> CTPC_PU = getCT(ConPump.Cont::new, "pmc_pump_c");

	public static final TileEntityType<OxiFn.TE> TET_OXIFN = getTET(OxiFn.TE::new, B_OXIFN, "oxidation_furnace_te");
	public static final TileEntityType<RedFn.TE> TET_REDFN = getTET(RedFn.TE::new, B_REDFN, "reduction_furnace_te");
	public static final TileEntityType<DraftCntr.TE> TETD_CNTR = getTET(DraftCntr.TE::new, BD_CNTR, "draft_center_te");
	public static final TileEntityType<DraftGate.TE> TETD_GATE = getTET(DraftGate.TE::new, BD_GATE, "draft_gate_te");
	public static final TileEntityType<DraftIn.TE> TETD_IN = getTET(DraftIn.TE::new, BD_IN, "draft_in_te");
	public static final TileEntityType<DraftOut.TE> TETD_OUT = getTET(DraftOut.TE::new, BD_OUT, "draft_out_te");
	public static final TileEntityType<DraftLnr.TE> TETD_LNR = getTET(DraftLnr.TE::new, BD_LNR, "draft_listener_te");
	public static final TileEntityType<AutoCraft.TE> TETA_CRAFT = getTET(AutoCraft.TE::new, BA_CRAFT, "auto_craft_te");
	public static final TileEntityType<RecMaker.TE> TETA_REC = getTET(RecMaker.TE::new, BA_REC, "recipe_maker_te");
	public static final TileEntityType<SoulExt.TE> TETA_SOUL = getTET(SoulExt.TE::new, BA_SOUL, "soul_extracter_te");
	public static final TileEntityType<PipeHead.TE> TETAP_HEAD = getTET(PipeHead.TE::new, BAP_HEAD, "pipe_head_te");
	public static final TileEntityType<PipeCore.TE> TETAP_CORE = getTET(PipeCore.TE::new, BAP_CORE, "pipe_core_te");
	public static final TileEntityType<EntAttack.TE> TETME_ATK = getTET(EntAttack.TE::new, BAME_ATK, "ent_attack_te");
	public static final TileEntityType<EntRepel.TE> TETME_REP = getTET(EntRepel.TE::new, BAME_REP, "ent_repel_te");
	public static final TileEntityType<EntAttr.TE> TETME_ATR = getTET(EntAttr.TE::new, BAME_ATR, "ent_attract_te");
	public static final TileEntityType<EntSpawn.TE> TETME_SPA = getTET(EntSpawn.TE::new, BAME_SPA, "ent_spawn_te");
	public static final TileEntityType<QuanChest.TE> TETQ_CHEST = getTET(QuanChest.TE::new, BQ_CHEST, "wq_chest_te");
	public static final TileEntityType<Transistor.TE> TETP_TR = getTET(Transistor.TE::new, BP_TR, "pm_transistor_te");
	public static final TileEntityType<GenThermal.TE> TETPG_TH = getTET(GenThermal.TE::new, BPG_TH, "pmg_thermal_te");
	public static final TileEntityType<ConFurnace.TE> TETPC_FN = getTET(ConFurnace.TE::new, BPC_FN, "pmc_furnace_te");
	public static final TileEntityType<ConPump.TE> TETPC_PU = getTET(ConPump.TE::new, BPC_PU, "pmc_pump_te");

	public static final EntityType<SmartTNT> ET_STNT = getET(SmartTNT::new, "smart_tnt");
	public static final EntityType<FogBall> ET_FB = getET(FogBall::new, "fog_ball");
	public static final EntityType<ItemPicker> ET_IP = getET(ItemPicker::new, "item_picker");
	public static final EntityType<QuanFly> ETM_QF = getQET(QuanFly::new, "quantum_fly", 1.9f, 0.9f);
	public static final EntityType<QuanStand> ETM_QS = getQET(QuanStand::new, "quantum_stand", 0.6f, 1.9f);

	public static final IRecipeType<OxiRecipe> RT_OXI = IRecipeType.register(MODID + ":oxidation");
	public static final IRecipeType<RedRecipe> RT_RED = IRecipeType.register(MODID + ":reduction");
	public static final IRecipeType<C3DRecipe> RT_C3D = IRecipeType.register(MODID + ":craft_3d");
	public static final IRecipeType<IPowderRecipe> RTP_PDR = IRecipeType.register(MODID + ":power_powder");
	public static final IRecipeType<IPlateRecipe> RTP_PLA = IRecipeType.register(MODID + ":power_plate");
	public static final IRecipeType<ICutRecipe> RTP_CUT = IRecipeType.register(MODID + ":power_cut");
	public static final IRecipeType<ICutRecipe> RTP_WIR = IRecipeType.register(MODID + ":power_wire");
	public static final IRecipeType<IWashRecipe> RTP_WSH = IRecipeType.register(MODID + ":power_wash");
	public static final IRecipeType<IElecRecipe> RTP_ELE = IRecipeType.register(MODID + ":power_electrolysis");
	public static final IRecipeType<ICentRecipe> RTP_CEN = IRecipeType.register(MODID + ":power_centrifuge");

	public static final IRecipeSerializer<?> RS_OXI = getRS(new OxiRecipe.Serializer(), "oxidation");
	public static final IRecipeSerializer<?> RS_RED = getRS(new RedRecipe.Serializer(), "reduction");
	public static final IRecipeSerializer<?> RS_C3D = getRS(new C3DRecipe.Serializer(), "craft_3d");
	public static final IRecipeSerializer<?> RS_MAX = getRS(new MaxwellRecipe.Serializer(), "maxwell");
	public static final IRecipeSerializer<?> RSC_OXI = getRS(new ChipOxiRec.Serializer(), "chip_oxi");
	public static final IRecipeSerializer<?> RSC_RED = getRS(new ChipRedRec.Serializer(), "chip_red");
	public static final IRecipeSerializer<?> RS_TELE = getRS(new TeleRecipe.Serializer(), "telescope");
	public static final IRecipeSerializer<?> RSF_OC = getRS(new FixCollector.Serializer(), "fix_collector");
	public static final IRecipeSerializer<?> RSF_WP = getRS(new FixMaxItem.Serializer(), "fix_weapon");
	public static final IRecipeSerializer<?> RS_EOXI = getRS(new EnchantOxiRec.Serializer(), "enchant_oxi");
	public static final IRecipeSerializer<?> RSP_PDR = getRS(IPowderRecipe.SERIALIZER, "power_powder");
	public static final IRecipeSerializer<?> RSP_PLA = getRS(IPlateRecipe.SERIALIZER, "power_plate");
	public static final IRecipeSerializer<?> RSP_CUT = getRS(ICutRecipe.SERIALIZER, "power_cut");
	public static final IRecipeSerializer<?> RSP_WIR = getRS(IWireRecipe.SERIALIZER, "power_wire");
	public static final IRecipeSerializer<?> RSP_WSH = getRS(IWashRecipe.SERIALIZER, "power_wash");
	public static final IRecipeSerializer<?> RSP_ELE = getRS(IElecRecipe.SERIALIZER, "power_electrolysis");
	public static final IRecipeSerializer<?> RSP_CEN = getRS(ICentRecipe.SERIALIZER, "power_centrifuge");

	public static final ContainerType<?>[] CTS = { CT_OXIFN, CT_REDFN, CTD_CNTR, CTD_GATE, CTD_IN, CTD_OUT, CTD_LNR,
			CTA_CRAFT, CTA_REC, CTA_SOUL, CTAP_HEAD, CTAP_CORE, CTME_ATK, CTME_REP, CTME_ATR, CTME_SPA, CTQ_CHEST,
			CTP_TR, CTPG_TH, CTPC_FN, CTPC_PU };

	public static final TileEntityType<?>[] TETS = { TET_OXIFN, TET_REDFN, TETD_CNTR, TETD_GATE, TETD_IN, TETD_OUT,
			TETD_LNR, TETA_CRAFT, TETA_REC, TETA_SOUL, TETAP_HEAD, TETAP_CORE, TETME_ATK, TETME_REP, TETME_ATR,
			TETME_SPA, TETQ_CHEST, TETP_TR, TETPG_TH, TETPC_FN, TETPC_PU };

	public static final EntityType<?>[] ETS = { ET_STNT, ET_FB, ET_IP, ETM_QF, ETM_QS };

	public static final IRecipeSerializer<?>[] RSS = { RS_OXI, RS_RED, RS_C3D, RS_MAX, RSC_OXI, RSC_RED, RS_TELE,
			RSF_OC, RSF_WP, RS_EOXI, RSP_PDR };

	public static <T extends ForgeRegistryEntry<T>> T addName(T t, String name) {
		return t.setRegistryName(MODID, name);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerRender() {
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
		ScreenManager.registerFactory(CTA_SOUL, SoulExt.Scr::new);
		ScreenManager.registerFactory(CTME_ATK, EntAttack.Scr::new);
		ScreenManager.registerFactory(CTME_REP, EntRepel.Scr::new);
		ScreenManager.registerFactory(CTME_ATR, EntAttr.Scr::new);
		ScreenManager.registerFactory(CTME_SPA, EntSpawn.Scr::new);
		ScreenManager.registerFactory(CTQ_CHEST, QuanChest.Scr::new);
		ScreenManager.registerFactory(CTPG_TH, GenThermal.Scr::new);
		ScreenManager.registerFactory(CTPC_FN, ConFurnace.Scr::new);

		ItemRenderer ir = Minecraft.getInstance().getItemRenderer();

		RenderingRegistry.registerEntityRenderingHandler(ET_STNT, SmartTNTRender::new);
		RenderingRegistry.registerEntityRenderingHandler(ET_FB, m -> new SpriteRenderer<>(m, ir, 1, true));
		RenderingRegistry.registerEntityRenderingHandler(ET_IP, m -> new SpriteRenderer<>(m, ir, 1, true));
		RenderingRegistry.registerEntityRenderingHandler(ETM_QF, QuanFly.Renderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ETM_QS, QuanStand.Renderer::new);
	}

	private static Item convert(Block block) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		return new BlockItem(block, p).setRegistryName(block.getRegistryName());
	}

	private static Item genArmor(String str, MaxMat mat, int i) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(1);
		EquipmentSlotType type = EquipmentSlotType.func_220318_a(EquipmentSlotType.Group.ARMOR, i);
		return addName(new MaxArmor(p, type, mat), str);
	}

	private static Item genDefEC(String str, Item item, BiFunction<World, PlayerEntity, ThrowableEntity> f) {
		return generate(str, 1, p -> new DefEC(p, item, f));
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

	private static Block generate(String str, Supplier<Block> b) {
		return addName(b.get(), str);
	}

	private static Item genMaxwell(String str, int max) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		p.maxStackSize(1);
		return addName(new MaxwellItem(p, max), str);
	}

	private static <T extends Container> ContainerType<T> getCT(ContainerType.IFactory<T> fact, String str) {
		ContainerType<T> ans = new ContainerType<>(fact);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	private static <T extends Entity> EntityType<T> getET(EntityType.IFactory<T> f, String str) {
		EntityType<T> ans = EntityType.Builder.<T>create(f, EntityClassification.MISC)
				.setShouldReceiveVelocityUpdates(true).build(str);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	private static <T extends Entity> EntityType<T> getQET(EntityType.IFactory<T> f, String str, float w, float h) {
		EntityType<T> ans = EntityType.Builder.<T>create(f, EntityClassification.MONSTER).immuneToFire().size(w, h)
				.build(str);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	private static <T extends ForgeRegistryEntry<IRecipeSerializer<?>>> T getRS(T rs, String str) {
		rs.setRegistryName(MODID, str);
		return rs;
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
