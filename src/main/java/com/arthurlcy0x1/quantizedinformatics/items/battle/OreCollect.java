package com.arthurlcy0x1.quantizedinformatics.items.battle;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.ItemUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreCollect extends Item {

	public OreCollect(Properties p) {
		super(p.maxDamage(240));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext iuc) {
		World w = iuc.getWorld();
		PlayerEntity pl = iuc.getPlayer();
		BlockPos p = getTarget(w, iuc.getPos());

		if (iuc.getWorld().isRemote)
			return p != null ? ActionResultType.SUCCESS : ActionResultType.PASS;
		if (p != null) {
			int c = 0;
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++)
					for (int k = -1; k <= 1; k++) {
						BlockPos bp = p.add(i, j, k);
						if (w.getBlockState(bp).getBlock() == Registrar.B_FOG) {
							c++;
							w.destroyBlock(bp, false);
						}
					}
			w.destroyBlock(p, false);
			ItemStack is = new ItemStack(c >= 18 ? Registrar.I_FOGBALL : Registrar.IB_FOGORE);
			ItemUtil.drop(is, pl);
			ItemUtil.damageItem(iuc.getItem(), pl);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	private BlockPos getTarget(World w, BlockPos cl) {
		if (w.getBlockState(cl).getBlock() != Registrar.B_FOG)
			return null;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				for (int k = -1; k <= 1; k++) {
					BlockPos p = cl.add(i, j, k);
					if (w.getBlockState(p).getBlock() == Registrar.B_FOGORE)
						return p;
				}
		return null;
	}

}
