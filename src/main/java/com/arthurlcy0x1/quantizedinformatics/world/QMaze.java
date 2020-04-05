package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.Random;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class QMaze extends Structure<NoFeatureConfig> {

	public static class BasePiece extends StructurePiece {

		public BasePiece(TemplateManager tm, CompoundNBT tag) {
			super(RegWorld.SPT_MAZE, tag);
		}

		private BasePiece(BlockPos p, int rx, int ry, int rz) {
			super(RegWorld.SPT_MAZE, 0);
			BlockPos min = p.add(-rx * 4 - 2, -ry * 4 - 2, -rz * 4 - 2);
			BlockPos max = p.add(rx * 4 + 2, ry * 4 + 2, rz * 4 + 2);
			this.boundingBox = new MutableBoundingBox(min, max);
			LogManager.getLogger().warn("maze piece instance created");// TODO
		}

		@Override
		public boolean func_225577_a_(IWorld w, ChunkGenerator<?> cg, Random r, MutableBoundingBox box, ChunkPos cpos) {
			LogManager.getLogger().warn("build maze start");// TODO
			int x0 = boundingBox.minX;
			int y0 = boundingBox.minY;
			int z0 = boundingBox.minZ;
			Vec3i len = boundingBox.getLength();
			for (int i = 0; i < len.getX(); i += 4)
				for (int j = 0; j < len.getY(); j += 4)
					for (int k = 0; k < len.getZ(); k += 4) {
						int x1 = x0 + i + 2;
						int y1 = y0 + j + 2;
						int z1 = z0 + k + 2;
						setBlockState(w, r, x1 - 2, y1, z1);
						setBlockState(w, r, x1, y1 - 2, z1);
						setBlockState(w, r, x1, y1, z1 - 2);
						setBlockState(w, r, x1 + 2, y1, z1);
						setBlockState(w, r, x1, y1 + 2, z1);
						setBlockState(w, r, x1, y1, z1 + 2);
					}
			return true;
		}

		private void setBlockState(IWorld w, Random r, int x, int y, int z) {
			if (r.nextDouble() < CHANCE)
				setBlockState(w, WALL, x, y, z);
		}

		private void setBlockState(IWorld w, BlockState b, int x, int y, int z) {
			this.setBlockState(w, b, x, y, z, boundingBox);
		}

		@Override
		protected void readAdditional(CompoundNBT tag) {
		}

	}

	private static class Start extends StructureStart {

		public Start(Structure<?> str, int cx, int cz, MutableBoundingBox box, int ref, long seed) {
			super(str, cx, cz, box, ref, seed);
		}

		@Override
		public void init(ChunkGenerator<?> cg, TemplateManager tm, int cx, int cz, Biome b) {
			BlockPos pos = new BlockPos(cx * 16 + 8, 126, cz * 16 + 8);
			components.add(new BasePiece(pos, 31, 31, 31));
			recalculateStructureSize();
		}

	}

	private static final double CHANCE = 0.1;
	private static final BlockState WALL = Registrar.BQ_MAZEWALL.getDefaultState();

	public QMaze() {
		super(NoFeatureConfig::deserialize);
	}

	@Override
	public boolean func_225558_a_(BiomeManager bm, ChunkGenerator<?> cg, Random r, int cx, int cz, Biome b) {
		return cx == 0 && cz == 0 && b.hasStructure(RegWorld.S_MAZE);
	}

	@Override
	public int getSize() {
		return 8;
	}

	@Override
	public IStartFactory getStartFactory() {
		return Start::new;
	}

	@Override
	public String getStructureName() {
		return Registrar.MODID + ":maze";
	}

	public ConfiguredFeature<NoFeatureConfig, QMaze> configure() {
		return new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG);
	}

	public ConfiguredFeature<?, ?> reconfigure() {
		return configure().func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
	}

}
