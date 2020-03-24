package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public interface Telescope {

	public static class TelescopeItem extends Item implements Telescope {

		public TelescopeItem(Properties p) {
			super(p);
		}

		@Override
		public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
			addInfo(is, w, list, b);
		}

		@Override
		public UseAction getUseAction(ItemStack stack) {
			return UseAction.BOW;
		}

		@Override
		public int getUseDuration(ItemStack stack) {
			return 72000;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World w, PlayerEntity pl, Hand hand) {
			ItemStack is = pl.getHeldItem(hand);
			pl.setActiveHand(hand);
			return ActionResult.func_226249_b_(is);
		}

	}

	public static final int MAX = 14;

	public static void addInfo(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
		float zoom = Math.round(100 / getItemZoom(is)) * 0.01f;
		list.add(Translator.getTooltip("magnification").shallowCopy().appendText("x" + zoom));

	}

	public static float getItemZoom(ItemStack is) {
		return ((Telescope) is.getItem()).getZoom(is);
	}

	public static int getLv(ItemStack is) {
		return MathHelper.clamp(is.getOrCreateTag().getInt("telescope"), 1, MAX);
	}

	public static ItemStack setLv(ItemStack is, int lv) {
		is.getOrCreateTag().putInt("telescope", MathHelper.clamp(lv, 1, MAX));
		return is;
	}

	public default float getZoom(ItemStack is) {
		float base = 0.85f;
		int lv = getLv(is);
		return (float) Math.pow(base, lv);
	}

}
