package com.klayman.invest.stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

	public static void main(String[] args) {

		// String date = "2015-04-22";
		// System.out.println(date.substring(0, 7));

//		int[] arg = { 5, 3, 6, 8, 2, 1, 9, 4, 7 };
//		sort(arg, 0, arg.length - 1);
//		System.out.println(Arrays.toString(arg));
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss SSS");
		System.out.println(f.format(Calendar.getInstance().getTime()));
	}

	class order {
		private double discount;
	}
	
	class orderr extends order {
		public orderr() {
			super.discount = 1d;
		}
	}
	
	public static void sort(int[] arg, int low, int high) {
		int[] arr = arg;
		int l = low;
		int h = high;
		int base = arg[low];
		while (l < h) {
			while (l < h && arg[h] >= base)
				h--;
			arg[l] = arg[h];
			arg[h] = base;
			while (l < h && arg[l] <= base)
				l++;
			arg[h] = arg[l];
			arg[l] = base;
		}
		if (h-1 > low)
			sort(arg, low, h - 1);
		if (high > h + 1)
			sort(arg, h + 1, high);
	}
}

class parent {
	static int a;
	static {
		a = 100;
	}
	int b;
	{
		b = 200;
	}
	public void print() {
		System.out.println("parent a:" + a + ", b:" + b);
	}
	parent(int v) {
		print();
		a = v;
	}
}

class child extends parent {
	static int a;
	static {
		a = 300;
	}
	int b;
	{
		b = 400;
	}
	child(int v) {
		super(v);
		System.out.println("child a:" + a + ", b:" + b);
	}
}
