package com.arthurlcy0x1.quantizedinformatics.items;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

import net.minecraft.item.Item;

public abstract class LogicDraft extends Item implements DraftLogicGate {

	private static class SimpleLogicDraft extends LogicDraft {

		private final int type;
		
		public SimpleLogicDraft(Properties p, int t) {
			super(p);
			type = t;
		}
		

		@Override
		public LogicGate getLogicGate() {
			return LogicGate.getPrimeGate(type, 2);//TODO
		}

		
	}
	
	public static LogicDraft getSimple(Properties p, int type) {
		return null;//TODO
	}
	
	public LogicDraft(Properties p) {
		super(p);
	}

}
