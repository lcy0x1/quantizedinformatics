package com.arthurlcy0x1.quantizedinformatics;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class WorldGen {

	private static class QuantumOreFeature extends Feature<OreFeatureConfig> {

		public QuantumOreFeature() {
			super(OreFeatureConfig::deserialize);
		}

		@Override
		public boolean place(IWorld w, ChunkGenerator<?> g, Random rand, BlockPos pos, OreFeatureConfig config) {
			if (!config.target.func_214738_b().test(w.getBlockState(pos)))
				return false;
			for (Direction dir : Direction.values()) {
				BlockPos p = pos.offset(dir);
				if (!config.target.func_214738_b().test(w.getBlockState(p)))
					return false;
			}
			w.setBlockState(pos, config.state, 2);
			return true;
		}

	}

	public static void addOre(Feature<OreFeatureConfig> f, BlockState bs, int size, int abu, int h) {
		FillerBlockType type = FillerBlockType.NATURAL_STONE;
		OreFeatureConfig ofc = new OreFeatureConfig(type, bs, size);
		CountRangeConfig crc = new CountRangeConfig(abu, 0, 0, h);
		ConfiguredPlacement<CountRangeConfig> cp = Placement.COUNT_RANGE.func_227446_a_(crc);
		ConfiguredFeature<?, ?> cf = f.func_225566_b_(ofc).func_227228_a_(cp);

		for (BiomeType bt : BiomeType.values())
			for (BiomeEntry be : BiomeManager.getBiomes(bt))
				be.biome.addFeature(Decoration.UNDERGROUND_ORES, cf);

	}

	public static void addOres() {
		addOre(new QuantumOreFeature(), Registrar.B_FOGORE.getDefaultState(), 1, 20, 128);
	}

}
