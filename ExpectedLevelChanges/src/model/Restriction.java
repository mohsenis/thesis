package model;

public class Restriction {
	private String factorName;
	private int size;
	private int order;
	
	public Restriction(String factor, int size, int order){
		this.factorName = factor;
		this.size = size;
		this.order = order;
	}
	
	/*public void setFactorName(Factor factor){
		this.factor = factor;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void setOrder(int order){
		this.order = order;
	}*/
	
	public String getFactorName(){
		return this.factorName;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public int getOrder(){
		return this.order;
	}
	
	public String toString(){
		return "{factor: "+factorName+", size: "+size+", order: "+order+"}";
	}
}
