package Optimization.Util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Util {
	private static double mean = 0;
	public static double getMean(ArrayList<Double> data) {
		double sum = 0;
		for(double d:data) {
			sum += d;
		}
		mean = sum/data.size();
		return mean;
	}
	
	public static double getSD(ArrayList<Double> data) {
		double sd_mean = (mean == 0 ? getMean(data) : mean);
		double sum = 0;
		for(double d:data) {
			sum += Math.pow(d-sd_mean, 2);
		}
		sum = sum/data.size();
		sum = Math.sqrt(sum);
		return sum;
	}
	public static void writeData(ArrayList<Double> mean,ArrayList<Double> sd,String path) {
		try {
			DecimalFormat df = new DecimalFormat("0.00E00");
			int functionNum = mean.size();
			BufferedWriter br = new BufferedWriter(new FileWriter(path));
			for(int i=0;i<functionNum;i++) {
				br.write("f"+(i+1)+","+"Mean,"+df.format(mean.get(i))+"\r\n");
				br.write(" ,"+"SD,"+df.format(sd.get(i))+"\r\n");
				br.flush();
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void writeTSPResult(Map<String, Double[]> cityDis,String path) throws IOException {
		double[] bestDis = new double[cityDis.size()];
		double[] meanDis = new double[cityDis.size()];
		int count = 0;
		for(Double[] dis:cityDis.values()) {
			double sumDis = 0;
			double minDis = Double.MAX_VALUE;
			int runtime = dis.length;
			for(Double d:dis) {
				if(d<minDis) {
					minDis = d;
				}
				sumDis += d;
			}
			bestDis[count] = minDis;
			meanDis[count] = sumDis/runtime;
			count ++;
		}
		count = 0;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
		bufferedWriter.write("Problem,Best Result,Average Result"+"\r\n");
		DecimalFormat df = new DecimalFormat("0.0");
		for(String city:cityDis.keySet()) {
			bufferedWriter.write(city+","+df.format(bestDis[count])+","+df.format(meanDis[count])+"\r\n");
			count ++;
		}
		bufferedWriter.close();
	}
	public static void writeData(HashMap<Integer,Double[]> errorSpeedMap,String path) {
		try {
			DecimalFormat df = new DecimalFormat("0.00E00");
			BufferedWriter br = new BufferedWriter(new FileWriter(path));
			for(Double[] errors:errorSpeedMap.values()) {
				for(double e:errors) {
					br.write(df.format(e)+",");
				}
				br.write("\r\n");
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int[] Arraysort(double[] arr, boolean desc) {
		double temp;
		int index;
		int k = arr.length;
		int[] Index = new int[k];
		for (int i = 0; i < k; i++) {
			Index[i] = i;
		}
 
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length - i - 1; j++) {
				if (desc) {
					if (arr[j] < arr[j + 1]) {
						temp = arr[j];
						arr[j] = arr[j + 1];
						arr[j + 1] = temp;
 
						index = Index[j];
						Index[j] = Index[j + 1];
						Index[j + 1] = index;
					}
				} else {
					if (arr[j] > arr[j + 1]) {
						temp = arr[j];
						arr[j] = arr[j + 1];
						arr[j + 1] = temp;
 
						index = Index[j];
						Index[j] = Index[j + 1];
						Index[j + 1] = index;
					}
				}
			}
		}
		return Index;
	}
    public static <T extends Serializable> T clone(T obj){
        T cloneObj = null;
        try {
            //写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();
            
            //分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}
