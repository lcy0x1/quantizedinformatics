package com.arthurlcy0x1.quantizedinformatics.items;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class PrepChip extends Item {

	public static int getProc(ItemStack is) {
		return is.getOrCreateTag().getInt("process");
	}

	public static int getStat(ItemStack is) {
		return is.getOrCreateTag().getInt("status");
	}

	public static ItemStack setProcStat(ItemStack is, Item dopant) {
		int stat = getStat(is);
		CompoundNBT tag = is.getOrCreateTag();
		if (dopant == null)
			tag.putInt("process", 3);
		else if (dopant == Registrar.IE_DARK)
			tag.putInt("process", 1);
		else if (dopant == Items.COAL)
			tag.putInt("process", 0);
		else {
			tag.putInt("process", 2);
			if (dopant == Registrar.IE_B)
				tag.putInt("status", stat < 3 ? stat + 1 : stat + 4);
			else if (dopant == Registrar.IE_P)
				tag.putInt("status", stat < 3 ? stat + 2 : stat + 8);
			else if (dopant == Registrar.IE_FEO)
				tag.putInt("status", stat < 16 ? stat + 16 : stat + 32);
		}
		return is;
	}

	public static boolean validForDope(ItemStack is, Item dopant) {
		int proc = getProc(is);
		int stat = getStat(is);
		if (dopant == null)
			return proc == 2;
		if (dopant == Registrar.IE_DARK)
			return proc == 0 && stat < 63;
		if (dopant == Items.COAL)
			return proc == 3;
		if (proc != 1)
			return false;
		if (dopant == Registrar.IE_B)
			return stat == 0 || stat == 2 || stat == 3 || stat == 11;
		if (dopant == Registrar.IE_P)
			return stat == 0 || stat == 1 || stat == 3 || stat == 7;
		if (dopant == Registrar.IE_FEO)
			return stat == 15 || stat == 31;
		return false;
	}

	public PrepChip(Properties properties) {
		super(properties);
	}

	@Override
	public void addInformation(ItemStack is, World w, List<ITextComponent> list, ITooltipFlag b) {
		int proc = getProc(is);
		int stat = getStat(is);
		if (proc > 0)
			list.add(Translator.getTooltip("prep_chip.proc=" + proc));
		for (int i = 0; i < 6; i++)
			if ((stat & 1 << i) > 0)
				list.add(Translator.getTooltip("prep_chip.stat=" + i));
	}

}
