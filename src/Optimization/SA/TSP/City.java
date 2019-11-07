package Optimization.SA.TSP;

import java.util.ArrayList;

public class City {
	private int id;
	private double x;
	private double y;
	public int getId() {
		return id;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public City(int id,double x,double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	public static void main(String[] args) {
		ArrayList<Integer> tArrayList = new ArrayList<>();
		tArrayList.add(1);
		tArrayList.add(2);
		tArrayList.remove(new Integer(0));
//		System.out.println(tArrayList.contains(0));
//		System.out.println(1.0/185676);
		System.out.println(1+"\t"+"h");
		System.out.println(10+"\t"+"h");
	}
}
