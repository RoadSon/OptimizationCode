package Optimization.SA.TSP;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class tspSA {
	private Tour tour;
	private double minDistance;
	private ArrayList<City> bestCitySequence;
	private double startT;
	private double minT;
	private double rate;
	private int iterNum;
	public tspSA(double startT,double minT,double rate,int iterNum) {
		tour = new Tour();
		minDistance = Double.MAX_VALUE;
		bestCitySequence = new ArrayList<City>();
		this.iterNum = iterNum;
		this.minT = minT;
		this.rate = rate;
		this.startT =startT;
	}
	public void readBenchmarkData(String path) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String line = null;
		ArrayList<String> allCity = new ArrayList<>();
		int count =0;
		while((line = br.readLine())!=null){
			count += 1;
			String[] splitLine = line.split(" ");
			for(String iterLine:splitLine) {
				if(!iterLine.equals("")) {
					allCity.add(iterLine);
				}
			}
		}
		for(int i=0;i<allCity.size();i+=3) {
			City city = new City(Integer.parseInt(allCity.get(i)),Double.parseDouble(allCity.get(i+1)),Double.parseDouble(allCity.get(i+2)));
			tour.addCity(city);
		}
	}
	public void printCity() {
		for(City city:tour.getAllCity()) {
			System.out.println(city.getId());
		}
	}
	public void sa() {
		int iter = 0;
		double newDistance;
		tour.init();
		minDistance = tour.getDistance();
		bestCitySequence = tour.getAllCity();
		while(startT > minT) {
			while(iter++ < iterNum) {
				int i = (int)(Math.random()*tour.getAllCity().size());
				int j = (int)(Math.random()*tour.getAllCity().size());
				tour.change(i, j);
				newDistance = tour.getDistance();
				if(newDistance- minDistance < 0) {
					minDistance = newDistance;
					bestCitySequence = tour.getAllCity();
				}
				else {
					if(Math.exp(((minDistance-newDistance)/startT))>Math.random()) {
						minDistance = newDistance;
						bestCitySequence = tour.getAllCity();
					}
					else {
						tour.change(i, j);
					}
				}
//				System.out.println("第"+iter+"次迭代："+"  距离："+minDistance);
			}
			startT = startT * rate;
			iter = 0;
		}
	}
	public double getMinDistance() {
		return minDistance;
	}
	public ArrayList<City> getBestCitySequence() {
		return bestCitySequence;
	}
}
