package main;

import java.util.ArrayList;
import java.util.List;

import utils.LevelChangeCost;
import utils.LevelChangeTime;
import utils.Organizer;
import utils.readCSV;
import model.Factor;
import model.Restriction;

public class Count {
	public static void main(String[] args){
		
		int replication = 2;
		
		List<String[]> infos = readCSV.readFile("factors");
		List<Factor> factors = Organizer.factorMapper(infos, replication); 
		
		infos = readCSV.readFile("designStructure");
		List<ArrayList<Restriction>> restrictions = Organizer.restrictionMapper(infos, factors);//it must make sure a higher level restriction has smaller level size
		
		int tlct = LevelChangeTime.tlct(factors, restrictions, replication);		
		int tlcc = LevelChangeCost.tlcc(factors, restrictions, replication);
		
		System.out.println(tlct);
		System.out.println(tlcc);
	}
}
