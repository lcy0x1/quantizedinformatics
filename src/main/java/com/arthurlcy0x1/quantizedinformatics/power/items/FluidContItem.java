package com.arthurlcy0x1.quantizedinformatics.power.items;

import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.QuanFluid;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidContItem extends Item {

	public static ItemStack setFluid(ItemStack is, QuanFluid f, double val) {
		if (is.isEmpty() || !(is.getItem() instanceof FluidContItem))
			return is;
		String fluid = f.getRegistryName().toString();
		if (val < getMaxStorage(is) * IFluidTE.ERR) {
			val = 0;
			fluid = "";
		}
		CompoundNBT tag = is.getOrCreateChildTag("quanfluid");
		tag.putString("fluid", fluid);
		tag.putDouble("amount", val);
		return is;
	}

	public static QuanFluid getFluid(ItemStack is) {
		if (is.isEmpty() || !(is.getItem() instanceof FluidContItem))
			return null;
		CompoundNBT tag = is.getOrCreateChildTag("quanfluid");
		String str = tag.getString("fluid");
		if (str.length() == 0)
			return null;
		ResourceLocation key = new ResourceLocation(str);
		return GameRegistry.findRegistry(QuanFluid.class).getValue(key);
	}

	public static double getMaxStorage(ItemStack is) {
		if (is.isEmpty() || !(is.getItem() instanceof FluidContItem))
			return 0;
		CompoundNBT tag = is.getOrCreateChildTag("quanfluid");
		double max = tag.getDouble("max");
		if (max == 0)
			return ((FluidContItem) is.getItem()).defMax;
		return max;
	}

	public static double getStorage(ItemStack is) {
		if (is.isEmpty() || !(is.getItem() instanceof FluidContItem))
			return 0;
		CompoundNBT tag = is.getOrCreateChildTag("quanfluid");
		return tag.getDouble("amount");
	}

	private final double defMax;

	public FluidContItem(Properties p, double storage) {
		super(p);
		defMax = storage;
	}

}
