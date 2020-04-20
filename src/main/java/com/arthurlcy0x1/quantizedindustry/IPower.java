package com.arthurlcy0x1.quantizedindustry;

import java.util.ArrayList;
import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;

public interface IPower {

	public static abstract class ConTE<T extends ConTE<T, C>, C extends PowerCont<T, C>> extends PowerTE<T, C>
			implements IConsumer, TempStorage {

		private int temp, powIn, powCon;

		public ConTE(TileEntityType<T> type, IPowerContFactory<T, C> fac, int size) {
			super(type, fac, size);
		}

		@Override
		public int get(int ind) {
			if (ind == 0)
				return temp;
			if (ind == 1)
				return getMaxStorage();
			if (ind == 2)
				return powIn;
			if (ind == 3)
				return powCon;
			return 0;
		}

		@Override
		public final int getMaxInputPower() {
			int max = getMaxPowerInternal();
			int sp = getSpace();
			if (sp < max)
				return sp;
			return max;
		}

		@Override
		public final int getMinInputPower() {
			int min = getMinPowerInternal();
			int sp = getSpace();
			if (sp < min)
				return sp;
			return min;
		}

		@Override
		public final int getStorage() {
			return temp;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			temp = tag.getInt("power.temp");
		}

		@Override
		public final void setPowerIn(int pow) {
			temp += pow;
			powIn = pow;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			powCon = 0;
			if (temp > getMaxStorage())
				temp = getMaxStorage();
			if (temp < 0)
				temp = 0;
			if (temp == 0 || temp < getLowPowerInternal())
				doNoPower();
			else if (temp < getMaxStorage() * FAC) {
				temp -= powCon = getLowPowerInternal();
				doLowPower();
			} else {
				temp -= powCon = getHighPowerInternal();
				doHighPower();
			}
			temp = (int) (temp * (1 - getDecayRate()));
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("power.temp", temp);
			return tag;
		}

		protected abstract void doHighPower();

		protected abstract void doLowPower();

		protected abstract void doNoPower();

		protected abstract int getHighPowerInternal();

		protected abstract int getLowPowerInternal();

		protected final void takeTempStorage(int sto) {
			temp -= sto;
		}

	}

	public static abstract class GenTE<T extends GenTE<T, C>, C extends PowerCont<T, C>> extends PowerTE<T, C>
			implements IGenerator, TempStorage {

		private int temp, powOut, powSup;

		public GenTE(TileEntityType<T> type, IPowerContFactory<T, C> fac, int size) {
			super(type, fac, size);
		}

		@Override
		public int get(int ind) {
			if (ind == 0)
				return temp;
			if (ind == 1)
				return getMaxStorage();
			if (ind == 2)
				return powOut;
			if (ind == 3)
				return powSup;
			return 0;
		}

		@Override
		public final int getMaxOutputPower() {
			if (temp < getMinPowerInternal())
				return 0;
			if (temp < getMaxPowerInternal())
				return temp;
			return getMaxPowerInternal();
		}

		@Override
		public final int getMinOutputPower() {
			if (temp < getMinPowerInternal())
				return 0;
			return getMinPowerInternal();
		}

		@Override
		public final int getStorage() {
			return temp;
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			temp = tag.getInt("power.temp");
		}

		@Override
		public final void setPowerDrawn(int pow) {
			temp -= pow;
			powOut = pow;
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			temp = (int) (temp * (1 - getDecayRate()));
			if (temp < 0)
				temp = 0;
			if (temp > getMaxStorage())
				temp = getMaxStorage();
			powSup = 0;
			int sp = getSpace();
			if (sp == 0 || sp < getLowPowerInternal())
				doNoPower();
			else if (sp < getMaxStorage() * FAC) {
				temp += powSup = getLowPowerInternal();
				doLowPower();
			} else {
				temp += powSup = getHighPowerInternal();
				doHighPower();
			}
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("power.temp", temp);
			return tag;
		}

		protected abstract void doHighPower();

		protected abstract void doLowPower();

		protected abstract void doNoPower();

		protected abstract int getHighPowerInternal();

		protected abstract int getLowPowerInternal();

	}

	public static interface ICapacitor {

		public static class NullCapacitor implements ICapacitor {

			private NullCapacitor() {
			}

			@Override
			public double getDecayRate() {
				return 0;
			}

			@Override
			public int getMaxPower() {
				return 0;
			}

