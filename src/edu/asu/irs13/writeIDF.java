package edu.asu.irs13;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import java.lang.*;
import java.util.*;

class MyComparator1 implements Comparator {

Map map;

public MyComparator1(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    if(map.get(o2).equals(map.get(o1)))
    	return 1;
    else
    	return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
}
}


public class writeIDF{
	
	public List<TreeMap<Integer, Double>> getTFIDF() throws CorruptIndexException, IOException
	{

		List<TreeMap<Integer, Double>> finalList = new ArrayList<TreeMap<Integer, Double>>();
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		HashMap<Integer, Double> moddMap = new HashMap<Integer, Double>();
		HashMap<String, Double> modqMap = new HashMap<String, Double>();
		HashMap<String, Double> modqIDFMap = new HashMap<String, Double>();
		HashMap<Integer, Double> dMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> dqMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> IDFMap = new HashMap<Integer, Double>();
		HashMap<String, Double> idfMap1 = new HashMap<String, Double>();
		
		MyComparator1 comp=new MyComparator1(IDFMap);
		MyComparator1 comp1=new MyComparator1(idfMap1);

		Map<Integer,Double> TREEMap = new TreeMap(comp);
		Map<String,Double> TREEMap1 = new TreeMap(comp1);

		
//		HashMap<String,String> idfMap = new HashMap<String,String>();
		String[] array;
		System.out.println("The number of documents in this index is: " + r.maxDoc());
		int i = 0;
		int j = 0;
		double k = 0;
		double idf = 0;
		int m = 0;
		int n =0;
		String str = new String();
		TermEnum t = r.terms();
		TermEnum t1 = r.terms();
		String a = new String();
		String b = new String();
		double dq1 = 0;

		long endTime1 = 0;
		long endTime2 = 0;
		long endTime3 = 0;
		long endTime4 = 0;
		long endTime5 = 0;
		long endTime6 = 0;
		long endTime7 = 0;

		
		
		
		long startTime1 = System.nanoTime();
		while(t.next())
		{
			if (i >= 0)
			{ 
				String st1= t.term().text();
				Term term = new Term("contents", st1);
				
				if(r.docFreq(term)!= 0)
				{
					idf = Math.log(r.maxDoc()/r.docFreq(term));
				}
				
				idfMap1.put(st1,idf);
			}
		}
		
		
		
		endTime1 = System.nanoTime();
		long timeneededIDFTABLE =  ((endTime1 - startTime1) /1000);
		System.out.println("Time needed to computer the IDF values of all the terms: "+ timeneededIDFTABLE);

		long startTime2 = System.nanoTime();
		while(t1.next())
		{

			Term te = new Term("contents", t1.term().text());
			TermDocs td = r.termDocs(te);

			
			
			while (td.next())
			{
				if(moddMap.containsKey(td.doc()))
					moddMap.put(td.doc(), moddMap.get(td.doc())+(Math.pow((td.freq()*(idfMap1.get(t1.term().text()))),2)));
				else
					moddMap.put(td.doc(), Math.pow(td.freq(),2));	
			}
		}
		
		endTime2 = System.nanoTime();
		long timeneededMODDTABLE =  ((endTime2 - startTime2) /1000);
		System.out.println("time taken to compute the |d| of all the documents:" + timeneededMODDTABLE );
		
		Scanner sc = new Scanner(System.in);
		str = "";
		System.out.print("query(enter: quit to exit)> ");
		while(!(str = sc.nextLine()).equals("quit"))
		{

			long startTime3 = System.nanoTime();
			array = str.split(" "); //array has all the terms of the query here.
			
			for(int x=0;x<array.length;x++)
			{
				if(modqIDFMap.containsKey(array[x]))
					modqIDFMap.put(array[x], (modqIDFMap.get(array[x])+ 1.0)*(idfMap1.get(array[x])));
				else
					modqIDFMap.put(array[x], (double) 1);
			}
			
			for(int y=0;y<array.length;y++)
			{
				if(modqMap.containsKey(array[y]))
					modqMap.put(array[y], (modqMap.get(array[y])+ 1.0));
				else
					modqMap.put(array[y], (double) 1);
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
			
			
			
			for (Map.Entry<String, Double> mapEntry : modqIDFMap.entrySet()) 
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
			
				
						
			int temp = 0;
			double temp1 = 0;
			double temp2 = 0;
			
			long startTime5 = System.nanoTime();
			System.out.println("----");

			for(m=0; m<qarray.length;m++)
			{
				Term term1 = new Term("contents", array[m]);
				TermDocs tdcs = r.termDocs(term1);
				while(tdcs.next())
				{
					temp = tdcs.doc();
					temp2 = Math.sqrt(moddMap.get(temp));
					temp1 = (temp2)*(Math.sqrt(mdq));
//					System.out.println("temp:"+temp+" dMap.get(temp):"+dMap.get(temp)+" temp1:"+temp1);
					IDFMap.put(temp, dMap.get(temp)/temp1);

				}

			}
			
//			System.out.println("-----");
			
			endTime5 = System.nanoTime();
			long timetocomputecostheta =  ((endTime5 - startTime5) /1000);
			System.out.println("time taken to compute cos theta similarity:" + timetocomputecostheta );
			endTime3 = System.nanoTime();
			long timetocomputethequery =  ((endTime3 - startTime3) /1000);
			System.out.println("time taken to compute all the documents relevant to the query(without sorting):" + timetocomputethequery );
			
			System.out.println("done");
			
			long startTime4 = System.nanoTime();
			TREEMap.putAll(IDFMap);
			endTime4 = System.nanoTime();
			long timetosortquery =  ((endTime4 - startTime4) /1000);
			System.out.println("time taken to sort all the documents relevant to the query:" + timetosortquery );

			System.out.println("Relevant Documents are: \n");
			Set set = TREEMap.entrySet();
			// Get an iterator 
			Iterator i1 = set.iterator();
			int i2=0;
			// Display elements 
			while(i1.hasNext()) 
			{ 
				Map.Entry me = (Map.Entry)i1.next(); 
				System.out.print(me.getKey()+ " "); 
				i2++;
				if(i2 == 10)
				{
					break;
				}
			} 
			TreeMap<Integer, Double> tm = new TreeMap<Integer, Double>(comp);
			tm.putAll(TREEMap);
			finalList.add(tm);
			modqMap.clear();
			dMap.clear();
			dqMap.clear();
			IDFMap.clear();
			TREEMap.clear();
			modqIDFMap.clear();
			mdq = 0;
		
		}
		return finalList;

//		TREEMap1.putAll(idfMap1);
//		Set set1 = TREEMap1.entrySet();
//		// Get an iterator 
//		Iterator i11 = set1.iterator();
//		int i21=0;
//		// Display elements 
//		while(i11.hasNext()) 
//		{ 
//			Map.Entry me = (Map.Entry)i11.next(); 
//			System.out.print(me.getKey()+ " "); 
//			i21++;
//			if(i21 == 10)
//			{
//				break;
//			}
//		} 

						
	
	}
	public static void main(String[] args) throws Exception
	{
		writeIDF tfidf = new writeIDF();
		tfidf.getTFIDF();
		String s = new String();
//		tfidf.getIDFstring(s);
	}
	
	public List<TreeMap<Integer, Double>> getIDFstring(String s) throws CorruptIndexException, IOException {
		


		List<TreeMap<Integer, Double>> finalList = new ArrayList<TreeMap<Integer, Double>>();
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		HashMap<Integer, Double> moddMap = new HashMap<Integer, Double>();
		HashMap<String, Double> modqMap = new HashMap<String, Double>();
		HashMap<String, Double> modqIDFMap = new HashMap<String, Double>();
		HashMap<Integer, Double> dMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> dqMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> IDFMap = new HashMap<Integer, Double>();
		HashMap<String, Double> idfMap1 = new HashMap<String, Double>();
		
		MyComparator1 comp=new MyComparator1(IDFMap);
		MyComparator1 comp1=new MyComparator1(idfMap1);

		Map<Integer,Double> TREEMap = new TreeMap(comp);
		Map<String,Double> TREEMap1 = new TreeMap(comp1);

		
//		HashMap<String,String> idfMap = new HashMap<String,String>();
		String[] array;
//		System.out.println("The number of documents in this index is: " + r.maxDoc());
		int i = 0;
		int j = 0;
		double k = 0;
		double idf = 0;
		int m = 0;
		int n =0;
		String str = new String();
		TermEnum t = r.terms();
		TermEnum t1 = r.terms();
		String a = new String();
		String b = new String();
		double dq1 = 0;

		long endTime1 = 0;
		long endTime2 = 0;
		long endTime3 = 0;
		long endTime4 = 0;
		long endTime5 = 0;
		long endTime6 = 0;
		long endTime7 = 0;

		
		
		
		long startTime1 = System.nanoTime();
		while(t.next())
		{
			if (i >= 0)
			{ 
				String st1= t.term().text();
				Term term = new Term("contents", st1);
				
				if(r.docFreq(term)!= 0)
				{
					idf = Math.log(r.maxDoc()/r.docFreq(term));
				}
				
				idfMap1.put(st1,idf);
			}
		}
		
		
		
		endTime1 = System.nanoTime();
		long timeneededIDFTABLE =  ((endTime1 - startTime1) /1000);
		System.out.println("Time needed to computer the IDF values of all the terms: "+ timeneededIDFTABLE);

		long startTime2 = System.nanoTime();
		while(t1.next())
		{

			Term te = new Term("contents", t1.term().text());
			TermDocs td = r.termDocs(te);

			
			
			while (td.next())
			{
				if(moddMap.containsKey(td.doc()))
					moddMap.put(td.doc(), moddMap.get(td.doc())+(Math.pow((td.freq()*(idfMap1.get(t1.term().text()))),2)));
				else
					moddMap.put(td.doc(), Math.pow(td.freq(),2));	
			}
		}
		
		endTime2 = System.nanoTime();
		long timeneededMODDTABLE =  ((endTime2 - startTime2) /1000);
		System.out.println("time taken to compute the |d| of all the documents:" + timeneededMODDTABLE );
		
		Scanner sc = new Scanner(System.in);
		str = s;
//		System.out.print("query(enter: quit to exit)> ");
	
		

			long startTime3 = System.nanoTime();
			array = str.split(" "); //array has all the terms of the query here.
			
			for(int x=0;x<array.length;x++)
			{
				if(modqIDFMap.containsKey(array[x]))
					modqIDFMap.put(array[x], (modqIDFMap.get(array[x])+ 1.0)*(idfMap1.get(array[x])));
				else
					modqIDFMap.put(array[x], (double) 1);
			}
			
			for(int y=0;y<array.length;y++)
			{
				if(modqMap.containsKey(array[y]))
					modqMap.put(array[y], (modqMap.get(array[y])+ 1.0));
				else
					modqMap.put(array[y], (double) 1);
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
			
			
			
			for (Map.Entry<String, Double> mapEntry : modqIDFMap.entrySet()) 
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
			
				
						
			int temp = 0;
			double temp1 = 0;
			double temp2 = 0;
			
			long startTime5 = System.nanoTime();
			System.out.println("----");

			for(m=0; m<qarray.length;m++)
			{
				Term term1 = new Term("contents", array[m]);
				TermDocs tdcs = r.termDocs(term1);
				while(tdcs.next())
				{
					temp = tdcs.doc();
					temp2 = Math.sqrt(moddMap.get(temp));
					temp1 = (temp2)*(Math.sqrt(mdq));
//					System.out.println("temp:"+temp+" dMap.get(temp):"+dMap.get(temp)+" temp1:"+temp1);
					IDFMap.put(temp, dMap.get(temp)/temp1);

				}

			}
			
//			System.out.println("-----");
			
			endTime5 = System.nanoTime();
			long timetocomputecostheta =  ((endTime5 - startTime5) /1000);
			System.out.println("time taken to compute cos theta similarity:" + timetocomputecostheta );
			endTime3 = System.nanoTime();
			long timetocomputethequery =  ((endTime3 - startTime3) /1000);
			System.out.println("time taken to compute all the documents relevant to the query(without sorting):" + timetocomputethequery );
			
//			System.out.println("done");
			
			long startTime4 = System.nanoTime();
			TREEMap.putAll(IDFMap);
			endTime4 = System.nanoTime();
			long timetosortquery =  ((endTime4 - startTime4) /1000);
			System.out.println("time taken to sort all the documents relevant to the query:" + timetosortquery );

			System.out.println("Relevant Documents are: \n");
			Set set = TREEMap.entrySet();
			// Get an iterator 
			Iterator i1 = set.iterator();
			int i2=0;
			// Display elements 
			while(i1.hasNext()) 
			{ 
				Map.Entry me = (Map.Entry)i1.next(); 
				System.out.print(me.getKey()+ " "); 
				i2++;
				if(i2 == 10)
				{
					break;
				}
			} 
			TreeMap<Integer, Double> tm = new TreeMap<Integer, Double>(comp);
			tm.putAll(TREEMap);
			finalList.add(tm);
			modqMap.clear();
			dMap.clear();
			dqMap.clear();
			IDFMap.clear();
			TREEMap.clear();
			modqIDFMap.clear();
			mdq = 0;
		
		
	
//		TREEMap1.putAll(idfMap1);
//		Set set1 = TREEMap1.entrySet();
//		// Get an iterator 
//		Iterator i11 = set1.iterator();
//		int i21=0;
//		// Display elements 
//		while(i11.hasNext()) 
//		{ 
//			Map.Entry me = (Map.Entry)i11.next(); 
//			System.out.print(me.getKey()+ " "); 
//			i21++;
//			if(i21 == 10)
//			{
//				break;
//			}
//		} 

						
			return finalList;

	
		
	}
	

}
