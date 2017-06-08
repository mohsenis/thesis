package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import jsc.distributions.NoncentralFishersF;
import model.SplitFactors;
import model.Splits;

public class PowerEstimation {
	
	static float[] p;
	static float[] permf;
	static ArrayList<HashMap<Float,Integer>> freq;
	
	public static void estimatedPower(Splits s){
		
		//name, ool, N, delta, sigma, 
		//cap, levels, ol, df, edf, tType, name
		System.out.print(s.getName());
		System.out.print(",");
		System.out.print(s.getOol());
		System.out.print(",");
		System.out.print(s.getN());
		System.out.print(",");
		System.out.print(s.getDelta());
		System.out.print(",");
		System.out.println(s.getSigma());
		
		ArrayList<SplitFactors> elms = s.getElm();
		
		Collections.sort(elms, new Comparator<SplitFactors>(){
			public int compare(SplitFactors elm1,SplitFactors elm2){
				return elm2.getOc() - elm1.getOc();
			}
		});
		
		for(SplitFactors elm:elms){
			Collections.sort(elm.getSf(), new Comparator<Integer[]>(){
				public int compare(Integer[] el1, Integer[] el2){
					if(el1[2] == el2[2]){
						return el1[3] - el2[3];
					}else{
						return el1[2] - el2[2];
					}
					
				}
			});
		}
		
		int elmLength = elms.get(0).getSf().size();
		int largestOl = elms.get(0).getSf().get(elmLength-1)[2];
		
		int[] perms = new int[largestOl];
		permf = new float[largestOl];
		p = new float[s.getOol()];
		freq = new ArrayList<HashMap<Float,Integer>>();
		
		for(int i=0;i<(int) Math.floor(largestOl/2);i++){
			perms[i] = 1;
		}
		for(int i=(int) Math.floor(largestOl/2);i<largestOl;i++){
			perms[i] = -1;
		}
		
		Permutations perm = new Permutations(perms);
		while(perm.hasNext()){
			perms = perm.next();
			
			for(int i=0;i<perms.length;i++){
				permf[i] = perms[i];
			}
			getP(elms.get(0).getSf(),0,0,0,s.getOol());
			
			for(SplitFactors sf:elms){
				splitFactor(sf.getSf(), 0, 0, 0, 0, s.getOol());
			}
//			if(s.getDelta()==232||s.getDelta()==0){
//				System.out.println(Arrays.toString(p));
//			}
	    }
		
		
		
//		for(SplitFactors elm:elms){
//			System.out.print(elm.getOc()+":");
//			for(Integer[] el:elm.getSf()){
//				System.out.print("[");
//				for(Integer e:el){
//					System.out.print(e+",");
//				}
//				System.out.print("]");
//			}
//			System.out.println();
//		}
//		
	}
	
	public static void splitFactor(ArrayList<Integer[]> list, int level1, int level2, int index1, int index2, int ool){
		
	}
	
	public static void getP(ArrayList<Integer[]> list, int level, int index, int i_sum, int ool){
//		System.out.println(level);
		if(level==list.size()-1){
			if((i_sum%2)==0){
				for(int i=0;i<list.get(level)[2];i++){
					p[index+i] = permf[i];
				}
			}else{
				for(int i=0;i<list.get(level)[2];i++){
					p[index+i] = (-1)*permf[i];
				}
			}
		}else{
			ool = ool/list.get(level)[2];
			for(int i=0;i<list.get(level)[2];i++){
//				i_sum += i;
//				index += ool*i;
//				level ++;
				getP(list, level+1, index+ool*i, i_sum+i, ool);
			}
		}
	}
	
	public static void main(String[] args){
//		int n=9;
//		for(int i=0;i<(int) Math.floor(n/2);i++){
//			System.out.println(1);
//		}
//			
//		for(int i=(int) Math.floor(n/2);i<n;i++){
//			System.out.println(-1);
//		}
		
		/*long start = System.currentTimeMillis();
		
		Permutations perm = new Permutations(new int[]{1,1,1,1,1,1,1,1,1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1});
		//Permutations perm = new Permutations(new Integer[]{1,1,-1,-1});
		int c=0;
	    while(perm.hasNext()){
	        System.out.println(Arrays.toString(perm.next()));
	        c++;
	    }
	    System.out.println(c);
	    
	    long end = System.currentTimeMillis();
	    System.out.println("Took : " + ((end - start) / 1000));*/
	    
		
		/*NoncentralFishersF ncf = new NoncentralFishersF(3,28,11.1111111111);
		NoncentralFishersF cf = new NoncentralFishersF(3,28,0);
		
		System.out.println(1-ncf.cdf(cf.inverseCdf(0.95)));
		
		NoncentralFishersF o = new NoncentralFishersF(3,28,0);
		System.out.println(1-o.cdf(cf.inverseCdf(0.95)));*/
	}

}
