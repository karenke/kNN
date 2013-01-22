package core;

import java.util.Arrays;

public class test {
	static int k = 10;
	static int[] arr;
	
	public static void main (String[] args){
		arr = new int[100];
		for (int i = 0; i < 100; i++){
			arr[i] = (int) (Math.random() * 100);
		}
		
		
		
		int[] res = new int[k];
		Arrays.fill(res, 0);
		for (int id = 0; id < 100; id++){
			int find = -1;
			int sim = arr[id];
			//System.out.print(sim + ": ");
			 
			for (int i = 0; i < k; i++){
				if (sim > res[i]) {
					find = i;
					break;
				}
			}
			if (find != -1){
				for (int i = k - 1; i > find; i--){
					res[i] = res[i - 1];
				}
				res[find] = sim;
			}
//			for (int i = 0; i < k; i++){
//				System.out.print(res[i] + " ");
//			}
//			System.out.println();
		}
		
		for (int i = 0; i < k; i++){
			System.out.print(res[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < 100; i++){
			System.out.print(arr[i] + " ");
		}
		System.out.println();
		Arrays.sort(arr);
		for (int i = 0; i < 100; i++){
			System.out.print(arr[i] + " ");
		}
		System.out.println();
		
	}
}
