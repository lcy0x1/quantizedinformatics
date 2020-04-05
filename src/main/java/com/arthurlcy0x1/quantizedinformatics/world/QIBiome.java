package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;
import java.util.stream.Stream;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
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

	private static final double fac = Math.sqrt(2 * Math.PI);
	private static final int DEF_STDEV = 64, CENTER = 128, MIN_CORE = 5;

	private static double normal(double x, double mean, double stdev) {
		double n = (x - mean) / stdev;
		return Math.exp(-0.5 * n * n) / stdev / fac;
	}

	private static class QIPC implements IPlacementConfig {

		private final double chance;
		private final int stdev;

		private QIPC(double ch, int std) {
			chance = ch;
			stdev = std;
		}

		@Override
		public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
			return new Dynamic<>(ops, (T) (ops.createMap(ImmutableMap.of(ops.createString("chance"),
					ops.createDouble(chance), ops.createString("stdev"), ops.createInt(stdev)))));
		}

		private static QIPC deserialize(Dynamic<?> data) {
			return new QIPC(data.get("chance").asDouble(0), data.get("stdev").asInt(DEF_STDEV));

		}

		private boolean addGen(Random r, BlockPos p, int h) {
			return r.nextDouble() > normal(h, CENTER, stdev);

		}

	}

	private static class QuantumIslandPlacement extends SimplePlacement<QIPC> {

		public QuantumIslandPlacement() {
			super(QIPC::deserialize);
		}

		@Override
		protected Stream<BlockPos> getPositions(Random r, QIPC config, BlockPos pos) {
			Stream.Builder<BlockPos> s = Stream.builder();
			for (int i = 0; i < 16; i++)
				while (config.addGen(r, pos, i * 16))
					s.add(getRand(r, pos, i * 16));
			return s.build();
		}

		private BlockPos getRand(Random r, BlockPos pos, int h) {
			return pos.add(r.nextInt(16), h + r.nextInt(16), r.nextInt(16));
		}

		private ConfiguredPlacement<QIPC> configure(QIPC qipc) {
			return new ConfiguredPlacement<QIPC>(this, qipc);
		}

	}

	private static class QIFC implements IFeatureConfig {

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

		private static QIFC deserialize(Dynamic<?> data) {
			return new QIFC(data.get("min_rad").asInt(0), data.get("max_rad").asInt(0), data.get("type").asInt(0));
		}

		private int getSize(Random r) {
			return (int) (r0 + (r1 - r0) * r.nextDouble());
		}

		private boolean fill(Random r, int rad, double dis) {
			return r.nextDouble() < Math.pow(1 - dis / rad, type);
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
						BlockPos pi = pos.add(i, j, k);
						if (!w.getBlockState(pi).isAir(w, pi))
							return false;
					}
			size--;
			for (int i = -size; i <= size; i++)
				for (int j = -size; j <= size; j++)
					for (int k = -size; k <= size; k++) {
						BlockPos pi = pos.add(i, j, k);
						double dis2 = Math.sqrt(pi.distanceSq(pos));
						if (dis2 > size)
							continue;
						int dis = Math.abs(i) + Math.abs(j) + Math.abs(k);
						if (dis == 0)
							setBlockState(w, pi, size < MIN_CORE ? BQ_STONE : BQ_CORE);
						else if (dis == 1 || config.fill(rand, size, dis2))
							setBlockState(w, pi, BQ_STONE);
					}
			return true;
		}

		private ConfiguredFeature<?, ?> configure(QIFC qifc, QIPC qipc) {
			return new ConfiguredFeature<>(this, qifc).func_227228_a_(PQI.configure(qipc));
		}

	}

	private static final int COL_WATER = 4159204;
	private static final int COL_WATER_FOG = 4159204;
	private static final int SP_WEIGHT = 10;
	private static final int SP_GROUP = 4;

	private static final BlockState BQ_STONE = Registrar.BQ_STONE.getDefaultState();
	private static final BlockState BQ_CORE = Registrar.BQ_CORE.getDefaultState();

	public static final SurfaceBuilderConfig SBC_Q = new SurfaceBuilderConfig(BQ_STONE, BQ_STONE, BQ_STONE);

	private static final EntityType<?> MONSTER = EntityType.ENDERMAN;

	private static final QuantumIslandFeature FQI = new QuantumIslandFeature();
	private static final QuantumIslandPlacement PQI = new QuantumIslandPlacement();

	private static Builder getBuilder() {
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

	protected QIBiome(double ch, int r0, int r1, int type) {
		super(getBuilder().surfaceBuilder(SurfaceBuilder.DEFAULT, SBC_Q));
		QIFC qifc = new QIFC(r0, r1, type);
		QIPC qipc = new QIPC(ch, DEF_STDEV);
		addFeature(GenerationStage.Decoration.RAW_GENERATION, FQI.configure(qifc, qipc));
		addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(MONSTER, SP_WEIGHT, SP_GROUP, SP_GROUP));
	}

	@OnlyIn(Dist.CLIENT)
	public int func_225529_c_() {
		return 0;
	}

}
