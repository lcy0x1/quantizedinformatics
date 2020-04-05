package com.arthurlcy0x1.quantizedinformatics.world;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.NoiseChunkGenerator;

public class QuantumCG extends NoiseChunkGenerator<QuantumGS> {

	public QuantumCG(IWorld w, BiomeProvider bp, QuantumGS gs) {
		super(w, bp, 4, 8, 128, gs, true);
	}

	protected void func_222548_a(double[] data, int x, int z) {
		func_222546_a(data, x, z, 1368.824, 684.412, 17.1103, 4.277575, 64, -3000);
	}

	protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
		return new double[] { (double) this.biomeProvider.func_222365_c(p_222549_1_, p_222549_2_), 0.0D };
	}

	protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
		return 8.0D - p_222545_1_;
	}

	protected double func_222551_g() {
		return (double) ((int) super.func_222551_g() / 2);
	}

	protected double func_222553_h() {
		return 8.0D;
	}

	public int getGroundHeight() {
		return 128;
	}

	public int getSeaLevel() {
		return 0;
	}

}
