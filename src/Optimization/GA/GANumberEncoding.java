package Optimization.GA;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Optimization.Util.Util;
import Optimization.cec13.testfunc;

public class GANumberEncoding {
	public int dimension = 30;  //x的维度
	public double max_x = 100;
	public double min_x = -100;
	int functionNum;  //cec13的函数编号
	//函数编号-1对应的最优值数组
	int[] functionIndex = {-1400,-1300,-1200,-1100,-1000,-900,-800,-700,-600,-500,-400,-300,-200,-100,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400};
	private testfunc tf = new testfunc();  //cec2103测试函数
	private int popNum = 30;	//种群数量
	public double[] fitness=new double[popNum];  //适应值，即对应的函数值
	private double[][] popGenes = new double[popNum][dimension];	//种群的所有个体的基因
	private double bestfitness = Double.MAX_VALUE;  //函数最优解
	private double[] bestGene = new double[dimension]; //最佳基因
	private double mutateRate = 0.4; //变异率
	private double crossoverRate = 0.7; //交叉率
	public GANumberEncoding(int functionNum) {
		this.functionNum = functionNum;
		initPop();
	}
	/**
	 * 初始化种群
	 */
	public void initPop() {
		for(int i=0;i<popNum;i++) {
			for(int j=0;j<dimension;j++) {
				popGenes[i][j] = Math.random() * (max_x-min_x) + min_x;
			}
		}
	}
	/**
	 * 计算个体的适应度
	 * @throws Exception
	 */
	public void calculateFitness() throws Exception {
		for(int i=0;i<popNum;i++) {
			double[] f = new double[1];
			tf.test_func(popGenes[i],f,dimension,1,functionNum);
			fitness[i] = f[0] - functionIndex[functionNum-1];
			if(bestfitness > fitness[i]) {
				bestfitness = fitness[i];
				bestGene = popGenes[i].clone();
			}
		}
		//System.out.println(Arrays.toString(fitness));
		//System.out.println(bestfitness);
	}
	
	/* 选择算子  */
	
	/**
	 * (1)轮盘赌选择
	 */
	public void rouletteSelect() {
		double sumFit = 0;
		for(int i=0;i<popNum;i++) {
			sumFit += fitness[i];
		}
		for(int i=0;i<popNum;i++) {
			double roulettePro = Math.random();
			double fitPro = 0;
			for(int p=0;p<popNum;p++) {
				fitPro += fitness[p]/sumFit;
				if(fitPro > roulettePro) {
					popGenes[i] = popGenes[p].clone();
					break;
				}
			}
		}
	}
	/**
	 * (2)锦标赛选择
	 */
	public void raceSelect() {
		for(int i=0;i<popNum;i++) {
			Set<Integer> competePop = new HashSet<>();
			while(true) {
				competePop.add((int)(Math.random()*popNum));
				if(competePop.size() == 4)
					break;
			}
			double bestFit = Double.MAX_VALUE;
			for(Integer set:competePop) {
				if(bestFit > fitness[set]) {
					bestFit = fitness[set];
					popGenes[i] = popGenes[set];
				}
			}
		}
	}
	/**
	 * (3)排序选择
	 */
	public void rankSelect() {
		double pr = 0.3; //淘汰率
		int dieOut = (int) (pr*popNum);
		int[] descendSortIndex = Util.Arraysort(fitness, true);
		int[] ascendSortIndex = Util.Arraysort(fitness, false);
		for(int i=0;i<dieOut;i++) {
			int dieOutIndex = descendSortIndex[i];
			int goodPopIndex = ascendSortIndex[i];
			popGenes[dieOutIndex] = popGenes[goodPopIndex].clone();
		}
	}
	
	/* 交叉算子 */
	
	/**
	 * (1)算术交叉
	 */
	public void arithmeticalCrossover() {
		//double alpha = 0.5;
		for(int i=0;i<popNum;i++) {
			if(Math.random() < crossoverRate) {
				double alpha = Math.random();
				double[] c1 = new double[dimension];
				double[] c2 = new double[dimension];
				for(int j=0;j<dimension;j++) {
					c1[j] = alpha * popGenes[i][j] + (1-alpha)*popGenes[(i+1)%popNum][j];
					c2[j] = alpha * popGenes[(i+1)%popNum][j] + (1-alpha) * popGenes[i][j];
			}
			popGenes[i] = c1;
			popGenes[(i+1)%popNum] = c2;
			}
		}
	}
	/**
	 * (2)扩展线性交叉
	 */
	public void extendLineCrossover() {
		for(int i=0;i<popNum;i++) {
			if(Math.random() < crossoverRate) {
				double[] c1 = new double[dimension];
				//double[] c2 = new double[dimension];
				for(int j=0;j<dimension;j++) {
					double alpha = Math.random()*1.5 - 0.25;
					c1[j] = popGenes[i][j] + alpha*(popGenes[(i+1)%popNum][j] - popGenes[i][j]);
					//c2[j] = popGenes[(i+1)%popNum][j] + alpha*(popGenes[(i+1)%popNum][j] - popGenes[i][j]);
				}
				popGenes[i] = c1;
				//popGenes[(i+1)%popNum] = c2;
			}
		}
	}
	/**
	 * (3)BLX交叉
	 */
	public void BLXCrossover() {
		double alpha = 0.5;
		for(int i=0;i<popNum;i++) {
			if(Math.random() < crossoverRate) {
				double[] c = new double[dimension];
				for(int j=0;j<dimension;j++) {
					double pmax = 0;
					double pmin = 0;
					double I = 0;
					if(popGenes[i][j] > popGenes[(i+1)%popNum][j]) {
						pmax = popGenes[i][j];
						pmin = popGenes[(i+1)%popNum][j];
						I = pmax - pmin;
					}else {
						pmax = popGenes[(i+1)%popNum][j];
						pmin = popGenes[i][j];
						I = pmax - pmin;
					}
					c[j] = (pmin - I*alpha) + alpha*Math.abs((pmax + I*alpha) - (pmin - I*alpha));
				}
				popGenes[i] = c;
			}
		}
	}
	/**
	 * (4)离散交叉
	 */
	public void discreteCrossover() {
		for(int i=0;i<popNum;i++) {
			if(Math.random() < crossoverRate) {
				double[] c = new double[dimension];
				for(int j=0;j<dimension;j++) {
					if(Math.random() > 0.5) {
						c[j] = popGenes[i][j];
					}
					else {
						c[j] = popGenes[(i+1)%popNum][j];
					}
				}
				popGenes[i] = c;
			}
		}
	}
	
