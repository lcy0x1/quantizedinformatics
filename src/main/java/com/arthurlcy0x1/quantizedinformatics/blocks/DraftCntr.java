package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTEScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import static com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftIO.*;

public class DraftCntr {

	public static class Cont extends CTEBlock.CTECont implements DraftCont {

		private final SignalWriter data;

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1), new IntArray(CNUM));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent, IIntArray arr) {
			super(Registrar.CTD_CNTR, id, inv, ent, 0);// TODO height
			addSlot(new ResultSlot(ent, 0, 0, 0));// TODO x and y
			trackIntArray(arr);
			data = new SignalWriter(0, CNUM, arr);
		}

		@Override
		public SignalWriter getSignal() {
			return data;
		}

	}

	public static class Scr extends CTEScr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 0);// TODO height
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			// TODO draw gui

		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements DraftTE, ITickableTileEntity {

		private static class TerminalSignal implements ISignalManager {

			private int[] data = new int[CNUM];

			@Override
			public int get(int index) {
				return data[index];
			}

			@Override
			public int getInput(int i) {
				return 0;
			}

			@Override
			public int getOutput(int i) {
				return i;
			}

			@Override
			public int[] getSignal() {
				return data;
			}

			@Override
			public int inputCount() {
				return 0;
			}

			@Override
			public int outputCount() {
				return 16;
			}

			@Override
			public void post() {
			}

			public void read(CompoundNBT tag) {
				int[] arr = tag.getIntArray("channels");
				for (int i = 0; i < Math.min(arr.length, data.length); i++)
					data[i] = arr[i];
			}

			@Override
			public void set(int index, int value) {
				data[index] = value;
			}

			@Override
			public int size() {
				return CNUM;
			}

			@Override
			public void updateSignal(int[] signal) {
			}

			public void write(CompoundNBT tag) {
				tag.putIntArray("channels", data);
			}

		}

		private Circuit cir;

		private final TerminalSignal data = new TerminalSignal();

		public TE() {
			super(Registrar.TET_CNTR, 1);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, pi, this, data);
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

		@Override
		public ISignalManager getSignal() {
			return data;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			data.read(tag);
		}

		@Override
		public void tick() {
			cir = null;
			if (world.isRemote)
				return;
			cir = new Circuit(world, pos);
			cir.updateSignal();

		}

		@Override
		public int[] update(int[] vals) {
			return data.getSignal();
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			data.write(tag);
			return tag;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_center");

}
