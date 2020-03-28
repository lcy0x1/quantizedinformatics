package com.arthurlcy0x1.quantizedinformatics.recipe;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.google.gson.JsonObject;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.TieredItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EnchantOxiRec extends OxiRecipe {

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<EnchantOxiRec> {

		@Override
		public EnchantOxiRec read(ResourceLocation id, JsonObject json) {
			return new EnchantOxiRec(id, JSONUtils.getInt(json, "time"));
		}

		@Override
		public EnchantOxiRec read(ResourceLocation id, PacketBuffer buffer) {
			return new EnchantOxiRec(id, buffer.readInt());
		}

		@Override
		public void write(PacketBuffer buffer, EnchantOxiRec recipe) {
			buffer.writeInt(recipe.time);
		}

	}

	public EnchantOxiRec(ResourceLocation rl, int t) {
		super(rl, new Ingredient[] { null, null }, new int[] { 1, 1 }, new ItemStack[] { null, null }, t);

	}

	@Override
	public ItemStack getCraftingResult(OxiRecipe.Inv inv) {
		ItemStack is = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_SIDE]);
		if (is.getItem() == Registrar.IEN_GOLD)
			return new ItemStack(Registrar.IEI_GOLD);
		return new ItemStack(Registrar.IEI_STEEL);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return Registrar.RS_EOXI;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(OxiRecipe.Inv inv, World worldIn) {
		ItemStack im = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_MAIN]);
		ItemStack is = inv.getStackInSlot(inv.getSlots()[RedRecipe.Inv.ING_SIDE]);
		Item i = im.getItem();
		IArmorMaterial iam = i instanceof ArmorItem ? ((ArmorItem) i).getArmorMaterial() : null;
		IItemTier iit = i instanceof TieredItem ? ((TieredItem) i).getTier() : null;
		boolean enc = im.isEnchanted();
		if (im.isEmpty())
			return false;
		if (is.isEmpty())
			return enc && iam == ArmorMaterial.CHAIN;
		else if (is.getItem() == Registrar.IEN_STEEL)
			return iam == ArmorMaterial.CHAIN || enc && (iam == ArmorMaterial.IRON || iit == ItemTier.IRON);
		else if (is.getItem() == Registrar.IEN_GOLD)
			return enc && (iam == ArmorMaterial.GOLD || iit == ItemTier.GOLD);
		return false;
	}
}
