package Optimization.SA.TSP;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import Optimization.Util.Util;

public class saTspBenchMarkTest {
	private static Map<String, Double[]> cityDis = new TreeMap<String, Double[]>();
	public static void main(String[] args) throws IOException {
		File file = new File("src\\Optimization\\TSPData");
		int maxRunTime = 5;
		for(File f:file.listFiles()) {
			int runTime = 0;
			Double[] bestDis = new Double[maxRunTime];
			while(runTime < maxRunTime) {
				tspSA tspsa = new tspSA(50000.0,1e-8,0.98,2000);
				tspsa.readBenchmarkData(f.toString());
				tspsa.sa();
				bestDis[runTime] = tspsa.getMinDistance();
				runTime ++;
			}
			String fileName = f.getName().substring(0,f.getName().indexOf(".")).toUpperCase();
			cityDis.put(fileName, bestDis.clone());
		}
		Util.writeTSPResult(cityDis, "src\\Optimization\\saResult.csv");
	}
}
