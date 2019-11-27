package Optimization.ACA.TSP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class acaTspBenchMarkTest {
	private static Map<String, Double[]> cityDis = new TreeMap<String, Double[]>();
	private static Map<String, String> cityOpt = new HashMap<>();
	public static void main(String[] args) throws IOException{
		getCityOpt();
		File file = new File("src\\Optimization\\TSPData");
		int maxRunTime = 5;
		for(File f:file.listFiles()) {
			int runTime = 0;
			Double[] bestDis = new Double[maxRunTime];
			while(runTime < maxRunTime) {
				ACA tspACA = new ACA();
				tspACA.readTSP(f.toString());
				tspACA.aca();
				bestDis[runTime] = tspACA.bestPathDis; 
				runTime ++;
			}
			String fileName = f.getName().substring(0,f.getName().indexOf(".")).toUpperCase();
			cityDis.put(fileName, bestDis.clone());
		}
//		cityDis.forEach((k,v)->{
//			System.out.println(k+"\t"+Arrays.toString(v));
//		});
		writeResult(cityDis,"src\\Optimization\\acaResult.csv");
	}
	public static void getCityOpt() throws IOException {
		BufferedReader bReader = new BufferedReader(new FileReader("src\\Optimization\\ACA\\TSP\\opt.txt"));
		String line = null;
		while((line=bReader.readLine())!=null) {
			String[] lines = line.split(":");
			cityOpt.put(lines[0].trim().toUpperCase(), lines[1].trim());
		}
	}
	public static void writeResult(Map<String, Double[]> cityDis,String path) throws IOException {
		double[] bestDis = new double[cityDis.size()];
		double[] meanDis = new double[cityDis.size()];
		int count = 0;
		for(Double[] dis:cityDis.values()) {
			double sumDis = 0;
			double minDis = Double.MAX_VALUE;
			int runTime = dis.length;
			for(Double d:dis) {
				if(d<minDis) {
					minDis = d;
				}
				sumDis += d;
			}
			bestDis[count] = minDis;
			meanDis[count] = sumDis/runTime;
			count ++;
		}
		count = 0;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
		bufferedWriter.write("Problem,Opt,Best Result,Average Result"+"\r\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for(String city:cityDis.keySet()) {
			bufferedWriter.write(city+","+cityOpt.get(city)+","+df.format(bestDis[count])+","+df.format(meanDis[count])+"\r\n");
			count ++;
		}
		bufferedWriter.close();
	}
}
