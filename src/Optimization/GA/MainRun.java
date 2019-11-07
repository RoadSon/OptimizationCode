package Optimization.GA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Optimization.SA.TSP.tspSA;

/**
 * 主函数运行类
 */

public class MainRun {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\桌面\\result.txt"));
		File file = new File("D:\\Eclipece-java\\Optimization\\src\\Optimization\\SA\\data");
		for(File f:file.listFiles()) {
			TSPData.LoadData(f.toString());
			GeneticAlgorithm GA=new GeneticAlgorithm();
			//创建初始种群
			SpeciesPopulation speciesPopulation = new SpeciesPopulation();
			//开始遗传算法（选择算子、交叉算子、变异算子）
			SpeciesIndividual bestRate=GA.run(speciesPopulation);
			bufferedWriter.write(f.getName().substring(0,f.getName().indexOf("."))+","+(int)bestRate.getDistance()+"\n");
		}
		bufferedWriter.close();
	}
}
