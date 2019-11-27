package Optimization.ACA.TSP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class City{
	public double x;
	public double y;
	public int id;
	public String toString() {
		return id+","+x+","+y;
	}
}
public class ACA {
	class Ant{
		public ArrayList<Integer> path;
		public ArrayList<Integer> unvisited;
		public Ant() {
			path = new ArrayList<>();
			unvisited = new ArrayList<>();
		}
		public void printPath() {
			for(Integer p:path) {
				System.out.print(p+"->");
			}
			System.out.println(path.get(0));
		}
		public double getPathDis() {
			double disSum = 0;
			for(int i=0;i<path.size()-1;i++) {
				disSum += dis[path.get(i)][path.get(i+1)];
			}
			disSum += dis[path.get(path.size()-1)][path.get(0)];
			return disSum;
		}
	}
	private int antNum = 20;
	private Ant[] ants = new Ant[antNum];
	private int cityNum;
	private int maxIter = 1000;
	private ArrayList<Integer> allCity;
	private double[][] dis;
	private double[][] info;
	private double[][] inspire;
	private double[][] pro;
	private double infoInit;
	private int sigmma;
	private int beta;
	private double rho;
	private City[] citys;
	public double bestPathDis;
	private ArrayList<Integer> bestPath;
	public void readTSP(String path) throws IOException {
		String tspTestFileName = path.substring(path.lastIndexOf("\\"),path.lastIndexOf("."));
		String reg = "[^0-9]";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(path);
		cityNum = Integer.parseInt(matcher.replaceAll("").trim());
		dis = new double[cityNum][cityNum];
		info = new double[cityNum][cityNum];
		inspire = new double[cityNum][cityNum];
		pro = new double[cityNum][cityNum];
		citys = new City[cityNum];
		
		BufferedReader bReader = new BufferedReader(new FileReader(path));
		String line = null;
		int cityCount = 0;
		while((line = bReader.readLine()) != null) {
			String[] cityStr = line.split(" ");
			citys[cityCount] = new City();
			citys[cityCount].id = Integer.parseInt(cityStr[0]);
			citys[cityCount].x = Double.parseDouble(cityStr[1]);
			citys[cityCount].y = Double.parseDouble(cityStr[2]);
			cityCount++;
		}
	}
	public ACA() {
		allCity = new ArrayList<>();
		sigmma = 1;
		beta = 5;
		rho = 0.1;
		bestPathDis = Double.MAX_VALUE;
		bestPath = new ArrayList<>();
	}
	public void init() throws IOException {
		int disSum = 0;
		for(int i=0;i<cityNum;i++) {
			for(int j=0;j<cityNum;j++) {
				dis[i][j] = Math.sqrt(Math.pow((citys[i].x-citys[j].x), 2)+Math.pow((citys[i].y-citys[j].y), 2));
				disSum += dis[i][j];
			}
		}
		infoInit = (double)cityNum/disSum;
		for(int i=0;i<cityNum;i++) {
			allCity.add(i);
			for(int j=0;j<cityNum;j++) {
				info[i][j] = infoInit;
				inspire[i][j] = 1/(dis[i][j]+1e-8);
			}
		}
		//System.out.println(Arrays.deepToString(inspire));
	}
	public void initAnt() {
		for(int i=0;i<antNum;i++) {
			ants[i] = new Ant();
			ants[i].unvisited = (ArrayList<Integer>) allCity.clone();
			int startCity = (int)(Math.random()*cityNum);
			ants[i].path.add(startCity);
			ants[i].unvisited.remove(new Integer(startCity));
			//ants[i].printPath();
		}
		for(int i=0;i<cityNum;i++) {
			for(int j=0;j<cityNum;j++) {
				pro[i][j] = Math.pow(info[i][j], sigmma) * Math.pow(inspire[i][j], beta);
			}
		}
	}
	public int selectNextCity(int k) {
		int i = ants[k].path.get(ants[k].path.size()-1);//第k个蚂蚁访问的当前城市
		int j = 0;//第k个蚂蚁访问的下一个城市
		if(ants[k].unvisited.isEmpty()) {
			j = ants[k].path.get(0);
			return j;
		}
		double proSum = 0;
		for(Integer unvisitCity:ants[k].unvisited) {
			proSum += pro[i][unvisitCity];
		}
		double randomPro = Math.random() * proSum;
		double selectProSum = 0;
		for(Integer unvisitCity:ants[k].unvisited) {
			selectProSum += pro[i][unvisitCity];
			if(selectProSum >= randomPro) {
				j = unvisitCity;
				return j;
			}
		}
		return 0;
	}
	public void tourAllCity() {
		for(int i=0;i<cityNum;i++) {
			for(int j=0;j<antNum;j++) {
				int nextCity = selectNextCity(j);
				ants[j].path.add(nextCity);
				if(ants[j].unvisited.contains(nextCity)) {
					ants[j].unvisited.remove(new Integer(nextCity));
				}
			}
		}
		//System.out.println("path"+ants[0].path.toString());
		//System.out.println("un"+ants[0].unvisited.toString());
	}
	public void updateInfo() {
		ArrayList<Integer> curBestPath = new ArrayList<>();
		double curBestPathDis = Double.MAX_VALUE;
		for(int i=0;i<antNum;i++) {
			double dis = ants[i].getPathDis();
			if(dis < curBestPathDis) {
				curBestPathDis = dis;
				curBestPath = (ArrayList<Integer>) ants[i].path.clone();
			}
		}
		if(curBestPathDis < bestPathDis) {
			bestPathDis = curBestPathDis;
			bestPath = (ArrayList<Integer>) curBestPath.clone();
		}
		for(int i=0;i<cityNum;i++) {
			for(int j=0;j<cityNum;j++) {
				info[i][j] = (1-rho) * info[i][j];
			}
		}
		for(int i=0;i<bestPath.size()-1;i++) {
			info[bestPath.get(i)][bestPath.get(i+1)] += 1.0 / bestPathDis;
			info[bestPath.get(i+1)][bestPath.get(i)] = info[bestPath.get(i)][bestPath.get(i+1)]; 
		}
		info[bestPath.get(0)][bestPath.get(bestPath.size()-1)] += 1.0 / bestPathDis;
		info[bestPath.get(bestPath.size()-1)][bestPath.get(0)] = info[bestPath.get(0)][bestPath.get(bestPath.size()-1)];
	}
	public void aca() throws IOException {
		int iterNum = 0;
		int repeatIter = 0;
		double curBestDis = Double.MAX_VALUE;
		init();
		while(iterNum++ < maxIter) {
			initAnt();
			tourAllCity();
			updateInfo();
//			System.out.println("迭代次数："+iterNum+"  "+"路径距离："+bestPathDis);
			if(curBestDis > bestPathDis) {
				curBestDis = bestPathDis;
				repeatIter = 0;
			}else {
				repeatIter += 1;
			}
			if(repeatIter > 100) {
				break;
			}
		}
//		System.out.println("总迭代次数："+iterNum);
//		System.out.println("最佳路径距离："+bestPathDis);
//		System.out.print("最佳路径：");
//		for(int i=0;i<bestPath.size()-1;i++) {
//			System.out.print(bestPath.get(i)+"->");
//		}
//		System.out.println(bestPath.get(0));
	}
	public static void main(String[] args) throws IOException {
//		ACA aca = new ACA();
//		aca.aca();
//		String path = "src\\optimization\\el123.txt";
//		String tspTestFileName = path.substring(path.lastIndexOf("\\")+1,path.lastIndexOf("."));
//		String reg = "[^0-9]";
//		Pattern pattern = Pattern.compile(reg);
//		Matcher matcher = pattern.matcher(path);
//		int cityNum = Integer.parseInt(matcher.replaceAll("").trim());
//		System.out.println(cityNum);
	}
}
