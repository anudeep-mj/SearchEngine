//Author: Anudeep Jayaram

package edu.asu.irs13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

class similPagesComp implements Comparator {

Map map;

public similPagesComp(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    if(map.get(o2).equals(map.get(o1)))
    	return 1;
    else
    	return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
}
}


public class similarPages {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {
		
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		
		Map<String,Double> DocVectorMap = new HashMap<String,Double>();
		Map<Integer, HashMap> DocMap = new HashMap<Integer, HashMap>();
		Map<String, Double> mapFromSimil = new HashMap<String, Double>();
//		similPagesComp comp=new similPagesComp(mapFromSimil);
//		Map<String,Double> TREEMap = new TreeMap(comp);
		Map<String, Double> mapFromSimilSorted = new HashMap<String, Double>();
		
		returnIDFmap idfmap = new returnIDFmap();
		Map<Integer, Double> IDFMap = idfmap.getTFIDF();
		Iterator<Entry<Integer, Double>> IDFMapiter = IDFMap.entrySet().iterator();
		int totaldocids[] = new int[50];
		
		int i = 0, a = 0;
		while(IDFMapiter.hasNext() && i!=50)
		{
			Map.Entry idfentry = (Map.Entry)IDFMapiter.next();
			Integer idfkey = (Integer) idfentry.getKey();
//			System.out.println(i);
			DocMap.put(idfkey, null);
			totaldocids[i++] = idfkey;
		}
		
//		System.out.println(DocMap);
		int totaldocslength = totaldocids.length;	
		DocMap = CreatingDocMap(r, DocMap);  //method for creating Doc Map which has the terms of all docs in hashmap which are in turn put in a hashmap.
		
		Scanner in = new Scanner(System.in);
		
		while(a <= 0 || a > 10)
		{
			System.out.println("Enter the document for which you need the similar pages: (choose between 1 - 10)");
			a = in.nextInt();
		}
		mapFromSimil = DocMap.get(totaldocids[a]);
		
		
//		System.out.println(mapFromSimil);
		
		mapFromSimilSorted = sortHashMapByValuesD(mapFromSimil); //sorts hashmap by values
//		System.out.println(mapFromSimilSorted);
		
		ArrayList<String> strKeys = new ArrayList<String>(mapFromSimilSorted.keySet());
//		System.out.println(strKeys);
		ArrayList<String> list = new ArrayList<String>();
		
		
		Iterator<String> it = strKeys.iterator();
		StringBuilder newInput = new StringBuilder();
		
		for(int j = 1; j<11; j++)
		{
			String s = strKeys.get(strKeys.size() - j);
			list.add(s);
			newInput.append(s + " ");
		}
		String newInputString = newInput.toString(); //new input string found
		newInputString = newInputString.trim();
		System.out.println(list);
		System.out.println(newInputString);
		
		
		long startSimilarTime = System.nanoTime();
		idfmap.getIDFstring(newInputString); // the new query is sent
		long endSimilarTime = System.nanoTime();
		long similarQuery =  ((endSimilarTime - startSimilarTime) /1000);
		System.out.println("\nTotal time taken to get the Similar Pages:" + similarQuery);
	}

	
	
	
	
	
	private static Map<Integer, HashMap> CreatingDocMap(IndexReader r,
			Map<Integer, HashMap> DocMap) throws IOException {

		TermEnum t = r.terms();
		while(t.next())
		{
			Term te = new Term("contents", t.term().text());
			TermDocs td = r.termDocs(te);
			
			while(td.next())
			{
				
				if(DocMap.containsKey(td.doc())) //this maintains the condition that the vectors are made only for the query docs.
				{
					HashMap<String, Double> TempMap = null;						

					if(DocMap.get(td.doc())==null)
					{
						TempMap = new HashMap<String, Double>();						
					}
					else
					{
						TempMap = DocMap.get(td.doc());
					}
																			
															
					if(TempMap.containsKey(te.text()))
					{
						TempMap.put(te.text(), (TempMap.get(te.text())+1));
					}
					else
					{
						TempMap.put(te.text(), (double) td.freq());
					}
					DocMap.put(td.doc(), TempMap);
				}
			}
		}		
		return DocMap;
	}
	

	
	public static LinkedHashMap sortHashMapByValuesD(Map<String, Double> mapFromSimil) {
		   List mapKeys = new ArrayList(mapFromSimil.keySet());
		   List mapValues = new ArrayList(mapFromSimil.values());
		   Collections.sort(mapValues);
		   Collections.sort(mapKeys);

		   LinkedHashMap sortedMap = 
		       new LinkedHashMap();

		   Iterator valueIt = mapValues.iterator();
		   while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		    Iterator keyIt = mapKeys.iterator();

		    while (keyIt.hasNext()) {
		        Object key = keyIt.next();
		        String comp1 = mapFromSimil.get(key).toString();
		        String comp2 = val.toString();

		        if (comp1.equals(comp2)){
		            mapFromSimil.remove(key);
		            mapKeys.remove(key);
		            sortedMap.put((String)key, (Double)val);
		            break;
		        }

		    }

		}
		return sortedMap;
		}
}



