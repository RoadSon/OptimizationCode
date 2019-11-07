package Optimization.SA.TSP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TspBenchMarkTest {
	public static void main(String[] args) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\桌面\\result.txt"));
		File file = new File("D:\\Eclipece-java\\Optimization\\src\\Optimization\\SA\\data");
		for(File f:file.listFiles()) {
			tspSA tspsa = new tspSA(50000.0,0.00001,0.98,5000);
			tspsa.readBenchmarkData(f.toString());
			tspsa.sa();
			bufferedWriter.write(f.getName().substring(0,f.getName().indexOf("."))+","+(int)tspsa.getMinDistance()+"\n");
//			System.out.print("最优路径长度：");
//			System.out.println((int)tspsa.getMinDistance());
//			System.out.print("最优路径：");
//			for(City city:tspsa.getBestCitySequence()) {
//				System.out.print(city.getId()+"->");
//			}
//			System.out.print(tspsa.getBestCitySequence().get(0).getId());
		}
		bufferedWriter.close();
	}
}
