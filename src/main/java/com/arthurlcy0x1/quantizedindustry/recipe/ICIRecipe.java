package com.arthurlcy0x1.quantizedindustry.recipe;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

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

public interface ICIRecipe<T extends ICIRecipe.Inv> extends IMachineRecipe<T> {

	public static abstract class CIRecipe<T extends CIRecipe.Inv> implements ICIRecipe<T> {

		public final ResourceLocation id;
		public final Ingredient[][] in;
		public final int time;
		public final IRecipeSerializer<?> irs;
		public final IRecipeType<?> irt;

		public CIRecipe(ResourceLocation rl, IRecipeSerializer<?> rs, IRecipeType<?> rt, Ingredient[][] ingr, int t) {
			id = rl;
			in = ingr;
			time = t;
			irs = rs;
			irt = rt;
		}

		@Override
		public boolean canFit(int width, int height) {
			return height >= in.length && width >= in[0].length;
		}

		@Override
		public int getCraftCost() {
			return time;
		}

		@Override
		public ItemStack getCraftingResult(T inv) {
			return getOutput();
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		public abstract ItemStack getOutput();

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
		public boolean matches(T inv, World worldIn) {
			for (int i = 0; i <= 3 - in[0].length; ++i)
				for (int j = 0; j <= 3 - in.length; ++j)
					if (checkMatch(inv, i, j, true) || checkMatch(inv, i, j, false))
						return true;
			return false;
		}

		private boolean checkMatch(T inv, int dw, int dh, boolean flip) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int k = i - dw;
					int l = j - dh;
					Ingredient ingr = Ingredient.EMPTY;
					if (k >= 0 && l >= 0 && k < in[0].length && l < in.length)
						ingr = in[l][flip ? in[0].length - k - 1 : k];
					if (!ingr.test(inv.getStackInSlot(inv.getStartIndex() + i + j * 3)))
						return false;
				}
			}
			return true;
		}

	}

	public static interface Inv extends IInventory {

		public int getStartIndex();

	}

	public static class Ser<T extends CIRecipe<C>, C extends CIRecipe.Inv>
			extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

		public static interface JsonReader<T> {

			public T read(ResourceLocation id, JsonObject json, Ingredient[][] in, int time);

		}

		public static interface PacketReader<T> {

			public T read(ResourceLocation id, PacketBuffer data, Ingredient[][] in, int time);

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
			int time = JSONUtils.getInt(json, "cost");
			Map<String, Ingredient> map = ShapedHelper.deserializeKey(JSONUtils.getJsonObject(json, "key"));
			String[] strs = ShapedHelper.shrink(ShapedHelper.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
			int w = strs[0].length();
			int h = strs.length;
			Ingredient[][] in = ShapedHelper.decIngrArr(strs, map, w, h);
			return jread.read(id, json, in, time);
		}

		@Nullable
		@Override
		public T read(@Nonnull ResourceLocation id, PacketBuffer buffer) {
			int time = buffer.readInt();
			int n = buffer.readByte();
			int m = buffer.readByte();
			Ingredient[][] in = new Ingredient[n][m];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					in[i][j] = Ingredient.read(buffer);
			return pread.read(id, buffer, in, time);
		}

		@Override
		public void write(PacketBuffer buffer, T r) {
			buffer.writeInt(r.time);
			buffer.writeByte(r.in.length);
			buffer.writeByte(r.in[0].length);
			for (int i = 0; i < r.in.length; i++)
				for (int j = 0; j < r.in[0].length; j++)
					r.in[i][j].write(buffer);
			pwrite.write(buffer, r);
		}

	}

	public static class ShapedHelper {

		private static final int MAX_WIDTH = 3;
		private static final int MAX_HEIGHT = 3;

		private static Ingredient[][] decIngrArr(String[] pattern, Map<String, Ingredient> keys, int w, int h) {
			Ingredient[][] ans = new Ingredient[h][w];
			Set<String> set = Sets.newHashSet(keys.keySet());
			set.remove(" ");
			for (int i = 0; i < pattern.length; ++i) {
				for (int j = 0; j < pattern[i].length(); ++j) {
					String s = pattern[i].substring(j, j + 1);
					Ingredient ing = keys.get(s);
					if (ing == null)
						throw new JsonSyntaxException(
								"Pattern references symbol '" + s + "' but it's not defined in the key");
					set.remove(s);
					ans[i][j] = ing;
				}
			}
			if (!set.isEmpty())
				throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
			return ans;
		}

		private static Map<String, Ingredient> deserializeKey(JsonObject json) {
			Map<String, Ingredient> map = Maps.newHashMap();

			for (Entry<String, JsonElement> entry : json.entrySet()) {
				if (entry.getKey().length() != 1) {
					throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey()
							+ "' is an invalid symbol (must be 1 character only).");
				}

				if (" ".equals(entry.getKey())) {
					throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
				}

				map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
			}

			map.put(" ", Ingredient.EMPTY);
			return map;
		}

		private static int firstNonSpace(String str) {
			int i;
			for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i)
				;
			return i;
		}

		private static int lastNonSpace(String str) {
			int i;
			for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i)
				;
			return i;
		}

		private static String[] patternFromJson(JsonArray jsonArr) {

			String[] astring = new String[jsonArr.size()];
			if (astring.length > MAX_HEIGHT) {
				throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
			} else if (astring.length == 0) {
				throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
			} else {
				for (int i = 0; i < astring.length; ++i) {
					String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
					if (s.length() > MAX_WIDTH) {
						throw new JsonSyntaxException(
								"Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
					}

					if (i > 0 && astring[0].length() != s.length()) {
						throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
					}

					astring[i] = s;
				}

				return astring;
			}
		}

		private static String[] shrink(String... toShrink) {
			int i = Integer.MAX_VALUE;
			int j = 0;
			int k = 0;
			int l = 0;

			for (int i1 = 0; i1 < toShrink.length; ++i1) {
				String s = toShrink[i1];
				i = Math.min(i, firstNonSpace(s));
				int j1 = lastNonSpace(s);
				j = Math.max(j, j1);
				if (j1 < 0) {
					if (k == i1) {
						++k;
					}

					++l;
				} else {
					l = 0;
				}
			}

			if (toShrink.length == l) {
				return new String[0];
			} else {
				String[] astring = new String[toShrink.length - l - k];

				for (int k1 = 0; k1 < astring.length; ++k1) {
					astring[k1] = toShrink[k1 + k].substring(i, j + 1);
				}

				return astring;
			}
		}

	}

}
