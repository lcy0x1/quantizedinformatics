package com.arthurlcy0x1.quantizedindustry;

import com.arthurlcy0x1.quantizedindustry.machines.ConFurnace;
import com.arthurlcy0x1.quantizedindustry.machines.ConPump;
import com.arthurlcy0x1.quantizedindustry.machines.GenThermal;
import com.arthurlcy0x1.quantizedindustry.machines.PMBlock;
import com.arthurlcy0x1.quantizedindustry.machines.Transistor;
import com.arthurlcy0x1.quantizedindustry.recipe.ICentRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.ICutRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.IElecRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.IPlateRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.IPowderRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.IWashRecipe;
import com.arthurlcy0x1.quantizedindustry.recipe.IWireRecipe;
import com.arthurlcy0x1.quantizedinformatics.AbReg;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MacReg extends AbReg {

	public static final MacReg ITEM_GROUP = new MacReg();

	public static final Block BP_TR = addName(new PMBlock(Transistor.TE::new, false), "pm_transistor");
	public static final Block BPG_TH = addName(new PMBlock(GenThermal.TE::new, true), "pmg_thermal");
	public static final Block BPG_GT = addName(new PMBlock(null, true), "pmg_lava");// TODO
	public static final Block BPC_FN = addName(new PMBlock(ConFurnace.TE::new, false), "pmc_furnace");
	public static final Block BPC_PU = addName(new PMBlock(ConPump.TE::new, true), "pmc_pump");
	public static final Block BPC_PDR = addName(new PMBlock(null, true), "pmc_powder");// TODO
	public static final Block BPC_WSH = addName(new PMBlock(null, true), "pmc_wash");// TODO
	public static final Block BPC_TC = addName(new PMBlock(null, true), "pmc_centrifuge");// TODO
	public static final Block BPC_PLA = addName(new PMBlock(null, true), "pmc_plate");// TODO
	public static final Block BPC_CUT = addName(new PMBlock(null, true), "pmc_cut");// TODO
	public static final Block BPC_WIR = addName(new PMBlock(null, true), "pmc_wire");// TODO
	public static final Block BPC_ELE = addName(new PMBlock(null, true), "pmc_electrolysis");// TODO

	public static final Item I_RUBBER = generate("rubber", 64);

	public static final Item IBP_TR = convert(BP_TR);
	public static final Item IBPG_TH = convert(BPG_TH);
	public static final Item IBPG_GT = convert(BPG_GT);
	public static final Item IBPC_FN = convert(BPC_FN);
	public static final Item IBPC_PU = convert(BPC_PU);
	public static final Item IBPC_PDR = convert(BPC_PDR);
	public static final Item IBPC_WSH = convert(BPC_WSH);
	public static final Item IBPC_TC = convert(BPC_TC);
	public static final Item IBPC_PLA = convert(BPC_PLA);
	public static final Item IBPC_WIR = convert(BPC_WIR);
	public static final Item IBPC_CUT = convert(BPC_CUT);
	public static final Item IBPC_ELE = convert(BPC_ELE);

	public static final ContainerType<Transistor.Cont> CTP_TR = getCT(Transistor.Cont::new, "pm_transistor_c");
	public static final ContainerType<GenThermal.Cont> CTPG_TH = getCT(GenThermal.Cont::new, "pmg_thermal_c");
	public static final ContainerType<ConFurnace.Cont> CTPC_FN = getCT(ConFurnace.Cont::new, "pmc_furnace_c");
	public static final ContainerType<ConPump.Cont> CTPC_PU = getCT(ConPump.Cont::new, "pmc_pump_c");

	public static final TileEntityType<Transistor.TE> TETP_TR = getTET(Transistor.TE::new, BP_TR, "pm_transistor_te");
	public static final TileEntityType<GenThermal.TE> TETPG_TH = getTET(GenThermal.TE::new, BPG_TH, "pmg_thermal_te");
	public static final TileEntityType<ConFurnace.TE> TETPC_FN = getTET(ConFurnace.TE::new, BPC_FN, "pmc_furnace_te");
	public static final TileEntityType<ConPump.TE> TETPC_PU = getTET(ConPump.TE::new, BPC_PU, "pmc_pump_te");

	public static final IRecipeType<IPowderRecipe> RTP_PDR = IRecipeType.register(MODID + ":power_powder");
	public static final IRecipeType<IPlateRecipe> RTP_PLA = IRecipeType.register(MODID + ":power_plate");
	public static final IRecipeType<ICutRecipe> RTP_CUT = IRecipeType.register(MODID + ":power_cut");
	public static final IRecipeType<ICutRecipe> RTP_WIR = IRecipeType.register(MODID + ":power_wire");
	public static final IRecipeType<IWashRecipe> RTP_WSH = IRecipeType.register(MODID + ":power_wash");
	public static final IRecipeType<IElecRecipe> RTP_ELE = IRecipeType.register(MODID + ":power_electrolysis");
	public static final IRecipeType<ICentRecipe> RTP_CEN = IRecipeType.register(MODID + ":power_centrifuge");

	public static final IRecipeSerializer<?> RSP_PDR = getRS(IPowderRecipe.SERIALIZER, "power_powder");
	public static final IRecipeSerializer<?> RSP_PLA = getRS(IPlateRecipe.SERIALIZER, "power_plate");
	public static final IRecipeSerializer<?> RSP_CUT = getRS(ICutRecipe.SERIALIZER, "power_cut");
	public static final IRecipeSerializer<?> RSP_WIR = getRS(IWireRecipe.SERIALIZER, "power_wire");
	public static final IRecipeSerializer<?> RSP_WSH = getRS(IWashRecipe.SERIALIZER, "power_wash");
	public static final IRecipeSerializer<?> RSP_ELE = getRS(IElecRecipe.SERIALIZER, "power_electrolysis");
	public static final IRecipeSerializer<?> RSP_CEN = getRS(ICentRecipe.SERIALIZER, "power_centrifuge");

	public static final ContainerType<?>[] CTS = { CTP_TR, CTPG_TH, CTPC_FN, CTPC_PU };

	public static final TileEntityType<?>[] TETS = { TETP_TR, TETPG_TH, TETPC_FN, TETPC_PU };

	public static final IRecipeSerializer<?>[] RSS = { RSP_PDR, RSP_PLA, RSP_CUT, RSP_WIR, RSP_WSH, RSP_ELE, RSP_CEN };

	@OnlyIn(Dist.CLIENT)
	public static void registerRender() {
		ScreenManager.registerFactory(CTPG_TH, GenThermal.Scr::new);
		ScreenManager.registerFactory(CTPC_FN, ConFurnace.Scr::new);
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

	public MacReg() {
		super(MODID + "_machine");
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(BP_TR);
	}

}
