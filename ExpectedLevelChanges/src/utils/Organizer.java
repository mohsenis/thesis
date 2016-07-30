package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Factor;
import model.Restriction;

public class Organizer {
	
	public static List<Factor> factorMapper(List<String[]> infos, int replication){
		List<Factor> factors = new ArrayList<Factor>();

		for(String[] info: infos){
			factors.add(new Factor(info[0],Integer.parseInt(info[1]),Integer.parseInt(info[2]),Integer.parseInt(info[3]),Integer.parseInt(info[4]),Integer.parseInt(info[5])));
		}
		
		Collections.sort(factors, new Comparator<Factor>(){
			public int compare(Factor f1, Factor f2){
				return -(f1.getLct() - f2.getLct());
			}
		});
		
		for(Factor f: factors){
			f.setRep(factors,replication);
		}
		
		return factors;
	}
	
	public static List<ArrayList<Restriction>> restrictionMapper(List<String[]> infos, List<Factor> factors){
		Map<Integer,ArrayList<Restriction>> restrictionMap = new HashMap<Integer,ArrayList<Restriction>>();
		ArrayList<Restriction> r;
		
		for(String[] info: infos){
			if(restrictionMap.containsKey(Integer.parseInt(info[2]))){
				restrictionMap.get(Integer.parseInt(info[2])).add(new Restriction(info[0],Integer.parseInt(info[1]),Integer.parseInt(info[2])));
			}else{
				r = new ArrayList<Restriction>();
				r.add(new Restriction(info[0],Integer.parseInt(info[1]),Integer.parseInt(info[2])));
				restrictionMap.put(Integer.parseInt(info[2]), r);
			}
		}
		
		List<ArrayList<Restriction>> restrictions = new ArrayList<ArrayList<Restriction>>();
		for(int i=0; i<restrictionMap.size();i++){
			restrictions.add(restrictionMap.get(i+1));
		}
		
		return restrictions;
	}
}
