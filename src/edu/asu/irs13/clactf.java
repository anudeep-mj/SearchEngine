package edu.asu.irs13;

import java.io.*;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import java.lang.*;
import java.util.*;

import javax.swing.text.Document;

class MyComparator implements Comparator {

Map map;

public MyComparator(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    if(map.get(o2).equals(map.get(o1)))
    	return 1;
    else
    	return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
}
}

public class clactf {
	
	public static void main(String[] args) throws Exception
	{
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		HashMap<Integer, Double> moddMap = new HashMap<Integer, Double>();
		HashMap<String, Double> modqMap = new HashMap<String, Double>();
		HashMap<Integer, Double> dMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> dqMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> tfMap = new HashMap<Integer, Double>();
		
		
		MyComparator comp=new MyComparator(tfMap);
		
		Map<Integer,Double> TREEMap = new TreeMap(comp);
		
		org.apache.lucene.document.Document d = r.document(22958);
		String url = d.getFieldable("path").stringValue();
		System.out.println("url: "+ url);
		
		String[] array;

		int j = r.maxDoc();
		TermEnum t = r.terms();
		double dq1 = 0;
		double idf;
		
		long endTime1 = 0;
		long endTime2 = 0;
		long endTime3 = 0;
		long endTime4 = 0;
		long endTime5 = 0;
		long endTime6 = 0;
		long endTime7 = 0;
		long startTime2 = 0;
		
		
		long startTime1 = System.nanoTime();
		while(t.next())
		{

			Term te = new Term("contents", t.term().text());
			TermDocs td = r.termDocs(te);

			if(r.docFreq(te)!= 0)
			{
			idf = Math.log(r.maxDoc()/r.docFreq(te));}
			
			while (td.next())
			{
				if(moddMap.containsKey(td.doc()))
					moddMap.put(td.doc(), moddMap.get(td.doc())+(Math.pow(td.freq(),2)));
				else
					moddMap.put(td.doc(), Math.pow(td.freq(),2));			
			}
		}
		endTime1 = System.nanoTime();
		long timeTFTABLE =  ((endTime1 - startTime1) /1000);
		System.out.println("time taken to compute the |d| of all the documents:" + timeTFTABLE );
		
		
		Scanner sc = new Scanner(System.in);
		String str = "";
		System.out.print("query(enter: quit to exit)> ");

		while(!(str = sc.nextLine()).equals("quit"))
		{
			startTime2 = System.nanoTime();
			array = str.split(" "); //array has all the terms of the query here.
			
			for(int k=0;k<array.length;k++)
			{
				if(modqMap.containsKey(array[k]))
					modqMap.put(array[k], (modqMap.get(array[k])+ 1.0) );
				else
					modqMap.put(array[k], (double) 1);
			}
			
			Iterator it = modqMap.entrySet().iterator();
			double mdq = 0;
			while (it.hasNext())
			{
		        Map.Entry pairs = (Map.Entry)it.next();
		        mdq = mdq + Math.pow((double) pairs.getValue(), 2);
		    }
			
			System.out.println(mdq);			
		
			Double[] qarray = new Double[modqMap.size()];
			Double[] darray = new Double[dMap.size()];
		
			int index = 0;
			int index1 = 0;
		
			long startTime4 = System.nanoTime();

			for (Map.Entry<String, Double> mapEntry : modqMap.entrySet()) 
			{
				Term term = new Term("contents", mapEntry.getKey());
				TermDocs tdocs = r.termDocs(term);
				while (tdocs.next())
				{
					if(dMap.containsKey(tdocs.doc()))
					{
						dMap.put(tdocs.doc(), (dMap.get(tdocs.doc())+mapEntry.getValue()*tdocs.freq()));
					}
					else
						dMap.put(tdocs.doc(), mapEntry.getValue()*tdocs.freq());
				}
			
			}
			

			
			endTime4 = System.nanoTime();
			long timetocomputedq =  ((endTime4 - startTime4) /1000);
			System.out.println("time taken to compute d.q:" + timetocomputedq );

			
			long startTime5 = endTime4;
			
			for(int m=0; m<qarray.length;m++)
			{
				Term term1 = new Term("contents", array[m]);
				TermDocs tdcs = r.termDocs(term1);
				while(tdcs.next())
				{
					tfMap.put(tdcs.doc(), dMap.get(tdcs.doc())/Math.sqrt((moddMap.get(tdcs.doc())))*Math.sqrt((mdq)));
				}
			

			}
			
			endTime5 = System.nanoTime();
			long timetocomputecostheta =  ((endTime5 - startTime5) /1000);
			System.out.println("time taken to compute cos theta similarity:" + timetocomputecostheta );

			
			endTime2 = endTime5;
			long timetocomputethequery =  ((endTime2 - startTime2) /1000);
			System.out.println("time taken to compute all the documents relevant to the query(without sorting):" + timetocomputethequery );


			long startTime3 = System.nanoTime();
			TREEMap.putAll(tfMap);
			endTime3 = System.nanoTime();
			long timetosortquery =  ((endTime3 - startTime3) /1000);
			System.out.println("time taken to sort all the documents relevant to the query:" + timetosortquery );
//			System.out.println(TREEMap.toString());
			
			System.out.println("Relevant Documents are: \n");
			Set set = TREEMap.entrySet(); 
			// Get an iterator 
			Iterator i1 = set.iterator(); 
			int i2=0;
			// Display elements 
			while(i1.hasNext()) 
			{ 
				Map.Entry me = (Map.Entry)i1.next(); 
				System.out.print(me.getKey() + " "); 
				i2++;
//				System.out.println(me.getValue()); //to also print the values
//				if(i2 == 10)			//in case we want only the top 10 values
//				{
//					break;
//				}
			} 

			
//			System.out.println(timetocomputecostheta); //in case we want to print the times at the end
//			System.out.println(timetocomputethequery);
//			System.out.println(timetosortquery);
			
			modqMap.clear();
			dMap.clear();
			dqMap.clear();
			tfMap.clear();
			TREEMap.clear();
			mdq = 0;
		}
		
		
	}
	
}

			
			
					