package model;

import java.util.ArrayList;
import java.util.Collections;

public class Effect {
	private ArrayList<Factor> factors = new ArrayList<Factor>();
	private ArrayList<Integer> bases = new ArrayList<Integer>();
	private ArrayList<Effect> nestedWithin = new ArrayList<Effect>();
	
	private boolean restricted = false;
	
	public String getName(){
		
		ArrayList<String> names = new ArrayList<String>();
		for(Factor f: this.factors){
			names.add(f.getName());
		}
		Collections.sort(names);
		String name="";
		for(String str: names){
			name+=str;
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
	
	public void addNestedWithin(Effect e){
		/*if(!this.nestedWithin.contains(e)){
			this.nestedWithin.add(e);
		}*/
		boolean b = true;
		for(Effect ee: this.nestedWithin){
			if(ee.getName().equals(e.getName())){
				b = false;
				break;
			}
		}
		if(b){
			this.nestedWithin.add(e);
		}
	}
	
	public void addAllNestedWithin(ArrayList<Effect> es){
		boolean b;
		for(Effect e: es){
			b = true;
			/*if(!this.nestedWithin.contains(e)){
				this.nestedWithin.add(e);
			}*/
			for(Effect ee: this.nestedWithin){
				if(ee.getName().equals(e.getName())){
					b = false;
					break;
				}
			}
			if(b){
				this.nestedWithin.add(e);
			}
		}
	}
	
	public ArrayList<Effect> getNestedWithin(){
		return this.nestedWithin;
	}
}
