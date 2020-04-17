package com.arthurlcy0x1.quantizedindustry.recipe;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public interface IClickableRecipe {

	public static int readClickFromJson(JsonObject json) {
		return JSONUtils.getInt(json, "click_cost", -1);
	}
	
	public static int readClickFromData(PacketBuffer data) {
		return data.readInt();
	}
	
	public static void writeClickToData(PacketBuffer data,IClickableRecipe r) {
		data.writeInt(r.getClickCost());
	}

	public int getClickCost();

}
