package com.arthurlcy0x1.quantizedindustry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedindustry.IPower.PowerCont;
import com.arthurlcy0x1.quantizedindustry.IPower.PowerTE;
import com.arthurlcy0x1.quantizedindustry.items.FluidContItem;
import com.arthurlcy0x1.quantizedinformatics.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface IFluidTE {

	public static class FluidManager implements IFluidTE {

		public static class FluidTank implements SingleFluidTank {

			private final Priority pri;
			private final QuanFluid type;

			private double max, cur;

			public FluidTank(Priority p, QuanFluid t) {
				pri = p;
				type = t;
			}

			@Override
			public double addStorage(double ncur) {
				if (Math.abs(ncur) < getMaxStorage())
					ncur = ncur >= 0 ? 0 : -getMaxStorage() * ERR;
				cur += ncur;
				if (cur >= 0 && cur <= max)
					return 0;
				else if (cur < 0) {
					double dif = cur;
					cur = 0;
					return dif;
				} else {
					double dif = cur - max;
					cur = max;
					return dif;
				}
			}

			@Override
			public QuanFluid getFluidType() {
				return type;
			}

			@Override
			public double getMaxStorage() {
				return max;
			}

			@Override
			public Priority getPriority() {
				return pri;
			}

			@Override
			public double getStorage() {
				return cur;
			}

			@Override
			public void read(CompoundNBT tag) {
				max = tag.getDouble("max");
				cur = tag.getDouble("cur");
			}

			@Override
			public void setMaxStorage(double nmax) {
				max = nmax;
				if (cur > max)
					cur = max;
			}

			@Override
			public CompoundNBT write(CompoundNBT tag) {
				tag.putDouble("max", max);
				tag.putDouble("cur", cur);
				return tag;
			}

		}

		public static interface SingleFluidTank {

			public default boolean acceptNewFluid(QuanFluid f) {
				return false;
			}

			public default ItemStack addFluidTo(ItemStack is, double limit) {
				QuanFluid it = FluidContItem.getFluid(is);
				QuanFluid tt = getFluidType();
				if (tt == null || it != null && tt != it)
					return is;
				double imax = FluidContItem.getMaxStorage(is);
				double iamount = FluidContItem.getStorage(is);
				double toAdd = Math.min(limit, imax - iamount);
				iamount += toAdd + addStorage(-toAdd);
				FluidContItem.setFluid(is, tt, iamount);
				return is;
			}

			public double addStorage(double ncur);

			public default ItemStack getFluidFrom(ItemStack is, double limit) {
				QuanFluid it = FluidContItem.getFluid(is);
				QuanFluid tt = getFluidType();
				if (it == null || it != tt && !acceptNewFluid(it))
					return is;
				if (tt == null)
					setFluidType(it);
				double amount = FluidContItem.getStorage(is);
				amount -= Math.min(limit, amount) - addStorage(Math.min(limit, amount));
				FluidContItem.setFluid(is, it, amount);
				return is;
			}

			public QuanFluid getFluidType();

			public default double getInFlowRate() {
				return Math.min(getSpace(), getMaxStorage() * FLOWRATE);
			}

			public double getMaxStorage();

			public default double getOutFlowRate() {
				return Math.min(getStorage(), getMaxStorage() * FLOWRATE);
			}

			public Priority getPriority();

			public default double getSpace() {
				return getMaxStorage() - getStorage();
			}

			public double getStorage();

			public void read(CompoundNBT tag);

			public default void setFluidType(QuanFluid f) {
			}

			public void setMaxStorage(double nmax);

			public CompoundNBT write(CompoundNBT tag);

		}

		public static class VarFluidTank implements SingleFluidTank {

			private final Priority pri;
			private final Predicate<QuanFluid> type;
			private double max, cur;
			private QuanFluid fluid;

			public VarFluidTank(Priority p, Predicate<QuanFluid> pred) {
				pri = p;
				type = pred;
			}

			@Override
			public boolean acceptNewFluid(QuanFluid f) {
				return fluid == null && type.test(f);
			}

			@Override
			public double addStorage(double ncur) {
				if (Math.abs(ncur) < getMaxStorage())
					ncur = ncur >= 0 ? 0 : -getMaxStorage() * ERR;
				cur += ncur;
				if (cur <= ERR * max) {
					double dif = cur;
					cur = 0;
					fluid = null;
					return dif;
				}
				if (cur <= max)
					return 0;
				double dif = cur - max;
				cur = max;
				return dif;
			}

			@Override
			public QuanFluid getFluidType() {
				return fluid;
			}

			@Override
			public double getMaxStorage() {
				return max;
			}

			@Override
			public Priority getPriority() {
				return pri;
			}

			@Override
			public double getStorage() {
				return cur;
			}

			@Override
			public void read(CompoundNBT tag) {
				max = tag.getDouble("max");
				cur = tag.getDouble("cur");

			}

			@Override
			public void setFluidType(QuanFluid f) {
				fluid = f;
			}

			@Override
			public void setMaxStorage(double nmax) {
				max = nmax;
				if (cur > max)
					cur = max;
			}

			@Override
			public CompoundNBT write(CompoundNBT tag) {
				tag.putDouble("max", max);
				tag.putDouble("cur", cur);
				return tag;
			}

		}

		private final SingleFluidTank[] tanks;

		/**
		 * warning: it does not allow two containers to accept same fluid. Be careful
		 * when using VarFluidTank to not conflict with others
		 */
		public FluidManager(SingleFluidTank... sfts) {
			tanks = sfts;
		}

		@Override
		public boolean acceptNewFluid(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.acceptNewFluid(f))
					return true;
			return false;
		}

		@Override
		public double addStorage(QuanFluid type, double max) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == type || t.acceptNewFluid(type)) {
					if (t.getFluidType() == null)
						t.setFluidType(type);
					return t.addStorage(max);
				}
			return max;
		}

		@Override
		public QuanFluid getFluidType(int ind) {
			return tanks[ind].getFluidType();
		}

		@Override
		public Set<QuanFluid> getFluidTypes() {
			Set<QuanFluid> s = new HashSet<>();
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() != null)
					s.add(t.getFluidType());
			return s;
		}

		@Override
		public double getInFlowLimit(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == f)
					return t.getInFlowRate();
			return 0;
		}

		@Override
		public double getMaxStorage(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == f || t.acceptNewFluid(f))
					return t.getMaxStorage();
			return 0;
		}

		@Override
		public double getOutFlowLimit(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == f)
					return t.getOutFlowRate();
			return 0;
		}

		@Override
		public Priority getPriority(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == f)
					return t.getPriority();
			return null;
		}

		@Override
		public double getStorage(QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == f)
					return t.getStorage();
			return 0;
		}

		@Override
		public boolean hasFluidType(Priority p, QuanFluid f) {
			for (SingleFluidTank t : tanks)
				if (t.getPriority() == p && (t.getFluidType() == f || p != Priority.PRODUCER && t.acceptNewFluid(f)))
					return true;
			return false;
		}

		public void read(CompoundNBT tag) {
			for (int i = 0; i < tanks.length; i++)
				tanks[i].read(tag.getCompound("" + i));
		}

		public double retrieveFluid(QuanFluid type, double max) {
			for (SingleFluidTank t : tanks)
				if (t.getFluidType() == type)
					return -t.addStorage(-max);
			return max;
		}

		public CompoundNBT write(CompoundNBT tag) {
			for (int i = 0; i < tanks.length; i++)
				tag.put("" + i, tanks[i].write(new CompoundNBT()));
			return tag;
		}

	}

	public static interface FluidManagerTE extends IFluidTE {

		@Override
		public default boolean acceptNewFluid(QuanFluid f) {
			return getManager().acceptNewFluid(f);
		}

		@Override
		public default double addStorage(QuanFluid f, double q) {
			return getManager().addStorage(f, q);
		}

		@Override
		public default QuanFluid getFluidType(int ind) {
			return getManager().getFluidType(ind);
		}

		@Override
		public default Set<QuanFluid> getFluidTypes() {
			return getManager().getFluidTypes();
		}

		@Override
		public default double getInFlowLimit(QuanFluid f) {
			return getManager().getInFlowLimit(f);
		}

		public FluidManager getManager();

		@Override
		public default double getMaxStorage(QuanFluid f) {
			return getManager().getMaxStorage(f);
		}

		@Override
		public default double getOutFlowLimit(QuanFluid f) {
			return getManager().getOutFlowLimit(f);
		}

		@Override
		public default Priority getPriority(QuanFluid f) {
			return getManager().getPriority(f);
		}

		@Override
		public default double getStorage(QuanFluid f) {
			return getManager().getStorage(f);
		}

		@Override
		public default boolean hasFluidType(Priority p, QuanFluid f) {
			return getManager().hasFluidType(p, f);
		}

	}

	public static class FluidPowerCont<T extends PowerTE<T, C> & IFluidTE, C extends FluidPowerCont<T, C>>
			extends PowerCont<T, C> implements IFluidCont {

		protected final QuanFluid[] fluids;

		protected FluidPowerCont(ContainerType<C> type, int id, PlayerInventory inv, IInventory ent, int h,
				IIntArray arr, int fluidCount) {
			super(type, id, inv, ent, h, arr);
			fluids = new QuanFluid[fluidCount];
		}

		@Override
		public void detectAndSendChanges() {
			super.detectAndSendChanges();
			boolean send = false;
			for (int i = 0; i < fluids.length; i++) {
				if (fluids[i] != te.getFluidType(i))
					send = true;
				fluids[i] = te.getFluidType(i);
			}
			if (send)
				PacketHandler.send(Msg.getMsg(windowId, fluids));
		}

		@Override
		public void setFluids(QuanFluid[] val) {
			for (int i = 0; i < Math.min(val.length, fluids.length); i++)
				fluids[i] = val[i];
		}

	}

	public static class FluidUpdator {

		private static class SingleUpdator {

			private final QuanFluid fluid;
			private final List<IFluidTE> pro = new ArrayList<>();
			private final List<IFluidTE> sto = new ArrayList<>();
			private final List<IFluidTE> con = new ArrayList<>();
			private final double flow, useStoFlow, useSupFlow, useConFlow;
			private double supFlow, conFlow, stoSup, stoCon;

			private SingleUpdator(QuanFluid f, List<IFluidTE> list) {
				fluid = f;
				for (IFluidTE cont : list)
					if (cont.hasFluidType(Priority.PRODUCER, fluid))
						pro.add(cont);
					else if (cont.hasFluidType(Priority.STORAGE, fluid))
						sto.add(cont);
					else if (cont.hasFluidType(Priority.CONSUMER, fluid))
						con.add(cont);
				for (IFluidTE c : pro)
					supFlow += c.getOutFlowLimit(fluid);
				for (IFluidTE c : con)
					conFlow += c.getInFlowLimit(fluid);
				for (IFluidTE c : sto) {
					stoSup += c.getOutFlowLimit(fluid);
					stoCon += c.getInFlowLimit(fluid);
				}
				if (supFlow >= conFlow) {
					useStoFlow = Math.min(stoCon, supFlow - conFlow);
					useSupFlow = conFlow + useStoFlow;
					useConFlow = conFlow;
					flow = useSupFlow;

				} else {
					useStoFlow = -Math.min(stoSup, conFlow - supFlow);
					useConFlow = supFlow - useStoFlow;
					useSupFlow = supFlow;
					flow = useConFlow;
				}
			}

			private void update(double fac) {
				for (IFluidTE c : pro) {
					double max = c.getOutFlowLimit(fluid);
					c.addStorage(fluid, -max * fac * useSupFlow / supFlow);
				}
				for (IFluidTE c : con) {
					double max = c.getInFlowLimit(fluid);
					c.addStorage(fluid, max * fac * useConFlow / conFlow);
				}
				for (IFluidTE c : pro)
					if (useStoFlow > 0) {
						double max = c.getInFlowLimit(fluid);
						c.addStorage(fluid, max * fac * useStoFlow / stoCon);
					} else {
						double max = c.getOutFlowLimit(fluid);
						c.addStorage(fluid, max * fac * useStoFlow / stoSup);
					}
			}

		}

		private final List<IFluidTE> list = new ArrayList<>();

		public void add(IFluidTE te) {
			list.add(te);
		}

		public void clear() {
			list.clear();
		}

		/** return required power */
		public double update(double power) {
			Set<QuanFluid> set = new HashSet<>();
			for (IFluidTE pro : list)
				set.addAll(pro.getFluidTypes());
			double reqPow = 0;
			for (QuanFluid fluid : set) {
				SingleUpdator su = new SingleUpdator(fluid, list);
				double p = fluid.getViscocity() * su.flow * su.flow;
				reqPow += p;
				if (p == 0 || power == 0)
					continue;
				if (p <= power) {
					power -= p;
					su.update(1);
				} else {
					su.update(Math.sqrt(power / fluid.getViscocity()) / su.flow);
					power = 0;
				}
			}
			return reqPow;
		}

	}

	public static interface IFluidCont {

		public void setFluids(QuanFluid[] val);

	}

	public static class Msg {

		public static Msg decode(PacketBuffer packet) {
			int wid = packet.readInt();
			int n = packet.readVarInt();
			String[] rls = new String[n];
			for (int i = 0; i < n; i++)
				rls[i] = packet.readString();
			return new Msg(wid, rls);
		}

		public static Msg getMsg(int id, QuanFluid[] fs) {
			String[] val = new String[fs.length];
			for (int i = 0; i < fs.length; i++)
				val[i] = fs[i] == null ? "" : fs[i].getRegistryName().toString();
			return new Msg(id, val);
		}

		private final String[] val;
		private final int wid;

		public Msg(int id, String... strs) {
			wid = id;
			val = strs;
		}

		public void encode(PacketBuffer packet) {
			packet.writeInt(wid);
			packet.writeVarInt(val.length);
			for (String rl : val)
				packet.writeString(rl);
		}

		public void handle(Supplier<Context> sup) {
			Context ctx = sup.get();
			ctx.enqueueWork(() -> this.handle(ctx));
			ctx.setPacketHandled(true);
		}

		private void handle(Context ctx) {
			Container c = Minecraft.getInstance().player.openContainer;
			if (c != null && c.windowId == wid && c instanceof IFluidCont) {
				QuanFluid[] fluids = new QuanFluid[val.length];
				for (int i = 0; i < val.length; i++) {
					if (val[i].length() == 0)
						continue;
					ResourceLocation rl = new ResourceLocation(val[i]);
					fluids[i] = GameRegistry.findRegistry(QuanFluid.class).getValue(rl);
				}
				((IFluidCont) c).setFluids(fluids);
			}
		}

	}

	public static interface NullFluidTE {

		public default boolean acceptNewFluid(QuanFluid f) {
			return false;
		}

		public default double addStorage(QuanFluid f, double q) {
			return q;
		}

		public default Set<QuanFluid> getFluidTypes() {
			return new HashSet<>();
		}

		public default double getInFlowLimit(QuanFluid f) {
			return 0;
		}

		public default double getMaxStorage(QuanFluid f) {
			return 0;
		}

		public default double getOutFlowLimit(QuanFluid f) {
			return 0;
		}

		public default Priority getPriority(QuanFluid f) {
			return null;
		}

		public default double getStorage(QuanFluid f) {
			return 0;
		}

		public default boolean hasFluidType(Priority p, QuanFluid f) {
			return false;
		}

	}

	public static enum Priority {
		PRODUCER, STORAGE, CONSUMER
	}

	public static final double ERR = 1e-6, FLOWRATE = 1e-2;

	public static boolean isFluidCont(ItemStack is) {
		return false;// TODO fluid cont pred
	}

	public boolean acceptNewFluid(QuanFluid f);

	public double addStorage(QuanFluid f, double q);

	public QuanFluid getFluidType(int ind);

	public Set<QuanFluid> getFluidTypes();

	public double getInFlowLimit(QuanFluid f);

	public double getMaxStorage(QuanFluid f);

	public double getOutFlowLimit(QuanFluid f);

	public Priority getPriority(QuanFluid f);

	public default double getSpace(QuanFluid f) {
		return getMaxStorage(f) - getStorage(f);
	}

	public double getStorage(QuanFluid f);

	public boolean hasFluidType(Priority p, QuanFluid f);

}
