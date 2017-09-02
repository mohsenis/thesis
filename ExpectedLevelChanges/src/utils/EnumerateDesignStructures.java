package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import model.Effect;
import model.Factor;
import model.Restriction;
import model.SplitFactors;
import model.Splits;

public class EnumerateDesignStructures {
	
	public static ArrayList<String> stringDesigns;
	//public static ArrayList<String> removedDesigns;
	
	public static List<ArrayList<Restriction>> listDesignStructures(List<Factor> factors){
		
		stringDesigns = new ArrayList<String>();
		//removedDesigns = new ArrayList<String>();
		
		structuresString(factors, "");
		
		//showDesigns(stringDesigns);
		
		List<ArrayList<Restriction>> structures = removeDuplicates();
		
		/*ArrayList<Integer> removeRests = new ArrayList<Integer>();
		for(int i=0; i<structures.size();i++){
			for(Restriction r: structures.get(i)){
				if(r.getFactorName().equals("R") && r.getSize()!=1){
					removeRests.add(i);
					break;
				}
			}
		}
		int index;
		for(int i=removeRests.size()-1;i>-1;i--){
			index = removeRests.get(i);
			structures.remove(index);
		}*/
		
		return structures;
	}
	
	public static List<ArrayList<Restriction>> removeDuplicates(){
		//HashMap<String,ArrayList<Restriction>> designStructures = new HashMap<String, ArrayList<Restriction>>();
		List<String> keys = new ArrayList<String>();
		List<ArrayList<Restriction>> designStructures = new ArrayList<ArrayList<Restriction>>();
		
		for(String str: stringDesigns){
			//System.out.println(++i);
			String[] strs = str.split(";");
			Arrays.sort(strs);
			String key = Arrays.toString(strs);
			
			if(keys.contains(key)){
				//removedDesigns.add(str);
				//removedDesigns.add(key);
			}else{
				strs = str.split(";");
				ArrayList<Restriction> rs = new ArrayList<Restriction>();
				for(String s: strs){
					String[] ss = s.split(",");
					Restriction r = new Restriction(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
					rs.add(r);
				}
				keys.add(key);
				designStructures.add(rs);
			}
			
			//System.out.println("------------");
		}
		
		return designStructures;
	}
	
	public static void showDesigns(ArrayList<String> designs){
//		int i=0;
//		for(String str: designs){
//			System.out.println(++i);
//			String[] strs = str.split(";");
//			for(String s: strs){
//				System.out.println(s);
//			}
//			System.out.println("------------");
//		}
	}
	
	public static void structuresString(List<Factor> factors, String str){
		String str1;
		String str2;
		String str3;
		int c;
		for(Factor f: factors){
			str1 = str;
			ArrayList<Integer> divisors;
			if(f.getName().equals("R")){
				if(!str1.equals("")){
					continue;
				}
				divisors = new ArrayList<Integer>();
				divisors.add(1);
			}else{
				divisors = allDivisors(f.getLevels());
			}
			
			for(Integer i: divisors){
				str2 = str1;
				for(int o=f.getMinOrder();o<=f.getMaxOrder();o++){
					c=1;
					str3 = str2;
					str3 += f.getName()+","+i+","+o;
					stringDesigns.add(str3);
					str3 += ";";
					
					List<Factor> newFactors = new ArrayList<Factor>();
					for(Factor ff:factors){
						Factor factor = new Factor(ff);
						if(ff.equals(f)){
							c *= i;
							factor.setLevels(i);
							factor.setMinOrder(o+1);
							factor.setMaxOrder(o+1);
						}else{
							c *= factor.getLevels();
							if(factor.getMinOrder()<o || factor.getMaxOrder()==1){
								factor.setMinOrder(o);
								factor.setMaxOrder(o+1);
							}
						}
						
						newFactors.add(factor);
					}
					if(c==1){
						stringDesigns.remove(stringDesigns.size()-1);	
					}
					structuresString(newFactors, str3);
				}
			}
		}
		
	}
	
	public static ArrayList<Integer> allDivisors(int n){
		ArrayList<Integer> divisors = new ArrayList<Integer>();
		
		for(int i=1;i<n;i++){
			if(n%i==0){
				divisors.add(i);
			}
		}
		
		return divisors;
	}
	
	public static ArrayList<ArrayList<Effect>> getEffects(List<Factor> factors, ArrayList<ArrayList<Restriction>> restrictions){
		ArrayList<Effect> effects; //for each level of interaction
		ArrayList<Effect> nestedEffects;
		ArrayList<ArrayList<Effect>> effectsList = new ArrayList<ArrayList<Effect>>();
		HashMap<String,Effect> effectMap = new HashMap<String, Effect>();
		
		ArrayList<Effect> allEffects = new ArrayList<Effect>();
		ArrayList<String> names = new ArrayList<String>();
		
		int N = 1;
		for(int i=0; i<factors.size(); i++){
			effects = new ArrayList<Effect>();
			effectsList.add(effects);
			N *= factors.get(i).getLevels();
		}
		
		int index;
		Factor newF;
		Effect e;
		ArrayList<Factor> newFactors;
		for(Factor f: factors){
			newFactors = new ArrayList<Factor>();
			int l = f.getLevels();
			index = factors.indexOf(f);
			
			for(ArrayList<Restriction> resList :restrictions){
				for(Restriction r: resList){
					if(r.getFactorName().equals(f.getName())){
						if(r.getSize()!=1){
							newF = new Factor(f.getName()+"("+/*(newFactors.size()+1)*/r.getSize()+")",l/r.getSize(),f.getType(),f.getLct(),f.getLcc(),f.getValue());
							newF.setBase(index);
							newFactors.add(newF);
							l = r.getSize();
						}
					}
				}
			}
			
			if(newFactors.size()==0){
				newF = new Factor(f);
				newF.setBase(index);
				newFactors.add(newF);
			}else{
				l = f.getLevels();
				for(Factor ff: newFactors){
					l /= ff.getLevels();
				}
				newF = new Factor(f.getName()+"("+/*(newFactors.size()+1)*/1+")",l,f.getType(),f.getLct(),f.getLcc(),f.getValue());
				newF.setBase(index);
				newFactors.add(newF);
			}
			
			if(newFactors.size()>1){ //add nesting
				for(int i=1; i<newFactors.size(); i++){
					newFactors.get(i).addNestedWithin(newFactors.get(i-1));
					newFactors.get(i).addAllNestedWithin(newFactors.get(i-1).getNestedWithin());
				}
			}
			
			nestedEffects = new ArrayList<Effect>();
			
			for(Factor ff: newFactors){
				e = new Effect();
				e.addBase(index);
				e.addFactor(ff);
				e.addAllNestedWithin(nestedEffects);
				e.setLevel(ff.getLevels());
				e.setDf(ff.getLevels()-1);
				
				nestedEffects.add(e);
				effectsList.get(0).add(e);
				e.updateDf();
				e.setCoef(N);
				e.setMSquare();
				
				allEffects.add(e);
				names.add(e.getName());
				effectMap.put(e.getName(), e);
			}
		}
		for(Effect eee:effectsList.get(0)){//add df to factors
			eee.getFactors().get(0).setDf(eee.getDf());
		}
		ArrayList<Effect> effs;
		int ind;
		for(int i=0; i<effectsList.size()-1;i++){
			effs= effectsList.get(i);
			for(Effect eff: effectsList.get(0)){
				for(Effect ef: effs){
					//if(eff.getBases().get(0) != ef.getBases().get(0)){
					if(!ef.getBases().contains(eff.getBases().get(0))){
						e = new Effect();
						e.addBase(eff.getBases().get(0));
						e.addAllBases(ef.getBases());
						e.addFactor(eff.getFactors().get(0));
						e.addAllFactors(ef.getFactors());
						e.setLevel(ef.getLevel()*eff.getLevel());
						e.setDf(ef.getDf()*eff.getDf());
						e.setCoef(N);
						e.setMSquare();
						
						ind = names.indexOf(e.getName());
						if(ind!=-1){
							e = allEffects.get(ind);
						}else{
							allEffects.add(e);
							names.add(e.getName());
							effectsList.get(i+1).add(e);
						}
						
						e.addNestedWithin(eff);
						e.addAllNestedWithin(eff.getNestedWithin());
						e.addNestedWithin(ef);
						e.addAllNestedWithin(ef.getNestedWithin());
//						for(Factor f: e.getFactors()){ //add nesting for interactions
//							e.addNestedWithin(f);
//							for(Factor FF)
//						}
						
					}
				}
			}
		}
		
		List<Factor> fs;
		ArrayList<String> eNames;
		String name;
		for(Effect es: allEffects){
			for(Factor f: es.getFactors()){
				for(Factor wf: f.getNestedWithin()){
					fs = new ArrayList<Factor>();
					fs.addAll(es.getFactors());
					fs.remove(f);
					fs.add(wf);
					
					eNames = new ArrayList<String>();
					for(Factor nf: fs){
						eNames.add(nf.getName());
					}
					Collections.sort(eNames);
					name = "";
					for(String str: eNames){
						name+=str;
					}
					
					ind = names.indexOf(name);
					if(ind!=-1){
						e = allEffects.get(ind);
						es.addNestedWithin(e);
						es.addAllNestedWithin(e.getNestedWithin());
					}
				}
			}
			es.updateMSquares();
		}
		
		//set the restrictions
		ArrayList<Effect> restrictedAll = new ArrayList<Effect>();
		ArrayList<Effect> restricted;
		ArrayList<Factor> restrictedFactors;
		ArrayList<Integer> restrictedBases;
		ArrayList<Integer> removes;
		String key1;
		String key2;
		int ii;
		for(ArrayList<Restriction> resList :restrictions){
			restricted = new ArrayList<Effect>();
			restrictedFactors = new ArrayList<Factor>();
			restrictedBases = new ArrayList<Integer>();
			removes = new ArrayList<Integer>();
			for(Restriction r: resList){
				key1 = r.getFactorName()+"("+r.getSize()+")";
				key2 = r.getFactorName();
				
				if(effectMap.containsKey(key1)){
					e = effectMap.get(key1);
				}else{
					e = effectMap.get(key2);
				}
				restricted.add(e);
				//restricted1.add(e);
				restrictedBases.add(e.getBases().get(0));
			}
			for(int i=0;i<restrictedAll.size();i++){
				e = restrictedAll.get(i);
				index = restrictedBases.indexOf(e.getBases().get(0));
				if(index!=-1){
					removes.add(i);
				}
			}
			
			for(int i=removes.size()-1;i>-1;i--){
				ii = removes.get(i);
				restrictedAll.remove(ii);
			}
			/*for(int i: removes){
				restrictedAll.remove(i);
			}*/
			restrictedAll.addAll(restricted);
			
			for(Effect ef: restrictedAll){
				restrictedFactors.add(ef.getFactors().get(0));
			}
			
			boolean b;
			for(ArrayList<Effect> efs: effectsList){
				b = false;
				for(Effect ef: efs){
					if(equalLists(restrictedFactors, ef.getFactors())){
						ef.setRestricted();
						
						ef.addRestrictedMSquare();
						
						b = true;
						break;
					}
				}
				if(b){
					break;
				}
			}
			
		}
		
		//build error terms
		ArrayList<Effect> errorE = new ArrayList<Effect>();
		for(ArrayList<Effect> efs: effectsList){
			for(Effect ef: efs){
				if(ef.getRepEff()){
					errorE.add(ef);
				}
			}
		}
		
		Effect e1;
		Effect e2;
//		ArrayList<Effect> removeE = new ArrayList<Effect>();
		for(int i=0; i<errorE.size()-1; i++){
			e1 = errorE.get(i);
			for(int j=i+1; j<errorE.size(); j++){
				e2 = errorE.get(j);
				if(e1.getMSquaresS().toString().equals(e2.getMSquaresS().toString())){
					e1.setRem();
					e2.setDf(e2.getDf()+e1.getDf());
					break;
				}
			}
		}
		
		int i=1;
		for(Effect ef:errorE){
			if(!ef.getRem()){
				ef.setErrIndex(i); 
				i++;
			}
		}
		
		//for allEffect, if they are not errorE, 
		//remove the second msquare (itself), check with msquare of the errorE's
		ArrayList<String> msquares;
		ArrayList<String> msquaresS;
		for(ArrayList<Effect> efs: effectsList){
			for(Effect ef: efs){
				if(!ef.getRepEff() && ef.getTest().equals("")){
					msquares = new ArrayList<String>();
					for(int j=0; j<ef.getMSquaresS().size();j++){
						if(j==1){
							continue;
						}
						msquares.add(ef.getMSquaresS().get(j));
					}
					for(Effect eef:errorE){
						if(!eef.getRem()){
							if(msquares.toString().equals(eef.getMSquaresS().toString())){
								ef.setEdf(eef.getDf());
								ef.setTest("e");
								break;
							}
						}
					}
					if(ef.getTest().equals("") && ef.getRestricted()){
						msquaresS = new ArrayList<String>();
						for(String s:msquares){
							if(!s.equals("\u03C3"+"²"+"r"+"("+ef.getName()+")")){
								msquaresS.add(s);
							}
						}
						for(Effect eef:errorE){
							if(!eef.getRem()){
								if(msquaresS.toString().equals(eef.getMSquaresS().toString())){
									ef.setEdf(eef.getDf());
									ef.setTest("c");
									break;
								}
							}
						}
					}
					//if still no test, check for interaction test
					if(ef.getTest().equals("")){
						ef.setTest("n");
					}
				}
			}
		}

		return effectsList;
	}
	
	public static boolean equalLists(ArrayList<Factor> fList1, ArrayList<Factor> fList2){
		HashSet<Factor> set1 = new HashSet<Factor>();
		set1.addAll(fList1);
		HashSet<Factor> set2 = new HashSet<Factor>();
		set2.addAll(fList2);
		
		return set1.equals(set2);
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		int replication = 1; //always
		
		List<String[]> infos = readCSV.readFile("factors");
		List<Factor> factors = Organizer.factorMapper(infos, replication); 
		
		int N = 1;
		for(Factor f:factors){
			N*=f.getLevels();
		}
		int delta = 4;
		int sigma = 4;
		
		List<float[]> lct_lcc_list = new ArrayList<float[]>();
		List<ArrayList<Restriction>> structures = listDesignStructures(factors);
		ArrayList<ArrayList<ArrayList<Effect>>> allEffects = new ArrayList<ArrayList<ArrayList<Effect>>>();
		ArrayList<ArrayList<Effect>> effects;
		
		//adding complete randomization
		structures.add(0,new ArrayList<Restriction>());
		
		//changing the format
		ArrayList<ArrayList<Restriction>> restrictions;
		
		for(ArrayList<Restriction> rs: structures) {
		    
			restrictions = new ArrayList<ArrayList<Restriction>>();
			int j = 1;
			ArrayList<Restriction> restriction = new ArrayList<Restriction>();
			for(Restriction r: rs){
				if(r.getOrder()==j){
					restriction.add(r);
				}else{
					j = r.getOrder();
					restrictions.add(restriction);
					restriction = new ArrayList<Restriction>();
					restriction.add(r);
				}
				
			}
			restrictions.add(restriction);
			//changing the format end
			//System.out.println(restrictions);
			//System.out.println(rs);
			float[] lct_lcc = new float[2];
			lct_lcc[0] = LevelChangeTime.tlct(factors, restrictions, replication);		
			lct_lcc[1] = LevelChangeCost.tlcc(factors, restrictions, replication);
			
			lct_lcc_list.add(lct_lcc);
			
			effects = getEffects(factors,restrictions);
			allEffects.add(effects);
		}
		double value;
		double dValue;
		String sep = "$";
//		PrintWriter writerS = new PrintWriter("C:/Users/mohse/git/thesis/ExpectedLevelChanges/src/files/printS.csv");
//		writerS.println("Index,Value,Time,Cost,Design");
		PrintWriter writer = new PrintWriter("C:/Users/mohse/git/thesis/ExpectedLevelChanges/src/files/values.txt", "UTF-8");
		writer.println("effectiveness"+sep+"efficiency"+sep+"design");
		
		PrintWriter printer = new PrintWriter("C:/Users/mohse/git/thesis/ExpectedLevelChanges/src/files/prints.txt", "UTF-8");
		System.out.println("Total number of design structures (including full randomization):");
		System.out.println(structures.size());
		System.out.println("---------------");
		printer.println("Total number of design structures (including full randomization):");
		printer.println(structures.size());
		printer.println("---------------");
		
		String name;
		String mSquare;
		String mSquareS;
		String printStr;
		PrintSorted ps = new PrintSorted();
		ps.setTotal(structures.size());
		
		for(int i=0; i<structures.size();i++) {
			printStr="";
			HashMap<ArrayList<Integer>,Splits> splits = new HashMap<ArrayList<Integer>,Splits>();
			printer.println(i+1);
			printer.println();
			value = 0;
			dValue = 0;
		    ArrayList<Restriction> rs = structures.get(i);
		    float[] lct_lcc = lct_lcc_list.get(i);
		    String design="";
		    //System.out.println("Structure:");
		    for(Restriction r: rs){
		    	System.out.println(r);
		    	printer.println(r);
		    	printStr+= String.format(r+"\n");
		    	design+=r;
		    }
		    System.out.println(i+1);
		    printer.println();
		    printStr+= String.format("\n");
		    //printer.printf("%-15s %-10s %-10s\n", "Effect", "DF", "Mean Square");
		    printer.printf("%-15s %-10s %-10s %-10s %-10s %-10s\n", "Effect", "Position", "DF", "EDF", "Test", "Mean Square");
		    printStr+=String.format("%-15s %-10s %-10s %-10s %-10s %-10s\n", "Effect", "Position", "DF", "EDF", "Test", "Mean Square");
//		    printer.printf("Effect,Position,DF,Mean Square\n");
		    //printer.println("Effect \t\t\t DF \t\t Mean Square");
		    for(ArrayList<Effect> effs: allEffects.get(i)){
		    	for(Effect e: effs){
//		    		System.out.print(e.getName());
		    		name = e.getName();
		    		//printer.print(e.getName());
		    		if(e.getRestricted()){
//		    			System.out.print("-r");
		    			name += "-r";
		    			//printer.print("-r");
		    			value -= e.getValue();
		    		}
		    		if(!e.getRepEff()){
		    			dValue += e.getTestValue()*e.getEdf()*e.getDfValue();
		    		}
		    		
		    		mSquare="";
		    		//printer.print(" \t\t\t "+e.getDf()+" \t\t ");
		    		for(int is=0;is<e.getMSquares().size()-1;is++){
		    			mSquare += e.getMSquareCoefs().get(is)+"*"+e.getMSquares().get(is)+"+";
		    		}
		    		mSquare += e.getMSquareCoefs().get(e.getMSquares().size()-1)+"*"+e.getMSquares().get(e.getMSquares().size()-1);
		    		
		    		mSquareS="";
		    		//printer.print(" \t\t\t "+e.getDf()+" \t\t ");
		    		for(int is=0;is<e.getMSquaresS().size()-1;is++){
		    			mSquareS += e.getMSquareCoefsS().get(is)+"*"+e.getMSquaresS().get(is)+"+";
		    		}
		    		mSquareS += e.getMSquareCoefsS().get(e.getMSquaresS().size()-1)+"*"+e.getMSquaresS().get(e.getMSquaresS().size()-1);
		    		
		    		if(e.getRem()){
		    			mSquareS="";
		    		}
		    		
		    		String splitName=e.getName();
		    		int cap;
		    		
		    		if(!splitName.contains("R")&&!splitName.contains("error")){
		    			cap = 1;
//		    			String str = "AB(2)CD(10)E(4)";
		    			int n;
		    			String next;
		    			int c;
		    			int pInd;
		    			String luName;
		    			ArrayList<Integer[]> ees = new ArrayList<Integer[]>();
		    			while (splitName.length()>0){
		    				Integer[] es = new Integer[4];
		    				n = splitName.substring(0, 1).toCharArray()[0];
		    				if(splitName.length()==1){
		    					c = 1;
		    					luName = splitName.substring(0, 1);
		    					splitName = splitName.substring(1);

//		    					System.out.println(name+"_"+c);
		    				}else{
			    				next = splitName.substring(1,2);
			    				if(next.equals("(")){
			    					pInd = splitName.indexOf(")");
			    					c = Integer.parseInt(splitName.substring(2,pInd));
			    					luName = splitName.substring(0, pInd+1);
			    					splitName = splitName.substring(pInd+1);
//			    					System.out.println(name+"_"+c);
			    				}else{
			    					luName = splitName.substring(0, 1);
			    					splitName = splitName.substring(1);
			    					c = 1;
//			    					System.out.println(name+"_"+c);
			    				}
		    				}
		    				for(Factor f:e.getFactors()){
		    					if(luName.equals(f.getName())){
		    						es[1] = f.getLevels();
//		    						if(i==138){
//		    							System.out.println(f.getName()+","+f.getLevels());
//		    							for(Factor ff:f.getNestedWithin()){
//		    								System.out.println("    "+ff.getName()+","+ff.getLevels());
//		    							}
//		    							
//		    						}
		    						break;
		    					}
		    				}
		    				cap*=c;
		    				es[0] = c;
//		    				es[1] = e.getLevel()/c;
		    				es[2] = es[0]*es[1];
		    				es[3] = n;
		    				
		    				ees.add(es);
		    			}
		    			
		    			int ool = cap*e.getLevel();
		    			splitName=e.getName();
		    			splitName = splitName.replace("(", "");
		    			splitName = splitName.replace(")", "");
		    			splitName = splitName.replaceAll("\\d","");
		    			
		    			SplitFactors elm = new SplitFactors();
		    			elm.setOc(cap);
		    			elm.setOl(e.getLevel());
		    			elm.setDf(e.getDf());
		    			elm.setEdf(e.getEdf());
		    			elm.setSf(ees);
		    			if(e.getTest().equals("e")){
		    				elm.setTtype(1);
		    			}else{
		    				elm.setTtype(0);
		    			}
		    			
//		    			Integer[] elm = new Integer[5];
//		    			
//		    			elm[0] = cap;
//		    			elm[1] = e.getLevel();
//		    			elm[2] = e.getDf();
//		    			elm[3] = e.getEdf();
//		    			if(e.getTest().equals("e")){
//		    				elm[4] = 1;
//		    			}else{
//		    				elm[4] = 0;
//		    			}
		    			if(splits.containsKey(e.getBases())){
		    				splits.get(e.getBases()).addElm(elm);
			    		}else{
			    			Splits split = new Splits(splitName, ool, delta, sigma, N);
			    			
			    			split.addElm(elm);
			    			splits.put(e.getBases(),split);
			    		}
		    		}
		    		
		    		
//		    		System.out.println(e.getName());
//		    		System.out.println(e.getBases());
//		    		System.out.println(e.getFactors());
//		    		System.out.println();
//		    		PowerEstimation.estimatedPower(splitFactors, N, delta, sigma);
		    		
		    		printer.printf("%-15s %-10s %-10s %-10s %-10s %-10s\n", name, e.getPosition(), e.getDf(), e.getEdf(), e.getTest(), mSquareS);
		    		printStr+=String.format("%-15s %-10s %-10s %-10s %-10s %-10s\n", name, e.getPosition(), e.getDf(), e.getEdf(), e.getTest(), mSquareS);
//		    		printer.printf("%-15s %-10s %-10s %-10s %-10s %-50s %-10s\n", name, e.getPosition(), e.getDf(), e.getEdf(), e.getTest(), mSquare, mSquareS);
//		    		printer.printf("%s,%s,%s,%s\n", name, e.getPosition(), e.getDf(), mSquare);

		    		
//		    		System.out.println();
		    		//printer.println();
		    	}
		    }
		    System.out.println();
		    printer.println();
		    printStr+=String.format("\n");
		    /*for (Splits s : splits.values()) {
		        PowerEstimation.estimatedPower(s);
		    }*/
//		    if(i==4){
		    boolean exact = true;
	    	HashMap<String,Double> powers = new HashMap<String,Double>();
		    for (Map.Entry<ArrayList<Integer>, Splits> entry : splits.entrySet()) {
//				    System.out.println(entry.getKey());
			    double estimatedPower = PowerEstimation.estimatedPower(entry.getValue());
			    powers.put(entry.getValue().getName(), estimatedPower);
			    if( estimatedPower==0.0){
			    	exact=false;
			    }
			    System.out.println(entry.getValue().getName()+": "+estimatedPower);
		    }
//		    }
			double effectiveness = getEffectiveness(powers);
			effectiveness = Math.round(effectiveness*1000.0)/1000.0;
		    //System.out.println("tlct:");
			    
		    System.out.println("Overall Effectiveness: "+ effectiveness);
		    printer.println("Overall Effectiveness: "+effectiveness);
		    printStr+=String.format("Oveall Effectiveness: "+effectiveness+"\n");
			
		    System.out.println("Total Level Changing Time: "+lct_lcc[0]);
		    printer.println("Expected Running Time: "+lct_lcc[0]);
		    printStr+=String.format("Expected Running Time: "+lct_lcc[0]+"\n");
		    
		    //System.out.println("tlcc:");
		    System.out.println("Total Level Changing Cost: "+lct_lcc[1]);
//		    printer.println("Total Level Changing Cost: "+lct_lcc[1]);
//		    System.out.println("value:" + value);
//		    printer.printf("Value: %.4f \n", value);
		    System.out.println("--------------");
		    printer.println("----------------------------");
		    printStr+=String.format("--------------------------------------------------------"+"\n");
		    if(exact){
		    	ps.write(i+1, effectiveness, lct_lcc[0], printStr);
		    }
		    
		    
		    writer.println(effectiveness+sep+lct_lcc[0]+sep+design);
		    
//		    writerS.println((i+1)+","+dValue+","+lct_lcc[0]+","+lct_lcc[1]+",\""+design+"\"");
		}
		writer.close();
		ps.print();
		//printer.println("\u03C3"); //sigma
		//printer.println("\u03A6"+"²"); //phi
		printer.close();
		System.out.println("done");
		
	}
	
	public static double getEffectiveness(HashMap<String,Double> powers){
		double effectiveness = 0;
		for (Map.Entry<String, Double> entry : powers.entrySet()) {
			effectiveness += entry.getValue()/(((double) entry.getKey().length()));
		}
		return effectiveness;
	}
}
