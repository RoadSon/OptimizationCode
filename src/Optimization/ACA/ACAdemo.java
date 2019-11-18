package Optimization.ACA;
import java.io.Serializable;
import java.util.Arrays;

import Optimization.cec13.testfunc;

class Ant implements Serializable{
	public int dimension = 10;  //x的维度
	public double[] x = new double[dimension];  //蚂蚁位置
	public double max_x = 100;
	public double min_x = -100;
	public int geneNum = 15;  //基因长度，对应x的精度为4位小数
	public String[] genes = new String[dimension];
	public double fitness;  //粒子适应值，即对应的函数值
	int functionNum;  //cec13的函数编号
	//函数编号-1对应的最优值数组
	int[] functionIndex = {-1400,-1300,-1200,-1100,-1000,-900,-800,-700,-600,-500,-400,-300,-200,-100,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400};
	private testfunc tf = new testfunc();  //cec2103测试函数
	public void initx() {
		for(int i=0;i<dimension;i++) {
			String geneStr = "";
			double rand = Math.random();
			if(rand > 0.5) {
				geneStr += "1";
			}else {
				geneStr += "0";
			}
			genes[i] = geneStr;
		}
	}
	public double computeFitness() throws Exception {
		for(int i=0;i<dimension;i++) {
			int genesInt = Integer.parseInt(genes[i],2);
			x[i] = (max_x - min_x) * genesInt / (Math.pow(2, geneNum)-1) + min_x;
		}
		double[] f = new double[1];
		tf.test_func(x,f,dimension,1,functionNum);
		fitness = f[0] - functionIndex[functionNum-1];
		return fitness;
	}
	public Ant(int functionNum) throws Exception {
		this.functionNum = functionNum;
		initx();
	}
}
public class ACAdemo {
	private int antNum = 20;	//蚁群数量
	private int geneNum = 15;
	private Ant[] ants = new Ant[antNum];
	private int maxIter = 30000;
	private int dimension = 10;
	private double[][][] info = new double[dimension][geneNum][2];	//信息素
	private double rho = 0.1;	//信息素衰减速率
	private int functionNum = 0;
	private double[] minX = new double[dimension];	
	private double minFit = Double.MAX_VALUE;	//函数最小值
	public ACAdemo(int functionNum) throws Exception {
		this.functionNum = functionNum;
	}
	/**
	 * 初始化蚁群
	 * @throws Exception
	 */
	public void initAnt() throws Exception {
		for(int i=0;i<antNum;i++) {
			ants[i] = new Ant(functionNum);
		}
	}
	/**
	 * 让初始化的蚁群遍历完所有的二进制路径，是为了得到初始的函数值来初始化信息素
	 */
	public void initAllDimension() {
		for(int i=0;i<dimension;i++) {
			for(int ant=0;ant<antNum;ant++) {
				for(int j=1;j<geneNum;j++) {
					double p = Math.random()*(info[i][j][0] + info[i][j][1]);
					if(p > 0.5) {
						ants[ant].genes[i] += "0";
					}else {
						ants[ant].genes[i] += "1";
					}
				}
			}
		}
	}
	/**
	 * 获得初始化的函数值最小的蚂蚁
	 * @throws Exception
	 */
	public void getInitMinAnt() throws Exception {
		for(int i=0;i<antNum;i++) {
			if(ants[i].computeFitness() < minFit) {
				minFit = ants[i].fitness;
			}
		}
	}
	/**
	 * 初始化信息素
	 * @throws Exception
	 */
	public void initAllInfo() throws Exception {
		initAnt();
		initAllDimension();
		getInitMinAnt();
		for(int i=0;i<dimension;i++) {
			for(int j=0;j<geneNum;j++) {
				info[i][j][0] = 1/minFit;
				info[i][j][1] = 1/minFit;
			}
		}
	}
	/**
	 * 遍历所有的二进制路径
	 */
	public void tourAllDimension() {
		for(int i=0;i<dimension;i++) {
			for(int ant=0;ant<antNum;ant++) {
				int c0 = 0;
				int c1 = 0;
				for(int j=1;j<geneNum;j++) {
					double p = Math.random()*(info[i][j][0] + info[i][j][1]);
					if(info[i][j][0] > p) {
						ants[ant].genes[i] += "0";
						c0++;
					}else {
						ants[ant].genes[i] += "1";
						c1++;
					}
				}
				System.out.println(c0+"\t"+c1);
			}
		}
	}
	/**
	 * 更新信息素
	 * @throws Exception
	 */
	public void updateInfo() throws Exception {
		double tempMinFit = Double.MAX_VALUE;
		int minAntIndex = 0;
		for(int i=0;i<antNum;i++) {
			if(ants[i].computeFitness() < tempMinFit) {
				tempMinFit = ants[i].fitness;
				minAntIndex = i;
			}
		}
		if(tempMinFit < minFit) {
			minFit = tempMinFit;
			minX = ants[minAntIndex].x.clone();
		}
		for(int i=0;i<dimension;i++) {
			for(int j=0;j<geneNum;j++) {
				info[i][j][0] *= (1-rho);
				info[i][j][1] *= (1-rho);
			}
		}
		for(int i=0;i<dimension;i++) {
			String minAntGene = ants[minAntIndex].genes[i];
			for(int j=0;j<geneNum;j++) {
				if(minAntGene.charAt(j) == '0') {
					info[i][j][0] += 1/tempMinFit;
				}else if(minAntGene.charAt(j)=='1') {
					info[i][j][1] += 1/tempMinFit;
				}
			}
		}
	}
	/**
	 * 蚁群算法
	 * @throws Exception
	 */
	public void aca() throws Exception {
		int iter = 1;
		initAllInfo();
		while(iter <= maxIter) {
			initAnt();
			tourAllDimension();
			updateInfo();
			System.out.println(iter+"\t"+minFit);
			iter++;
		}
	}
	public static void main(String[] args) throws Exception {
		ACAdemo aca = new ACAdemo(1);
		aca.aca();
 	}
}
