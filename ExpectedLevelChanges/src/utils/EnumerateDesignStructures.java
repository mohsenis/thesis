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

import model.Effect;
import model.Factor;
import model.Restriction;

public class EnumerateDesignStructures {
	
	public static ArrayList<String> stringDesigns;
	//public static ArrayList<String> removedDesigns;
	
	public static List<ArrayList<Restriction>> listDesignStructures(List<Factor> factors){
		
		stringDesigns = new ArrayList<String>();
		//removedDesigns = new ArrayList<String>();
		
		structuresString(factors, "");
		
		//showDesigns(stringDesigns);
		
		List<ArrayList<Restriction>> structures = removeDuplicates();
		
		//structures = removeReplicateRestrictions(structures);
		ArrayList<Integer> removeRests = new ArrayList<Integer>();
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
		}
		
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
		int i=0;
		for(String str: designs){
			System.out.println(++i);
			String[] strs = str.split(";");
			for(String s: strs){
				System.out.println(s);
			}
			System.out.println("------------");
		}
	}
	
	public static void structuresString(List<Factor> factors, String str){
		String str1;
		String str2;
		String str3;
		for(Factor f: factors){
			str1 = str;
			ArrayList<Integer> divisors;
//			if(f.getName().equals("R")){
//				divisors = new ArrayList<Integer>();
//				divisors.add(1);
//			}else{
				divisors = allDivisors(f.getLevels());
//			}
			
			for(Integer i: divisors){
				str2 = str1;
				for(int o=f.getMinOrder();o<=f.getMaxOrder();o++){
					str3 = str2;
					str3 += f.getName()+","+i+","+o;
					stringDesigns.add(str3);
					str3 += ";";
					
					List<Factor> newFactors = new ArrayList<Factor>();
					for(Factor ff:factors){
						Factor factor = new Factor(ff);
						if(ff.equals(f)){
							factor.setLevels(i);
							factor.setMinOrder(o+1);
							factor.setMaxOrder(o+1);
						}else{
							if(factor.getMinOrder()<o || factor.getMaxOrder()==1){
								factor.setMinOrder(o);
								factor.setMaxOrder(o+1);
							}
						}
						newFactors.add(factor);
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
		
		for(int i=0; i<factors.size(); i++){
			effects = new ArrayList<Effect>();
			effectsList.add(effects);
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
							newF = new Factor(f.getName()+"("+/*(newFactors.size()+1)*/r.getSize()+")",l/r.getSize(),f.getLct(),f.getLcc(),f.getValue());
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
				newF = new Factor(f.getName()+"("+/*(newFactors.size()+1)*/1+")",l,f.getLct(),f.getLcc(),f.getValue());
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
				nestedEffects.add(e);
				effectsList.get(0).add(e);
				
				allEffects.add(e);
				names.add(e.getName());
				effectMap.put(e.getName(), e);
			}
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
						b = true;
						break;
					}
				}
				if(b){
					break;
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
		
		List<float[]> lct_lcc_list = new ArrayList<float[]>();
		List<ArrayList<Restriction>> structures = listDesignStructures(factors);
		ArrayList<ArrayList<ArrayList<Effect>>> allEffects = new ArrayList<ArrayList<ArrayList<Effect>>>();
		ArrayList<ArrayList<Effect>> effects;
		
		//adding complete randomization
		structures.add(0,new ArrayList<Restriction>());
		
		System.out.println("Total number of design structures (including full randomization):");
		System.out.println(structures.size());
		System.out.println("---------------");
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
		PrintWriter writer = new PrintWriter("ExpectedLevelChanges/src/files/values.txt", "UTF-8");
		writer.println("value,time,cost");
		for(int i=0; i<structures.size();i++) {
			value = 0;
		    ArrayList<Restriction> rs = structures.get(i);
		    float[] lct_lcc = lct_lcc_list.get(i);
		    
		    //System.out.println("Structure:");
		    for(Restriction r: rs){
		    	System.out.println(r);
		    }
		    for(ArrayList<Effect> effs: allEffects.get(i)){
		    	for(Effect e: effs){
		    		System.out.print(e.getName());
		    		if(e.getRestricted()){
		    			System.out.print("-r");
		    			value -= e.getValue();
		    		}
		    		
		    		if(e.getNestedWithin().size()!=0){
		    			System.out.print("[");
		    			for(Effect we: e.getNestedWithin()){
		    				System.out.print(we.getName());
		    				System.out.print("-");
		    			}
		    			System.out.print("]");
		    		}
		    		
		    		System.out.print(", ");

	    			System.out.println("{"+e.getEffectLevel()+"}");
		    	}
		    }
		    System.out.println();
		    System.out.println("value:" + value);
		    //System.out.println("tlct:");
		    System.out.println(lct_lcc[0]);
		    //System.out.println("tlcc:");
		    System.out.println(lct_lcc[1]);
		    System.out.println("--------------");
		    
		    writer.println(value+","+lct_lcc[0]+","+lct_lcc[1]);
		}
		writer.close();
		System.out.println(structures.size());
	}
}
