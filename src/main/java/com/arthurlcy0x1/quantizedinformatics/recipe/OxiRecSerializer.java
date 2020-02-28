package com.arthurlcy0x1.quantizedinformatics.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class OxiRecSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<OxiRecipe> {

	@Override
	@Nonnull
	public OxiRecipe read(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
		Ingredient[] in = new Ingredient[3];
		int[] inc = new int[3];
		ItemStack[] out = new ItemStack[2];
		int time = JSONUtils.getInt(json, "time");
		in[0] = Ingredient.deserialize(JSONUtils.getJsonObject(json, "in_main"));
		inc[0] = json.has("in_main_count") ? JSONUtils.getInt(json, "in_main_count") : 1;

		if (json.has("in_side")) {
			in[1] = Ingredient.deserialize(JSONUtils.getJsonObject(json, "in_side"));
			inc[1] = json.has("in_side_count") ? JSONUtils.getInt(json, "in_side_count") : 1;
		}
		if (json.has("medium")) {
			in[1] = Ingredient.deserialize(JSONUtils.getJsonObject(json, "medium"));
			inc[1] = json.has("medium_count") ? JSONUtils.getInt(json, "medium_count") : 1;
		}
		String s0 = JSONUtils.getString(json, "out_main");
		ResourceLocation rl0 = new ResourceLocation(s0);
		Item i0 = ForgeRegistries.ITEMS.getValue(rl0);
		int ouc0 = json.has("out_main_count") ? JSONUtils.getInt(json, "out_main_count") : 1;
		out[0] = new ItemStack(i0, ouc0);

		if (json.has("out_side")) {
			String s1 = JSONUtils.getString(json, "out_side");
			ResourceLocation rl1 = new ResourceLocation(s1);
			Item i1 = ForgeRegistries.ITEMS.getValue(rl1);
			int ouc1 = json.has("out_side_count") ? JSONUtils.getInt(json, "out_side_count") : 1;
			out[1] = new ItemStack(i1, ouc1);
		}
		return new OxiRecipe(id, in, inc, out, time);
	}

	@Nullable
	@Override
	public OxiRecipe read(@Nonnull ResourceLocation id, PacketBuffer buffer) {
		int time = buffer.readInt();
		boolean in1 = buffer.readBoolean();
		boolean in2 = buffer.readBoolean();
		boolean ou1 = buffer.readBoolean();
		int[] inc = { buffer.readInt(), buffer.readInt(), buffer.readInt() };
		Ingredient[] ing = new Ingredient[3];
		ing[0] = Ingredient.read(buffer);
		if (in1)
			ing[1] = Ingredient.read(buffer);
		if (in2)
			ing[2] = Ingredient.read(buffer);
		ItemStack[] is = new ItemStack[2];
		is[0] = buffer.readItemStack();
		if (ou1)
			is[1] = buffer.readItemStack();
		return new OxiRecipe(id, ing, inc, is, time);
	}

	@Override
	public void write(PacketBuffer buffer, OxiRecipe r) {
		buffer.writeInt(r.time);
		buffer.writeBoolean(r.ing[1] != null);
		buffer.writeBoolean(r.ing[2] != null);
		buffer.writeBoolean(r.output[1] != null);
		buffer.writeInt(r.inc[0]);
		buffer.writeInt(r.inc[1]);
		buffer.writeInt(r.inc[2]);
		r.ing[0].write(buffer);
		if (r.ing[1] != null)
			r.ing[1].write(buffer);
		if (r.ing[2] != null)
			r.ing[2].write(buffer);
		buffer.writeItemStack(r.output[0]);
		if (r.output[1] != null)
			buffer.writeItemStack(r.output[1]);
	}

}
