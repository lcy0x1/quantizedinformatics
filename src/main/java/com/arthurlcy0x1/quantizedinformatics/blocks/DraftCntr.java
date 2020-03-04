package com.arthurlcy0x1.quantizedinformatics.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock.CTEScr;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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

		private static class GateNode implements Comparable<GateNode> {

			/** mask: NONE | INPUT | OUTPUT */
			private final int type;
			private final BlockPos pos;
			private final BlockState bs;
			private final DraftTE te;

			private WireNode input, output;

			private GateNode(World w, BlockPos p, int t) {
				pos = p;
				type = t;
				bs = w.getBlockState(p);
				te = (DraftTE) w.getTileEntity(p);
			}

			@Override
			public int compareTo(GateNode o) {
				return pos.compareTo(o.pos);
			}

		}

		private static class WireNode {

			private final GateNode[] nsin, nsou;
			private int[] vals = null;

			private WireNode(GateNode[] in, GateNode[] out) {
				nsin = in;
				nsou = out;
			}

			public int[] getSignal() {
				if (vals != null)
					return vals;
				vals = new int[CNUM];
				Arrays.fill(vals, S_FLOAT);
				for (GateNode n : nsou) {
					SignalManager sm = n.te.getSignal();
					int[] sigs = sm.getSignal();
					for (int i = 0; i < sm.outputCount(); i++) {
						int ch = sm.getOutput(i);
						if (vals[ch] == S_ERR)
							continue;
						int val = sigs[ch];
						if (val == S_ERR || vals[ch] != S_FLOAT && val != S_FLOAT && vals[ch] != val)
							vals[ch] = S_ERR;
						else if (vals[ch] == S_FLOAT)
							vals[ch] = val;
					}
				}
				return vals;
			}

		}

		/**
		 * A list of writer blocks that serves as external input point to the circuit
		 */
		private final List<GateNode> input = new ArrayList<>();

		/**
		 * A list of reader blocks that serves as external output point to the circuit
		 */
		private final List<GateNode> output = new ArrayList<>();

		/** A map of blocks in the circuit */
		private final Map<BlockPos, GateNode> nodes = new HashMap<>();

		/** A list of wire nodes in the circuit */
		private final List<WireNode> node = new ArrayList<>();

		private final SignalManager data = new SignalManager(this, CNUM, 0);

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
		public SignalManager getSignal() {
			return data;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			structureCheck();
			updateSignal();

		}

		@Override
		public int[] update(int[] vals) {
			// TODO add inputs
			return null;
		}

		private void structureCheck() {
			BlockPos[] endPoints = DraftWire.queryPoint(world, pos);
			GateNode self = new GateNode(world, pos, OUTPUT);

			input.clear();
			output.clear();
			nodes.clear();
			node.clear();

			nodes.put(pos, self);
			input.add(self);
			for (BlockPos p : endPoints) {
				BlockState bs = world.getBlockState(p);
				DraftIO t = (DraftIO) bs.getBlock();
				boolean hasIn = t.getInDire(bs) != null;
				boolean hasOut = t.getOutDire(bs) != null;
				GateNode n = new GateNode(world, p, (hasIn ? INPUT : 0) | (hasOut ? OUTPUT : 0));
				if (hasIn)
					output.add(n);
				if (hasOut)
					input.add(n);
				nodes.put(p, n);
			}

			TreeSet<GateNode> ins = new TreeSet<GateNode>(output);
			TreeSet<GateNode> ous = new TreeSet<GateNode>(input);

			while (ins.size() > 0) {
				GateNode n = ins.first();
				BlockState bs = n.bs;
				DraftIO d = (DraftIO) bs.getBlock();
				BlockPos p0 = n.pos.offset(d.getInDire(bs));
				BlockPos[][] q = DraftWire.queryGate(world, p0);
				GateNode[] nsin = new GateNode[q[0].length];
				GateNode[] nsou = new GateNode[q[1].length];
				WireNode wire = new WireNode(nsin, nsou);
				for (int i = 0; i < q[0].length; i++) {
					GateNode node = nodes.get(q[0][i]);
					nsin[i] = node;
					ins.remove(node);
					node.input = wire;
				}
				for (int i = 0; i < q[1].length; i++) {
					GateNode node = nodes.get(q[1][i]);
					nsou[i] = node;
					ous.remove(node);
					node.output = wire;
				}
			}

			while (ous.size() > 0) {
				GateNode n = ous.first();
				BlockState bs = n.bs;
				DraftIO d = (DraftIO) bs.getBlock();
				BlockPos p0 = n.pos.offset(d.getOutDire(bs));
				BlockPos[][] q = DraftWire.queryGate(world, p0);
				GateNode[] nsin = new GateNode[q[0].length];
				GateNode[] nsou = new GateNode[q[1].length];
				WireNode wire = new WireNode(nsin, nsou);
				for (int i = 0; i < q[0].length; i++) {
					GateNode node = nodes.get(q[0][i]);
					nsin[i] = node;
					ins.remove(node);
					node.input = wire;
				}
				for (int i = 0; i < q[1].length; i++) {
					GateNode node = nodes.get(q[1][i]);
					nsou[i] = node;
					ous.remove(node);
					node.output = wire;
				}
			}
		}

		private void updateSignal() {
			for (GateNode n : nodes.values())
				n.te.getSignal().updateSignal(n.input.getSignal());
			for (GateNode n : nodes.values())
				n.te.getSignal().post();
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_center");

}
