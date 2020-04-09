package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;
import java.util.stream.Stream;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.SimplePlacement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class QIBiome extends Biome {

	private static class QIFC implements IFeatureConfig {

		private static QIFC deserialize(Dynamic<?> data) {
			return new QIFC(data.get("min_rad").asInt(0), data.get("max_rad").asInt(0), data.get("type").asInt(0));
		}

		private final int r0, r1, type;

		private QIFC(int s0, int s1, int t) {
			r0 = s0;
			r1 = s1;
			type = t;
		}

		@Override
		public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
			return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("min_rad"), ops.createInt(r0),
					ops.createString("max_rad"), ops.createInt(r1), ops.createString("type"), ops.createInt(type))));
		}

		private boolean fill(Random r, int rad, double dis) {
			return r.nextDouble() < Math.pow(1 - dis / rad, type);
		}

		private int getSize(Random r) {
			return (int) (r0 + (r1 - r0) * r.nextDouble());
		}
	}

	private static class QIPC implements IPlacementConfig {

		private static QIPC deserialize(Dynamic<?> data) {
			return new QIPC(data.get("chance").asDouble(0), data.get("stdev").asInt(DEF_STDEV));
		}

		private final double chance;

		private final int stdev;

		private QIPC(double ch, int std) {
			chance = ch;
			stdev = std;
		}

		@Override
		public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
			return new Dynamic<>(ops, (ops.createMap(ImmutableMap.of(ops.createString("chance"),
					ops.createDouble(chance), ops.createString("stdev"), ops.createInt(stdev)))));
		}

		private boolean addGen(Random r, BlockPos p, int h) {
			double rand = r.nextDouble();
			double nor = normal(h, CENTER, stdev);
			double ch = chance % 1;
			// if (rand < ch * nor)
			// System.out.println("r = " + rand + ", ch = " + ch + ", nor = " + nor + ", suc
			// = " + (rand < ch * nor));
			return rand < ch * nor;

		}

		private int[] getGen(Random r) {
			int[] arr = new int[(int) chance];
			for (int i = 0; i < arr.length; i++)
				arr[i] = (int) ((r.nextGaussian() + 1) * CENTER);
			return arr;
		}

	}

	private static class QuantumIslandFeature extends Feature<QIFC> {

		public QuantumIslandFeature() {
			super(QIFC::deserialize);
		}

		@Override
		public boolean place(IWorld w, ChunkGenerator<? extends GenerationSettings> gen, Random rand, BlockPos pos,
				QIFC config) {
			int size = config.getSize(rand);
			size++;
			for (int i = -size; i <= size; i++)
				for (int j = -size; j <= size; j++)
					for (int k = -size; k <= size; k++) {
						BlockPos po = pos.add(i, j, k);
						BlockState pi = w.getBlockState(po);
						if (pi.getBlock() == Blocks.VOID_AIR || !pi.isAir(w, po))
							return false;
					}
			size--;
			for (int i = -size; i <= size; i++)
				for (int j = -size; j <= size; j++)
					for (int k = -size; k <= size; k++) {
						BlockPos pi = pos.add(i, j, k);
						double dis2 = Math.sqrt(i * i + j * j + k * k);
						if (dis2 > size + 0.5)
							continue;
						int dis = Math.abs(i) + Math.abs(j) + Math.abs(k);
						if (dis < 0.5)
							setBlockState(w, pi, size < MIN_CORE ? BQ_STONE : BQ_CORE);
						else if (dis < 1.2 || config.fill(rand, size, dis2))
							setBlockState(w, pi, BQ_STONE);
					}
			return true;
		}

		private ConfiguredFeature<?, ?> configure(QIFC qifc, QIPC qipc) {
			return new ConfiguredFeature<>(this, qifc).func_227228_a_(PQI.configure(qipc));
		}

	}

	private static class QuantumIslandPlacement extends SimplePlacement<QIPC> {

		public QuantumIslandPlacement() {
			super(QIPC::deserialize);
		}

		@Override
		protected Stream<BlockPos> getPositions(Random r, QIPC config, BlockPos pos) {
			Stream.Builder<BlockPos> s = Stream.builder();
			for (int h : config.getGen(r))
				s.add(getRand(r, pos, h));
			for (int i = 0; i < 16; i++)
				if (config.addGen(r, pos, i * 16))
					s.add(getRand(r, pos, i * 16));
			return s.build();
		}

		private ConfiguredPlacement<QIPC> configure(QIPC qipc) {
			return new ConfiguredPlacement<QIPC>(this, qipc);
		}

		private BlockPos getRand(Random r, BlockPos pos, int h) {
			return pos.add(r.nextInt(16), h + r.nextInt(16), r.nextInt(16));
		}

	}

	private static final double fac = Math.sqrt(2 * Math.PI);

	private static final int DEF_STDEV = 64, CENTER = 128, MIN_CORE = 5;

	private static final int COL_WATER = 4159204;

	private static final int COL_WATER_FOG = 4159204;
	private static final BlockState BQ_STONE = Registrar.BQ_STONE.getDefaultState();
	private static final BlockState BQ_CORE = Registrar.BQ_CORE.getDefaultState();
	public static final SurfaceBuilderConfig SBC_Q = new SurfaceBuilderConfig(BQ_STONE, BQ_STONE, BQ_STONE);

	public static final QuantumIslandFeature FQI = new QuantumIslandFeature();
	public static final QuantumIslandPlacement PQI = new QuantumIslandPlacement();

	private static final QIFC FC_DEF = new QIFC(10, 0, 0);
	private static final QIPC PC_DEF = new QIPC(10.6, DEF_STDEV);

	public static Builder getBuilder() {
		Builder b = new Builder();
		b.precipitation(Biome.RainType.NONE);
		b.category(Biome.Category.NONE);
		b.depth(0.1F);
		b.scale(0.2F);
		b.temperature(0.5F);
		b.downfall(0.5F);
		b.waterColor(COL_WATER);
		b.waterFogColor(COL_WATER_FOG);
		b.parent(null);
		return b;
	}

	public static double normal(double x, double mean, double stdev) {
		double n = (x - mean) / stdev;
		return Math.exp(-0.5 * n * n) / stdev / fac;
	}

	protected QIBiome(boolean gen) {
		this();
		// addSpawn(EntityClassification.MONSTER, new
		// Biome.SpawnListEntry(Registrar.ETM_QS, 100, 4, 4));
	}

	protected QIBiome(double ch, int r0, int r1, int type) {
		this();
		QIFC qifc = new QIFC(r0, r1, type);
		QIPC qipc = new QIPC(ch, DEF_STDEV);
		addFeature(GenerationStage.Decoration.RAW_GENERATION, FQI.configure(qifc, qipc));
		addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(Registrar.ETM_QF, 50, 4, 4));

		if (r0 + r1 > 0)
			addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, FQI.configure(FC_DEF, PC_DEF));

	}

	private QIBiome() {
		super(getBuilder().surfaceBuilder(SurfaceBuilder.DEFAULT, SBC_Q));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int func_225529_c_() {
		return 0;
	}

}
