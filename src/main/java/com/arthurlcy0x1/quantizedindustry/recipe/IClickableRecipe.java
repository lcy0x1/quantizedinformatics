package com.arthurlcy0x1.quantizedindustry.recipe;

import com.google.gson.JsonObject;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public interface IClickableRecipe<I extends IInventory> extends IRecipe<I> {

	public static int readClickFromData(PacketBuffer data) {
		return data.readInt();
	}

	public static int readClickFromJson(JsonObject json) {
		return JSONUtils.getInt(json, "click_cost", -1);
	}

	public static void writeClickToData(PacketBuffer data, IClickableRecipe<?> r) {
		data.writeInt(r.getClickCost());
	}

	public int getClickCost();

}
