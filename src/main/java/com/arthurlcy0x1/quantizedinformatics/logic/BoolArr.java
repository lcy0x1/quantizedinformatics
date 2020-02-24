package com.arthurlcy0x1.quantizedinformatics.logic;

import java.util.Arrays;

public interface BoolArr {

	public static class LongABA implements BoolArr {

		private final int size, length;
		private final long[][] arr;

		private LongABA(int siz, int len) {
			length = 1 << len;
			arr = new long[siz][(length - 1 >> 6) + 1];
			size = siz;
		}

		@Override
		public void copyFrom(BoolArr ba, int dst, int src) {
			if (ba.len() == length)
				arr[dst] = ba.getLongArr(src).clone();
			else
				for (byte i = 0; i < length; i++)
					if (ba.get(src, i))
						set(dst, i, true);
		}

		@Override
		public boolean get(int i, byte j) {
			j &= length - 1;
			return (arr[i][j >> 6] & 1l << (j & 63)) != 0;
		}

		@Override
		public long getLong(int i) {
			throw new LogicRE("too long to fit in long: " + length);
		}

		@Override
		public long[] getLongArr(int i) {
			return arr[i];
		}

		@Override
		public int len() {
			return length;
		}

		@Override
		public void set(int i, byte j, boolean val) {
			arr[i][j >> 6] |= 1l << (j & 63);
			if (!val)
				arr[i][j >> 6] -= 1l << (j & 63);
		}

		@Override
		public void setAll(int i, boolean val) {
			Arrays.fill(arr[i], val ? -1l : 0l);
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
				for (int j = length - 1; j >= 0; j--)
					ans.append(get(i, (byte) j) ? '1' : '0');
				if (i < size - 1)
					ans.append(",");
			}
			ans.append("]");
			return ans.toString();
		}

	}

	public static class LongBA implements BoolArr {

		private final int size, length;
		private final long[] arr;

		private LongBA(int siz, int len) {
			arr = new long[siz];
			size = siz;
			length = 1 << len;
		}

		@Override
		public void copyFrom(BoolArr ba, int dst, int src) {
			if (ba.len() == length)
				arr[dst] = ba.getLong(src);
			else
				for (byte i = 0; i < length; i++)
					if (ba.get(src, i))
						arr[dst] |= 1l << i;
		}

		@Override
		public boolean get(int i, byte j) {
			j &= length - 1;
			return (arr[i] & 1l << j) != 0;
		}

		@Override
		public long getLong(int i) {
			return arr[i];
		}

		@Override
		public long[] getLongArr(int i) {
			return new long[] { arr[i] };
		}

		@Override
		public int len() {
			return length;
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
				for (int j = length - 1; j >= 0; j--)
					ans.append(get(i, (byte) j) ? '1' : '0');
				if (i < size - 1)
					ans.append(",");
			}
			ans.append("]");
			return ans.toString();
		}

	}

	public static BoolArr getNew(int siz, int len) {
		if (siz <= 0 || len <= 0)
			throw new LogicRE("siz and len should be positive: siz=" + siz + ", len=" + len);
		if (siz > 8 || len > 8)
			throw new LogicRE("length too large: it should be within 8, but it is " + len);
		if (len <= 6)
			return new LongBA(siz, len);
		return new LongABA(siz, len);
	}

	public static BoolArr wrap(int len, long sig) {
		if (len > 6)
			throw new LogicRE("length too large: it should be within 6, but it is " + len);
		LongBA ans = new LongBA(1, len);
		ans.arr[0] = sig;
		return ans;
	}

	public void copyFrom(BoolArr ba, int dst, int src);

	public boolean get(int i, byte j);

	public long getLong(int i);

	public long[] getLongArr(int i);

	/** length of data */
	public int len();

	public void set(int i, byte j, boolean val);

	public void setAll(int i, boolean val);

	/** size of data array */
	public int size();

}
