package utils;

import java.util.ArrayList;
import java.util.List;

import model.Factor;
import model.Restriction;

public class LevelChangeTime {

	public static int tlct(List<Factor> factors, List<ArrayList<Restriction>> restrictions, int replication) {
		if(restrictions.size()==0){
			return randomBoxTime(factors);
		}
		
		List<Factor> newFactors = new ArrayList<Factor>();
		List<Factor> newFactorsB = new ArrayList<Factor>(); //including the one-leveled dominant factor 
		//newFactors.addAll(factors);
		for(Factor f: factors){
			newFactors.add((Factor) f.clone());
		}
		
		List<ArrayList<Restriction>> newRestrictions = new ArrayList<ArrayList<Restriction>>();
		newRestrictions.addAll(restrictions);
		
		List<Integer> removes = new ArrayList<Integer>();
		for(int i=1;i<newFactors.size();i++){
			if(newFactors.get(i).getLevels()==1){
				removes.add(i);
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
		if(newFactors.get(0).getLevels()==1){
			//newFactorsB.addAll(newFactors);
			for(Factor f: newFactors){
				newFactorsB.add((Factor) f.clone());
			}
			newFactors.remove(0);
		}
		
		ArrayList<Restriction> res = newRestrictions.remove(0);
		String name;
		Factor factor1=null;
		Factor factor2=null;
		int multiple = 1;
		for(Restriction r: res){
			name = r.getFactorName();
			
			for(int j=0; j<newFactors.size(); j++){
				if(newFactors.get(j).getName().equals(name)){
					factor1 = newFactors.get(j);
					if(newFactorsB.size()!=0){
						factor2 = newFactorsB.get(j+1);
					}
					break;
				}
			}
			multiple *= factor1.getLevels()/r.getSize();
			factor1.setLevels(r.getSize());
			if(newFactorsB.size()!=0){
				factor2.setLevels(r.getSize());
			}
		}
		for(Factor f: newFactors){
			f.setRep(newFactors, replication);
		}
		if(newFactorsB.size()!=0){
			for(Factor f: newFactorsB){
				f.setRep(newFactorsB, replication);
			}
		}
		
		if(newFactorsB.size()==0){
			return multiple*tlct(newFactors, newRestrictions, replication);
		}else{
			return (multiple-1)*tlct(newFactors, newRestrictions, replication) + tlct(newFactorsB, newRestrictions, replication);
		}
	}
	
	public static int randomBoxTime(List<Factor> factors){
		int lct = factors.get(0).getLct();
		int denom;
		int enom;
		
		for(int i=0; i<factors.size();i++){
			enom = factors.get(i).getRep()*(factors.get(i).getLevels()-1);
			denom = 1;
			for(int j=0; j<i; j++){
				denom *= factors.get(j).getLevels();
			}
			lct += (enom/denom)*factors.get(i).getLct();
		}
		return lct;
	}
}