			@Override
			public int getMaxStorage() {
				return 0;
			}

			@Override
			public int getMinPower() {
				return 0;
			}

		}

		public static ICapacitor NULL = new NullCapacitor();

		public double getDecayRate();

		public int getMaxPower();

		public int getMaxStorage();

		public int getMinPower();

	}

	public static interface ICapMachine extends TempStorage {

		public ICapacitor getCapacitor();

		@Override
		public default double getDecayRate() {
			ICapacitor cap = getCapacitor();
			return cap == null ? 0 : cap.getDecayRate();
		}

		@Override
		public default int getMaxPowerInternal() {
			ICapacitor cap = getCapacitor();
			return cap == null ? 0 : cap.getMaxPower();
		}

		@Override
		public default int getMaxStorage() {
			ICapacitor cap = getCapacitor();
			return cap == null ? 0 : cap.getMaxStorage();
		}

		@Override
		public default int getMinPowerInternal() {
			ICapacitor cap = getCapacitor();
			return cap == null ? 0 : cap.getMinPower();
		}

	}

	public static interface IConsumer extends IPower {

		public int getMaxInputPower();

		public int getMinInputPower();

		@Override
		public default Type getPowerType() {
			return Type.CONSUMER;
		}

		public void setPowerIn(int powerIn);

	}

	public static interface IGenerator extends IPower {

		public int getMaxOutputPower();

		public int getMinOutputPower();

		@Override
		public default Type getPowerType() {
			return Type.GENERATOR;
		}

		public void setPowerDrawn(int powerOut);

	}

	public static interface IPowerBlock extends WireConnect {

	}

	public static interface IStorage extends IPower {

		@Override
		public default Type getPowerType() {
			return Type.STORAGE;
		}

		public int maxInputPower();

		public int maxOutputPower();

		public void setNetPower(int powerIn);

	}

	public static interface ItemCapacitor {

		public static class ICap implements ICapacitor {

			private final ItemCapacitor ic;
			private final ItemStack is;

			private ICap(ItemCapacitor icIn, ItemStack isIn) {
				ic = icIn;
				is = isIn;
			}

			@Override
			public double getDecayRate() {
				return ic.getDecayRate(is);
			}

			@Override
			public int getMaxPower() {
				return ic.getMaxPower(is);
			}

			@Override
			public int getMaxStorage() {
				return ic.getMaxStorage(is);
			}

			@Override
			public int getMinPower() {
				return ic.getMinPower(is);
			}

		}

		public double getDecayRate(ItemStack is);

		public default ICapacitor getICap(ItemStack is) {
			return new ICap(this, is);
		}

		public int getMaxPower(ItemStack is);

		public int getMaxStorage(ItemStack is);

		public int getMinPower(ItemStack is);

	}

	public static abstract class PowerCont<T extends PowerTE<T, C>, C extends PowerCont<T, C>>
			extends CTEBlock.CommCont {

		protected T te;

		@SuppressWarnings("unchecked")
		protected PowerCont(ContainerType<C> type, int id, PlayerInventory inv, IInventory ent, int h, IIntArray arr) {
			super(type, id, inv, ent, h, arr);
			if (ent instanceof PowerTE)
				te = (T) ent;
		}

	}

	public static abstract class PowerTE<T extends PowerTE<T, C>, C extends PowerCont<T, C>> extends CTEBlock.CTETE<T>
			implements ITickableTileEntity, IIntArray, IPower {

		public interface IPowerContFactory<T extends PowerTE<T, C>, C extends PowerCont<T, C>> {

			public C get(int wid, PlayerInventory inv, IInventory te, IIntArray ia);

		}

		private final IPowerContFactory<T, C> genCont;

		public PowerTE(TileEntityType<T> type, IPowerContFactory<T, C> fac, int size) {
			super(type, size);
			genCont = fac;
		}

		@Override
		public Container createMenu(int wid, PlayerInventory inv, PlayerEntity pl) {
			return genCont.get(wid, inv, this, this);
		}

	}

	public static class PowerUpdator {

		private final List<IGenerator> genList = new ArrayList<>();
		private final List<IStorage> stoList = new ArrayList<>();
		private final List<IConsumer> conList = new ArrayList<>();

		public void add(IPower p) {
			if (p.getPowerType() == Type.GENERATOR)
				genList.add((IGenerator) p);
			if (p.getPowerType() == Type.STORAGE)
				stoList.add((IStorage) p);
			if (p.getPowerType() == Type.CONSUMER)
				conList.add((IConsumer) p);
		}

