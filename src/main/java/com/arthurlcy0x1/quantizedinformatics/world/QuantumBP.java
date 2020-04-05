package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public class QuantumBP extends BiomeProvider {

	public static class QuantumBPS implements IBiomeProviderSettings {

		private final Random rand;

		public QuantumBPS(Random r) {
			rand = r;
		}

		private Random getRand() {
			return rand;
		}

	}

	private static final int OFF = 1024;

	private final SimplexNoiseGenerator generator;

	protected QuantumBP(QuantumBPS settings) {
		super(RegWorld.BS_Q);
		this.generator = new SimplexNoiseGenerator(settings.getRand());
	}

	public Biome func_225526_b_(int x, int y, int z) {
		double dx = x / 16.0;
		double dz = z / 16.0;
		double f0 = this.generator.getValue(dx, dz);
		double f1 = this.generator.getValue(dx + OFF, dz + OFF);
		double f2 = this.generator.getValue(dx - OFF, dz - OFF);
		Biome[][] b0 = RegWorld.BSQS[(int) ((f0 + 1) / 2 * 3)];
		Biome[] b1 = b0[(int) ((f1 + 1) / 2 * 3)];
		if (b1.length == 1)
			return b1[0];
		else
			return f2 < 0 ? b1[0] : b1[1];
	}

}
