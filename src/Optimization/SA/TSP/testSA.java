package Optimization.SA.TSP;
/**
 * 模拟退火算法
 * @author RoadSon
 *
 */
public class testSA {
	final double xMin = 0;
	final double xMax = 15;
	int iterNum = 1000;
	final double tMin = 1e-3;
	double startT = 100;
	double x;
	double fValue;
	public testSA() {
		this.x = Math.random()*8;
		fValue = f(x);
	}
	public double f(double x) {
		return Math.sin(x)*x;
	}
	public void sa() {
		double newfValue;
		double tempX = x;
		int count = 0;
		while(startT>tMin) {
			while(iterNum > 0) {
				count += 1;
				iterNum -= 1;
				tempX = tempX + Math.random()*2-1;
				if(tempX >= xMin && tempX <= xMax) {
					newfValue = f(tempX);
					if(newfValue - fValue >= 0) {
						x = tempX;
						fValue = newfValue ;
					}
					else {
						if((Math.exp((newfValue - fValue)/startT)) > Math.random()){
							x = tempX;
							fValue = newfValue ;
						}
					}
				}else if(tempX <= xMin) {
					tempX = tempX + xMax - xMin;
				}else if(tempX >= xMax){
					tempX = tempX - xMax-xMin;
				}
				System.out.println("第"+count+"次迭代："+"  x的值："+x+"  y的值："+fValue);
			}
			startT = startT * 0.1;
			iterNum = 1000;
		}
	}
	public static void main(String[] args) {
		testSA testsa = new testSA();
		testsa.sa();
	}
	
}
