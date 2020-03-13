package com.arthurlcy0x1.quantizedinformatics.items;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public abstract class LogicDraft extends Item {

	public static class CompoundLogicDraft extends LogicDraft {

		public CompoundLogicDraft(Properties p) {
			super(p);
		}

		@Override
		public LogicGate getLogicGate(ItemStack is) {
			CompoundNBT nbt = is.getTag();
			if (nbt == null)
				return null;
			return LogicGate.decode(nbt);
		}

	}

	private static class SimpleLogicDraft extends LogicDraft {

		private final LogicGate gate;

		public SimpleLogicDraft(Properties p, int t, int n) {
			super(p);
			gate = LogicGate.getPrimeGate(t, n);
		}

		@Override
		public LogicGate getLogicGate(ItemStack is) {
			return gate;
		}

	}

	public static LogicDraft getSimple(Properties p, String name) {
		int n = -1;
		for (int i = 3; i <= 6; i++)
			if (name.endsWith("_" + i)) {
				n = i;
				name = name.substring(0, name.length() - 2);
				break;
			}
		int t = -1;
		String[] gate = { "_buff", "_not", "_nand", "_nor", "_and", "_or", "_xor" };
		for (int i = 0; i < gate.length; i++)
			if (name.endsWith(gate[i])) {
				t = i;
				if (n < 0)
					n = i < 2 ? 1 : 2;
			}
		return new SimpleLogicDraft(p, t, n);
	}

	public LogicDraft(Properties p) {
		super(p);
	}

	public abstract LogicGate getLogicGate(ItemStack is);

}
