package utils;

import java.util.ArrayList;
import java.util.List;

import model.Factor;
import model.Restriction;

public class LevelChangeCost {

	public static float tlcc(List<Factor> factors, List<ArrayList<Restriction>> restrictions, int replication) {
		if(restrictions.size()==0){
			return randomBoxCost(factors);
		}
		
		List<Factor> newFactors = new ArrayList<Factor>();
		//newFactors.addAll(factors);
		for(Factor f: factors){
			newFactors.add((Factor) f.clone());
		}
		
		List<ArrayList<Restriction>> newRestrictions = new ArrayList<ArrayList<Restriction>>();
		newRestrictions.addAll(restrictions);
		
		List<Integer> removes = new ArrayList<Integer>();
		int constant = 0;
		for(int i=0;i<newFactors.size();i++){
			if(newFactors.get(i).getLevels()==1){
				removes.add(i);
				constant+=newFactors.get(i).getLcc();
			}
		}
		int ii;
		for(int i=removes.size()-1;i>-1;i--){
			ii = removes.get(i);
			newFactors.remove(ii);
		}
		/*for(Integer i: removes){
			newFactors.remove(i);
		}*/
		
		ArrayList<Restriction> res = newRestrictions.remove(0);
		List<Integer> splits = new ArrayList<Integer>();
		List<Integer> newLs = new ArrayList<Integer>();
		List<Integer> lccs = new ArrayList<Integer>();
		String name;
		Factor factor=null;
		int multiple = 1;
		int restrictedIndex = 0;
		for(Factor f: newFactors){
			splits.add(1);
			newLs.add(f.getLevels());
			lccs.add(f.getLcc());
		}
		
		for(Restriction r: res){
			name = r.getFactorName();
			
			for(int j=0; j<newFactors.size(); j++){
				if(newFactors.get(j).getName().equals(name)){
					factor = newFactors.get(j);
					restrictedIndex = j;
					break;
				}
			}
			multiple *= factor.getLevels()/r.getSize();
			splits.set(restrictedIndex, factor.getLevels()/r.getSize());
			factor.setLevels(r.getSize());
			newLs.set(restrictedIndex, factor.getLevels());
			//lccs.add(factor.getLcc());
		}
		
		if(res.size()==1){
			splits.remove(restrictedIndex);
			newLs.remove(restrictedIndex);
			lccs.remove(restrictedIndex);
		}
		int n;
		for(int i=0;i<splits.size();i++){
			n = splits.get(i);
			splits.set(i, multiple/n);
		}
		
		for(Factor f: newFactors){
			f.setRep(newFactors, replication);
		}
		float reponse = constant + multiple*tlcc(newFactors, newRestrictions, replication);
		//if(splits.size()>1){
		for(int i=0; i<splits.size();i++){
			reponse -= ((float) 1/newLs.get(i))*(splits.get(i)-1)*lccs.get(i);
		}
			
		//}
		return reponse;
	}
	
	public static float randomBoxCost(List<Factor> factors){
		int lcc = 0;
		for(Factor f: factors){
			lcc += f.getLcc();
		}
		
		for(Factor f: factors){
			lcc += f.getRep()*(f.getLevels()-1)*f.getLcc();
		}
		return (float) lcc;
	}
}
