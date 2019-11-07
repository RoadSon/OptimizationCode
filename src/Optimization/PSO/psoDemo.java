package Optimization.PSO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 粒子群算法
 * @author RoadSon
 *
 */
public class psoDemo {
	public class Particle{
		private int dimension = 2;
		private double[] v = new double[dimension];   //粒子速度
		private double[] x = new double[dimension];  //粒子位置
		private double fitness;  //粒子适应值，即对应的函数值
		private double[] pbest = new double[dimension];
		public double computeFitness() {
			return x[0]*x[0] + x[1]*x[1];
		}
		public void initx() {
			for(int i=0;i<dimension;i++) {
				Random random = new Random();
				x[i] = random.nextInt(20) - 10;
				pbest[i] = x[i];
			}
		}
		public void initv() {
			for(int i=0;i<dimension;i++) {
				Random random = new Random();
				v[i] = random.nextDouble()*4 - 2;
			}
		}
	}
	private double[] gbest;
	private double gbest_fitness = Double.MAX_VALUE;
	private int particleNum = 10;
	private double w = 0.5;
	private double max_v = 2;
	private double min_v = -2;
	private int c1 = 2;
	private int c2 = 2;
	public List<Particle> particles = new ArrayList<Particle>();
	public void initParticle() {
		for(int i=0;i<particleNum;i++) {
			Particle particle = new Particle();
			particle.initx();
			particle.initv();
			particle.fitness = particle.computeFitness();
			particles.add(particle);
		}
	}
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
	public void updateX() {
		for(int i=0;i<particles.size();i++) {
			for(int j=0;j<particles.get(i).dimension;j++) {
				particles.get(i).x[j] = particles.get(i).x[j] + particles.get(i).v[j];
			}
			double tempFitness = particles.get(i).computeFitness();
			if(tempFitness<particles.get(i).fitness) {
				particles.get(i).fitness = tempFitness;   //评估每个粒子的函数适应值
				particles.get(i).pbest = particles.get(i).x.clone();  //更新每个粒子的最优位置
			}
		}
	}
	public static void main(String[] args) {
		psoDemo pso = new psoDemo();
		pso.initParticle();
		pso.getGbest();
		int iterNum = 20;  //迭代次数
		int N = 0;
		while(N++<iterNum) {
			pso.updateV();
			pso.updateX();
			pso.getGbest();
			System.out.println(N+".当前gbest:("+pso.gbest[0]+","+pso.gbest[1]+")  fitness="+pso.gbest_fitness);
		}
	}
}
