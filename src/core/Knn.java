package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public class Knn {
//	static ArrayList<MusicTuple> data[];
	static HashMap<Integer, Integer> hashData[];
	static HashSet<Integer> test[];
	//static ArrayList<Integer> neighbor;
	static int k = 500;
	static boolean weighted = false;
	static int metric = 3;
	static int qu, qa;
	static int max, count;
	static PrintWriter output;
	static double totalP10;
	static double[] vectorA;

	public static void readTrainingData(String fileName){
		String line = "";
		count = 0;
		max = 0;
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			while ((line = input.readLine()) != null){
				count++;
//				System.out.println("COUNT==" + count);
				int start = line.indexOf('-') + 1;
				line = line.substring(start + 1);
				//data[count] = new ArrayList<MusicTuple>();
				hashData[count] = new HashMap<Integer, Integer>();
				StringTokenizer token = new StringTokenizer(line, " ");
				while (token.hasMoreElements()) {
					String cur = token.nextToken();
					int div = cur.indexOf(':');
					int musicId = Integer.parseInt(cur.substring(0,  div));
					int numPlayed = Integer.parseInt(cur.substring(div + 1));
//					System.out.println(musicId + " " + numPlayed);
					//data[count].add(new MusicTuple(musicId, numPlayed));
					hashData[count].put(musicId, numPlayed);
					if (musicId > max) {
						max = musicId;
					}
				}
//				System.out.println(line);
			}
			input.close();
			System.out.println("TOTAL USER=" + count);
			System.out.println("MAX ID=" + max);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void readTestData(String fileName){
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			for (int i = 1; i <= count; i++){
				String line = input.readLine();
				test[i] = new HashSet<Integer>();
				int start = line.indexOf('-') + 1;
				line = line.substring(start + 1);
				StringTokenizer token = new StringTokenizer(line, " ");
				while (token.hasMoreElements()) {
					test[i].add(Integer.parseInt(token.nextToken()));
				}
			}
			input.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getEuclideanDist(double[] vectori, double[] vectorj){
		double res = 0;
		for (int i = 1; i <= max; i++) {
			res += Math.pow((vectori[i] - vectorj[i]), 2);
		}
		res = (double) 1.0 / Math.sqrt(res);
		return res;
	}
	
	public static double getDotDist(double[] vectori, double[] vectorj){
		double res = 0;
		for (int i = 1; i <= max; i++) {
			res += vectori[i] * vectorj[i];
		}
		return res;
	}
	
	public static double getCosineDist(double[] vectori, double[] vectorj){
		DoubleMatrix1D a = new DenseDoubleMatrix1D(vectori);
		DoubleMatrix1D b = new DenseDoubleMatrix1D(vectorj);
		return a.zDotProduct(b)/Math.sqrt(a.zDotProduct(a)*b.zDotProduct(b));
		//		double res = 0, sum1 = 0, sum2 = 0;
//				for (int i = 1; i <= max; i++) {
//			sum1 += Math.pow(vectori[i], 2);
//			sum2 += Math.pow(vectorj[i], 2);
//			res += vectori[i] * vectorj[i];
//		}
//		return res / (Math.sqrt(sum1) + Math.sqrt(sum2));
	}
	
	public static double getDist(int indexi, int indexj){
		//System.out.println(indexi + " " + indexj);
		double[] vectori = new double[max + 1];
		Arrays.fill(vectori, 0);
		double[] vectorj = new double[max + 1];
		Arrays.fill(vectorj, 0);
		if (indexi == 0) {
			vectori = vectorA.clone();
		}
		else{
			Iterator<Entry<Integer, Integer>> it = hashData[indexi].entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
		        vectori[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
		    }
//			for (int i = 0; i < data[indexi].size(); i++){
//				MusicTuple mt = data[indexi].get(i);
//				vectori[mt.getId()] = mt.getCount();
//			}
		}
		
		Iterator<Entry<Integer, Integer>> it2 = hashData[indexj].entrySet().iterator();
		while (it2.hasNext()) {
	        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it2.next();
	        vectorj[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
	    }
		
//		for (int i = 0; i < data[indexj].size(); i++){
//			MusicTuple mt = data[indexj].get(i);
//			vectorj[mt.getId()] = mt.getCount();
//		}
		switch (metric) {
			case 1: return getEuclideanDist(vectori, vectorj);
			case 2: return getDotDist(vectori, vectorj);
			case 3: return getCosineDist(vectori, vectorj);
			default: return -1;
		}
	}
	
	public static void queryUser(int userId) {
		System.out.println(userId);
//		output.println("BEGIN OF TEST USER " + userId);
		double[] res = new double[k];
		int[] resId = new int[k];
		Arrays.fill(res, 0.0);
		Arrays.fill(resId, 0);
		for (int id = 1; id <= count; id++) {
			if (id == userId) {
				continue;
			}
			double sim = getDist(userId, id);
			if (sim != 0) {
				int find = -1;
				for (int i = 0; i < k; i++){
					if (sim > res[i]) {
						find = i;
						break;
					}
				}
				if (find != -1){
					for (int i = k - 1; i > find; i--){
						res[i] = res[i - 1];
						resId[i] = resId[i - 1];
					}
					res[find] = sim;
					resId[find] = id;
				}
			}
		}
		
//		for (int i = 0; i < k; i++){
//			System.out.println(resId[i] + " " + res[i]);
//		}
//		System.out.println("***");
		
//		File file = new File("QueryUser" + userId + ".txt");
//		try {
//			PrintWriter output = new PrintWriter(file);
//			for (int i = 0; i < k; i++){
//				output.println(resId[i] + " " + res[i]);
//				//System.out.println(resId[i]);
//			}
//			output.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// Add the possible music to a set
		HashSet<Integer> musicSet = new HashSet<Integer>();
		for (int i = 0; i < k; i++) {
			int id = resId[i];
			if (id != 0) {
				Iterator<Entry<Integer, Integer>> it = hashData[id].entrySet().iterator();
				while (it.hasNext()) {
			        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
			        int mid = pairs.getKey();
			        if (!musicSet.contains(mid)) {
			        	musicSet.add(mid);
			        }
			    }
			}
		}
		
		double[] score = new double[10];
		int[] musicId = new int[10];
		Arrays.fill(score, 0.0);
		Arrays.fill(musicId, 0);
		
		Iterator<Integer> it = musicSet.iterator();
		while (it.hasNext()) {
	        int id =  it.next();
	        if (hashData[userId].containsKey(id)) {
				// If the music is already in query user's collection, find the next one
				continue;
			}
			else{
				double value = 0;
				if (!weighted) {
					// Unweighted
					for (int j = 0; j < k; j++){
						if (resId[j] != 0) {
							HashMap<Integer, Integer> hm = hashData[resId[j]];
							if (hm.containsKey(id)){
								value += hm.get(id);
							}
						}
					}
					value /= k;
				}
				else {
					// Weighted
					double divide = 0;
					for (int j = 0; j < k; j++){
						if (resId[j] != 0) {
							HashMap<Integer, Integer> hm = hashData[resId[j]];
							if (hm.containsKey(id)){
								value += hm.get(id) * res[j];
							}
						}
						divide += res[j];
					}
					value /= divide;
				}
				
				int mfind = -1;
				for (int ii = 0; ii < 10; ii++){
					if (value > score[ii]) {
						mfind = ii;
						break;
					}
				}
				if (mfind != -1){
					for (int ii = 10 - 1; ii > mfind; ii--){
						score[ii] = score[ii - 1];
						musicId[ii] = musicId[ii - 1];
					}
					score[mfind] = value;
					musicId[mfind] = id;
				}
			}
	    }
		
        if (userId != 0) {
            double precision = 0;
            for (int i = 0; i < 10; i++){
//                output.println(musicId[i] + " " + score[i]);
//            	System.out.println(musicId[i] + " " + score[i]);
                if (test[userId].contains(musicId[i])) {
                    precision++;
                }
            }
            precision /= 10.0;
            totalP10 += precision;
            output.println("For user," + userId + ", P@10 = " + precision);
//            System.out.println("For this user, P@10 = " + precision);
        }
        else{
            for (int i = 0; i < 10; i++){
                output.println(musicId[i] + " " + score[i]);
            }
        }
//		output.println("END OF TEST USER " + userId);
	}
	
	public static void queryArtist(){
		vectorA = new double[max + 1];
		Arrays.fill(vectorA, 0);
		//data[0] = new ArrayList<MusicTuple>();
        hashData[0] = new HashMap<Integer, Integer>();
		try {
			BufferedReader input = new BufferedReader(new FileReader("song_mapping.txt"));
			String line = "";
			while ((line = input.readLine()) != null) {
				if (line.endsWith("Metallica")) {
                    String temp = line.substring(0, line.indexOf('	'));
					System.out.println(temp);
                    int id = Integer.parseInt(temp);
                    //data[0].add(new MusicTuple(id, 1));
                    hashData[0].put(id, 1);
					vectorA[id] = 1;
                    
				}
			}
			input.close();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		queryUser(0);
        
        
	}
	
	public static void processQuery(int num){
        System.out.println("k = " + k);
		System.out.println("metric = " + metric);
        System.out.println("weighted ? " + weighted);
		totalP10 = 0.0;
		for (int i = 1; i <= num; i++){
			queryUser(i);
		}
		totalP10 /= num;
		output.println("Average P@10 =" + totalP10);
//		queryUser(1);
	}
	
	public static void getBaseline() {
		// Random
		int[] random = new int[10];
		System.out.println("Random:");
		for (int i = 0; i < 10; i++) {
			random[i] = (int) (Math.random() * 74594);
			System.out.println(random[i]);
		}
		
		double total_precision = 0;
		for (int id = 1; id <= count; id++) {
			double precision = 0;
			for (int i = 0; i < 10; i++) {
				if (test[id].contains(random[i])) {
					precision += 0.1;
				}
			}
			total_precision += precision;
		}
		total_precision /= count;
		System.out.println("Random baseline: Average P@10 = " + total_precision);
		
		// Popularity-based
		System.out.println("Popularity-based:");
		int[] musicCount = new int[max + 1];
		Arrays.fill(musicCount, 0);
		for (int id = 1; id <= count; id++) {
			Iterator<Entry<Integer, Integer>> it = hashData[id].entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
		        musicCount[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
		    }
//			ArrayList<MusicTuple> list = data[id];
//			for (int i = 0; i < list.size(); i++) {
//				MusicTuple mt = list.get(i);
//				musicCount[mt.getId()] += mt.getCount();
//			}
		}
		
		int[] topCount = new int[10];
		int[] topId = new int[10];
		
		for (int sid = 1; sid <= max; sid++) {
			int find = -1;
			for (int i = 0; i < 10; i++){
				if (musicCount[sid] > topCount[i]) {
					find = i;
					break;
				}
			}
			if (find != -1){
				for (int i = 10 - 1; i > find; i--){
					topCount[i] = topCount[i - 1];
					topId[i] = topId[i - 1];
				}
				topCount[find] = musicCount[sid];
				topId[find] = sid;
			}
		}
        
        for (int i = 0; i < 10; i++) {
			System.out.println(topId[i]);
		}
		
		total_precision = 0;
		for (int id = 1; id <= count; id++) {
			double precision = 0;
			for (int i = 0; i < 10; i++) {
				if (test[id].contains(topId[i])) {
					precision += 0.1;
				}
			}
			total_precision += precision;
		}
		total_precision /= count;
		System.out.println("Popularity-based baseline: Average P@10 = " + total_precision);
	}
	
	public static void readQuery(){
		Scanner input = new Scanner(System.in);
		System.out.println("Input k: the number of neighbors to consider:");
		k = input.nextInt();
		System.out.println("Input whether to use weighted or unweighted version (y/n):");
		weighted =  input.next().equals("y") ? true : false;
		System.out.println("Choose similarity metric (1:Euclidean 2:Dot 3:Cosine):");
		metric = input.nextInt();
		System.out.println("Would you like to do a user query or artist query (u/a):");
		String temp = input.next();
		if(temp.equals("u")){
			System.out.println("Input user ID, 0 to compute average of all users:");
			qu = input.nextInt();
		}
		else{
			System.out.println("Input artist ID:");
			qa = input.nextInt();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException{
		//data = new ArrayList[3325];
		hashData = new HashMap[3325];
		output = new PrintWriter("RESULT-C-UNWEIGHTED-500.txt");
		output.println("START TIME: " + new java.util.Date().toString());
		readTrainingData("user_train.txt");
		test = new HashSet[count + 1];
		readTestData("user_test.txt");
        //queryArtist();
        //getBaseline();
		processQuery(3323);
		output.println("FINISH TIME: " + new java.util.Date().toString());
		output.close();
	}
	
	static class MusicTuple {
		int id;
		int count;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public MusicTuple(int id, int count) {
			super();
			this.id = id;
			this.count = count;
		}
	}
}