	/* 变异算子  */
	
	/**
	 * (1)均匀变异
	 */
	public void uniformMutate() {
//		for(int i=0;i<popNum;i++) {
//			for(int j=0;j<dimension;j++) {
//				if(Math.random() < mutateRate) {
//					popGenes[i][j] = Math.random() * (max_x-min_x) + min_x;
//				}
//			}
//		}
		int mutateNum = (int) (popNum*mutateRate);
		int[] sortIndex = Util.Arraysort(fitness, true);
		for(int i=0;i<mutateNum;i++) {
			int index = sortIndex[i];
			for(int j=0;j<dimension;j++) {
				popGenes[index][j] = Math.random() * (max_x-min_x) + min_x;
			}
		}
	}
	/**
	 * (2)边界变异
	 */
	public void boundaryMutate() {
//		for(int i=0;i<popNum;i++) {
//			for(int j=0;j<dimension;j++) {
//				if(Math.random() < mutateRate) {
//					if(Math.random() > 0.5) {
//						popGenes[i][j] = max_x;
//					}else {
//						popGenes[i][j] = min_x;
//					}
//				}
//			}
//		}
		int mutateNum = (int) (popNum*mutateRate);
		int[] sortIndex = Util.Arraysort(fitness, true);
		for(int i=0;i<mutateNum;i++) {
			int index = sortIndex[i];
			for(int j=0;j<dimension;j++) {
				if(Math.random() > 0.5) {
				popGenes[index][j] = max_x;
			}else {
				popGenes[index][j] = min_x;
			}
			}
		}
	}
	/**
	 * (3)高斯变异
	 */
	public void gaussMutate() {
//		for(int i=0;i<popNum;i++) {
//			for(int j=0;j<dimension;j++) {
//				if(Math.random() < mutateRate) {
//					popGenes[i][j] = popGenes[i][j] * (1+Math.random()*new Random().nextGaussian());
//				}
//			}
//		}
		int mutateNum = (int) (popNum*mutateRate);
		int[] sortIndex = Util.Arraysort(fitness, true);
		for(int i=0;i<mutateNum;i++) {
			int index = sortIndex[i];
			for(int j=0;j<dimension;j++) {
				popGenes[index][j] = popGenes[i][j] * (1 + Math.random()*new Random().nextGaussian());
			}
		}
	}
	/**
	 * (4)PSO变异
	 */
	public void PSOMutate() {
		int mutateNum = (int) (popNum*mutateRate);
		int c1 = 2;
		int c2 = 2;
		int[] sortIndex = Util.Arraysort(fitness, true);
		int bestFitIndex = sortIndex[sortIndex.length-1];
		for(int i=0;i<mutateNum;i++) {
			int index = sortIndex[i];
			for(int j=0;j<dimension;j++) {
				popGenes[index][j] = popGenes[index][j] + c1*Math.random()*(popGenes[bestFitIndex][j]-popGenes[index][j]) + c2*Math.random()*(bestGene[j]-popGenes[index][j] );
			}
		}
	}
	public static void main(String[] args) throws Exception {
		ArrayList<Double> error = new ArrayList<>();
		ArrayList<Double> sd = new ArrayList<>();
		ArrayList<Double> mean = new ArrayList<>();
		int functionNum = 1;	//函数编号
		int maxIterNum = 10000;  //迭代次数
		int maxRunTime = 5;  //运行次数
		long startTime = System.currentTimeMillis();
		while(functionNum <= 28) {
			int runtime = 1;
			while(runtime <= maxRunTime) {
				int iter = 1;
				GANumberEncoding gaNumberEncoding = new GANumberEncoding(functionNum);
				while(iter <= maxIterNum) {
					gaNumberEncoding.calculateFitness();
					gaNumberEncoding.rankSelect();
					gaNumberEncoding.extendLineCrossover();
					gaNumberEncoding.uniformMutate();
					iter ++ ;
				}
				System.out.println(functionNum+"\t"+gaNumberEncoding.bestfitness);
				error.add(gaNumberEncoding.bestfitness);
				runtime ++;
			}
			double tempMean = Util.getMean(error);
			double tempSD = Util.getSD(error);
			mean.add(tempMean);
			sd.add(tempSD);
			error.clear();
			functionNum ++;
		}
		long endTime = System.currentTimeMillis();
		Util.writeData(mean, sd,"src/Optimization/GA/BenchMarkResult.csv");
		System.out.println("runtime:"+(endTime - startTime)+"ms");
	}
}
