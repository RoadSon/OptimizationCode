package Optimization.PSO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import Optimization.Util.Util;

import Optimization.cec13.testfunc;


/**
 * 粒子群算法
 * @author RoadSon
 * 求最小值
 */
class Particle{
	public int dimension = 20;  //x的维度
	public double[] v = new double[dimension];   //粒子速度
	public double[] x = new double[dimension];  //粒子位置
	public double fitness;  //粒子适应值，即对应的函数值
	int functionNum;  //cec13的函数编号
	//函数编号-1对应的最优值数组
	int[] functionIndex = {-1400,-1300,-1200,-1100,-1000,-900,-800,-700,-600,-500,-400,-300,-200,-100,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400};
	public double[] pbest = new double[dimension];  //粒子最优解
	private testfunc tf = new testfunc();  //cec2103测试函数
	public Particle(int functionNum) throws Exception {
		this.functionNum = functionNum;
		initx();
		initv();
		fitness = computeFitness();
	}
	/**
	 * 计算函数值
	 * @return
	 * @throws Exception
	 */
	public double computeFitness() throws Exception {
		double[] f = new double[1];
		tf.test_func(x,f,dimension,1,functionNum);
		return f[0] - functionIndex[functionNum-1];
	}
	/**
	 * 初始化x，随机设置x取-100~100之间的任意值
	 */
	public void initx() {
		for(int i=0;i<dimension;i++) {
			Random random = new Random();
			x[i] = random.nextDouble()*200 - 100;
			pbest[i] = x[i];
		}
	}
	/**
	 * 初始化v，随机设置v取-100~100之间的任意值
	 */
	public void initv() {
		for(int i=0;i<dimension;i++) {
			Random random = new Random();
			v[i] = random.nextDouble()*200 - 100;
		}
	}
}
public class psoDemo {
	private double[] gbest;  //全局最优解对应的x
	private double gbest_fitness = Double.MAX_VALUE;  //全局最优解f(x)
	private int particleNum = 20;  //粒子数目
	private double w = 0.4;  //惯性权重
	/*
	 * 速度和位置的范围
	 */
	private double max_x = 100;
	private double min_x = -100;
	private double max_v = 100;
	private double min_v = -100;
	//加速因子
	private int c1 = 2;
	private int c2 = 2;
	public List<Particle> particles = new ArrayList<Particle>();  //粒子
	/**
	 * 初始化所有粒子
	 * @param functionNum 函数编号
	 * @throws Exception
	 */
	public void initParticle(int functionNum) throws Exception {
		for(int i=0;i<particleNum;i++) {
			Particle particle = new Particle(functionNum);
			particles.add(particle);
		}
		getGbest();
	}
	/**
	 * 获得全局最优解
	 */
	public void getGbest() {
		double tempFitness = Double.MAX_VALUE;
		for(int i=0;i<particles.size();i++) {
			if(particles.get(i).fitness < tempFitness) {
				tempFitness =  particles.get(i).fitness;
				gbest = particles.get(i).pbest.clone();
			}
		}
		if(tempFitness < gbest_fitness) {
			gbest_fitness = tempFitness;
		}
	}
	/**
	 * 更新速度v
	 */
	public void updateV() {
		for(int i=0;i<particles.size();i++) {
			for(int j=0;j<particles.get(i).dimension;j++) {
				double tempV = w*particles.get(i).v[j] + c1*Math.random()*(particles.get(i).pbest[j]-particles.get(i).x[j])+c2*Math.random()*(gbest[j]-particles.get(i).x[j]);
				if(tempV<min_v) {
					particles.get(i).v[j] = min_v;
				}else if(tempV>max_v) {
					particles.get(i).v[j] = max_v;
				}else {
					particles.get(i).v[j] = tempV;
				}
			}
		}
	}
	/**
	 * 更新位置x
	 * @throws Exception
	 */
	public void updateX() throws Exception {
		for(int i=0;i<particles.size();i++) {
			for(int j=0;j<particles.get(i).dimension;j++) {
				particles.get(i).x[j] = particles.get(i).x[j] + particles.get(i).v[j];
				if(particles.get(i).x[j] > max_x) {
					particles.get(i).x[j] = max_x;
				}else if(particles.get(i).x[j] < min_x) {
					particles.get(i).x[j] = min_x;
				}
			}
			double tempFitness = particles.get(i).computeFitness();
			if(tempFitness<particles.get(i).fitness) {
				particles.get(i).fitness = tempFitness;   //评估每个粒子的函数适应值
				particles.get(i).pbest = particles.get(i).x.clone();  //更新每个粒子的最优位置
			}
		}
	}
	/**
	 * 主函数PSO算法
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<Double> error = new ArrayList<>();
		ArrayList<Double> sd = new ArrayList<>();
		ArrayList<Double> mean = new ArrayList<>();
		HashMap<Integer,Double[]> errorSpeedMap = new HashMap<>();  //存储28个函数对应的收敛速度
		/*
		 * 开始测试cec2013的28个函数
		 */
		int functionNum = 1;	//函数编号
		int maxIterNum = 200000;  //迭代次数
		int maxRunTime = 5;  //运行次数
		int first = 10000;  //从迭代1000次开始记录
		int[] index = new int[(maxIterNum/first)];
		Double[] sumError = new Double[(maxIterNum/first)];
		int temp = 0;
		while(first <= maxIterNum) {
			index[temp] = first;
			first += 10000;
			temp++;
		}
		while(functionNum <= 10) {
			int runtime = 1;
			Arrays.fill(sumError, 0.0);
			while(runtime <= maxRunTime) {
				psoDemo pso = new psoDemo();
				pso.initParticle(functionNum);
				int iterNum = 1;
				temp = 0;
				while(iterNum <= maxIterNum) {
					pso.updateV();
					pso.updateX();
					pso.getGbest();
					if(iterNum == index[temp]) {  //存储迭代次数分别为10000、20000、...、200000次对应的误差，用来评估收敛速度
						sumError[temp] += pso.gbest_fitness/maxRunTime;
						System.out.println(pso.gbest_fitness);
						temp++;
					}
					iterNum++;
				}
				System.out.println(functionNum+"\t"+pso.gbest_fitness);
				error.add(pso.gbest_fitness);
				runtime++;
			}
			errorSpeedMap.put(functionNum, sumError.clone());
			// 计算每个函数对应的mean和sd
			double tempMean = Util.getMean(error);
			double tempSD = Util.getSD(error);
			mean.add(tempMean);
			sd.add(tempSD);
			error.clear();
			functionNum++;
		}
		/*
		 * 将28个函数对应的mean和sd以及迭代次数分别为10000、20000、...、200000次对应的误差写入csv文件
		 */
		Util.writeData(mean, sd,"src/Optimization/PSO/BenchMarkResult.csv");
		Util.writeData(errorSpeedMap,"src/Optimization/PSO/ConvergenceResult.csv");
	}
}
