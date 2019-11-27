package Optimization.GA.TSP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import Optimization.Util.Util;

/**
 * 主函数运行类
 */

public class gaTspBenchMarkTest {
	private static Map<String, Double[]> cityDis = new TreeMap<String, Double[]>();
	public static void main(String[] args) throws IOException {
		File file = new File("src\\Optimization\\TSPData");
		int maxRunTime = 5;
		for(File f:file.listFiles()) {
			int runTime = 0;
			Double[] bestDis = new Double[maxRunTime];
			while(runTime < maxRunTime) {
				TSPData.LoadData(f.toString());
				GeneticAlgorithm GA=new GeneticAlgorithm();
				SpeciesPopulation speciesPopulation = new SpeciesPopulation();
				SpeciesIndividual bestRate=GA.run(speciesPopulation);
				bestDis[runTime] = bestRate.getDistance();
				runTime ++;
			}
			String fileName = f.getName().substring(0,f.getName().indexOf(".")).toUpperCase();
			cityDis.put(fileName, bestDis.clone());
		}
		Util.writeTSPResult(cityDis, "src\\Optimization\\gaResult.csv");
	}
}