		public void update() {
			int maxSup = 0, minSup = 0;
			for (IGenerator gen : genList) {
				maxSup += gen.getMaxOutputPower();
				minSup += gen.getMinOutputPower();
			}
			int maxCon = 0, minCon = 0;
			for (IConsumer con : conList) {
				maxCon += con.getMaxInputPower();
				minCon += con.getMinInputPower();
			}
			int addSup = 0, addCon = 0;
			for (IStorage sto : stoList) {
				addSup += sto.maxOutputPower();
				addCon += sto.maxInputPower();
			}
			if (minCon > maxSup) {
				if (minCon > maxSup + addSup) {
					double fac = 1.0 * (maxSup + addSup) / minCon;
					stoList.forEach(e -> e.setNetPower(-e.maxOutputPower()));
					for (IConsumer con : conList)
						con.setPowerIn((int) Math.floor(fac * con.getMinInputPower()));
				} else {
					double fac = 1.0 * (minCon - maxSup) / addSup;
					for (IStorage sto : stoList)
						sto.setNetPower(-(int) Math.ceil(fac * sto.maxOutputPower()));
					conList.forEach(e -> e.setPowerIn(e.getMinInputPower()));
				}
				genList.forEach(e -> e.setPowerDrawn(e.getMaxOutputPower()));
			} else if (maxCon < minSup) {
				if (maxCon + addCon < minSup) {
					stoList.forEach(e -> e.setNetPower(e.maxInputPower()));
				} else {
					double fac = 1.0 * (minSup - maxCon) / addCon;
					for (IStorage sto : stoList)
						sto.setNetPower((int) Math.floor(fac * sto.maxInputPower()));
				}
				genList.forEach(e -> e.setPowerDrawn(e.getMinOutputPower()));
				conList.forEach(e -> e.setPowerIn(e.getMaxInputPower()));
			} else {
				if (maxCon > maxSup) {
					int pow = maxSup - minCon;
					int tak = maxCon - minCon;
					double confac = 1.0 * pow / tak;
					genList.forEach(e -> e.setPowerDrawn(e.getMaxOutputPower()));
					for (IConsumer con : conList) {
						int min = con.getMinInputPower();
						int max = con.getMaxInputPower();
						con.setPowerIn((int) Math.floor(min + confac * (max - min)));
					}
				} else {
					int pow = maxSup - minSup;
					int tak = maxCon - minSup;
					double supfac = 1.0 * tak / pow;
					for (IGenerator gen : genList) {
						int min = gen.getMinOutputPower();
						int max = gen.getMaxOutputPower();
						gen.setPowerDrawn((int) Math.ceil(min + supfac * (max - min)));
					}
					conList.forEach(e -> e.setPowerIn(e.getMaxInputPower()));
				}
				stoList.forEach(e -> e.setNetPower(0));
			}
		}

	}

	public static abstract class StoTE<T extends StoTE<T, C>, C extends PowerCont<T, C>> extends PowerTE<T, C>
			implements IStorage {

		private int energy;
		private int temp;

		public StoTE(TileEntityType<T> type, IPowerContFactory<T, C> fac, int size) {
			super(type, fac, size);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			energy = tag.getInt("power.energy");
			temp = tag.getInt("power.temp");
		}

		@Override
		public final void setNetPower(int pow) {
			if (pow > 0)
				temp += input(pow);
			if (pow < 0)
				temp -= output(-pow);
		}

		@Override
		public void tick() {
			if (world.isRemote)
				return;
			energy += temp;
			temp = 0;
		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putInt("power.energy", energy);
			tag.putInt("power.temp", temp);
			return tag;
		}

		protected final int getEnergy() {
			return energy;
		}

		/** parse the input into the stored energy, subtracting loss */
		protected abstract int input(int inp);

		/** parse the output into the drawn energy, adding loss */
		protected abstract int output(int out);

	}

	public static interface TempStorage {

		public double getDecayRate();

		public int getMaxPowerInternal();

		public int getMaxStorage();

		public int getMinPowerInternal();

		public default int getSpace() {
			return getMaxStorage() - getStorage();
		}

		public int getStorage();

	}

	public static enum Type {
		GENERATOR, STORAGE, CONSUMER
	}

	public static final double FAC = 0.75;

	public Type getPowerType();

}
