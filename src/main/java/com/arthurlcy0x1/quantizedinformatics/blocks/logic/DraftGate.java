package com.arthurlcy0x1.quantizedinformatics.blocks.logic;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.*;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

public class DraftGate extends BaseBlock implements WireConnect.DraftIO {

	public static class Cont extends CTECont implements DraftCont {

		private static boolean isChip(ItemStack is) {
			return is.getItem() instanceof LogicDraft;
		}

		private final SignalWriter data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1), new IntArray(CNUM * 2));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray d) {
			super(Registrar.CTD_GATE, id, inv, ent, 86);
			this.addSlot(new CondSlot(slots, 0, 80, 36, Cont::isChip));
			trackIntArray(d);
			data = new SignalWriter(id, CNUM, CNUM, d);
		}

		@Override
		public SignalWriter getSignal() {
			return data;
		}

	}

	public static class Scr extends CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/draft_gate.png");

		private int sele = -1;

		public Scr(DraftGate.Cont cont, PlayerInventory inv, ITextComponent tit) {
			super(cont, inv, tit, 168);
		}

		@Override
		public boolean charTyped(char ch, int t) {
			if (sele >= 0) {
				sele = container.data.updateSele(sele, ch);
				return true;
			}
			return super.charTyped(ch, t);
		}

		@Override
		public boolean mouseClicked(double x, double y, int t) {
			sele = getSele(x, y);
			if (sele >= 0 && container.data.get(sele) == C_FORBID)
				sele = -1;
			if (sele == -1)
				return super.mouseClicked(x, y, t);
			return true;
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int x = guiLeft;
			int y = guiTop;
			blit(x, y, 0, 0, xSize, ySize);
			Cont cont = container;
			for (int i = 0; i < 16; i++) {
				int x0 = x + 53 - i / 4 * 13;
				int y0 = y + 18 + i % 4 * 13;
				int x1 = x + 110 + i / 4 * 13;
				drawSymbol(x0, y0, i, cont.data.getInputData(i), false);
				drawSymbol(x1, y0, i, cont.data.getOutputData(i), false);
			}
			if (sele >= 0)
				if (sele < CNUM)
					drawSymbol(x + 53 - sele / 4 * 13, y + 18 + sele % 4 * 13, sele, 36, true);
				else
					drawSymbol(x + 58 + sele / 4 * 13, y + 18 + sele % 4 * 13, sele - CNUM, 36, true);
		}

		private void drawSymbol(int x, int y, int i, int cv, boolean valid) {
			if (cv == C_FORBID && i >= 4)
				return;
			if (!valid)
				if (cv >= C_ERR && cv < C_ERR + CNUM)
					cv -= C_ERR;
				else
					valid = true;
			int cx = 176 + cv % 4 * 13;
			int cy = cv / 4 * 13 + (valid ? 0 : 65);
			blit(x, y, cx, cy, 13, 13);
		}

		private int getSele(double x, double y) {
			int xc = guiLeft;
			int yc = guiTop;
			for (int i = 0; i < CNUM; i++) {
				int x0 = xc + 53 - i / 4 * 13;
				int y0 = yc + 18 + i % 4 * 13;
				int x1 = xc + 110 + i / 4 * 13;
				if (i < container.data.inputCount() && x >= x0 && x < x0 + 13 && y > y0 && y < y0 + 13)
					return i;
				if (i < container.data.outputCount() && x >= x1 && x < x1 + 13 && y > y0 && y < y0 + 13)
					return i + CNUM;
			}
			return -1;
		}

	}

	public static class TE extends CTETE<TE> implements WireConnect.DraftTE {

		private LogicGate chip;
		private final SignalManager data = new SignalManager(this, CNUM, CNUM);

		public TE() {
			super(Registrar.TETD_GATE, 1);
			onChange(-1);
		}

		public TE(BlockState bs, IBlockReader w) {
			this();
		}

		@Override
		public Container createMenu(int type, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(type, pi, this, data);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("draft_gate");
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		public LogicGate getLogicGate() {
			return chip;
		}

		@Override
		public SignalManager getSignal() {
			return data;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return Cont.isChip(stack);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			onChange(-1);
			data.read(tag);
		}

		@Override
		public int[] update(int[] vals) {
			int[] ans = new int[CNUM];
			if (chip != null) {
				int input = 0;
				for (int i = 0; i < chip.input; i++) {
					int ch = data.getInput(i);
					int val = S_ERR;
					if (ch == C_HIGH)
						val = S_HIGH;
					if (ch == C_FLOAT)
						val = S_FLOAT;
					if (ch == C_LOW)
						val = S_LOW;
					if (ch >= 0 && ch < CNUM)
						val = vals[ch];
					if (val == S_FLOAT) {
						for (int j = 0; j < chip.output; j++)
							ans[j] = S_FLOAT;
						return ans;
					}
					if (val == S_ERR) {
						for (int j = 0; j < chip.output; j++)
							ans[j] = S_ERR;
						return ans;
					}
					if (val == S_HIGH)
						input |= 1 << i;
				}
				int output = chip.compute(input);
				for (int i = 0; i < chip.output; i++) {
					int ch = data.getOutput(i);
					if (ch >= 0 && ch < CNUM)
						ans[ch] = (output & 1 << i) > 0 ? S_HIGH : S_LOW;
				}
			}
			return ans;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			data.write(tag);
			return tag;
		}

		@Override
		protected void onChange(int ind) {
			LogicGate temp = chip;
			chip = loadChip();
			if (temp == null || chip == null || temp.input != chip.input) {
				int cin = chip == null ? 0 : chip.input;
				for (int i = 0; i < CNUM; i++)
					if (i >= cin)
						data.setInput(i, C_FORBID);
					else
						data.setInput(i, C_LOW);
			}
			if (temp == null || chip == null || temp.output != chip.output) {
				int cout = chip == null ? 0 : chip.output;

				for (int i = 0; i < CNUM; i++)
					if (i >= cout)
						data.setOutput(i, C_FORBID);
					else
						data.setOutput(i, C_FLOAT);
			}
		}

		private LogicGate loadChip() {
			ItemStack is = this.getStackInSlot(0);
			if (is.isEmpty() || !(is.getItem() instanceof LogicDraft))
				return null;
			return ((LogicDraft) is.getItem()).getLogicGate(is);
		}

	}

	public DraftGate() {
		super(construct(Material.ROCK).addImpls((STE) TE::new, HOR));
	}

	@Override
	public Direction getInDire(BlockState b) {
		return b.get(HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public Direction getOutDire(BlockState b) {
		return b.get(HORIZONTAL_FACING);
	}
}
