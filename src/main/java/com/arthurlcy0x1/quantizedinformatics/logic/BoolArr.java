package com.arthurlcy0x1.quantizedinformatics.logic;

import java.util.Arrays;

import net.minecraft.nbt.CompoundNBT;

public interface BoolArr {

	public static class LongBA implements BoolArr {

		private static LongBA decode(CompoundNBT tag) {
			int ver = tag.getByte("version");
			int siz = tag.getByte("size");
			int len = tag.getByte("len");
			long[] arr = tag.getLongArray("data");
			if (ver != 1 || siz == 0 || len == 0 || arr == null || arr.length != len)
				throw new LogicRE("invalid data");
			return new LongBA(siz, len, arr);
		}

		private final int size, len;

		private final long[] arr;

		private LongBA(int siz, int l) {
			arr = new long[siz];
			size = siz;
			len = l;
		}

		private LongBA(int s, int l, long[] ar) {
			size = s;
			len = l;
			arr = ar;
		}

		@Override
		public void copyFrom(BoolArr ba, int dst, int src) {
			if (ba.len() == 1 << len)
				arr[dst] = ba.getLong(src);
			else
				for (byte i = 0; i < 1 << len; i++)
					if (ba.get(src, i))
						arr[dst] |= 1l << i;
		}

		@Override
		public boolean equals(Object ba) {
			if (ba instanceof LongBA) {
				LongBA lba = (LongBA) ba;
				return size == lba.size && len == lba.len && Arrays.equals(arr, lba.arr);
			}
			return false;
		}

		@Override
		public boolean get(int i, byte j) {
			j &= (1 << len) - 1;
			return (arr[i] & 1l << j) != 0;
		}

		@Override
		public long getLong(int i) {
			return arr[i];
		}

		@Override
		public int len() {
			return 1 << len;
		}

		@Override
		public void set(int i, byte j, boolean val) {
			arr[i] |= 1l << j;
			if (!val)
				arr[i] -= 1l << j;
		}

		@Override
		public void setAll(int i, boolean val) {
			arr[i] = val ? -1l : 0l;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public String toString() {
			StringBuilder ans = new StringBuilder();
			ans.append("[");
			for (int i = 0; i < size; i++) {
				for (int j = (1 << len) - 1; j >= 0; j--)
					ans.append(get(i, (byte) j) ? '1' : '0');
				if (i < size - 1)
					ans.append(",");
			}
			ans.append("]");
			return ans.toString();
		}

		@Override
		public CompoundNBT toTag() {
			if (size == 0 || len == 0 || size > 127 || len > 127)
				throw new LogicRE("array not in range");
			CompoundNBT ans = new CompoundNBT();
			ans.putByte("version", (byte) 1);
			ans.putByte("size", (byte) size);
			ans.putByte("len", (byte) len);
			ans.putLongArray("data", arr);
			return ans;
		}

	}

	public static BoolArr decode(CompoundNBT tag) {
		byte ver = tag.getByte("version");
		if (ver != 1)
			throw new LogicRE("cannot identify version");
		byte type = tag.getByte("type");
		if (type != 1)
			throw new LogicRE("cannot identify type");
		return LongBA.decode(tag.getCompound("data"));
	}

	public static BoolArr getNew(int siz, int len) {
		if (siz <= 0 || len <= 0)
			throw new LogicRE("siz and len should be positive: siz=" + siz + ", len=" + len);
		return new LongBA(siz, len);
	}

	public static BoolArr wrap(int len, long sig) {
		if (len > 6)
			throw new LogicRE("length too large: it should be within 6, but it is " + len);
		LongBA ans = new LongBA(1, len);
		ans.arr[0] = sig;
		return ans;
	}

	public void copyFrom(BoolArr ba, int dst, int src);

	@Override
	public boolean equals(Object ba);

	public boolean get(int i, byte j);

	public long getLong(int i);

	/** length of data */
	public int len();

	public void set(int i, byte j, boolean val);

	public void setAll(int i, boolean val);

	/** size of data array */
	public int size();

	public CompoundNBT toTag();

}
