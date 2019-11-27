package Optimization.GA;

import Optimization.cec13.testfunc;

public class gaDemo {
	public int dimension = 10;  //x的维度
	public double[] x = new double[dimension]; 
	public double max_x = 100;
	public double min_x = -100;
	public String[] genes = new String[dimension];
	public double fitness;  //适应值，即对应的函数值
	int functionNum;  //cec13的函数编号
	//函数编号-1对应的最优值数组
	int[] functionIndex = {-1400,-1300,-1200,-1100,-1000,-900,-800,-700,-600,-500,-400,-300,-200,-100,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400};
	private testfunc tf = new testfunc();  //cec2103测试函数
	private int popNum = 30;	//染色体数量
	private String[][] popGenes = new String[popNum][dimension];	 	//一个种群中染色体总数
	public int geneNum = 15; 		//基因数
	private double bestfitness = Double.MAX_VALUE;  //函数最优解
	private String[] beststr;   		//最优解的染色体的二进制码
	public gaDemo(int functionNum) {
		this.functionNum = functionNum;
	}
	/**
	 * 初始化一条染色体（用二进制字符串表示）
	 */
	public String[] initChr() {
		String[] gene = new String[dimension];
		for(int dim=0;dim<dimension;dim++) {
			String res = "";
			for (int i = 0; i < geneNum; i++) {
				if (Math.random() > 0.5) {
					res += "0";
				} else {
					res += "1";
				}
			}
			gene[dim] = res;
		}
		return gene;
	}

	/**
	 * 初始化一个种群
	 */
	public String[][] initPop() {
		String[][] popGenes = new String[popNum][dimension];
		for (int i = 0; i < popNum; i++) {
			popGenes[i] = initChr();
		}
		return popGenes;
	}

	/**
	 * 计算函数值
	 * @throws Exception 
	 */
	public double calculatefitnessvalue(String[] genes) throws Exception {
		//fitness = 0;
		for(int i=0;i<dimension;i++) {
			int genesInt = Integer.parseInt(genes[i],2);
			x[i] = (max_x - min_x) * genesInt / (Math.pow(2, geneNum)-1) + min_x;
			//fitness += Math.pow(x[i], 2);
		}
		double[] f = new double[1];
		tf.test_func(x,f,dimension,1,functionNum);
		fitness = f[0] - functionIndex[functionNum-1];
		return fitness;
	}

	/**
	 * 轮盘选择
	 * 计算群体上每个个体的适应度值; 
	 * 按由个体适应度值所决定的某个规则选择将进入下一代的个体;
	 * @throws Exception 
	 */
	public void select() throws Exception {
		double evals[] = new double[popNum]; // 所有染色体适应值
		double p[] = new double[popNum]; // 各染色体选择概率
		double F = 0; // 累计适应值总和
		for (int i = 0; i < popNum; i++) {
			evals[i] = calculatefitnessvalue(popGenes[i]);
			if (evals[i] < bestfitness){  // 记录下种群中的最小值，即最优解
				bestfitness = evals[i];
				beststr = popGenes[i];
			}
			F = F + 1/evals[i]; // 所有染色体适应值总和
		}
		for (int i = 0; i < popNum; i++) {
			p[i] = (1/evals[i]) / F;
		}
		for(int chr=0;chr<popNum;chr++) {
			double select = Math.random() * F;
			double pSelect = 0;
			for(int i=0;i<popNum;i++) {
				pSelect += p[i];
				if(pSelect > select) {
					popGenes[chr] = popGenes[i];
					break;
				}
			}
		}
	}

	/**
	 * 交叉操作,交叉率为70%
	 */
	public void cross() {
		String temp1, temp2;
		for (int i = 0; i < popNum; i++) {
			for(int j=0;j<dimension;j++) {
				if (Math.random() < 0.7) {
					int pos = (int)(Math.random()*geneNum)+1;     //pos位点前后二进制串交叉
					temp1 = popGenes[i][j].substring(0, pos) + popGenes[(i + 1) % popNum][j].substring(pos); 
					temp2 = popGenes[(i + 1) % popNum][j].substring(0, pos) + popGenes[i][j].substring(pos);
					popGenes[i][j] = temp1;
					popGenes[(i + 1) / popNum][j] = temp2;
				}
			}
		}
	}

	/**
	 * 基因突变操作5%基因变异
	 */
	public void mutation() {
		for(int i=0;i<popNum;i++) {
			for(int j=0;j<dimension;j++) {
				for(int k=0;k<popGenes[i][j].length();k++) {
					if(Math.random()<0.05) {
						char newChar = (popGenes[i][j].charAt(k) == '0'?'1':'0');
						popGenes[i][j] = popGenes[i][j].substring(0,k) + newChar +popGenes[i][j].substring(k+1);
					}
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {
		gaDemo Tryer = new gaDemo(1);
		Tryer.popGenes = Tryer.initPop(); //产生初始种群
		for (int i = 0; i < 200000; i++) {
			Tryer.select();
			Tryer.cross();
			Tryer.mutation();
			System.out.println(i+"\t"+Tryer.bestfitness);
		}
	}
}