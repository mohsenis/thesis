package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PrintSorted {
	private int total;
	private TreeMap<Entry<Integer,Double>,Entry<Float,String>> writer = new TreeMap<Entry<Integer,Double>,Entry<Float,String>>(new eComparator());
	private ArrayList<String> finalList = new ArrayList<String>();
	
	class eComparator implements Comparator<Entry<Integer,Double>>
    {
        public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
        	if (e1.getValue() < e2.getValue()){
        		return 1;
        	}else{
        		return -1;
        	}
		}
    }
	
	public void setTotal(int total){
		this.total = total;
	}
	
	public int getTotal(){
		return this.total;
	}
	
	public void write(int index, double effectiveness, float time, String str){
		Entry<Integer, Double> key = new AbstractMap.SimpleEntry<>(index, effectiveness);
		Entry<Float,String> value = new AbstractMap.SimpleEntry<>(time, str);
		writer.put(key, value);
	}
	
	public void getFinalList(){
		float maxT = 100000;
		for(Entry<Entry<Integer, Double>, Entry<Float,String>> entry : writer.entrySet()) {
			System.out.println(entry.getKey().getValue()+" , "+entry.getValue().getKey());
		}
		for(Entry<Entry<Integer, Double>, Entry<Float,String>> entry : writer.entrySet()) {
			  float time = entry.getValue().getKey();
			  String str = entry.getValue().getValue();
			  
			  if(time<maxT){
				  maxT = time;
				  this.finalList.add(str);
//				  System.out.println(str);
			  }
		}
	}
	
	public void print() throws FileNotFoundException, UnsupportedEncodingException{
		getFinalList();
		PrintWriter printer = new PrintWriter("C:/Users/mohse/git/thesis/ExpectedLevelChanges/src/files/finals.txt", "UTF-8");
		
		printer.println("Total number of design structures: "+total);
		printer.println("Total number of selected design structures: "+finalList.size());
		printer.println("------------------------------");
		printer.println();
		
		for(String str: finalList){
			printer.println(str);
			printer.println();
		}
		
		printer.close();
	}
	
}
