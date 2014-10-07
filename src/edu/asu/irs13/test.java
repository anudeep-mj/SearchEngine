package edu.asu.irs13;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
public class test {
//	public static void writeFile(String text) {
//		try {
/*			String a = new String();
			String b = new String();
			FileWriter maxFrequencyTXT = new FileWriter("out3.txt");
			BufferedWriter out = new BufferedWriter(maxFrequencyTXT);
		//	System.out.println("hi");
		//	maxFrequencyTXT.write(a+" "+b+"\n"+"hi");
			out.write(a+" "+b+"\n");
			maxFrequencyTXT.close();
		} 
		catch (Exception e) {
		}
	}*/
	public static void main(String[] args) throws Exception
	{
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		int i = 0;
		int j = 0;

		TermEnum t = r.terms();
		
		HashMap<String, Integer> TempAddMap = new HashMap<String, Integer>();
		HashMap<String, Integer> TempAddMap1 = new HashMap<String, Integer>();
		HashMap<String, Integer> TempAddMap2 = new HashMap<String, Integer>();
		
		TempAddMap1.put("a", 1);
		TempAddMap1.put("b", 1);
		TempAddMap1.put("c", 1);
		TempAddMap1.put("d", 1);
		
		TempAddMap2.put("a", 1);
		TempAddMap2.put("b", 1);
		TempAddMap2.put("e", 1);
		TempAddMap2.put("f", 1);

		System.out.println(TempAddMap1);
		System.out.println(TempAddMap2);		
		
		
		TempAddMap.putAll(TempAddMap2);
		Iterator tempit = TempAddMap1.entrySet().iterator();
		while(tempit.hasNext())
		{
			System.out.println("here");
			Map.Entry entry = (Map.Entry)tempit.next();
	        if(TempAddMap2.containsKey(entry.getKey()))
	        {
	        	String key = (String) entry.getKey();
	        	Integer map1val = TempAddMap1.get(entry.getKey());
	        	Integer map2val = TempAddMap2.get(entry.getKey());
	        	TempAddMap.put((String) entry.getKey(), (map1val+map2val));
	        }
	        else
	        {
	        	Integer map1val = TempAddMap1.get(entry.getKey());
	        	TempAddMap.put((String) entry.getKey(), map1val);
	        }
		}
		
				
		
		System.out.println(TempAddMap);
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		FileWriter maxFrequencyTXT = new FileWriter("out3.txt");

		while(t.next())
		{
			if (i >= 0)
			{ 
			//System.out.println(i+++" " + t.term().text());
				String st1= t.term().text();
				
				maxFrequencyTXT.write(j+++" "+st1+"\r\n");
//				writeFile("["+j+++"] " + st1);
			
			}
		}
		System.out.println("done");
		
		maxFrequencyTXT.close();
*/	}
}