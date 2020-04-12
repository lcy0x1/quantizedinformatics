package com.arthurlcy0x1.quantizedinformatics.power.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTETE;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.PowerCont;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ConFurnace {

	public static class Cont extends PowerCont<TE, Cont> {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTPC_FN, id, inv, ent, 0, arr);// TODO
		}
	}

	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pmc_furnace.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 0);// TODO
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
			// TODO
		}

	}

	public static class TE extends IPower.ConTE<TE, Cont> implements IPower.ICapMachine {

		private static final int CAP = 2, INGD = 0, RESULT = 1;

		private static boolean isValid(int ind, ItemStack is) {
			if (ind == CAP)
				return is.getItem() instanceof ItemCapacitor;
			if (ind == INGD)
				return true;
			return false;
		}

		private float prog = 0;
		private int max_prog = 0;
		private ICapacitor cap = null;

		public TE() {
			super(Registrar.TETPC_FN, Cont::new, SIZE);
		}

		@Override
		public int get(int index) {
			if (index < 4)
				return super.get(index);
			if (index == 4)
				return (int) prog;
			if (index == 5)
				return max_prog;
			return 0;
		}

		@Override
		public ICapacitor getCapacitor() {
			if (cap != null)
				return cap;
			ItemStack is = getStackInSlot(CAP);
			if (is.getItem() instanceof ItemCapacitor)
				return cap = ((ItemCapacitor) is.getItem()).getICap(is);
			return cap = ICapacitor.NULL;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("pmc_furnace");
		}

		@Override
		public boolean isItemValidForSlot(int ind, ItemStack is) {
			return isValid(ind, is);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getFloat("power.prog");
			max_prog = tag.getInt("power.max_prog");
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putFloat("power.prog", prog);
			tag.putInt("power.max_prog", max_prog);
			return tag;
		}

		@Override
		protected void doHighPower() {
			tickWork(2);
		}

		@Override
		protected void doLowPower() {
			tickWork(1);
		}

		@Override
		protected void doNoPower() {
			tickWork(0);
		}

		@Override
		protected int getHighPowerInternal() {
			return max_prog == 0 ? 0 : 1300;
		}

		@Override
		protected int getLowPowerInternal() {
			return max_prog == 0 ? 0 : 500;
		}

		@Override
		protected void onChange(int index) {
			if (index == -1 || index == 0)
				cap = null;
		}

		private void tickWork(int speed) {
			ItemStack is = getStackInSlot(INGD);
			FurnaceRecipe r = is.isEmpty() ? null
					: world.getRecipeManager().getRecipe(IRecipeType.SMELTING, this, world).orElse(null);
			if (r == null || !CTETE.canMerge(r.getCraftingResult(this), getStackInSlot(RESULT)))
				prog = max_prog = 0;
			else if (max_prog == 0) {
				max_prog = r.getCookTime();
				prog = 0;
			}
			if (prog < max_prog)
				prog++;
			if (max_prog > 0 && prog >= max_prog) {
				decrStackSize(INGD, 1);
				if (is.hasContainerItem())
					setInventorySlotContents(INGD, is.getContainerItem());
				incrOrSet(RESULT, r.getCraftingResult(this));
				prog = max_prog = 0;
			}
		}

	}

	private static final int SIZE = 4, LEN = 6;

}
