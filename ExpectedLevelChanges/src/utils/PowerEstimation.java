package utils;

import jsc.distributions.NoncentralFishersF;

public class PowerEstimation {
	public static void main(String[] args){
		
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
	    
		
		NoncentralFishersF ncf = new NoncentralFishersF(3,28,11.1111111111);
		NoncentralFishersF cf = new NoncentralFishersF(3,28,0);
		
		System.out.println(1-ncf.cdf(cf.inverseCdf(0.95)));
		
		NoncentralFishersF o = new NoncentralFishersF(3,28,0);
		System.out.println(1-o.cdf(cf.inverseCdf(0.95)));
	}

}
