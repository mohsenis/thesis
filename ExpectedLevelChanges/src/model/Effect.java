package model;

import java.util.ArrayList;
import java.util.Collections;

public class Effect {
	private ArrayList<Factor> factors = new ArrayList<Factor>();
	private ArrayList<Integer> bases = new ArrayList<Integer>();
	private ArrayList<Effect> nestedWithin = new ArrayList<Effect>();
	private ArrayList<String> mSquares = new ArrayList<String>();
	private ArrayList<Integer> mSquaresCoefs = new ArrayList<Integer>();
	
	private boolean restricted = false;
	private int position = 1;
	private int level;
	private int df;
	private int coef;
	private String mSquare;
	
	public void setLevel(int l){
		this.level = l;
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public void setDf(int df){
		this.df = df;
	}
	
	public void updateDf(){
		int c = 1;
		for(Effect e: this.nestedWithin){
			c *= e.getFactors().get(0).getLevels();
		}
		this.df *= c;
		this.level *= c;
	}
	
	public int getCoef(){
		return this.coef;
	}
	
	public void setCoef(int N){
		this.coef = N/this.level;
	}
	
	public int getDf(){
		return this.df;
	}
	
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
	
	public int getPosition(){
		return this.position;
	}
	
	/*public void updateEffectLevel(){
		if(this.nestedWithin.size()!=0){
			for(Effect e: this.nestedWithin){
				if(e.getEffectLevel()+1>this.effectLevel){
					
				}
			}
		}
	}*/
	
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
			if(e.getPosition()+1>this.position){
				this.position = e.getPosition() + 1;
			}
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
				if(e.getPosition()+1>this.position){
					this.position = e.getPosition() + 1;
				}
			}
		}
	}
	
	public ArrayList<Effect> getNestedWithin(){
		return this.nestedWithin;
	}
	
	public int getType(){
		for(Factor f: this.factors){
			if(f.getType()==1){
				return 1;
			}
		}
		return 0;
	}
	
	public String getMSquare(){
		String mSquare;
		if(this.getType()==0){
			mSquare = "\u03A6"+"("+this.getName()+")";
		}else{
			mSquare = "\u03C3"+"�"+"("+this.getName()+")";
		}
		//this.mSquare = mSquare;
		return mSquare;
	}
	
	public ArrayList<String> getMSquares(){
		return this.mSquares;
	}
	
	public ArrayList<Integer> getMSquareCoefs(){
		return this.mSquaresCoefs;
	}
	
	public void setMSquare(){
		this.mSquare = this.getMSquare();
		
		this.mSquares.add("\u03C3"+"�");
		this.mSquaresCoefs.add(1);
		this.mSquares.add(this.mSquare);
		this.mSquaresCoefs.add(this.coef);
	}
	
	public void updateMSquares(){
		if(this.getType()==1){
			for(Effect e: this.nestedWithin){
				if(this.getType(e)==1){
					e.getMSquares().add(this.mSquare);
					e.getMSquareCoefs().add(this.coef);
				}
			}
		}
	}
	
	public void addRestrictedMSquare(){
		String mSquare = "\u03C3"+"�"+"r"+"("+this.getName()+")";
		this.mSquares.add(mSquare);
		this.mSquaresCoefs.add(this.coef);
		
		for(Effect e: this.nestedWithin){
			e.getMSquares().add(mSquare);
			e.getMSquareCoefs().add(this.coef);
		}
	}
	
	public int getType(Effect e){
		for(Factor f: this.factors){
			if(!e.getFactors().contains(f)){
				if(f.getType()==0){
					return 0;
				}
			}
			
		}
		return 1;
	}
}
