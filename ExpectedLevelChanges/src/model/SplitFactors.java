package model;

import java.util.ArrayList;

public class SplitFactors {
	
	public static void main(String[] args){
		String str = "AB(2)CD(10)E(4)";
		String name;
		String next;
		String c;
		int pInd;
		while (str.length()>0){
			if(str.length()==1){
				name = str.substring(0, 1);
				c = "1";
				System.out.println(name+"_"+c);
				break;
			}
			name = str.substring(0, 1);
			next = str.substring(1,2);
			if(next.equals("(")){
				pInd = str.indexOf(")");
				c = str.substring(2,pInd);
				str = str.substring(pInd+1);
				System.out.println(name+"_"+c);
			}else{
				str = str.substring(1);
				c = "1";
				System.out.println(name+"_"+c);
			}
		}
	}
	
	private int oc;
	private int ol;
	private int df;
	private int edf;
	private int ttype;
	private ArrayList<Integer[]> sf = new ArrayList<Integer[]>();
	
	public void setOl(int ol){
		this.ol = ol;
	}
	
	public int getOl(){
		return this.ol;
	}
	
	public void setOc(int oc){
		this.oc = oc;
	}
	
	public int getOc(){
		return this.oc;
	}
	
	public void setSf(ArrayList<Integer[]> sf){
		this.sf = sf;
	}
	
	public ArrayList<Integer[]> getSf(){
		return this.sf;
	}
	
	public void setDf(int df){
		this.df = df;
	}
	
	public int getDf(){
		return this.df;
	}
	
	public void setEdf(int edf){
		this.edf = edf;
	}
	
	public int getEdf(){
		return this.edf;
	}
	
	public void setTtype(int ttype){
		this.ttype = ttype;
	}
	
	public int getTtype(){
		return this.ttype;
	}
}
