package Optimization.AFSA;

import java.io.Serializable;
import java.util.ArrayList;

class Fish implements Serializable{
	public int dim;
	public double x[];
	public double y;
	public Fish(int dim) {
		this.dim = dim;
		x = new double[dim];
		for(int i=0;i<dim;i++) {
			//x[i] = Math.random() * 2 - 1;
			x[i] = Math.floor(256*Math.random());
			//x[i] = 1000;
		}
		y = calY();
	}
	public double calY() {
//		for(int i=0;i<dim;i++) {
//			y += -x[i]*x[i];
//		}
		y = -(x[0]*x[0]-160*x[0]+640+x[1]*x[1]-260*x[1]+16900);
		return y;
	}
	public double calDis(Fish f) {
		double dis = 0;
		for(int i=0;i<dim;i++) {
			dis += Math.pow(x[i]-f.x[i], 2);
		}
		dis = Math.sqrt(dis);
		return dis;
	}
}
public class AFSA {
	private double step;
	private double visual;
	private double delta;
	private int maxIterNum = 100;
	private int tryNum;
	private int fishNum;
	private int dim;
	private Fish[] fishs;
	private Fish bestFish;
	private Fish[] nextFish = new Fish[3];
	public AFSA(int fishNum,int tryNum,int dim,double step,double visual,double delta) {
		this.fishNum = fishNum;
		fishs = new Fish[fishNum];
		this.dim = dim;
		this.tryNum = tryNum;
		this.step = step;
		this.visual = visual;
		this.delta = delta;
	}
	public void init() {
		for(int i=0;i<fishNum;i++) {
			fishs[i] = new Fish(dim);
		}
		bestFish = new Fish(dim);
	}
	public void prey(int curFish) {
		Fish tryFish = new Fish(dim);
		nextFish[0] = new Fish(dim);
		for(int i=0;i<tryNum;i++) {
			for(int j=0;j<dim;j++) {
				tryFish.x[j] = fishs[curFish].x[j] + (Math.random()*2-1)*visual;
			}
			if(tryFish.calY() > fishs[curFish].y) {
				double dis = tryFish.calDis(fishs[curFish]);
				for(int j=0;j<dim;j++) {
					nextFish[0].x[j] = fishs[curFish].x[j] + Math.random()*step*(tryFish.x[j] - fishs[curFish].x[j])/(dis+1e-8);
				}
				break;
			}
			else {
				for(int j=0;j<dim;j++) {
					nextFish[0].x[j] = fishs[curFish].x[j] + Math.random()*step;
				}
			}
		}
		nextFish[0].y = nextFish[0].calY();
	}
	public ArrayList<Fish> getFriendFishs(Fish curFish) {
		ArrayList<Fish> friendFish = new ArrayList<>();
		for(int i=0;i<fishNum;i++) {
			if(curFish.calDis(fishs[i]) < visual) {
				friendFish.add(fishs[i]);
			}
		}
		return friendFish;
	}
	public void swarm(int curFish) {
		ArrayList<Fish> friendFish = new ArrayList<>();
		nextFish[1] = new Fish(dim);
		friendFish = getFriendFishs(fishs[curFish]);
		int n_f = friendFish.size();
		Fish centerFish = new Fish(dim);
		for(int i=0;i<dim;i++) {
			centerFish.x[i] = 0;
			for(Fish f:friendFish) {
				centerFish.x[i] += f.x[i];
			}
			centerFish.x[i] = centerFish.x[i] / n_f;
		}
		if(centerFish.calY()/(n_f+1e-8)>delta*fishs[curFish].y) {
			double dis = fishs[curFish].calDis(centerFish);
			for(int i=0;i<dim;i++) {
				nextFish[1].x[i] = fishs[curFish].x[i] + Math.random()*step*(centerFish.x[i] - fishs[curFish].x[i])/(dis+1e-8);
			}
		}
		else {
			prey(curFish);
		}
	}
	public void follow(int curFish) {
		ArrayList<Fish> friendFish = new ArrayList<>();
		nextFish[2] = new Fish(dim);
		friendFish = getFriendFishs(fishs[curFish]);
		Fish bestFriend = new Fish(dim);
		double bestY = -Double.MAX_VALUE;
		for(Fish f:friendFish) {
			if(f.calY()>bestY) {
				bestY = f.calY();
				bestFriend = f;
			}
		}
		if(bestY < fishs[curFish].y) {
			prey(curFish);
		}else {
			ArrayList<Fish> bestFriendFriend = new ArrayList<>();
			bestFriendFriend = getFriendFishs(bestFriend);
			int n_f = bestFriendFriend.size();
			if(bestFriend.calY()/(n_f+1e-8)>delta*fishs[curFish].y) {
				double dis = fishs[curFish].calDis(bestFriend);
				for(int i=0;i<dim;i++) {
					nextFish[2].x[i] = fishs[curFish].x[i] + Math.random()*step*(bestFriend.x[i] - fishs[curFish].x[i])/(dis+1e-8);
				}
			}else {
				prey(curFish);
			}
			nextFish[2].y = nextFish[2].calY();
		}
	}
	public void evaluateAciton(int curFish) {
		Fish maxFish = nextFish[0];
		for(int i=1;i<3;i++) {
			if(maxFish.y<nextFish[i].y) {
				maxFish = nextFish[i];
			}
		}
		if(fishs[curFish].y < maxFish.y) {
			fishs[curFish] = CloneUtil.clone(maxFish);
		}
		if(maxFish.y>bestFish.y) {
			bestFish = maxFish;
		}
	}
	public void afsa() {
		init();
		int iterNum = 0;
		while(iterNum++<maxIterNum) {
			for(int curFish=0;curFish<fishNum;curFish++) {
				prey(curFish);
				swarm(curFish);
				follow(curFish);
				evaluateAciton(curFish);
			}
			System.out.println("迭代次数："+iterNum+"  "+"当前最佳函数值："+bestFish.y);
		}
	}
	public static void main(String[] args) {
		//int fishNum,int tryNum,int dim,double step,double visual,double delta
		AFSA afsa = new AFSA(10,5,2,5,10,0.2);
		afsa.afsa();
	}
}

