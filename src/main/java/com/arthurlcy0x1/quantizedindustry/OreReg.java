package com.arthurlcy0x1.quantizedindustry;

import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.ORE_0;
import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.ORE_1;
import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.ORE_2;

import com.arthurlcy0x1.quantizedindustry.machines.PowerWire;

import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.METAL_0;
import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.METAL_1;
import static com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp.METAL_2;

import com.arthurlcy0x1.quantizedinformatics.AbReg;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class OreReg extends AbReg {

	public static final ItemGroup ITEM_GROUP = new OreReg();

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
	public static final Block BO_S = generate("sulfur_ore", ORE_0);
	public static final Block BO_PT = generate("platinum_ore", ORE_2);

	public static final Block BM_AL = generate("aluminum_block", METAL_0);
	public static final Block BM_CU = generate("copper_block", METAL_1);
	public static final Block BM_PB = generate("lead_block", METAL_0);
	public static final Block BM_MN = generate("manganese_block", METAL_1);
	public static final Block BM_NI = generate("nickel_block", METAL_1);
	public static final Block BM_AG = generate("silver_block", METAL_0);
	public static final Block BM_SN = generate("tin_block", METAL_0);
	public static final Block BM_TI = generate("titanium_block", METAL_2);
	public static final Block BM_W = generate("tungsten_block", METAL_2);
	public static final Block BM_CO = generate("cobalt_block", METAL_2);
	public static final Block BM_PT = generate("platinum_block", METAL_2);
	public static final Block BM_BRONZE = generate("bronze_block", METAL_1);
	public static final Block BM_STEEL = generate("steel_block", METAL_1);

	public static final Block BPW_FE = addName(new PowerWire(), "iron_wire");
	public static final Block BPW_CU = addName(new PowerWire(), "copper_wire");
	public static final Block BPW_AG = addName(new PowerWire(), "silver_wire");
	public static final Block BPW_AL = addName(new PowerWire(), "aluminum_wire");

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
	public static final Item IBO_S = convert(BO_S);
	public static final Item IBO_PT = convert(BO_PT);

	public static final Item IBM_CU = convert(BM_CU);
	public static final Item IBM_SN = convert(BM_SN);
	public static final Item IBM_AG = convert(BM_AG);
	public static final Item IBM_PB = convert(BM_PB);
	public static final Item IBM_AL = convert(BM_AL);
	public static final Item IBM_W = convert(BM_W);
	public static final Item IBM_NI = convert(BM_NI);
	public static final Item IBM_MN = convert(BM_MN);
	public static final Item IBM_TI = convert(BM_TI);
	public static final Item IBM_CO = convert(BM_CO);
	public static final Item IBM_PT = convert(BM_PT);
	public static final Item IBM_BRONZE = convert(BM_BRONZE);
	public static final Item IBM_STEEL = convert(BM_STEEL);

	public static final Item IBPW_FE = convert(BPW_FE);
	public static final Item IBPW_CU = convert(BPW_CU);
	public static final Item IBPW_AG = convert(BPW_AG);
	public static final Item IBPW_AL = convert(BPW_AL);

	public static final Item[][] IOPS;
	public static final Item[][] IMPS;
	public static final Item[][] IMAS;

	static {
		String[] ores = { "iron", "gold", "copper", "tin", "silver", "lead", "uranium", "aluminum", "tungsten", "borax",
				"beryllium", "nickel", "manganese", "titanium", "cobalt", "platinum" };
		String[] type = { "_ore_powder", "_ore_powder_clean", "_ore_powder_clean_tiny" };
		int n = ores.length;
		int m = type.length;
		IOPS = new Item[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				IOPS[i][j] = generate(ores[i] + type[j], 64);
	}

	static {
		String[] metal = { "iron", "gold", "copper", "tin", "silver", "lead", "aluminum", "tungsten", "nickel",
				"manganese", "titanium", "cobalt", "platinum" };
		String[] type = { "_ingot", "_nugget", "_powder", "_powder_tiny", "_plate" };
		int n = metal.length;
		int m = type.length;
		IMPS = new Item[n][];
		for (int i = 0; i < n; i++) {
			if (i < 2) {
				IMPS[i] = new Item[m - 2];
				for (int j = 0; j < m - 2; j++)
					IMPS[i][j] = generate(metal[i] + type[j + 2], 64);
			} else {
				IMPS[i] = new Item[m];
				for (int j = 0; j < m; j++)
					IMPS[i][j] = generate(metal[i] + type[j], 64);
			}
		}
	}

	static {
		String[] allv = { "bronze", "steel", "al_alloy" };
		String[] al = { "bronze_c", "steel_c", "steel_n", "pb_alloy", "w_alloy", "ti_alloy", "co_alloy", "adv_alloy" };
		String[] type = { "_ingot", "_nugget", "_plate" };
		int n = allv.length * 4 + al.length;
		int m = type.length;
		IMAS = new Item[n][m];
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < allv.length; i++)
				for (int k = 0; k < 4; k++)
					IMAS[i * 4 + k][j] = generate(allv[i] + "_" + k + type[j], 64);
			for (int i = 0; i < al.length; i++)
				IMAS[allv.length * 4 + i][j] = generate(al[i] + type[j], 64);
		}
	}

	public static final Item IMPD_BRONZE = generate("bronze_powder", 64);

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
	public static final Item IE_U = generate("elem_u", 64);
	public static final Item IE_UO = generate("elem_uo", 64);
	public static final Item IE_BE = generate("elem_be", 64);
	public static final Item IE_BEO = generate("elem_beo", 64);

	public static final Item IET_P = generate("elem_p_tiny", 64);
	public static final Item IET_B = generate("elem_b_tiny", 64);
	public static final Item IET_PO = generate("elem_po_tiny", 64);
	public static final Item IET_BO = generate("elem_bo_tiny", 64);
	public static final Item IET_SI = generate("elem_si_tiny", 64);
	public static final Item IET_FEO = generate("elem_feo_tiny", 64);
	public static final Item IET_CAO = generate("elem_cao_tiny", 64);
	public static final Item IET_CACO3 = generate("elem_caco3_tiny", 64);
	public static final Item IET_S = generate("elem_s_tiny", 64);
	public static final Item IET_AS = generate("elem_as_tiny", 64);
	public static final Item IET_ASO = generate("elem_aso_tiny", 64);
	public static final Item IET_U = generate("elem_u_tiny", 64);
	public static final Item IET_UO = generate("elem_uo_tiny", 64);
	public static final Item IET_BE = generate("elem_be_tiny", 64);
	public static final Item IET_BEO = generate("elem_beo_tiny", 64);

	public static final Item IMW_AU = generate("gold_wire", 64);
	public static final Item IMW_W = generate("tungsten_wire", 64);

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

	private OreReg() {
		super(MODID + "_ore");
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(BO_CU);
	}

}
