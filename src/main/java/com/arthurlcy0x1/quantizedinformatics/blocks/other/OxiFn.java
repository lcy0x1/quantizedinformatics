package com.arthurlcy0x1.quantizedinformatics.blocks.other;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTECont;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTEScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;
import com.arthurlcy0x1.quantizedinformatics.recipe.OxiRecipe;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;

public class OxiFn {

	public static class Cont extends CTECont {

		private final IIntArray data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(6), new IntArray(DATA_LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CT_OXIFN, id, inv, ent, 92);
			addSlot(new Slot(ent, 0, 56, 21));
			addSlot(new Slot(ent, 1, 34, 21));
			addSlot(new CondSlot(ent, 2, 56, 61, CTECont::isFuel));
			addSlot(new ResultSlot(ent, 3, 116, 40));
			addSlot(new ResultSlot(ent, 4, 138, 40));
			addSlot(new ResultSlot(ent, 5, 116, 61));
			trackIntArray(data = arr);
		}

		public boolean burning() {
			return data.get(0) > 0;
		}

		public int getProg() {
			if (data.get(3) == 0 || data.get(1) > data.get(3))
				return 0;
			return 24 * (data.get(3) - data.get(1)) / data.get(3);
		}

		public int getRem() {
			if (data.get(0) == 0 || data.get(2) == 0 || data.get(0) > data.get(2))
				return 0;
			return 13 * data.get(0) / data.get(2);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Scr extends CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/oxidation_furnace.png");

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
			Cont cont = container;
			if (cont.burning()) {
				int k = cont.getRem();
				blit(i + 56, j + 44 + 12 - k, 176, 12 - k, 14, k + 1);
			}
			blit(i + 79, j + 38, 176, 14, cont.getProg() + 1, 16);

		}

	}

	public static class TE extends CTETE<TE> implements OxiRecipe.Inv, ITickableTileEntity, ISidedInventory {

		private static final int[] SLOTS = { 0, 1, 2, 3, 4, 5 };

		private int burnTime = 0, procTime = 0, burnTotal = 0, procTotal = 0;
		private OxiRecipe rec = null;

		private final IIntArray data = new IIntArray() {
			@Override
			public int get(int index) {
				return index == 0 ? burnTime : index == 1 ? procTime : index == 2 ? burnTotal : procTotal;
			}

			@Override
			public void set(int index, int value) {
				if (index == 0)
					burnTime = value;
				else if (index == 1)
					procTime = value;
				else if (index == 2)
					burnTotal = value;
				else if (index == 3)
					procTotal = value;
			}

			@Override
			public int size() {
				return DATA_LEN;
			}
		};

		public TE() {
			super(Registrar.TET_OXIFN, 6);
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
			return index == RES_MAIN || index == RES_SIDE || index == FUEL_REMAIN;
		}

		@Override
		public boolean canInsertItem(int index, ItemStack is, Direction d) {
			if (d == Direction.UP)
				return index == ING_MAIN;
			if (d == this.getBlockState().get(BaseBlock.HORIZONTAL_FACING))
				return index == FUEL && CTECont.isFuel(is);
			if (d == Direction.DOWN)
				return false;
			return index == ING_SIDE;
		}

		@Override
		public Container createMenu(int type, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(type, pi, this, data);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("oxidation_furnace");
		}

		@Override
		public int[] getSlots() {
			return SLOTS;
		}

		@Override
		public int[] getSlotsForFace(Direction side) {
			return SLOTS;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			if (index < FUEL)
				return true;
			if (index == FUEL)
				return CTECont.isFuel(stack);
			return false;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			burnTime = tag.getInt("burn");
			procTime = tag.getInt("proc");
			burnTotal = tag.getInt("tb");
			procTotal = tag.getInt("tp");
		}

		@Override
		public void tick() {
			ItemStack fuel = getStackInSlot(FUEL);
			ItemStack fc = getStackInSlot(FUEL_REMAIN);
			ItemStack cont = fuel.getContainerItem().copy();
			ItemStack main = getStackInSlot(ING_MAIN);
			boolean canBurn = fuel != ItemStack.EMPTY && canMerge(fc, cont);
			boolean inputCheck = !main.isEmpty() && procTime == 0 && (burnTime > 0 || canBurn);
			if (inputCheck)
				rec = world.getRecipeManager().getRecipe(Registrar.RT_OXI, this, world).orElse(null);
			else if (main.isEmpty() || rec == null || !rec.matches(this, world)) {
				rec = null;
				procTime = 0;
				procTotal = 0;
			}
			boolean validInput = rec != null && canMerge(rec.output[0], getStackInSlot(RES_MAIN))
					&& canMerge(rec.output[1], getStackInSlot(RES_SIDE));
			if (validInput && rec.isDynamic())
				validInput = getStackInSlot(RES_MAIN).isEmpty();
			if ((procTime > 0 || validInput) && burnTime == 0 && canBurn) {
				burnTotal = burnTime = ForgeHooks.getBurnTime(fuel) / FUEL_CONS;
				decrStackSize(FUEL, 1);
				incrOrSet(FUEL_REMAIN, cont);
			}
			if (burnTime > 0 && procTime == 0 && validInput)
				procTotal = procTime = rec.getCraftCost();
			if (burnTime > 0 && procTime > 0) {
				procTime--;
				if (procTime == 0) {
					ItemStack out = rec.getCraftingResult(this);
					decrStackSize(ING_MAIN, rec.inc[0]);
					if (!getStackInSlot(ING_SIDE).isEmpty() && rec.inc[1] > 0)
						decrStackSize(ING_SIDE, rec.inc[1]);
					incrOrSet(RES_MAIN, out);
					if (rec.output[1] != null)
						incrOrSet(RES_SIDE, rec.output[1].copy());
					procTotal = 0;
				}
			}
			if (burnTime > 0)
				burnTime--;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("burn", burnTime);
			tag.putInt("proc", procTime);
			tag.putInt("tb", burnTotal);
			tag.putInt("tp", procTotal);
			return tag;
		}

	}

	private static final int DATA_LEN = 4, FUEL_CONS = 8;

}
