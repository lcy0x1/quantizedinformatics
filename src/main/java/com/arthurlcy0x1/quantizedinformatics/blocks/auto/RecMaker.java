package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.AutoRecipe;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecMaker {

	public static class Cont extends CTEBlock.CTECont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(1));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTA_REC, id, inv, ent, 92);
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(ent, i, 8 + i % 3 * 18, 17 + i / 3 * 18));
			addSlot(new Slot(ent, 9, 98, 35));
			addSlot(new CondsSlot(ent, 10, 98, 61, TE::isValid, 1));
			addSlot(new ResultSlot(ent, 11, 152, 61));
			trackIntArray(data = arr);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/recipe_maker.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 174);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
			if (container.data.get(0) > 0)
				blit(i + 68, j + 35, 176, 0, 22, 15);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, ITickableTileEntity {

		private static boolean isValid(int index, ItemStack stack) {
			if (index < 10)
				return true;
			if (index == 10)
				return stack.getItem() == Items.PAPER;
			return false;
		}

		private ICraftingRecipe rec;

		private boolean dirty = true;

		public TE() {
			super(Registrar.TETA_REC, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			return rec == null ? 0 : 1;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("recipe_maker");
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return isValid(index, stack);
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public void tick() {
			if (world.isRemote || !dirty)
				return;
			dirty = false;
			rec = null;
			CraftingInventory ci = getFake();
			List<ICraftingRecipe> list = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, ci, world);
			for (ICraftingRecipe icr : list)
				if (Container.areItemsAndTagsEqual(icr.getCraftingResult(ci), getStackInSlot(9)))
					rec = icr;
			if (rec != null && !getStackInSlot(10).isEmpty() && getStackInSlot(11).isEmpty()) {
				NonNullList<ItemStack> il = NonNullList.create();
				for (int i = 0; i < 9; i++) {
					ItemStack st = getStackInSlot(i);
					if (st.isEmpty())
						continue;
					for (int j = 0; j < il.size(); j++)
						if (Container.areItemsAndTagsEqual(st, il.get(j))) {
							il.get(j).grow(1);
							st = null;
							break;
						}
					if (st == null)
						continue;
					st = st.copy();
					st.setCount(1);
					il.add(st);
				}
				decrStackSize(10, 1);
				ItemStack out = rec.getCraftingResult(ci).copy();
				setInventorySlotContents(11, AutoRecipe.addTag(il, out));
				markDirty();
			}
		}

		@Override
		protected void onChange(int index) {
			dirty = true;
		}

		private CraftingInventory getFake() {
			CraftingInventory ci = new CraftingInventory(null, 3, 3);
			for (int i = 0; i < 9; i++)
				try {
					ci.setInventorySlotContents(i, getStackInSlot(i).copy());
				} catch (NullPointerException e) {
				}
			return ci;
		}

	}

	private static final int SIZE = 12;

}
