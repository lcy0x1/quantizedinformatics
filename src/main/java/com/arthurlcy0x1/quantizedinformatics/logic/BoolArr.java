package com.arthurlcy0x1.quantizedinformatics.logic;

public interface BoolArr {

	public static class LongBA implements BoolArr {

		private final int size, length;
		private final long[] arr;

		private LongBA(int siz, int len) {
			arr = new long[siz];
			size = siz;
			length = len;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public int len() {
			return length;
		}

		@Override
		public boolean get(int i, int j) {
			return (arr[i] & 1l << j) != 0;
		}

		@Override
		public void set(int i, int j, boolean val) {
			arr[i] |= 1l<< j;
			if (!val)
				arr[i] -= 1l << j;
		}

	}

	/** size of data array */
	public int size();

	/** length of data */
	public int len();

	public boolean get(int i, int j);

	public void set(int i, int j, boolean val);

}
