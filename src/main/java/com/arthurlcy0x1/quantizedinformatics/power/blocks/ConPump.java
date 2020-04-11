package com.arthurlcy0x1.quantizedinformatics.power.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.NullFluidTE;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IFluidTE.FluidUpdator;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.ConTE;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.ICapMachine;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.PowerCont;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class ConPump {

	public static class Scr extends CTEBlock.CommScr<Cont> {
		
		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pmc_pump.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 0);// TODO height
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
		}

	}

	public static class Cont extends PowerCont<TE,Cont> {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE), new IntArray(LEN));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTPC_PU, id, inv, ent, 0, arr);// TODO height
		}

	}

	public static class TE extends ConTE<TE, Cont> implements ICapMachine, NullFluidTE {

		private static final int CAP = 0;

		private final FluidUpdator upd = new FluidUpdator();

		private double req;

		public TE() {
			super(null, Cont::new, SIZE);
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("pmc_pump");
		}

		@Override
		protected void doHighPower() {
			updateFluid(req);
		}

		@Override
		protected void doLowPower() {
			updateFluid(req);
		}

		@Override
		protected void doNoPower() {
			updateFluid(0);
		}

		private void updateFluid(double power) {
			BlockPos[] ps = Wire.queryFluid(world, getPos());
			upd.clear();
			for (BlockPos pos : ps) {
				TileEntity te = world.getTileEntity(pos);
				if (te == null || !(te instanceof IFluidTE))
					continue;
				upd.add((IFluidTE) te);
			}
			req = upd.update(power);
		}

		@Override
		protected int getHighPowerInternal() {
			return 0;
		}

		@Override
		protected int getLowPowerInternal() {
			return (int) Math.ceil(req);
		}

		@Override
		public ICapacitor getCapacitor() {
			ItemStack is = getStackInSlot(CAP);
			if (is.getItem() instanceof ItemCapacitor)
				return ((ItemCapacitor) is.getItem()).getICap(is);
			return ICapacitor.NULL;
		}

	}

	private static final int SIZE = 1, LEN = 5;

}
