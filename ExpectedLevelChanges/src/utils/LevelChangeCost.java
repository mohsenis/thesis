package utils;

import java.util.ArrayList;
import java.util.List;

import model.Factor;
import model.Restriction;

public class LevelChangeCost {

	public static int tlcc(List<Factor> factors, List<ArrayList<Restriction>> restrictions, int replication) {
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
		String name;
		Factor factor=null;
		int multiple = 1;
		for(Restriction r: res){
			name = r.getFactorName();
			
			for(int j=0; j<newFactors.size(); j++){
				if(newFactors.get(j).getName().equals(name)){
					factor = newFactors.get(j);
					break;
				}
			}
			multiple *= factor.getLevels()/r.getSize();
			factor.setLevels(r.getSize());
		}
		for(Factor f: newFactors){
			f.setRep(newFactors, replication);
		}
		
		return constant + multiple*tlcc(newFactors, newRestrictions, replication);
	}
	
	public static int randomBoxCost(List<Factor> factors){
		int lcc = 0;
		for(Factor f: factors){
			lcc += f.getLcc();
		}
		
		for(Factor f: factors){
			lcc += f.getRep()*(f.getLevels()-1)*f.getLcc();
		}
		return lcc;
	}
}
