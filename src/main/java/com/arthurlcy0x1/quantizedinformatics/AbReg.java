package com.arthurlcy0x1.quantizedinformatics;

import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AbReg extends ItemGroup {

	public static final String MODID = "quantizedinformatics";

	public static <T extends ForgeRegistryEntry<T>> T addName(T t, String name) {
		return t.setRegistryName(MODID, name);
	}

	public static Block generate(String str, BlockProp mat) {
		return addName(new Block(mat.getProps()), str);
	}

	public static <T extends Container> ContainerType<T> getCT(ContainerType.IFactory<T> fact, String str) {
		ContainerType<T> ans = new ContainerType<>(fact);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	public static <T extends ForgeRegistryEntry<IRecipeSerializer<?>>> T getRS(T rs, String str) {
		rs.setRegistryName(MODID, str);
		return rs;
	}

	public static <T extends TileEntity> TileEntityType<T> getTET(Supplier<T> fact, Block b, String str) {
		TileEntityType<T> ans = TileEntityType.Builder.create(fact, b).build(null);
		ans.setRegistryName(MODID, str);
		return ans;
	}

	public AbReg(String label) {
		super(label);
	}

}
