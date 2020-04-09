package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.utils.maze.MazeUtil;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
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

	private static int countBit(int cell) {
		int c = 0;
		for (int i = 0; i < 4; i++)
			if ((cell & 1 << i) > 0)
				c++;
		return c;
	}

	private static Direction face(int cell) {
		int c = countBit(cell);
		if ((cell & 16) > 0)
			return c == 1 ? Direction.WEST : Direction.EAST;
		if ((cell & 64) > 0)
			return c == 1 ? Direction.NORTH : Direction.SOUTH;
		return null;
	}

	private final SimplexNoiseGenerator gen;
	private final Random mazeRan;

	private final int[][][] state = new int[63][][];

	public QuantumCG(IWorld w, BiomeProvider bp, QuantumGS gs) {
		super(w, bp, gs);
		LogManager.getLogger().warn("Chunk Generator Created");
		gen = new SimplexNoiseGenerator(new Random(w.getSeed()));
		mazeRan = new Random(w.getSeed());
		for (int i = 0; i < 63; i++)
			state[i] = MazeUtil.generate(31, 64, 16, mazeRan);
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
		if (Math.abs(c.getPos().x + 0.5) <= 8 && Math.abs(c.getPos().z + 0.5) <= 8)
			buildMaze(w, c);
		else
			buildWall(w, c);
	}

	private void buildMaze(IWorld w, IChunk c) {
		ChunkPos cpos = c.getPos();
		BlockPos.Mutable pos = new BlockPos.Mutable();
		BlockState qair = Registrar.BQ_AIR.getDefaultState();
		BlockState wall = Registrar.BQ_MAZEWALL.getDefaultState();
		BlockState stand = Registrar.B_STAND.getDefaultState();
		int maxhor = 4 * 31 + 2;
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 256; j++)
				for (int k = 0; k < 16; k++) {
					int x = cpos.x * 16 + i;
					int z = cpos.z * 16 + k;
					if (x < -maxhor || x > maxhor || z < -maxhor || z > maxhor || j >= 252) {
						c.setBlockState(pos.setPos(i, j, k), qair, false);
						continue;
					}
					if (j == 0 || Math.abs(x) == maxhor || Math.abs(z) == maxhor) {
						c.setBlockState(pos.setPos(i, j, k), wall, false);
						continue;
					}
					int cell = state[(j - 1) / 4][(x + maxhor) / 4][(z + maxhor) / 4];
					int ix = (x + maxhor) % 4;
					int iz = (z + maxhor) % 4;
					int dx = 2 - Math.abs(2 - Math.abs(x) % 4);
					int dy = Math.abs(j % 4 - 2);
					int dz = 2 - Math.abs(2 - Math.abs(z) % 4);
					boolean sw = false;
					sw |= dx * dz == 4;
					sw |= (cell & 1) == 0 && ix == 0;
					sw |= (cell & 4) == 0 && iz == 0;
					sw |= (cell & 16) > 0 && ix == 0 && (j % 4 == 3 || dz != 0);
					sw |= (cell & 64) > 0 && iz == 0 && (j % 4 == 3 || dx != 0);
					sw |= dy == 2;
					BlockState bs = sw ? wall : qair;
					if (dx + dz == 0 && j % 4 == 1 && countBit(cell) == 1 && (cell & 0xf0) > 0) {
						if ((cell & 256) > 0)
							bs = Registrar.BQ_KEY.getDefaultState();
						else
							bs = Registrar.BQ_CHEST.getDefaultState();
					}
					if ((cell & 16) > 0 && ix == 0 && j % 4 == 1 && dz == 0)
						bs = stand.with(BaseBlock.HORIZONTAL_FACING, face(cell));
					if ((cell & 64) > 0 && iz == 0 && j % 4 == 1 && dx == 0)
						bs = stand.with(BaseBlock.HORIZONTAL_FACING, face(cell));
					c.setBlockState(pos.setPos(i, j, k), bs, false);
				}
	}

	private void buildWall(IWorld w, IChunk c) {
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
