package com.arthurlcy0x1.quantizedinformatics.power.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTECont;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.FluidManagerTE;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.FluidPowerCont;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.FluidManager.FluidTank;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.FluidManager.VarFluidTank;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.ICapMachine;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;

public class GenThermal {

	public static class Cont extends FluidPowerCont<TE, Cont> {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTPG_TH, id, inv, ent, 0, arr, 3);// TODO
		}

	}

	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pmg_thermal.png");

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

	public static class TE extends IPower.GenTE<TE, Cont> implements ICapMachine, FluidManagerTE {

		private static final int CAP = 0, FUEL = 3;

		private static final double WATER_COST = 1e3, LUB_COST = 1e-3;
		private static boolean isValid(int ind, ItemStack is) {
			if (ind == CAP)
				return is.getItem() instanceof ItemCapacitor;
			if (ind == FUEL)
				return CTECont.isFuel(is);
			return false;
		}

		private final FluidTank waterTank = new FluidTank(Priority.CONSUMER, QuanFluid.MC_WATER);
		private final FluidTank lubTank = new FluidTank(Priority.CONSUMER, QuanFluid.OIL_LUB);
		private final VarFluidTank fuelTank = new VarFluidTank(Priority.CONSUMER, e -> e.isFuel());
		private final FluidManager fluidManager = new FluidManager(fuelTank, waterTank, lubTank);

		private float prog = 0;
		private int max_prog = 0;
		private ICapacitor cap = null;

		public TE() {
			super(Registrar.TETPG_TH, Cont::new, SIZE);
		}

		@Override
		public int get(int index) {
			if (index < 4)
				return super.get(index);
			if (index == 4)
				return (int) prog;
			if (index == 5)
				return max_prog;
			if (index == 6)
				return (int) (100 * waterTank.getStorage() / waterTank.getMaxStorage());
			if (index == 7)
				return (int) (100 * lubTank.getStorage() / lubTank.getMaxStorage());
			if (index == 8)
				return (int) (100 * fuelTank.getStorage() / fuelTank.getMaxStorage());
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
			return Translator.getCont("pmg_thermal");
		}

		@Override
		public FluidManager getManager() {
			return fluidManager;
		}

		@Override
		public boolean isItemValidForSlot(int ind, ItemStack is) {
			return isValid(ind, is);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			fluidManager.read(tag.getCompound("fluids"));
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
		public void tick() {
			super.tick();
			if (world.isRemote)
				return;

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.put("fluids", fluidManager.write(new CompoundNBT()));
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
			if (max_prog == 0)
				return 0;
			if (waterTank.getStorage() < WATER_COST * 2)
				return 0;
			if (lubTank.getStorage() < LUB_COST * 4)
				return 0;
			return 800;
		}

		@Override
		protected int getLowPowerInternal() {
			if (max_prog == 0)
				return 0;
			if (waterTank.getStorage() < WATER_COST)
				return 0;
			if (lubTank.getStorage() < LUB_COST)
				return 0;
			return 500;
		}

		@Override
		protected void onChange(int index) {
			if (index == -1 || index == CAP)
				cap = null;
		}

		private void tickWork(double speed) {
			if (prog > 0) {
				prog -= speed;
				waterTank.addStorage(-speed * WATER_COST);
				lubTank.addStorage(-speed * speed * LUB_COST);
			}
			if (prog <= 0) {
				max_prog = 0;
				ItemStack is = getStackInSlot(FUEL);
				if (!is.isEmpty()) {
					decrStackSize(FUEL, 1);
					max_prog = ForgeHooks.getBurnTime(is);
					if (is.hasContainerItem())
						this.setInventorySlotContents(FUEL, is.getContainerItem());
				} else if (fuelTank.getFluidType() != null) {
					QuanFluid t = fuelTank.getFluidType();
					double get = Math.max(200 / t.getFuelValue(), fuelTank.getStorage());
					fuelTank.addStorage(-get);
					max_prog = (int) (t.getFuelValue() * get);
				}
				prog += max_prog;
			}
		}

	}

	private static final int SIZE = 4, LEN = 9;

}
