package com.arthurlcy0x1.quantizedinformatics.entities;

import java.util.TreeSet;
import java.util.function.Predicate;

import com.arthurlcy0x1.quantizedinformatics.Registrar;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface QuanMob {

	public static class NearestGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

		private static Predicate<LivingEntity> append(Predicate<LivingEntity> pred) {
			return e -> isValid(e) && pred.test(e);
		}

		private static boolean isValid(LivingEntity e) {
			if (e instanceof QuanMob)
				return false;
			return true;
		}

		public NearestGoal(MobEntity mob, Class<T> cls) {
			super(mob, cls, 10, true, false, NearestGoal::isValid);
		}

		public NearestGoal(MobEntity mob, Class<T> cls, Predicate<LivingEntity> pred) {
			super(mob, cls, 10, true, false, append(pred));
		}

	}

	public static BlockPos genDrop(World w, BlockPos ps, int h, int r) {
		TreeSet<BlockPos> set = new TreeSet<>((a, b) -> a.manhattanDistance(ps) - b.manhattanDistance(ps));
		for (int i = -r; i <= r; i++)
			for (int j = -r; j <= r; j++)
				for (int k = -r; k <= r; k++)
					set.add(ps.add(i, j, k));
		for (BlockPos p : set)
			if (validLocation(w, p, h))
				return p;
		return null;
	}

	public static boolean validLocation(World w, BlockPos ps, int h) {
		for (int i = 0; i < h; i++) {
			BlockState bs = w.getBlockState(ps.add(0, i, 0));
			if (bs.getBlock() != Registrar.BQ_AIR && !bs.isAir(w, ps))
				return false;
		}
		return true;
	}

}
