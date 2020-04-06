package com.arthurlcy0x1.quantizedinformatics.world;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IChunkGeneratorFactory;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.util.ITeleporter;

public class RegWorld {

	public static final QMaze S_MAZE = new QMaze();

	public static final IStructurePieceType SPT_MAZE = getSPT("maze", QMaze.BasePiece::new);

	public static final Set<Biome> BS_Q = new HashSet<>();

	public static final Biome QIB_MAZE = getQIBiome("maze", new QIBiome(false));
	public static final Biome QIB_SS0 = getQIBiome("solid_small_lo", new QIBiome(10.6, 0, 0, 0));
	public static final Biome QIB_SS1 = getQIBiome("solid_small_mi", new QIBiome(40.7, 0, 0, 0));
	public static final Biome QIB_SS2 = getQIBiome("solid_small_hi", new QIBiome(160.8, 0, 0, 0));
	public static final Biome QIB_SM0 = getQIBiome("solid_mid_lo", new QIBiome(1.05, 1, 4, 0));
	public static final Biome QIB_SM1 = getQIBiome("solid_mid_mi", new QIBiome(1.10, 1, 4, 0));
	public static final Biome QIB_SM2 = getQIBiome("solid_mid_hi", new QIBiome(1.15, 1, 4, 0));
	public static final Biome QIB_SL0 = getQIBiome("solid_large_lo", new QIBiome(0.03, 4, 8, 1));
	public static final Biome QIB_SL1 = getQIBiome("solid_large_mi", new QIBiome(0.06, 4, 8, 1));
	public static final Biome QIB_SL2 = getQIBiome("solid_large_hi", new QIBiome(0.09, 4, 8, 1));
	public static final Biome QIB_LM0 = getQIBiome("loose_mid_lo", new QIBiome(1.05, 5, 7, 1));
	public static final Biome QIB_LM1 = getQIBiome("loose_mid_mi", new QIBiome(1.10, 5, 7, 1));
	public static final Biome QIB_LM2 = getQIBiome("loose_mid_hi", new QIBiome(1.15, 5, 7, 1));
	public static final Biome QIB_LL0 = getQIBiome("loose_large_lo", new QIBiome(0.03, 7, 12, 2));
	public static final Biome QIB_LL1 = getQIBiome("loose_large_mi", new QIBiome(0.06, 7, 12, 2));
	public static final Biome QIB_LL2 = getQIBiome("loose_large_hi", new QIBiome(0.09, 7, 12, 2));

	public static final Biome[][][] BSQS = { { { QIB_SS0 }, { QIB_SS1 }, { QIB_SS2 } },
			{ { QIB_LM0, QIB_SM0 }, { QIB_LM1, QIB_SM1 }, { QIB_LM2, QIB_SM2 } },
			{ { QIB_LL0, QIB_SL0 }, { QIB_LL1, QIB_SL1 }, { QIB_LL2, QIB_SL2 } } };

	public static final ChunkGeneratorType<QuantumGS, QuantumCG> CGT_Q = getCGT("qc", QuantumCG::new, QuantumGS::new);

	public static final ModDimension MD_Q = getMD("quantum_world", new QuantumDim.Type());

	public static final ChunkGeneratorType<?, ?>[] CGTS = { CGT_Q };

	public static final Feature<?> FS_MAZE = Registrar.addName(S_MAZE, "maze");
	public static final Feature<?> FS_FQI = Registrar.addName(QIBiome.FQI, "island");
	public static final Placement<?> PS_PQI = Registrar.addName(QIBiome.PQI, "island");

	public static final Feature<?>[] FS = { FS_MAZE, FS_FQI };
	public static final Placement<?>[] PS = { PS_PQI };

	public static final ITeleporter TELE_Q = new QuantumTele();

	public static DimensionType getDT(String str, ModDimension md) {
		ResourceLocation key = new ResourceLocation(Registrar.MODID, str);
		if (DimensionType.byName(key) == null)
			return DimensionManager.registerDimension(key, md, new PacketBuffer(Unpooled.buffer()), false);
		return DimensionType.byName(key);
	}

	public static void regDimType() {
		getDT("quantum_world", MD_Q);
	}

	private static <GS extends GenerationSettings, CG extends ChunkGenerator<GS>> ChunkGeneratorType<GS, CG> getCGT(
			String name, IChunkGeneratorFactory<GS, CG> cgnew, Supplier<GS> gsnew) {
		ChunkGeneratorType<GS, CG> ans = new ChunkGeneratorType<>(cgnew, true, gsnew);
		Registrar.addName(ans, name);
		return ans;
	}

	private static ModDimension getMD(String name, ModDimension dimtype) {
		Registrar.addName(dimtype, name);
		return dimtype;
	}

	private static Biome getQIBiome(String name, QIBiome ans) {
		Registrar.addName(ans, name);
		BS_Q.add(ans);
		return ans;
	}

	private static IStructurePieceType getSPT(String name, IStructurePieceType type) {
		ResourceLocation key = new ResourceLocation(Registrar.MODID, name);
		Registry.register(Registry.STRUCTURE_PIECE, key, type);
		return type;
	}

	public static boolean isQuantumWorld(World w) {
		return w.getDimension().getType().getModType() == MD_Q;
	}
	
}
