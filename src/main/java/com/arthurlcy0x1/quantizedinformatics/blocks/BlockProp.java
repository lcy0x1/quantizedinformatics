package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockProp {

	public static final BlockProp ORE_0 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp ORE_1 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 1);
	public static final BlockProp ORE_2 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 2);
	

	public static final BlockProp METAL_0 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp METAL_1 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 1);
	public static final BlockProp METAL_2 = new BlockProp(Material.ROCK, 3, 3).setTool(ToolType.PICKAXE, 2);

	public static final BlockProp WIRE = new BlockProp(Material.MISCELLANEOUS, 0.2f, 0.2f);
	public static final BlockProp FURNACE = new BlockProp(Material.ROCK, 50, 1200).setTool(ToolType.PICKAXE, 3);
	public static final BlockProp QUANTUM_FOG = new BlockProp(Material.ROCK, 1, 1).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp QUANTUM_ORE = new BlockProp(Material.ROCK, 2, 2);
	public static final BlockProp M_DRAFT = new BlockProp(Material.ROCK, 2, 2).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp M_CRAFT = new BlockProp(Material.ROCK, 2, 2).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp M_PIPE = new BlockProp(Material.WOOD, 2, 2).setTool(ToolType.AXE, 0);
	public static final BlockProp M_ENT = new BlockProp(Material.ROCK, 2, 2).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp M_POWER = new BlockProp(Material.ROCK, 2, 2).setTool(ToolType.PICKAXE, 0);
	public static final BlockProp QUANTUM_WORLD = new BlockProp(Material.ROCK, -1, 3600000);
	public static final BlockProp QW_BREAK = new BlockProp(Material.ROCK, 2, 3600000);
	public static final BlockProp STONE_STAND = new BlockProp(
			Properties.create(Material.MISCELLANEOUS).noDrops().func_226896_b_(), 2, 3600000).setTool(ToolType.PICKAXE,
					0);

	public static final BlockProp PORTAL = new BlockProp(Properties.create(Material.PORTAL).doesNotBlockMovement(), 2,
			2);

	private final Properties props;

	private BlockProp(Material mat, float hard, float rest) {
		this(Properties.create(mat), hard, rest);
	}

	private BlockProp(Properties mat, float hard, float rest) {
		props = mat;
		props.hardnessAndResistance(hard, rest);
	}

	public Block.Properties getProps() {
		return props;
	}

	private BlockProp setTool(ToolType tool, int level) {
		props.harvestTool(tool);
		props.harvestLevel(level);
		return this;
	}

}
