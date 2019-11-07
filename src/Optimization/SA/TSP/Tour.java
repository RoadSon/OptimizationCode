package Optimization.SA.TSP;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {
	private ArrayList<City> allCity;
	public Tour() {
		allCity = new ArrayList<City>();
	}
	public void addCity(City city) {
		allCity.add(city);
	}
	public ArrayList<City> getAllCity() {
		return allCity;
	}
	public double getDistance() {
		double distance = 0.0;
		int j = 0;
		for(int i=0;i<allCity.size();i++) {
			j = i+1;
			if(j == allCity.size()) {
				distance = distance + Math.sqrt((allCity.get(0).getX()-allCity.get(i).getX())*(allCity.get(0).getX()-allCity.get(i).getX())+(allCity.get(0).getY()-allCity.get(i).getY())*(allCity.get(0).getY()-allCity.get(i).getY()));
			}
			else {
				distance = distance + Math.sqrt((allCity.get(i).getX()-allCity.get(j).getX())*(allCity.get(i).getX()-allCity.get(j).getX())+(allCity.get(i).getY()-allCity.get(j).getY())*(allCity.get(i).getY()-allCity.get(j).getY()));
			}
		}
		return distance;
	}
	public void change(int a,int b) {
		Collections.swap(allCity, a, b);
	}
	public void init() {
		Collections.shuffle(allCity);
	}
}
