package model;

import java.util.List;

public class Factor implements Cloneable{
	private String factorName;
	private int levels;
	private int lct;
	private int lcc;
	private int replication;
	private int minOrder;
	private int maxOrder;
	private int value;
	
	private Factor nestedWithin = null;
	private int base;
	
	public Factor(String name, int levels, int lct, int lcc, int value){
		this.factorName = name;
		this.levels = levels;
		this.lct = lct;
		this.lcc = lcc;
		this.minOrder = 1;
		this.maxOrder = 1;
		this.value = value;
	}
	
	public Factor(Factor f){
		this.factorName = f.getName();
		this.levels = f.getLevels();
		this.lct = f.getLct();
		this.lcc = f.getLcc();
		this.minOrder = f.getMinOrder();
		this.maxOrder = f.getMaxOrder();
		this.replication = f.getRep();
		this.value = f.value;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public void setBase(int base){
		this.base = base;
	}
	
	public int getBase(){
		return this.base;
	}
	
	public void setNestedWithin(Factor f){
		this.nestedWithin = f;
	}
	
	public Factor getNestedFactor(){
		return this.nestedWithin;
	}
	
	public void setMinOrder(int o){
		this.minOrder = o;
	}
	
	public int getMinOrder(){
		return this.minOrder;
	}
	
	public void setMaxOrder(int o){
		this.maxOrder = o;
	}
	
	public int getMaxOrder(){
		return this.maxOrder;
	}
	
	/*public void setFactorName(String name){
		this.factorName = name;
	}*/
	
	public String getName(){
		return this.factorName;
	}
	
	/*public void setLevels(int levels){
		this.levels = levels;
	}*/
	
	public int getLevels(){
		return this.levels;
	}
	
	public void setLevels(int l){
		this.levels = l;
	}
	
	public int getLct(){
		return this.lct;
	}
	
	public int getLcc(){
		return this.lcc;
	}
	
	public void setRep(int replication){
		this.replication = replication;
	}
	
	public void setRep(List<Factor> factors, int replication){
		
		for(Factor f: factors){
			replication *= f.getLevels();
		}
		this.replication = replication/this.levels;
	}
	
	public int getRep(){
		return this.replication;
	}
	
	public String toString(){
		return "(Factor Name: "+factorName+", Number of Levels: "+levels+", LCT: "+lct+", LCC: "+lcc+")";
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	    	System.out.println("return null");
	        return null; 
	    }
	}
}
