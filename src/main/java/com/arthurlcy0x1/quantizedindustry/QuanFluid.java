package com.arthurlcy0x1.quantizedindustry;

import com.arthurlcy0x1.quantizedinformatics.AbReg;
import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.registries.ForgeRegistryEntry;

/** liquid unit: ml */
public class QuanFluid extends ForgeRegistryEntry<QuanFluid> {

	public static class RealFluid extends QuanFluid {

		private Fluid fluid;

		public RealFluid(Fluid f) {
			fluid = f;
		}

		public Fluid getFluid() {
			return fluid;
		}

	}

	public static final QuanFluid MC_WATER = Registrar.addName(new RealFluid(Fluids.WATER), "water");

	public static final QuanFluid MC_LAVA = Registrar.addName(new RealFluid(Fluids.LAVA), "lava");
	public static final QuanFluid GAS_O2 = reg("gas_oxygen");
	public static final QuanFluid GAS_H2 = reg("gas_hydrogen");
	public static final QuanFluid GAS_N2 = reg("gas_nitrogen");

	public static final QuanFluid GAS_AIR = reg("gas_air");
	public static final QuanFluid LIQ_O2 = reg("liquid_oxygen");
	public static final QuanFluid LIQ_H2 = reg("liquid_hydrogen");

	public static final QuanFluid LIQ_N2 = reg("liquid_nitrogen");
	public static final QuanFluid OIL_CRUDE = reg("oil_crude");
	public static final QuanFluid OIL_FUEL = reg("oil_fuel");
	public static final QuanFluid OIL_LUB = reg("oil_lubricant");

	public static final QuanFluid OIL_GAS = reg("oil_gas");

	public static int getID(QuanFluid fluid) {
		return -1;// TODO
	}

	private static QuanFluid reg(String str) {
		return AbReg.addName(new QuanFluid(), str);
	}

	public double getFuelValue() {
		return 0;
	}

	public double getViscocity() {
		return 1e-6;
	}

	public boolean isFuel() {
		return false;
	}

}