package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import jsc.distributions.NoncentralFishersF;
import model.SplitFactors;
import model.Splits;

public class PowerEstimation {
	
	static float[] p;
	static float[] permf;
	static HashMap<Double,Integer> freq;
	static float sum;
	static int oc;
	static ArrayList<Integer> indices;
	static ArrayList<Double> keys;
	static int ind;
	static float sumSquare;
	
	public static double estimatedPower(Splits s){
		
		//name, ool, N, delta, sigma, 
		//cap, levels, ol, df, edf, tType, name
//		System.out.print(s.getName());
//		System.out.print(",");
//		System.out.print(s.getOol());
//		System.out.print(",");
//		System.out.print(s.getN());
//		System.out.print(",");
//		System.out.print(s.getDelta());
//		System.out.print(",");
//		System.out.println(s.getSigma());
		
		ArrayList<SplitFactors> elms = s.getElm();
		
		Collections.sort(elms, new Comparator<SplitFactors>(){
			public int compare(SplitFactors elm1,SplitFactors elm2){
				return elm2.getOc() - elm1.getOc();
			}
		});
		
		//assigning ol based on the first elm
		HashMap<Integer, Integer> ols = new HashMap<Integer, Integer>();
		for(Integer[] el:elms.get(0).getSf()){
			ols.put(el[3], el[2]);
		}
		
		for(SplitFactors elm:elms){
			for(Integer[] el:elm.getSf()){
				el[2] = ols.get(el[3]);
			}
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
		
//		for(SplitFactors elm:elms){
//			System.out.print(elm.getOc()+","+elm.getDf()+","+elm.getEdf()+","+elm.getOl()+":");
//			for(Integer[] el:elm.getSf()){
//				System.out.print("[");
//				for(Integer e:el){
//					System.out.print(e+",");
//				}
//				System.out.print("]");
//			}
//			System.out.println();
//		}
		
		int elmLength = elms.get(0).getSf().size();
		int largestOl = elms.get(0).getSf().get(elmLength-1)[2];
//		System.out.println(largestOl);
//		System.out.println(s.getOol());
		int[] perms = new int[largestOl];
		permf = new float[largestOl];
		p = new float[s.getOol()];
		freq = new HashMap<Double,Integer>();
		
		for(int i=0;i<(int) Math.floor(largestOl/2);i++){
			perms[i] = 1;
		}
		for(int i=(int) Math.floor(largestOl/2);i<largestOl;i++){
			perms[i] = -1;
		}
		
		Permutations perm = new Permutations(perms);
		
		int counter = 0;
		while(perm.hasNext()){
			counter++;
			perms = perm.next();
//			System.out.println(Arrays.toString(perms));
			for(int i=0;i<perms.length;i++){
				permf[i] = perms[i];
			}
			getP(elms.get(0).getSf(),0,0,0,s.getOol());
//			System.out.println(Arrays.toString(p));
			keys = new ArrayList<Double>();
			for(int i=0; i<elms.size(); i++){
				SplitFactors sf = elms.get(i);
				ind = i;
				sum = 0;
				oc = sf.getOc();
				indices = new ArrayList<Integer>();
				sumSquare = 0;
//				System.out.println(Arrays.toString(p));
				splitFactor(sf.getSf(), 0, 0, 0, 0, s.getOol(), s.getOol());
//				System.out.println(sf.getOc()+","+sumSquare);
				double power = getPower(sumSquare, s.getN(), s.getDelta(), s.getSigma(), elms.size(), sf.getDf(), sf.getEdf(), sf.getOl(), sf.getTtype());
				keys.add(power);
			}
			double overalPower = getOverallPower(keys);
			if(freq.containsKey(overalPower)){
				int frequency = freq.get(overalPower);
				frequency++;
				freq.put(overalPower, frequency);
			}else{
				freq.put(overalPower, 1);
			}
	    }
		
		double estimatedPower = getEstimatedPower(freq, counter);
		
		
		return estimatedPower;
		
		

//		return 1;
		
	}
	
	private static double getEstimatedPower(HashMap<Double, Integer> freq, int counter) {
		double estimatedPower = 0;
		
		for(Map.Entry<Double, Integer> entry : freq.entrySet()) {
			estimatedPower += entry.getValue()*entry.getKey();
		}
		
		return estimatedPower/counter;
	}

	private static double getOverallPower(ArrayList<Double> keys) {
		double overallPower = 0;
		int coef;
		double mult;
		
		Set<Double> powers = ImmutableSet.copyOf(keys);
		Set<Set<Double>> powerSets = Sets.powerSet(powers);
		
		
		for(Set<Double> pSet:powerSets){
			if(pSet.size()==0){
				continue;
			}
			
			mult = 1;
			
			if(pSet.size()%2 == 0){
				coef = -1;
			}else{
				coef = 1;
			}
			
			for(Double p: pSet){
				mult *= p;
			}
			
			overallPower += coef*mult;
		}
		
		return overallPower;
	}

	private static double getPower(float sumSquare, int N, int delta, int sigma, int size, int df, int edf, int ol, int ttype) {
		double power = 0;
		if(edf==0){
			return 0;
		}
//		System.out.println(df+","+edf);
		NoncentralFishersF cf = new NoncentralFishersF(df,edf,0);
		
		float d = delta;
		d = (d/(2*sigma))*(d/(2*sigma))*(N/ol)*sumSquare;
//		System.out.println("ncp: "+d);
		NoncentralFishersF ncf = new NoncentralFishersF(df, edf, d);
		power = 1-ncf.cdf(cf.inverseCdf(1-0.05/size));
//		System.out.println(cf.inverseCdf(1-0.05/size));
		return power*ttype;
	}

	public static void splitFactor(ArrayList<Integer[]> list, int level1, int level2, int index1, int index2, int ool1, int ool2){
		if(level1 == list.size()){
			if(level2 == list.size()-1){
				for(int i=0; i<list.get(level2)[0];i++){
					sum += p[index1 + index2 + i];
					indices.add(index1+index2+i);
				}
				
				if(indices.size()==oc){
					float average = sum/oc;
					sumSquare += average*average;
					
					for(Integer j:indices){
						p[j] -= average;
					}
					indices = new ArrayList<Integer>();
					sum = 0;
				}
			}else{
				ool2 /= list.get(level2)[2];
				for(int i=0; i<list.get(level2)[0];i++){
					splitFactor(list, level1, level2+1, index1, index2+ool2*i, ool1, ool2);
				}
			}
		}else{
			ool1/=list.get(level1)[2];
			for(int i=0;i<(list.get(level1)[2]/list.get(level1)[0]);i++){
				splitFactor(list, level1+1, level2, index1+ool1*i*list.get(level1)[0], index2, ool1, ool2);
			}
		}
	}
	
	public static void getP(ArrayList<Integer[]> list, int level, int index, int i_sum, int ool){
		if(level==list.size()-1){
			if((i_sum%2)==0){
				for(int i=0;i<list.get(level)[2];i++){
					p[index+i] = permf[i];
				}
			}else{
				for(int i=0;i<list.get(level)[2];i++){
					p[index+i] = (0)*permf[i];
				}
			}
		}else{
			ool = ool/list.get(level)[2];
			for(int i=0;i<list.get(level)[2];i++){
				getP(list, level+1, index+ool*i, i_sum+i, ool);
			}
		}
	}
	
//	public static void main(String[] args){
		
//		Integer[] ps = new Integer[]{1,2,3,4};
//		
//		Set<Integer> psSet = ImmutableSet.copyOf(ps);
//		Set<Set<Integer>> psSets = Sets.powerSet(psSet);
//		
//		for(Set<Integer> psss:psSets){
//			//for(Integer pss:psss){
//				System.out.println(psss);
//			//}
//		}
		
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
	    ////////////////////////////////////////////////////////////////////////
		
//		NoncentralFishersF ncf = new NoncentralFishersF(3,28,11.1111111111);
//		NoncentralFishersF cf = new NoncentralFishersF(3,28,0);
//		System.out.println(cf.inverseCdf(0.95));
//		System.out.println(1-ncf.cdf(cf.inverseCdf(0.95)));
//		
//		NoncentralFishersF o = new NoncentralFishersF(3,28,0);
//		System.out.println(1-o.cdf(cf.inverseCdf(0.95)));
//	}

}
