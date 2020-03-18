package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class AutoRecipe extends Item {

	public static ItemStack addTag(NonNullList<ItemStack> list, ItemStack res) {
		CompoundNBT tag = new CompoundNBT();
		tag.putByte("length", (byte) list.size());
		ItemStackHelper.saveAllItems(tag, list);
		res.write(tag);
		ItemStack ans = new ItemStack(Registrar.IA_RECIPE);
		ans.setTagInfo("recipe", tag);
		return ans;
	}

	public AutoRecipe(Properties properties) {
		super(properties);
	}

	@Override
	public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
		CompoundNBT tag = is.getChildTag("recipe");
		if (tag == null)
			return;
		int len = tag.getInt("length");
		NonNullList<ItemStack> lis = NonNullList.withSize(len, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, lis);
		ItemStack res = ItemStack.read(tag);
		list.add(Translator.getTooltip("ingredient"));
		for (ItemStack i : lis)
			list.add(i.getDisplayName().shallowCopy().appendText(" x" + i.getCount()));
		list.add(Translator.getTooltip("result"));
		list.add(res.getDisplayName());
	}

}
