package com.arthurlcy0x1.quantizedinformatics.blocks.logic;

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

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.Wire;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftIO;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.DraftTE;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.ISignalManager;
import com.arthurlcy0x1.quantizedinformatics.blocks.logic.DraftGate.TE;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.GateContainer;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.ParentDiagram;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Circuit {

	private static class GateNode implements Comparable<GateNode> {

		private final int type;
		private final BlockPos pos;
		private final BlockState bs;
		private final DraftTE te;
		private final ISignalManager sm;

		private WireNode input, output;

		/** assigned and used by circuit */
		private int[] ids;
		private int dep, state;
		private GateContainer cont;

		private int[] rev;

		private GateNode(World w, BlockPos p) {
			pos = p;
			bs = w.getBlockState(p);
			te = (DraftTE) w.getTileEntity(p);
			sm = te.getSignal();
			if (te instanceof DraftGate.TE)
				type = T_GATE;
			else if (te instanceof DraftIn.TE)
				type = T_EIN;
			else if (te instanceof DraftOut.TE)
				type = T_EOUT;
			else if (te instanceof DraftCntr.TE)
				type = T_EIN;
			else if (te instanceof DraftLnr.TE)
				type = T_LNR;
			else
				type = T_UNK;
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

		private void clear() {
			ids = null;
			dep = 0;
			state = 0;
			cont = null;
			rev = null;
		}

	}

	private static class WireNode {

		private final GateNode[] nsin, nsou;
		private int[] vals = null;
		private int[] lnrs = null;
		private int[] outs = null;
		private GateNode[] pvd = null;
		private boolean dupOut = false;
		private int errorCode = 0;

		private WireNode(GateNode[] in, GateNode[] out) {
			nsin = in;
			nsou = out;
		}

		private void clear() {
			vals = null;
			lnrs = null;
			pvd = null;
			dupOut = false;
			errorCode = 0;
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
				return errorCode = ERR_CONFLICT;
			return errorCode = -1;
		}

		/** for circuit generation */
		private int[] getLnrs() {
			if (lnrs != null)
				return lnrs;
			lnrs = new int[CNUM];
			for (GateNode n : nsin)
				if (n.type != T_LNR)
					for (int i = 0; i < n.sm.inputCount(); i++) {
						int ch = n.sm.getInput(i);
						if (ch >= 0 && ch < CNUM)
							lnrs[ch]++;

					}
			return lnrs;
		}

		private int[] getOuts() {
			if (outs != null)
				return outs;
			outs = new int[CNUM];
			for (GateNode n : nsou)
				for (int i = 0; i < n.sm.outputCount(); i++) {
					int ch = n.sm.getOutput(i);
					if (ch < 0 || ch >= CNUM)
						continue;
					outs[ch]++;
				}
			return outs;
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
				for (int i = 0; i < n.sm.outputCount(); i++) {
					int ch = n.sm.getOutput(i);
					if (ch < 0 || ch >= CNUM || vals[ch] == S_ERR)
						continue;
					int val = n.sm.getSignal(ch);
					if (val == S_ERR || vals[ch] != S_FLOAT && val != S_FLOAT && vals[ch] != val)
						vals[ch] = S_ERR;
					else if (vals[ch] == S_FLOAT)
						vals[ch] = val;
				}
			}
			return vals;
		}

	}

	private final static int T_UNK = 0, T_EIN = 1, T_EOUT = 2, T_GATE = 3, T_LNR = 4;

	public static final int ERR_CONFLICT = 1, ERR_FLOAT = 2, ERR_LOOP = 3;

	private final World world;

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

	private int loop = -1;

	public Circuit(World w, BlockPos pos) {
		world = w;
		BlockPos[] endPoints = Wire.queryPoint(world, pos);
		GateNode self = new GateNode(world, pos);

		nodes.put(pos, self);
		input.add(self);
		for (BlockPos p : endPoints) {
			BlockState bs = world.getBlockState(p);
			DraftIO t = (DraftIO) bs.getBlock();
			boolean hasIn = t.getInDire(bs) != null;
			boolean hasOut = t.getOutDire(bs) != null;
			GateNode n = new GateNode(world, p);
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
			Direction dir = d.getInDire(bs);
			BlockPos p0 = n.pos.offset(dir);
			BlockState bs0 = world.getBlockState(p0);
			Block b0 = bs0.getBlock();
			if (b0 == Registrar.BD_WIRE)
				addWire(Wire.queryGate(world, p0));
			else if (b0 instanceof DraftIO && ((DraftIO) b0).ioType(bs0, dir.getOpposite()) == OUTPUT)
				addWire(new BlockPos[][] { { n.pos }, { p0 } });
			else
				addWire(new BlockPos[][] { { n.pos }, {} });
		}

		while (ous.size() > 0) {
			GateNode n = ous.first();
			BlockState bs = n.bs;
			DraftIO d = (DraftIO) bs.getBlock();
			Direction dir = d.getOutDire(bs);
			BlockPos p0 = n.pos.offset(dir);
			BlockState bs0 = world.getBlockState(p0);
			Block b0 = bs0.getBlock();
			if (b0 == Registrar.BD_WIRE)
				addWire(Wire.queryGate(world, p0));
			else if (b0 instanceof DraftIO && ((DraftIO) b0).ioType(bs0, dir.getOpposite()) == INPUT)
				addWire(new BlockPos[][] { { p0 }, { n.pos } });
			else
				addWire(new BlockPos[][] { {}, { n.pos } });
		}
	}

	public void clear() {
		for (GateNode gn : nodes.values())
			gn.clear();
		for (WireNode wn : node)
			wn.clear();
		loop = -1;
	}

	public ParentDiagram getLogic() {
		int id = 0;
		for (GateNode gn : input) {
			int n = 0;
			gn.ids = new int[CNUM];
			Arrays.fill(gn.ids, -1);
			GateNode[] pvd = gn.output.getProvider();
			for (int i = 0; i < gn.sm.outputCount(); i++) {
				int ch = gn.sm.getOutput(i);
				if (ch >= 0 && ch < CNUM && gn.sm.getSignal(ch) != S_FLOAT && pvd[ch] == gn)
					gn.ids[n++] = id++;
			}
		}

		ParentDiagram diag = new ParentDiagram(id, output.size());
		List<GateNode> cont = new ArrayList<>();
		for (GateNode gn : nodes.values())
			if (gn.type == T_GATE) {
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

	public boolean multiCntr() {
		int count = 0;
		for (GateNode gn : nodes.values())
			if (gn.te instanceof DraftCntr.TE)
				count++;
		return count > 1;
	}

	/** update the blocks in this circuit */
	public void updateSignal() {
		for (GateNode n : nodes.values())
			n.sm.updateSignal(n.input == null ? null : n.input.getSignal());
		for (GateNode n : nodes.values()) {
			n.sm.post();
			if (n.sm.inputCount() > 0 && n.input != null)
				n.sm.updateValidity(true, n.input.getOuts());
			if (n.sm.outputCount() > 0 && n.output != null)
				n.sm.updateValidity(false, n.output.getOuts());
		}
	}

	protected int[] getInfo() {
		int[] ans = new int[nodes.size() * 6];
		int i = 0;
		isValid();
		for (GateNode n : nodes.values()) {
			int id = Registrar.BDS.indexOf(n.bs.getBlock());
			int err0 = Math.max(0, n.input == null ? 0 : n.input.getErrorCode());
			int err1 = Math.max(0, n.output == null ? 0 : n.output.getErrorCode());
			int err2 = n.dep > 0 ? 1 : 0;
			ans[i++] = id | err0 << 4 | err1 << 6 | err2 << 8 | n.state << 9;
			ans[i++] = n.pos.getX();
			ans[i++] = n.pos.getY();
			ans[i++] = n.pos.getZ();
			ans[i++] = n.input == null ? 0 : node.indexOf(n.input) + 1;
			ans[i++] = n.output == null ? 0 : node.indexOf(n.output) + 1;
		}
		return ans;
	}

	private void addWire(BlockPos[][] q) {
		GateNode[] nsin = new GateNode[q[0].length];
		GateNode[] nsou = new GateNode[q[1].length];
		WireNode wire = new WireNode(nsin, nsou);
		for (int i = 0; i < q[0].length; i++) {
			GateNode gn = getNode(q[0][i]);
			nsin[i] = gn;
			ins.remove(gn);
			gn.input = wire;
		}
		for (int i = 0; i < q[1].length; i++) {
			GateNode gn = getNode(q[1][i]);
			nsou[i] = gn;
			ous.remove(gn);
			gn.output = wire;
		}
		node.add(wire);
	}

	private GateNode getNode(BlockPos p) {
		if (nodes.containsKey(p))
			return nodes.get(p);
		GateNode ans = new GateNode(world, p);
		nodes.put(p, ans);
		if (ans.sm.inputCount() > 0)
			ins.add(ans);
		if (ans.sm.outputCount() > 0)
			ous.add(ans);
		return nodes.get(p);
	}

	private boolean hasLoop() {
		for (WireNode w : node)
			if (w.getErrorCode() != -1)
				return false;
		if (loop >= 0)
			return loop == 0;
		for (GateNode gn : nodes.values())
			gn.dep = 0;
		for (GateNode gn : nodes.values())
			if (gn.type != T_LNR && gn.input != null) {
				GateNode[] pvd = gn.input.getProvider();
				for (int i = 0; i < gn.sm.inputCount(); i++) {
					int ch = gn.sm.getInput(i);
					if (ch >= 0 && ch < CNUM)
						pvd[ch].dep++;
				}
			}
		Queue<GateNode> q = new ArrayDeque<>();
		for (GateNode gn : nodes.values())
			if (gn.type != T_LNR && gn.dep == 0)
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
		loop = nodes.size() - rmv;
		return loop == 0;
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
