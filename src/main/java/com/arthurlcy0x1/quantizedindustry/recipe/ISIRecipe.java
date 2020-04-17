package com.arthurlcy0x1.quantizedindustry.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public interface ISIRecipe<T extends ISIRecipe.Inv> extends IMachineRecipe<T> {

	public static interface Inv extends IInventory {

		public ItemStack getIngredient();

	}

	public static class Ser<T extends SIRecipe<C>, C extends SIRecipe.Inv>
			extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

		public static interface JsonReader<T> {

			public T read(ResourceLocation id, JsonObject json, Ingredient in, int time);

		}

		public static interface PacketReader<T> {

			public T read(ResourceLocation id, PacketBuffer data, Ingredient in, int time);

		}

		public static interface PacketWriter<T> {

			public void write(PacketBuffer data, T t);

		}

		private final JsonReader<T> jread;
		private final PacketReader<T> pread;
		private final PacketWriter<T> pwrite;

		public Ser(JsonReader<T> jr, PacketReader<T> pr, PacketWriter<T> pw) {
			jread = jr;
			pread = pr;
			pwrite = pw;
		}

		@Override
		@Nonnull
		public T read(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
			Ingredient in;
			int time = JSONUtils.getInt(json, "time");
			in = Ingredient.deserialize(JSONUtils.getJsonObject(json, "in"));
			return jread.read(id, json, in, time);
		}

		@Nullable
		@Override
		public T read(@Nonnull ResourceLocation id, PacketBuffer buffer) {
			int time = buffer.readInt();
			Ingredient in = Ingredient.read(buffer);
			return pread.read(id, buffer, in, time);
		}

		@Override
		public void write(PacketBuffer buffer, T r) {
			buffer.writeInt(r.time);
			r.in.write(buffer);
			pwrite.write(buffer, r);
		}

	}

	public static abstract class SIRecipe<T extends SIRecipe.Inv> implements ISIRecipe<T> {

		public final ResourceLocation id;
		public final Ingredient in;
		public final int time;
		public final IRecipeSerializer<?> irs;
		public final IRecipeType<?> irt;

		public SIRecipe(ResourceLocation rl, IRecipeSerializer<?> rs, IRecipeType<?> rt, Ingredient ingr, int t) {
			id = rl;
			in = ingr;
			time = t;
			irs = rs;
			irt = rt;
		}

		@Override
		public boolean canFit(int width, int height) {
			return width * height >= 1;
		}

		@Override
		public int getCraftCost() {
			return time;
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return irs;
		}

		@Override
		public IRecipeType<?> getType() {
			return irt;
		}

		@Override
		public boolean isDynamic() {
			return true;
		}

		@Override
		public boolean test(ItemStack is) {
			return in.test(is);
		}

	}

	@Override
	public default boolean matches(T inv, World worldIn) {
		return test(inv.getIngredient());
	}

	public boolean test(ItemStack is);

}
