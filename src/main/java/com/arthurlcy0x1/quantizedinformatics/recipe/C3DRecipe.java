package com.arthurlcy0x1.quantizedinformatics.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicRE;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class C3DRecipe implements IRecipe<C3DRecipe.Craft3DInv> {

	public interface Craft3DInv extends IInventory {

		public int[][] getBounds();

		public int[] getDim();

	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<C3DRecipe> {

		@Override
		@Nonnull
		public C3DRecipe read(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
			JsonArray ja = JSONUtils.getJsonArray(json, "pattern");
			int n = ja.size();
			if (n == 0)
				throw new LogicRE("empty pattern");
			char[][][] arr = new char[n][][];
			List<Character> list = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				JsonArray ai = ja.get(i).getAsJsonArray();
				int m = ai.size();
				if (m == 0 || i > 0 && m != arr[i - 1].length)
					throw new LogicRE("array size mismatch");
				arr[i] = new char[m][];
				for (int j = 0; j < m; j++) {
					String str = ai.get(j).getAsString();
					arr[i][j] = str.toCharArray();
					if (str.length() == 0 || j > 0 && str.length() != arr[i][j - 1].length)
						throw new LogicRE("array size mismatch");
					if (i > 0 && str.length() != arr[i - 1][0].length)
						throw new LogicRE("array size mismatch");
					for (char c : arr[i][j])
						list.add(c);
				}
			}
			JsonObject ji = JSONUtils.getJsonObject(json, "keys");
			Map<Character, Block> map = new HashMap<>();
			for (char c : list) {
				if (c == ' ')
					continue;
				String name = JSONUtils.getString(ji, Character.toString(c));
				Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
				if (b == null)
					throw new LogicRE("block not found");
				map.put(c, b);
			}
			Block[][][] rec = new Block[arr.length][arr[0].length][arr[0][0].length];
			for (int i = 0; i < arr.length; i++)
				for (int j = 0; j < arr[0].length; j++)
					for (int k = 0; k < arr[0][0].length; k++)
						rec[i][j][k] = arr[i][j][k] == ' ' ? null : map.get(arr[i][j][k]);

			String sout = JSONUtils.getString(json, "out");
			ResourceLocation rl = new ResourceLocation(sout);
			Item item = ForgeRegistries.ITEMS.getValue(rl);
			int ouc = json.has("out_count") ? JSONUtils.getInt(json, "out_count") : 1;
			ItemStack is = new ItemStack(item, ouc);
			return new C3DRecipe(id, rec, is);
		}

		@Nullable
		@Override
		public C3DRecipe read(@Nonnull ResourceLocation id, PacketBuffer buffer) {
			int n = buffer.readByte();
			int m = buffer.readByte();
			int s = buffer.readByte();
			byte[][][] bs = new byte[n][m][s];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					for (int k = 0; k < s; k++)
						bs[i][j][k] = buffer.readByte();
			int l = buffer.readByte();
			Block[] bl = new Block[l];
			for (int i = 0; i < l; i++)
				bl[i] = buffer.readRegistryId();
			Block[][][] rec = new Block[n][m][s];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					for (int k = 0; k < s; k++)
						rec[i][j][k] = bs[i][j][k] == -1 ? null : bl[bs[i][j][k]];
			ItemStack is = buffer.readItemStack();
			return new C3DRecipe(id, rec, is);
		}

		@Override
		public void write(PacketBuffer buffer, C3DRecipe r) {
			List<Block> set = new ArrayList<Block>();
			buffer.writeByte(r.ing.length);
			buffer.writeByte(r.ing[0].length);
			buffer.writeByte(r.ing[0][0].length);
			for (Block[][] bss : r.ing)
				for (Block[] bs : bss)
					for (Block b : bs) {
						int ind = -1;
						if (b != null) {
							ind = set.indexOf(b);
							if (ind == -1) {
								ind = set.size();
								set.add(b);
							}
						}
						buffer.writeByte(ind);
					}
			buffer.writeByte(set.size());
			for (Block b : set)
				buffer.writeRegistryId(b);
			buffer.writeItemStack(r.output);
		}

	}

	public final ResourceLocation id;
	public final Block[][][] ing;
	public final ItemStack output;
	private final int type;

	public C3DRecipe(ResourceLocation rl, Block[][][] in, ItemStack out) {
		id = rl;
		ing = in;
		output = out;
		type = getSymType();
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	public void craft(Craft3DInv inv, World w) {
		int[][] bs = inv.getBounds();
		for (int x = bs[0][0] + 1; x < bs[0][1]; x++)
			for (int y = bs[1][0] + 1; y < bs[1][1]; y++)
				for (int z = bs[2][0] + 1; z < bs[2][1]; z++)
					w.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
	}

	@Override
	public ItemStack getCraftingResult(Craft3DInv inv) {
		return output.copy();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_C3D;
	}

	@Override
	public IRecipeType<?> getType() {
		return Registrar.RT_C3D;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(Craft3DInv inv, World w) {
		int[] dim = inv.getDim();
		if (!matchSize(dim))
			return false;
		int[][] bs = inv.getBounds();
		Block[][][] bl = new Block[dim[0]][dim[1]][dim[2]];
		for (int x = 0; x < dim[0]; x++)
			for (int y = 0; y < dim[1]; y++)
				for (int z = 0; z < dim[2]; z++) {
					BlockPos bp = new BlockPos(x + 1 + bs[0][0], y + 1 + bs[1][0], z + 1 + bs[2][0]);
					bl[x][y][z] = w.getBlockState(bp).getBlock();
				}
		for (int r = 0; r < 8; r++) {
			if ((type & 1 << r) != 0)
				continue;
			if ((r & 1) == 0 && ing[0].length != dim[0])
				continue;
			if ((r & 1) == 1 && ing[0].length != dim[2])
				continue;
			if (check(r, dim, bl))
				return true;
		}
		return false;

	}

	private boolean check(int r, int[] dim, Block[][][] bl) {
		for (int y = 0; y < dim[1]; y++)
			for (int x = 0; x < dim[0]; x++)
				for (int z = 0; z < dim[2]; z++) {
					Block b = bl[x][y][z];
					Block c = get(x, y, z, r);
					if (c == null && b != Blocks.AIR)
						return false;
					if (c != null && b != c)
						return false;
				}
		return true;
	}

	private Block get(int x, int y, int z, int r0) {
		int my = ing.length - 1;
		int mx = ing[0].length - 1;
		int mz = ing[0][0].length - 1;
		int r = r0 & 3;
		int x0, z0;
		if (r == 0) {
			x0 = x;
			z0 = z;
		} else if (r == 1) {
			x0 = mx - z;
			z0 = x;
		} else if (r == 2) {
			x0 = mx - x;
			z0 = mz - z;
		} else {
			x0 = z;
			z0 = mz - x;
		}
		if (r0 >= 4)
			x0 = mx - x0;
		return ing[my - y][x0][z0];
	}

	private int getSymType() {
		int ans = 0;
		int[] d0 = { ing[0].length, ing.length, ing[0][0].length };
		int[] d1 = { d0[2], d0[1], d0[0] };
		for (int i = 1; i < 8; i++)
			for (int j = 0; j < i; j++) {
				if (d0[0] != d0[2] && (i & 1) != (j & 1))
					continue;
				int[] d = (i & 1) == 0 ? d0 : d1;
				if (match(i, j, d)) {
					ans |= 1 << i;
					break;
				}
			}
		return ans;
	}

	private boolean match(int r0, int r1, int[] d) {
		for (int x = 0; x < d[0]; x++)
			for (int y = 0; y < d[1]; y++)
				for (int z = 0; z < d[2]; z++) {
					Block b0 = get(x, y, z, r0);
					Block b1 = get(x, y, z, r1);
					if (b0 != b1)
						return false;
				}
		return true;
	}

	private boolean matchSize(int[] dim) {
		if (dim == null)
			return false;
		if (dim[1] != ing.length)
			return false;
		int l0 = ing[0].length;
		int l1 = ing[0][0].length;
		return dim[0] == l0 && dim[2] == l1 || dim[0] == l1 && dim[2] == l0;
	}

}
