package com.arthurlcy0x1.quantizedinformatics.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.google.gson.JsonObject;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class OxiRecipe implements IRecipe<OxiRecipe.Inv> {

	public interface Inv extends IInventory {

		public static final int ING_MAIN = 0, ING_SIDE = 1, FUEL = 2, RES_MAIN = 3, RES_SIDE = 4, FUEL_REMAIN = 5;

		public int[] getSlots();

	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<OxiRecipe> {

		@Override
		@Nonnull
		public OxiRecipe read(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
			Ingredient[] in = new Ingredient[2];
			int[] inc = new int[2];
			ItemStack[] out = new ItemStack[2];
			int time = JSONUtils.getInt(json, "time");

			in[0] = Ingredient.deserialize(JSONUtils.getJsonObject(json, "in_main"));
			inc[0] = json.has("in_main_count") ? JSONUtils.getInt(json, "in_main_count") : 1;

			if (json.has("in_side")) {
				in[1] = Ingredient.deserialize(JSONUtils.getJsonObject(json, "in_side"));
				inc[1] = json.has("in_side_count") ? JSONUtils.getInt(json, "in_side_count") : 1;
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
			boolean ou1 = buffer.readBoolean();
			int[] inc = { buffer.readInt(), buffer.readInt() };
			Ingredient[] ing = new Ingredient[2];
			ing[0] = Ingredient.read(buffer);
			if (in1)
				ing[1] = Ingredient.read(buffer);
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
			buffer.writeBoolean(r.output[1] != null);
			buffer.writeInt(r.inc[0]);
			buffer.writeInt(r.inc[1]);
			r.ing[0].write(buffer);
			if (r.ing[1] != null)
				r.ing[1].write(buffer);
			buffer.writeItemStack(r.output[0]);
			if (r.output[1] != null)
				buffer.writeItemStack(r.output[1]);
		}

	}

	public final ResourceLocation id;
	public final Ingredient[] ing;
	public final ItemStack[] output;
	public final int[] inc;
	public final int time;

	public OxiRecipe(ResourceLocation rl, Ingredient[] ingr, int[] in, ItemStack[] is, int t) {
		id = rl;
		ing = ingr;
		output = is;
		inc = in;
		time = t;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= ing.length;
	}

	public void craft(Inv inv) {
		int ing0 = inv.getSlots()[Inv.ING_MAIN];
		int ing1 = inv.getSlots()[Inv.ING_SIDE];
		inv.decrStackSize(ing0, inc[0]);
		if (ing[1] != null)
			inv.decrStackSize(ing1, inc[1]);
		int res0 = inv.getSlots()[Inv.ING_MAIN];
		int res1 = inv.getSlots()[Inv.ING_SIDE];
		if (inv.getStackInSlot(res0) == ItemStack.EMPTY)
			inv.setInventorySlotContents(res0, output[0].copy());
		else
			inv.getStackInSlot(res0).grow(output[0].getCount());
		if (output[1] != null)
			if (inv.getStackInSlot(res1) == ItemStack.EMPTY)
				inv.setInventorySlotContents(res1, output[1].copy());
			else
				inv.getStackInSlot(res1).grow(output[1].getCount());
	}

	public int getCraftCost() {
		return time;
	}

	@Override
	public ItemStack getCraftingResult(Inv inv) {
		return output[0].copy();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output[0];
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_OXI;
	}

	@Override
	public IRecipeType<?> getType() {
		return Registrar.RT_OXI;
	}

	@Override
	public boolean matches(Inv inv, World worldIn) {
		int ing0 = inv.getSlots()[Inv.ING_MAIN];
		int ing1 = inv.getSlots()[Inv.ING_SIDE];
		ItemStack ig0 = inv.getStackInSlot(ing0);
		ItemStack ig1 = inv.getStackInSlot(ing1);
		if (!ing[0].test(ig0) || ig0.getCount() < inc[0])
			return false;

		if (ing[1] != null) {
			if (!ing[1].test(ig1) || ig1.getCount() < inc[1])
				return false;
		} else if (ig1 != ItemStack.EMPTY)
			return false;
		int res0 = inv.getSlots()[Inv.ING_MAIN];
		int res1 = inv.getSlots()[Inv.ING_SIDE];
		ItemStack is0 = inv.getStackInSlot(res0);
		ItemStack is1 = inv.getStackInSlot(res1);
		if (is0.getCount() + output[0].getCount() > is0.getItem().getItemStackLimit(is0))
			return false;
		if (output[1] != null && is1.getCount() + output[1].getCount() > is1.getItem().getItemStackLimit(is1))
			return false;
		if (is0 != ItemStack.EMPTY && output[0].getItem() != is0.getItem())
			return false;
		if (output[1] != null)
			if (is1 != ItemStack.EMPTY && output[1].getItem() != is1.getItem())
				return false;
		return true;
	}

}
