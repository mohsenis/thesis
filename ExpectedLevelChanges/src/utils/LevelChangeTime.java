package utils;

import java.util.ArrayList;
import java.util.List;

import model.Factor;
import model.Restriction;

public class LevelChangeTime {

	public static float tlct(List<Factor> factors, List<ArrayList<Restriction>> restrictions, int replication) {
		if(restrictions.size()==0){
			return randomBoxTime(factors);
		}
		
		boolean firstFactorSingle = false;
		boolean firstFactorRestrictedOnly = true;
		boolean secondFactorRestrictedOnly = true;
		int firstFactorL;
		int secondFactorL=0;
		int firstFactorSplit = 0;
		int secondFactorSplit = 0;
		
		List<ArrayList<Restriction>> newRestrictions = new ArrayList<ArrayList<Restriction>>();
		newRestrictions.addAll(restrictions);
		List<Factor> newFactors = new ArrayList<Factor>();
		List<Factor> newFactorsB = new ArrayList<Factor>(); //not including the first dominant factor 
		List<Factor> newFactorsC = new ArrayList<Factor>(); //not including the first and second dominant factors
		
		for(Factor f: factors){
			newFactors.add((Factor) f.clone());
		}
		
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
		
		if(newFactors.get(0).getLevels()==1){
			firstFactorSingle = true;
		}
		for(Factor f: newFactors){
			newFactorsB.add((Factor) f.clone());
			newFactorsC.add((Factor) f.clone());
		}
		newFactorsB.remove(0);
		newFactorsC.remove(0);
		if(newFactorsC.size()!=0){
			newFactorsC.remove(0);
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
					if(j!=0){
						factor2 = newFactorsB.get(j-1);
						firstFactorRestrictedOnly = false;
					}
					if(j==0){
						firstFactorSplit = factor1.getLevels()/r.getSize();
					}
					if(j!=1){
						secondFactorRestrictedOnly = false;
					}
					if(j==1){
						secondFactorSplit = factor1.getLevels()/r.getSize();
					}
					break;
				}
			}
			if(factor1!=null){
				multiple *= factor1.getLevels()/r.getSize();
				factor1.setLevels(r.getSize());
			}
			
			if(factor2!=null){
				factor2.setLevels(r.getSize());
				factor2 = null;
			}
		}
		
		if(firstFactorSplit==0){
			firstFactorSplit=multiple;
		}else{
			firstFactorSplit = multiple/firstFactorSplit;
		}
		if(secondFactorSplit==0){
			secondFactorSplit=1;
		}else{
			secondFactorSplit = multiple/secondFactorSplit;
		}
		
		for(Factor f: newFactors){
			f.setRep(newFactors, replication);
		}
		for(Factor f: newFactorsB){
			f.setRep(newFactorsB, replication);
		}
		firstFactorL = newFactors.get(0).getLevels();
		if(newFactorsB.size()!=0){
			secondFactorL = newFactors.get(1).getLevels();
		}
		if(!firstFactorSingle){
			//System.out.println(((float) 1/firstFactorL)*((firstFactorSplit)-1)*(newFactors.get(0).getLct()-newFactors.get(1).getLct()));

			if(!firstFactorRestrictedOnly){
				float response = multiple*tlct(newFactors, newRestrictions, replication)-((float) 1/firstFactorL)*(firstFactorSplit-1)*(newFactors.get(0).getLct()-newFactors.get(1).getLct());
				return response;
			}else{
				return multiple*tlct(newFactors, newRestrictions, replication);
			}
		}else{
			float response = tlct(newFactors, newRestrictions, replication);
			if(!secondFactorRestrictedOnly){
				response += (multiple-1)*tlct(newFactorsB, newRestrictions, replication)-((float) 1/secondFactorL)*(secondFactorSplit-1)*(newFactors.get(1).getLct()-newFactors.get(2).getLct());
			}else{
				response += (multiple-1)*tlct(newFactorsB, newRestrictions, replication);
			}
			return response;
		}
	}///////////
	
	public static float randomBoxTime(List<Factor> factors){
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
		return (float) lct;
	}
}
