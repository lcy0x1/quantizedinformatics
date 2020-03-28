package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class SoulItem extends Item {

	public static class SoulBottle extends EncItem {

		public static ItemStack addExp(ItemStack is, int exp) {
			if (is.isEmpty() || is.getItem() != Registrar.IS_EXP) {
				ItemStack ans = new ItemStack(Registrar.IS_EXP);
				ans.getOrCreateTag().putInt("exp", exp);
				return ans;
			}
			is.getOrCreateTag().putInt("exp", is.getOrCreateTag().getInt("exp") + exp);
			return is;
		}

		public static int getExp(ItemStack is) {
			if (is.isEmpty() || is.getItem() != Registrar.IS_EXP)
				return 0;
			return is.getOrCreateTag().getInt("exp");
		}

		public SoulBottle(Properties p) {
			super(p.containerItem(Items.GLASS_BOTTLE));
		}

		@Override
		public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
			int exp = is.getOrCreateTag().getInt("exp");
			list.add(Translator.getTooltip("exp").deepCopy().appendText("" + exp));
		}

	}

	public static class SoulCollector extends SoulItem {

		public SoulCollector(Properties p) {
			super(p);
		}

		@Override
		public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
			CompoundNBT tag = is.getOrCreateChildTag("souls");
			int total = is.getOrCreateTag().getInt("total");
			list.add(Translator.getTooltip("total_soul").deepCopy().appendText("" + total));
			for (String key : tag.keySet()) {
				int count = tag.getInt(key);
				EntityType<?> et = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(key));
				list.add(et.getName().deepCopy().appendText(" x" + count));
			}
		}

		@Override
		public int getTotal(ItemStack is) {
			return is.getOrCreateTag().getInt("total");
		}

		@Override
		public void killEntity(ItemStack is, LivingEntity target) {
			CompoundNBT tag = is.getOrCreateChildTag("souls");
			int total = is.getOrCreateTag().getInt("total");
			String str = target.getType().getRegistryName().toString();
			is.getOrCreateTag().putInt("total", total + 1);
			tag.putInt(str, tag.getInt(str) + 1);
		}

	}

	public static class SoulMarker extends Item {

		public SoulMarker(Properties p) {
			super(p);
		}

		@Override
		public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
			CompoundNBT tag = is.getOrCreateChildTag("souls");
			for (String key : tag.keySet()) {
				int val = tag.getInt(key);
				if (val > 0) {
					EntityType<?> et = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(key));
					list.add(et.getName());
				}
			}
		}

		public boolean interact(ItemStack is, PlayerEntity pl, Entity target) {
			CompoundNBT tag = is.getOrCreateChildTag("souls");
			String str = target.getType().getRegistryName().toString();
			if (tag.contains(str))
				tag.remove(str);
			else
				tag.putInt(str, 1);
			return true;
		}

	}

	public static class SoulTrap extends SoulItem {

		public SoulTrap(Properties p) {
			super(p.defaultMaxDamage(64));
		}

		@Override
		public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
			CompoundNBT tag = is.getOrCreateTag();
			if (tag.contains("soul"))
				list.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("soul"))).getName());
		}

		@Override
		public int getTotal(ItemStack is) {
			return 64 - is.getDamage();
		}

		@Override
		public void killEntity(ItemStack is, LivingEntity target) {
			CompoundNBT tag = is.getOrCreateTag();
			String str = target.getType().getRegistryName().toString();
			if (!tag.contains("soul")) {
				tag.putString("soul", str);
				is.setDamage(63);
			} else if (tag.getString("soul").equals(str) && is.getDamage() > 0)
				is.setDamage(is.getDamage() - 1);
		}

		@Override
		public ActionResultType onItemUse(ItemUseContext context) {
			World world = context.getWorld();
			ItemStack is = context.getItem();
			BlockPos pos = context.getPos();
			BlockState bs = world.getBlockState(pos);
			Block block = bs.getBlock();
			EntityType<?> et = getType(is);
			if (block != Blocks.SPAWNER || et == null)
				return ActionResultType.PASS;
			if (!world.isRemote) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof MobSpawnerTileEntity) {
					AbstractSpawner as = ((MobSpawnerTileEntity) te).getSpawnerBaseLogic();
					as.setEntityType(et);
					te.markDirty();
					world.notifyBlockUpdate(pos, bs, bs, 3);
					is.getTag().remove("soul");
				}
			}
			return ActionResultType.SUCCESS;
		}

		private EntityType<?> getType(ItemStack is) {
			CompoundNBT tag = is.getOrCreateTag();
			if (tag.contains("soul") && is.getDamage() == 0)
				return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("soul")));
			return null;
		}

	}

	public SoulItem(Properties properties) {
		super(properties);
	}

	public abstract int getTotal(ItemStack is);

	@Override
	public boolean hitEntity(ItemStack is, LivingEntity target, LivingEntity attacker) {
		if (target.getHealth() > 0)
			return false;
		killEntity(is, target);
		return true;
	}

	public abstract void killEntity(ItemStack is, LivingEntity target);

}
