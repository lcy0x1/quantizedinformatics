package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate;
import com.arthurlcy0x1.quantizedinformatics.blocks.DraftWire;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;

public class Registrar extends ItemGroup {

	public static final String MODID = "quantizedinformatics";

	public static final Block B_FOG = generate("quantumfog", Material.EARTH);
	public static final Block BD_WIRE = new DraftWire().setRegistryName(MODID, "draftwire");
	public static final Block BD_GATE = new DraftGate().setRegistryName(MODID, "draftgate");

	public static final ItemGroup ITEM_GROUP = new Registrar();

	public static final Item IB_FOG = convert(B_FOG);
	public static final Item IBD_WIRE = convert(BD_WIRE);
	public static final Item IBD_GATE = convert(BD_GATE);

	private static Block generate(String str, Material mat) {
		Block.Properties p = Block.Properties.create(mat);
		return new Block(p).setRegistryName(MODID, str);
	}

	private static Item convert(Block block) {
		Item.Properties p = new Item.Properties();
		p.group(ITEM_GROUP);
		return new BlockItem(block, p).setRegistryName(block.getRegistryName());
	}

	private Registrar() {
		super(MODID);
	}

	public ItemStack createIcon() {
		return new ItemStack(B_FOG);
	}

	protected static void registerBlock(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(B_FOG,BD_WIRE,BD_GATE);
	}

	protected static void registerItem(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(IB_FOG,IBD_WIRE,IBD_GATE);
	}

}
