package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.PacketHandler;
import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.Msg;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

public class DIOBlock {

	public static class DIOCont extends Container implements DraftCont {

		private final TermWriter data;

		protected DIOCont(ContainerType<?> type, int id, IIntArray arr) {
			super(type, id);
			data = new TermWriter(arr);
			trackIntArray(data.getData());
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}

		@Override
		public TermWriter getSignal() {
			return data;
		}

	}

	public static class DIOScr<T extends DIOCont> extends ContainerScreen<T> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/draft_term.png");

		public DIOScr(T cont, PlayerInventory inv, ITextComponent title) {
			super(cont, inv, title);
			xSize = 176;
			ySize = 84;
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			int sele = getSele(x, y);
			if (sele == -1)
				return super.mouseClicked(x, y, t);
			if (sele >= 0 && sele <= CNUM)
				PacketHandler.send(new Msg(0, sele));
			return true;
		}

		@Override
		public void render(int x, int y, float t) {
			renderBackground();
			super.render(x, y, t);
			renderHoveredToolTip(x, y);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int x = guiLeft;
			int y = guiTop;
			blit(x, y, 0, 52, xSize, ySize);
			TermWriter data = container.getSignal();
			for (int i = 0; i < 16; i++) {
				int cond = data.get(i + 1);
				int x0 = x + xSize / 2 - 26 + i % 4 * 13;
				int y0 = y + ySize / 2 - 26 + i / 4 * 13;
				int xi = i % 4 * 13 + (cond == 0 ? 8 * 13 : cond > 1 ? 4 * 13 : 0);
				int yi = i / 4 * 13;
				blit(x0, y0, xi, yi, 13, 13);
				if ((data.get(0) & C_MASK) == i)
					blit(x0, y0, 13 * 12, 0, 13, 13);
			}
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			String s = this.title.getFormattedText();
			this.font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6.0F, 4210752);
		}

		private int getSele(double x, double y) {
			int xc = guiLeft + xSize / 2 - 26;
			int yc = guiTop + ySize / 2 - 26;
			for (int i = 0; i < CNUM; i++) {
				int x0 = xc + i % 4 * 13;
				int y0 = yc + i / 4 * 13;
				if (x >= x0 && x < x0 + 13 && y > y0 && y < y0 + 13)
					return i;
			}
			return -1;
		}

	}

	public static class DIOTerm<T extends TileEntity> extends CTEBlock<T> implements DraftIO {

		private final int type;

		public DIOTerm(Supplier<T> sup, int t) {
			super(sup);
			type = t;
		}

		@Override
		public boolean canConnectFrom(int type, BlockState b, Direction d) {
			if (type == CRAFT) {
				Direction dire = b.get(HorizontalBlock.HORIZONTAL_FACING);
				return d != dire && d != dire.getOpposite();
			} else
				return WireConnect.DraftIO.super.canConnectFrom(type, b, d);
		}

		@Override
		public Direction getInDire(BlockState bs) {
			return type == INPUT ? bs.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite() : null;
		}

		@Override
		public Direction getOutDire(BlockState bs) {
			return type == OUTPUT ? bs.get(HorizontalBlock.HORIZONTAL_FACING).getOpposite() : null;
		}

		@Override
		public int ioType(BlockState b, Direction d) {
			return b.get(HorizontalBlock.HORIZONTAL_FACING) == d.getOpposite() ? type : NONE;
		}

	}

	public abstract static class DTETerm<T extends DTETerm<T>> extends TileEntity implements DraftTE {

		private final TermManager data;

		public DTETerm(TileEntityType<T> t, int type) {
			super(t);
			data = new TermManager(this, 2 - type, type - 1);
		}

		@Override
		public ISignalManager getSignal() {
			return data;
		}

	}

	private static class TermManager extends SignalManager {

		private int[] cond = new int[CNUM];

		private TermManager(DraftTE te, int input, int output) {
			super(te, input, output);
		}

		@Override
		public int get(int i) {
			if (i < super.size())
				return super.getRaw(i);
			return cond[i - super.size()];
		}

		@Override
		public void set(int i, int v) {
			if (i < super.size())
				super.setRaw(i, v);
			else
				cond[i - super.size()] = v;
		}

		@Override
		public int size() {
			return super.size() + CNUM;
		}

		@Override
		public void updateValidity(boolean isInput, int[] vali) {
			super.updateValidity(isInput, vali);
			if (isInput && inputCount() > 0 || !isInput && outputCount() > 0)
				cond = vali.clone();
		}

	}

	private static class TermWriter extends MsgWriter {

		private TermWriter(IIntArray arr) {
			super(arr);
		}

		@Override
		protected boolean allowed(int sele, int bit) {
			return bit >= 0 && bit < CNUM;
		}

	}

}
