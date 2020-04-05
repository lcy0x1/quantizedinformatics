package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.WorldGenRegion;

public class QuantumCG extends ChunkGenerator<QuantumGS> {

	private static final double NOISE_FAC = 0.003;

	private final SimplexNoiseGenerator gen;

	public QuantumCG(IWorld w, BiomeProvider bp, QuantumGS gs) {
		super(w, bp, gs);
		gen = new SimplexNoiseGenerator(new Random(w.getSeed()));
	}

	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type p_222529_3_) {
		return 0;
	}

	@Override
	public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
	}

	@Override
	public int getGroundHeight() {
		return 128;
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public void makeBase(IWorld w, IChunk c) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		BlockState bs = Registrar.BQ_AIR.getDefaultState();
		BlockState bb = Registrar.BQ_BARRIER.getDefaultState();
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 256; j++)
				for (int k = 0; k < 16; k++)
					c.setBlockState(pos.setPos(i, j, k), bs, false);
		ChunkPos p = c.getPos();
		int[] cs = new int[2];
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++) {
				cs[0] = cs[1] = 0;
				noise(cs, p, i, j);
				noise(cs, p, i + 1, j);
				noise(cs, p, i - 1, j);
				noise(cs, p, i, j + 1);
				noise(cs, p, i, j - 1);
				if (cs[0] * cs[1] > 0)
					for (int k = 0; k < 256; k++)
						c.setBlockState(pos.setPos(i, k, j), bb, false);
			}
	}

	private void noise(int[] cs, ChunkPos p, int x, int z) {
		double f = gen.getValue(NOISE_FAC * (p.x * 16 + x), NOISE_FAC * (p.z * 16 + z));
		if (f < 0)
			cs[0]++;
		else
			cs[1]++;
	}

}
