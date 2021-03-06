package com.arthurlcy0x1.quantizedinformatics;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Translator {

	private static final Map<String, ITextComponent> MAP = new HashMap<>();

	public static ITextComponent get(String str) {
		if (MAP.containsKey(str))
			return MAP.get(str);
		ITextComponent text = new TranslationTextComponent(str);
		MAP.put(str, text);
		return text;
	}

	public static ITextComponent getCont(String str) {
		return get("quantizedinformatics:container." + str);
	}

	public static String getContText(String str) {
		return getCont(str).getFormattedText();
	}

	public static String getText(String str) {
		return get(str).getFormattedText();
	}

	public static ITextComponent getTooltip(String str) {
		return get("quantizedinformatics:tooltip." + str);
	}

	public static String getTooltipText(String str) {
		return get("quantizedinformatics:tooltip." + str).getFormattedText();
	}

}
