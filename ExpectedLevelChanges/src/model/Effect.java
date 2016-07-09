package model;

import java.util.ArrayList;

public class Effect {
	private ArrayList<Factor> factors = new ArrayList<Factor>();
	private ArrayList<Integer> bases = new ArrayList<Integer>();
	
	private boolean restricted = false;
	
	public String getName(){
		String name="";
		for(Factor f: this.factors){
			name+=f.getName();
		}
		return name;
	}
	
	public double getValue(){
		double value= 1;
		String name;
		for(Factor f: factors){
			name = f.getName();
			int i = name.indexOf('(');
			if(i==-1){
				value *= ((double) f.getValue())/1;
				continue;
			}
			int j = name.indexOf(')');
			String s = name.substring(i+1, j);
			value *= ((double) f.getValue())/Integer.parseInt(s);
		}
		return value/(factors.size()*factors.size());
	}
	
	public void setRestricted(){
		this.restricted = true;
	}
	
	public boolean getRestricted(){
		return this.restricted;
	}
	
	public void addBase(int base){
		this.bases.add(base);
	}
	
	public void addAllBases(ArrayList<Integer> bases){
		this.bases.addAll(bases);
	}
	
	public void addFactor(Factor f){
		this.factors.add(f);
	}
	
	public void addAllFactors(ArrayList<Factor> factorsList){
		this.factors.addAll(factorsList);
	}
	
	public ArrayList<Factor> getFactors(){
		return this.factors;
	}
	
	public ArrayList<Integer> getBases(){
		return this.bases;
	}
}
