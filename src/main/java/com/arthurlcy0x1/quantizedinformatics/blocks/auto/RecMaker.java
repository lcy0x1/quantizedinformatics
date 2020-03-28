package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.List;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.items.AutoRecipe;
import com.arthurlcy0x1.quantizedinformatics.items.MaxwellItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
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
			this(id, inv, new Inventory(SIZE), new IntArray(2));
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

		private Merge getStat() {
			return Merge.getById(data.get(1));
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
			if (container.getStat().red())
				blit(i + 68, j + 35, 176, 15, 22, 15);
		}

		@Override
		protected void renderHoveredToolTip(int mx, int my) {
			super.renderHoveredToolTip(mx, my);
			Merge m = container.getStat();
			if (mx > guiLeft + 68 && mx < guiLeft + 90 && my > guiTop + 35 && my < guiTop + 50 && m.red())
				renderTooltip(Translator.getContText("recipe_maker." + m.toString()), mx, my);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements IIntArray, ITickableTileEntity, ISidedInventory {

		private static final int[] UP = { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, FRONT = { 9 }, DOWN = { 11 }, SIDE = { 10 };

		private static boolean isValid(int index, ItemStack stack) {
			if (index < 10)
				return true;
			if (index == 10)
				return stack.getItem() == Items.PAPER;
			return false;
		}

		private ICraftingRecipe rec;

		private Merge merge;

		private boolean dirty = true;

		public TE() {
			super(Registrar.TETA_REC, SIZE);
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, Direction d) {
			if (d == Direction.UP)
				return index < 9;
			if (d == Direction.DOWN)
				return index == 11;
			if (d == getBlockState().get(BaseBlock.HORIZONTAL_FACING))
				return index == 9;
			return index == 10;
		}

		@Override
		public boolean canInsertItem(int index, ItemStack is, Direction d) {
			if (!isItemValidForSlot(index, is))
				return false;
			if (d == Direction.UP)
				return index < 9;
			if (d == getBlockState().get(BaseBlock.HORIZONTAL_FACING))
				return index == 9;
			return index == 10;
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this, this);
		}

		@Override
		public int get(int index) {
			if (index == 0)
				return rec == null && merge != Merge.SUCCESS ? 0 : 1;
			return merge == null ? 0 : merge.ind;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("recipe_maker");
		}

		@Override
		public int[] getSlotsForFace(Direction side) {
			if (side == Direction.UP)
				return UP;
			if (side == Direction.DOWN)
				return DOWN;
			if (side == getBlockState().get(BaseBlock.HORIZONTAL_FACING))
				return FRONT;
			return SIDE;
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
			return 2;
		}

		@Override
		public void tick() {
			if (world.isRemote || !dirty)
				return;
			dirty = false;
			rec = null;
			merge = null;
			ItemStack out = getStackInSlot(9);
			if (out.isEmpty())
				return;
			else if (out.getItem() == Registrar.IA_RECIPE) {
				merge = tryMerge();
			} else if (!tryFog()) {
				CraftingInventory ci = getFake();
				List<ICraftingRecipe> list = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, ci, world);
				for (ICraftingRecipe icr : list)
					if (Container.areItemsAndTagsEqual(icr.getCraftingResult(ci), out))
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
					ItemStack is = rec.getCraftingResult(ci).copy();
					setInventorySlotContents(11, AutoRecipe.addTag(il, is));
					markDirty();
				}
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

		private boolean tryFog() {
			ItemStack out = getStackInSlot(9);
			ItemStack in = null;
			for (int i = 0; i < 9; i++) {
				ItemStack is = getStackInSlot(i);
				if (!is.isEmpty())
					if (in != null)
						return false;
					else
						in = is;
			}
			if (in == null || out == null || in.isEmpty() || out.isEmpty())
				return false;
			if (in.getItem() != Registrar.IM_FOG)
				return false;
			int inlv = MaxwellItem.getLevel(in);
			if (out.getItem() == Registrar.IB_FOG) {
				if (inlv != 0)
					return false;
			} else if (out.getItem() != Registrar.IM_FOG)
				return false;
			else {
				int outlv = MaxwellItem.getLevel(out);
				if (inlv - outlv != 1)
					return false;
			}
			merge = Merge.SUCCESS;
			if (!getStackInSlot(10).isEmpty() && getStackInSlot(11).isEmpty()) {
				ItemStack cout = out.copy();
				cout.setCount(8);
				ItemStack cin = in.copy();
				cin.setCount(1);
				NonNullList<ItemStack> il = NonNullList.withSize(1, cin);
				decrStackSize(10, 1);
				setInventorySlotContents(11, AutoRecipe.addTag(il, cout));
				markDirty();
			}
			return true;
		}

		private Merge tryMerge() {
			ItemStack out = getStackInSlot(9);
			NonNullList<ItemStack> dnnl = AutoRecipe.loadList(out);
			if (dnnl == null)
				return Merge.EMPTY;
			NonNullList<ItemStack> list = NonNullList.create();
			list.addAll(dnnl);
			ItemStack dout = ItemStack.read(out.getChildTag("recipe"));
			for (ItemStack lis : list)
				if (Container.areItemsAndTagsEqual(lis, dout)) {
					int min = Math.min(lis.getCount(), dout.getCount());
					lis.shrink(min);
					dout.shrink(min);
					if (dout.isEmpty())
						return Merge.ZERO;
				}
			list.removeIf(is -> is.isEmpty());
			for (int i = 0; i < 9; i++) {
				ItemStack iis = getStackInSlot(i);
				if (iis.isEmpty())
					continue;
				if (iis.getItem() != Registrar.IA_RECIPE)
					return Merge.MIXED;
				NonNullList<ItemStack> nnl = AutoRecipe.loadList(iis);
				if (nnl == null)
					return Merge.EMPTY;
				ItemStack rep = ItemStack.read(iis.getChildTag("recipe"));
				if (rep.hasContainerItem())
					return Merge.CONT;
				ItemStack ltar = null;
				for (ItemStack lis : list)
					if (Container.areItemsAndTagsEqual(lis, rep))
						ltar = lis;
				if (ltar == null || ltar.getCount() < rep.getCount())
					return Merge.NONEXI;
				ltar.shrink(rep.getCount());
				if (ltar.isEmpty())
					list.remove(ltar);
				for (ItemStack xis : nnl) {
					ltar = null;
					for (ItemStack lis : list)
						if (Container.areItemsAndTagsEqual(lis, xis))
							ltar = lis;
					if (ltar == null)
						list.add(xis);
					else {
						if (ltar.getCount() + xis.getCount() > 64)
							return Merge.EXCEED;
						ltar.grow(xis.getCount());
					}
				}
			}
			if (!getStackInSlot(10).isEmpty() && getStackInSlot(11).isEmpty()) {
				decrStackSize(10, 1);
				setInventorySlotContents(11, AutoRecipe.addTag(list, dout));
				markDirty();
			}
			return Merge.SUCCESS;
		}

	}

	private static enum Merge {
		NULL(0, "null"), SUCCESS(1, "success"), EMPTY(2, "empty"), MIXED(3, "mixed"), ZERO(4, "zero"),
		EXCEED(5, "exceed"), CONT(6, "cont"), NONEXI(7, "nonexi");

		private static Merge getById(int id) {
			return Merge.values()[id];
		}

		private final int ind;

		private final String str;

		private Merge(int index, String code) {
			ind = index;
			str = code;
		}

		@Override
		public String toString() {
			return str;
		}

		private boolean red() {
			return ind > 1;
		}
	}

	private static final int SIZE = 12;

}
