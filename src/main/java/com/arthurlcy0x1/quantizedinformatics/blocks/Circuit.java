package com.arthurlcy0x1.quantizedinformatics.blocks;

import static com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;
import static com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;

import com.arthurlcy0x1.quantizedinformatics.blocks.DraftGate.TE;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftIO;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.ISignalManager;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.GateContainer;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.ParentDiagram;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Circuit {

	private static class GateNode implements Comparable<GateNode> {

		private final BlockPos pos;
		private final BlockState bs;
		private final DraftTE te;
		private final ISignalManager sm;

		private WireNode input, output;

		/** assigned and used by circuit */
		private int[] ids;
		private int dep;
		private GateContainer cont;

		private int[] rev;

		private GateNode(World w, BlockPos p, int t) {
			pos = p;
			bs = w.getBlockState(p);
			te = (DraftTE) w.getTileEntity(p);
			sm = te.getSignal();
		}

		@Override
		public int compareTo(GateNode o) {
			return pos.compareTo(o.pos);
		}

		public int rev(int ch) {
			if (rev != null)
				return rev[ch];
			int[] rev = new int[CNUM];
			Arrays.fill(rev, -1);
			for (int i = 0; i < sm.outputCount(); i++) {
				int cha = sm.getOutput(i);
				if (cha >= 0 && cha < CNUM)
					rev[cha] = i;
			}
			return rev[ch];
		}

	}

	private static class WireNode {

		private final GateNode[] nsin, nsou;
		private int[] vals = null;
		private int[] lnrs = null;
		private GateNode[] pvd = null;
		private boolean dupOut = false;
		private int errorCode = 0;

		private WireNode(GateNode[] in, GateNode[] out) {
			nsin = in;
			nsou = out;
		}

		private int getErrorCode() {
			if (errorCode != 0)
				return errorCode;
			int[] sig = getSignal();
			for (int i : sig)
				if (i == S_ERR)
					return errorCode = ERR_CONFLICT;
			int[] lnr = getLnrs();
			for (int i = 0; i < CNUM; i++)
				if (lnr[i] > 0 && sig[i] == S_FLOAT)
					return errorCode = ERR_FLOAT;
			if (getProvider() == null)
				return errorCode = ERR_FLOAT;
			return errorCode = -1;
		}

		private int[] getLnrs() {
			if (lnrs != null)
				return lnrs;
			lnrs = new int[CNUM];
			for (GateNode n : nsin)
				for (int i = 0; i < n.sm.inputCount(); i++) {
					int ch = n.sm.getInput(i);
					if (ch >= 0 && ch < CNUM)
						lnrs[ch]++;

				}
			return lnrs;
		}

		private GateNode[] getProvider() {
			if (dupOut)
				return null;
			if (pvd != null)
				return pvd;
			pvd = new GateNode[CNUM];
			for (GateNode n : nsou)
				for (int i = 0; i < n.sm.outputCount(); i++) {
					int ch = n.sm.getOutput(i);
					if (ch < 0 || ch >= CNUM)
						continue;
					if (pvd[ch] == null)
						pvd[ch] = n;
					else {
						dupOut = true;
						return null;
					}

				}
			return pvd;
		}

		private int[] getSignal() {
			if (vals != null)
				return vals;
			vals = new int[CNUM];
			for (GateNode n : nsou) {
				int[] sigs = n.sm.getSignal();
				for (int i = 0; i < n.sm.outputCount(); i++) {
					int ch = n.sm.getOutput(i);
					if (ch < 0 || ch >= CNUM || vals[ch] == S_ERR)
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

	public static final int ERR_CONFLICT = 1, ERR_FLOAT = 2, ERR_LOOP = 3;

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

	/** for constructor use only */
	private TreeSet<GateNode> ins, ous;

	public Circuit(World world, BlockPos pos) {
		BlockPos[] endPoints = DraftWire.queryPoint(world, pos);
		GateNode self = new GateNode(world, pos, OUTPUT);

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

		ins = new TreeSet<GateNode>(output);
		ous = new TreeSet<GateNode>(input);

		while (ins.size() > 0) {
			GateNode n = ins.first();
			BlockState bs = n.bs;
			DraftIO d = (DraftIO) bs.getBlock();
			BlockPos p0 = n.pos.offset(d.getInDire(bs));
			addWire(world, p0);
		}

		while (ous.size() > 0) {
			GateNode n = ous.first();
			BlockState bs = n.bs;
			DraftIO d = (DraftIO) bs.getBlock();
			BlockPos p0 = n.pos.offset(d.getOutDire(bs));
			addWire(world, p0);
		}
	}

	public ParentDiagram getLogic() {
		int id = 0;
		for (GateNode gn : input) {
			int n = 0;
			gn.ids = new int[CNUM];
			Arrays.fill(gn.ids, -1);
			GateNode[] pvd = gn.output.getProvider();
			for (int i = 0; i < gn.sm.inputCount(); i++) {
				int ch = gn.sm.getInput(i);
				if (ch >= 0 && ch < CNUM && gn.sm.getSignal()[ch] != S_FLOAT && pvd[ch] == gn)
					gn.ids[n++] = id++;
			}
		}

		ParentDiagram diag = new ParentDiagram(id, output.size());
		List<GateNode> cont = new ArrayList<>();
		for (GateNode gn : nodes.values())
			if (gn.te instanceof TE) {
				TE te = (TE) gn.te;
				cont.add(gn);
				gn.cont = diag.addGate(te.getLogicGate());
			}
		for (GateNode gn : cont)
			for (int i = 0; i < gn.sm.inputCount(); i++)
				setCont(gn.cont, gn, i, gn.sm.getInput(i));

		for (int i = 0; i < output.size(); i++) {
			GateNode gn = output.get(i);
			setCont(diag, gn, i, gn.sm.getInput(i));
		}

		return diag;
	}

	/**
	 * return if this circuit is valid for production<br>
	 * check for error lines, floating lines, and loops
	 */
	public int isValid() {
		for (WireNode w : node)
			if (w.getErrorCode() != -1)
				return w.getErrorCode();
		if (!hasLoop())
			return ERR_LOOP;
		return -1;
	}

	/** update the blocks in this circuit */
	public void updateSignal() {
		for (GateNode n : nodes.values())
			n.sm.updateSignal(n.input.getSignal());
		for (GateNode n : nodes.values())
			n.sm.post();
	}

	private void addWire(World world, BlockPos p0) {
		BlockPos[][] q = DraftWire.queryGate(world, p0);
		GateNode[] nsin = new GateNode[q[0].length];
		GateNode[] nsou = new GateNode[q[1].length];
		WireNode wire = new WireNode(nsin, nsou);
		for (int i = 0; i < q[0].length; i++) {
			GateNode gn = nodes.get(q[0][i]);
			nsin[i] = gn;
			ins.remove(gn);
			gn.input = wire;
		}
		for (int i = 0; i < q[1].length; i++) {
			GateNode gn = nodes.get(q[1][i]);
			nsou[i] = gn;
			ous.remove(gn);
			gn.output = wire;
		}
		node.add(wire);
	}

	private boolean hasLoop() {
		for (GateNode gn : nodes.values())
			gn.dep = 0;
		for (GateNode gn : nodes.values()) {
			GateNode[] pvd = gn.input.getProvider();
			for (int i = 0; i < gn.sm.inputCount(); i++) {
				int ch = gn.sm.getInput(i);
				if (ch >= 0 && ch < CNUM)
					pvd[ch].dep++;
			}
		}
		Queue<GateNode> q = new ArrayDeque<>();
		for (GateNode gn : nodes.values())
			if (gn.dep == 0)
				q.add(gn);
		int rmv = 0;
		while (q.size() > 0) {
			GateNode gn = q.poll();
			for (int i = 0; i < gn.sm.inputCount(); i++) {
				int ch = gn.sm.getInput(i);
				if (ch >= 0 && ch < CNUM) {
					GateNode pvd = gn.input.getProvider()[ch];
					pvd.dep--;
					if (pvd.dep == 0)
						q.add(pvd);
				}
			}
			rmv++;
		}
		return rmv == nodes.size();
	}

	private void setCont(LogicDiagram cont, GateNode gn, int i, int ch) {
		if (ch == C_HIGH)
			cont.setInput(i, null, TRUE);
		if (ch == C_LOW)
			cont.setInput(i, null, FALSE);
		GateNode inp = gn.input.getProvider()[ch];
		if (inp.cont == null)
			cont.setInput(i, null, inp.ids[ch]);
		else
			cont.setInput(i, inp.cont, inp.rev(ch));
	}

}
