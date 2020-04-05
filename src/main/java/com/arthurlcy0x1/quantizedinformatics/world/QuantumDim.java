package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.world.QuantumBP.QuantumBPS;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ModDimension;

public class QuantumDim extends Dimension {

	public static class Type extends ModDimension {

		@Override
		public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
			return QuantumDim::new;
		}

	}

	public QuantumDim(World w, DimensionType type) {
		super(w, type, 0);
	}

	@Override
	@Nullable
	@OnlyIn(Dist.CLIENT)
	public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return 0.0F;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		BiomeProvider bp = new QuantumBP(new QuantumBPS(world.getRandom()));
		QuantumGS gs = RegWorld.CGT_Q.createSettings();
		gs.setDefaultBlock(Registrar.BQ_STONE.getDefaultState());
		gs.setDefaultFluid(Registrar.BQ_AIR.getDefaultState());
		return RegWorld.CGT_Q.create(world, bp, gs);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
		return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		return new Vec3d(1, 1, 1);
	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

}
