package model;

import java.util.ArrayList;

public class Splits {
	
	private String name;
	private int ool;
//	private int cap;
//	private int level;
//	private int ol;
//	private int name;
	private int delta;
	private int sigma;
	private int N;
	private ArrayList<SplitFactors> elms;
	
	public Splits(String name, int ool, int delta, int sigma, int N){
		this.name = name;
		this.ool = ool;
		this.delta = delta;
		this.sigma = sigma;
		this.N = N;
		this.elms = new ArrayList<SplitFactors>();
	};
	
	public void addElm(SplitFactors elm){
		this.elms.add(elm);
	}
	
	public int getOol(){
		return this.ool;
	}
	
	public ArrayList<SplitFactors> getElm(){
		return this.elms;
	}
	
	public int getDelta(){
		return this.delta;
	}
	
	public int getSigma(){
		return this.sigma;
	}
	
	public int getN(){
		return this.N;
	}
	
	public String getName(){
		return this.name;
	}
}
