package com.arthurlcy0x1.quantizedinformatics.power.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.PowerCont;
import com.arthurlcy0x1.quantizedinformatics.power.blocks.IPower.PowerTE;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class Transistor {

	public static class Scr extends CTEBlock.CommScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pm_transistor.png");

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
			super(Registrar.CTP_TR, id, inv, ent, 0, arr);// TODO height
		}

	}

	public static class TE extends PowerTE<TE, Cont> {

		public TE() {
			super(Registrar.TETP_TR, Cont::new, SIZE);
		}

		@Override
		public int get(int index) {
			return 0;
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return LEN;
		}

		@Override
		public Type getPowerType() {
			return null;
		}

		public void tick() {
			if (world.isRemote)
				return;
			BlockPos[] ps = Wire.queryPower(world, getPos());
			PowerUpdator art = new PowerUpdator();
			for (BlockPos pos : ps) {
				TileEntity te = world.getTileEntity(pos);
				if (te == null || !(te instanceof IPower))
					continue;
				art.add((IPower) te);
			}
			art.update();
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("transistor");
		}

	}

	private static final int SIZE = 1, LEN = 5;

}
