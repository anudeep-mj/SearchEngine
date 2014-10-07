//Author: Anudeep Jayaram

package edu.asu.irs13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class kmeans2 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {
		
		kmeans2 km = new kmeans2();
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		
		
		Map<String,Integer> terms = new HashMap<String,Integer>();
	    TermEnum termEnum = r.terms(new Term("contents"));
	    int postn = 0;
	    while (termEnum.next()) {
	      Term term = termEnum.term();
	      if (! "contents".equals(term.field())) 
	        break;
	      terms.put(term.text(), postn++);
	    }
		
	    
//		Map<String,Double> DocVectorMap = new HashMap<String,Double>();
		Map<Integer, HashMap> DocMap = new HashMap<Integer, HashMap>();
		
		
		
		returnIDFmap idfmap = new returnIDFmap();
		Map<Integer, Double> IDFMap = idfmap.getTFIDF(); //calls the tf/idf function from project part 1
		Iterator<Entry<Integer, Double>> IDFMapiter = IDFMap.entrySet().iterator();
		
		int totaldocids[] = new int[50];
		
		ArrayList centroidlist1 = new ArrayList();
		ArrayList centroidlist2 = new ArrayList();

		
		int i = 0;
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
		
		DocMap = km.CreatingDocMap(r, DocMap);  //method to create the DocMap which contains all the documents ids and its respective term maps
		
				
//		System.out.println(DocMap);
		int firstVectorDoc = totaldocids[0];
		int secondVectorDoc = totaldocids[1];
		
		int kval = 1;
		
		System.out.println("K value: " + kval); //give k value here.
		
		
		HashMap<String, Double> first_map = DocMap.get(totaldocids[0]);
		HashMap<String, Double> second_map = DocMap.get(totaldocids[1]);
		HashMap<String, Double> third_map = DocMap.get(totaldocids[2]);
		
		
		HashMap<String, HashMap<String, Double>> centroidMap= new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> summaryMap= new HashMap<String, HashMap<String, Double>>();
		HashMap<String, ArrayList<Integer>> membershipMap = new HashMap<String, ArrayList<Integer>>();
		
		km.createCentroidHashmap(kval, centroidMap, DocMap, totaldocids, membershipMap, summaryMap);
}
	
	

	private void createCentroidHashmap(int kval,
			HashMap<String, HashMap<String, Double>> centroidMap, Map<Integer, HashMap> DocMap, int[] totaldocids, HashMap<String,ArrayList<Integer>> membershipMap, HashMap<String, HashMap<String, Double>> summaryMap) {
		// TODO Auto-generated method stub	
		
		long startKmeansFullTime = System.nanoTime();


		int[] arrayFormember = new int[kval];
		
		for(int i = 0; i< kval; i++)
		{
			int number = 1 + (int)(Math.random() * ((49 - 1) + 1));
//			System.out.println(totaldocids[number]);
			arrayFormember[i] = totaldocids[number];
			centroidMap.put("C"+i, DocMap.get(totaldocids[number])); //initial centroid map as per the k value
		}
		
//		System.out.println("initial centroid map:" + centroidMap);
		
		for(int i = 0; i< kval; i++)
		{
//			System.out.println(arrayFormember[i]);
			summaryMap.put("C"+i, DocMap.get(arrayFormember[i])); //initial summary map for computation of summary
		}
		
		for(int i = 0; i<kval; i++)
		{
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			int temp = arrayFormember[i];
			tempList.add(temp);
			membershipMap.put("C"+i, tempList);			//initial membership map which contains all the documents of the respective clusters
		}
		
//		System.out.println("initial membershipmap: " + membershipMap);
		
		HashMap<String, HashMap<String, Double>> newCentroidMap = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> newSummaryMap = new HashMap<String, HashMap<String, Double>>();		
		
//		System.out.println("initial centroid map: " + centroidMap);
		int j = 0;
		boolean flag = true;
		
		HashMap<String, ArrayList<Integer>> oldMembershipMap = new HashMap<String, ArrayList<Integer>>();
		oldMembershipMap.putAll(membershipMap);
		
		int iterations = 0;
		
		long startIterationsTime = System.nanoTime();

		while(iterations < 60)
		{
			Iterator DocMapiter = DocMap.entrySet().iterator();
			
			double[] val = new double[kval];
			
//			oldMembershipMap.putAll(membershipMap);
			
			
			while(DocMapiter.hasNext()) //for each doc.
			{
				int k = 0;
				Iterator centroiditer= centroidMap.entrySet().iterator();
				Map.Entry DocMapEntry = (Map.Entry)DocMapiter.next();
				while(centroiditer.hasNext()) //compute the document's similarity with the centroids
				{
					Map.Entry centroidEntry = (Map.Entry)centroiditer.next();
					HashMap<String, Double> map1 = (HashMap<String, Double>) centroidEntry.getValue();
					HashMap<String, Double> map2 = (HashMap<String, Double>) DocMapEntry.getValue();
					val[k] = multiplyDocVectors(map1, map2, DocMap);
					k = k+1;
				}
				
				int index = argMax(val); //find the cluster to which the document belongs to
				
				int docId = (int) DocMapEntry.getKey();
	/*			System.out.println(docId);
				System.out.println("C"+index);
				System.out.println(membershipMap);
				System.out.println(membershipMap.get("C"+index));
*/				ArrayList<Integer> value = membershipMap.get("C"+index);
				if(!value.contains(docId))
				{
					membershipMap.get("C"+index).add(docId); //add document to cluster
				}
				
			}
			
	//		System.out.println("membership map:" + membershipMap);
			Iterator membershipMapiter = membershipMap.entrySet().iterator();
			int Index = 0;
			while(membershipMapiter.hasNext())
			{
				Map.Entry membershipEntry = (Map.Entry)membershipMapiter.next();
				ArrayList<Integer> memberList = new ArrayList<Integer>();
				HashMap<String, Double> newCentroidDocMap = new HashMap<String, Double>();
				HashMap<String, Double> newSummaryDocMap = new HashMap<String, Double>();

				memberList = (ArrayList<Integer>) membershipEntry.getValue();
				newCentroidDocMap = addHashMaps(memberList, DocMap);
				newSummaryDocMap = addHashMaps2(memberList, DocMap);
				newCentroidMap.put("C"+Index, newCentroidDocMap); //find the updated centroid map
				newSummaryMap.put("C"+Index, newSummaryDocMap); //find the updated summary map
				Index++;
			}
			
			centroidMap.clear();
			
			summaryMap.clear();
			
			centroidMap.putAll(newCentroidMap);
			
			summaryMap.putAll(newSummaryMap);
			
			if(iterations == 50)
			{
				break;
			}
			
			
			membershipMap.clear();
			newCentroidMap.clear();
			newSummaryMap.clear();
			membershipMap.putAll(oldMembershipMap);
			
//			System.out.println("New Centroid map: " + newCentroidMap);
			
//			flag = validateCentroid(membershipMap, oldMembershipMap);

//			System.out.println("final membership map:" + membershipMap);
			
/*			if(flag = true)
			{
				centroidMap.clear();
				centroidMap.putAll(newCentroidMap);
			}
*/		
			iterations++;
		}
		
		long endIterationsTime = System.nanoTime();
		long endKmeansFullTime = System.nanoTime();
		long kMeansFullTime = (endKmeansFullTime - startKmeansFullTime)/1000;
		System.out.println("Total Time for k-means algorithm to work including initializing the centroids and computing the iteratioaions: " + kMeansFullTime);
		
		long iterationsTime = (endIterationsTime - startIterationsTime)/1000;
		System.out.println("Time taken to compute the iterations for converging: "+ iterationsTime);

		System.out.println("Final membership map of clusters: " + membershipMap);
		System.out.println("Final centroid map: " + newCentroidMap);
		System.out.println("final summary map:" + newSummaryMap);
		
		long startSummaryTime = System.nanoTime();
		sort(newSummaryMap);
		long endSummaryTime = System.nanoTime();
		long summaryTime = (endSummaryTime - startSummaryTime)/1000;
		System.out.println("Time taken to compute the summary of the clusters: " + summaryTime);
//		itraClusterDist(newCentroidMap, DocMap, kval); //used to find the intra cluster distance
	}


	private void itraClusterDist(
			HashMap<String, HashMap<String, Double>> newCentroidMap,
			Map<Integer, HashMap> DocMap, int kval) {
		// TODO Auto-generated method stub
		
		Iterator it = newCentroidMap.entrySet().iterator();
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
//		System.out.println(newCentroidMap);
		for(int i = 0; i<kval; i++)
		{
			int count = i+1;
						//kval = 5
			while(count<(kval))
			{
				System.out.println("i: "+ i);
				System.out.println("count: "+count);

				
				HashMap<String, Double> tempMap1 = new HashMap<String, Double>();
				HashMap<String, Double> tempMap2 = new HashMap<String, Double>();

				tempMap1 = newCentroidMap.get("C"+i);
				tempMap2 = newCentroidMap.get("C"+count);
				double tempval = multiplyDocVectors(tempMap1, tempMap2, DocMap);
				tempMap.put("C"+i+"C"+count, tempval);
				count++;
			}
		}
		System.out.println("\nIntra cluster dist: " + tempMap);
	}



	private void sort(HashMap<String, HashMap<String, Double>> newSummaryMap) {
		Iterator it1 = newSummaryMap.entrySet().iterator();
		Iterator it2 = newSummaryMap.entrySet().iterator();
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		
		while(it1.hasNext())
		{
			Map.Entry entry1 = (Map.Entry)it1.next();
			tempMap = (HashMap<String, Double>) entry1.getValue();
			tempMap = srtHMByVal(tempMap);
			String key = (String) entry1.getKey();
			newSummaryMap.put(key, tempMap);
		}
//		System.out.println("nfewSummMap:" + newSummaryMap);
		while(it2.hasNext())
		{
			Map.Entry entry2 = (Map.Entry)it2.next();
			HashMap<String, Double> tempMap2 = new HashMap<String, Double>();
			tempMap2 = (HashMap<String, Double>) entry2.getValue();
//			System.out.println("dfaf" + tempMap2);
			ArrayList<String> strKeys = new ArrayList<String>(tempMap2.keySet());
			ArrayList<String> list = new ArrayList<String>();
			
			Iterator<String> it = strKeys.iterator();
			StringBuilder newInput = new StringBuilder();
			
			for(int j = 1; j<11; j++)
			{
				String s = strKeys.get(strKeys.size() - j);
				list.add(s);
				newInput.append(s + " ");
			}
			String newInputString = newInput.toString();
			newInputString = newInputString.trim();
//			System.out.println(list);
			System.out.println("Cluster: " + entry2.getKey() + " summary: " + newInputString);
		}
		
	}



	private static boolean validateCentroid(
			HashMap<String,ArrayList<Integer>> membershipMap,
			HashMap<String,ArrayList<Integer>> oldMembershipMap) {
		
		System.out.println("hmap1: " + membershipMap);
		Iterator it1 = membershipMap.entrySet().iterator();
		Iterator it2 = oldMembershipMap.entrySet().iterator();
		boolean flag = true;
		
		while(it1.hasNext() && it2.hasNext())
		{
			Map.Entry entry1 = (Map.Entry)it1.next();
			Map.Entry entry2 = (Map.Entry)it2.next();
			ArrayList<Integer> ar1 = new ArrayList<Integer>();
			ArrayList<Integer> ar2 = new ArrayList<Integer>();
//			System.out.println("hehaehfj" + entry1.getValue());
			ar1 = (ArrayList<Integer>) entry1.getValue();
			ar2 = (ArrayList<Integer>) entry2.getValue();
			if (ar1.containsAll(ar2));
			{
				flag = false;
			}
		}
		
		return flag;
		
	}


	private static HashMap<String, Double> addHashMaps(
			ArrayList<Integer> memberList, Map<Integer, HashMap> DocMap) {
		// TODO Auto-generated method stub
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		HashMap<String, Double> tempMap2 = new HashMap<String, Double>();
		
		for(int i = 0; i< memberList.size(); i++)
		{
			tempMap2 = DocMap.get(memberList.get(i));
			tempMap = AddHashMap(tempMap, tempMap2);
		}
		
		Iterator it = tempMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry tempMapEntry = (Map.Entry)it.next();
			double tempval = (double)((double) tempMapEntry.getValue() / (double)memberList.size());
			String tempkey = (String) tempMapEntry.getKey();
			tempMap.put(tempkey, tempval);
		}
		return tempMap;
	}

	private static HashMap<String, Double> addHashMaps2(
			ArrayList<Integer> memberList, Map<Integer, HashMap> DocMap) {
		// TODO Auto-generated method stub
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		HashMap<String, Double> tempMap2 = new HashMap<String, Double>();
		
		for(int i = 0; i< memberList.size(); i++)
		{
			tempMap2 = DocMap.get(memberList.get(i));
			tempMap = AddHashMap(tempMap, tempMap2);
		}
		
		Iterator it = tempMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry tempMapEntry = (Map.Entry)it.next();
			double tempval = (double) tempMapEntry.getValue();
			String tempkey = (String) tempMapEntry.getKey();
			tempMap.put(tempkey, tempval);
		}
		return tempMap;
	}

	private static int argMax(double[] val) {
		// TODO Auto-generated method stub
		double maxVal = 0; 
		int maxIndex = 0;
		for(int i = 0; i < val.length; i++)
		{
			if(maxVal < val[i]){
				maxVal = val[i];
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}


	private static void similaccordingtok(int kval, int[] totaldocids, Map<Integer, HashMap> DocMap) {
		
		Double[] centroids = new Double[kval];
		
		HashMap<Integer, ArrayList<Double>> similarityHashMap = new HashMap<Integer, ArrayList<Double>>();
		
		for(int k = 0; k<kval; k++)
		{
			ArrayList<Double> list = new ArrayList<Double>();
			int Document = totaldocids[k];
			System.out.println("Document: " + Document);
			HashMap<String, Double> DocumentVector = DocMap.get(Document);
			Iterator docmapiterator = DocMap.entrySet().iterator();
			while(docmapiterator.hasNext())
			{
				Map.Entry docMapEntry = (Map.Entry)docmapiterator.next();
				HashMap<String, Double> docMapEntryMap = (HashMap<String, Double>) docMapEntry.getValue();
				Double result = multiplyDocVectors(DocumentVector, docMapEntryMap, DocMap);
				list.add(result);
			}
			similarityHashMap.put(Document, list);
		}
		
//		System.out.println(similarityHashMap);
		
	}

	public static LinkedHashMap srtHMByVal(Map<String, Double> mapFromSimil) { //sorts hashmap by values
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
	
	private static HashMap AddHashMap(HashMap tempMap, HashMap hashMap) { //adds two hashmaps
		
		
		HashMap<String, Double> TempAddMap = new HashMap<String, Double>();
		TempAddMap.putAll(hashMap);
		
		Iterator tempit = tempMap.entrySet().iterator();
		while(tempit.hasNext())
		{
			Map.Entry entry = (Map.Entry)tempit.next();
	        if(hashMap.containsKey(entry.getKey()))
	        {
	        	String key = (String) entry.getKey();
	        	Double map1val = (Double) tempMap.get(entry.getKey());
	        	Double map2val = (Double) hashMap.get(entry.getKey());
	        	TempAddMap.put((String) entry.getKey(), (map1val+map2val));
	        }
	        else
	        {
	        	Double map1val = (Double) tempMap.get(entry.getKey());
	        	TempAddMap.put((String) entry.getKey(), map1val);
	        }
		}
		
		return TempAddMap;
		
	}
	
	
	
	private Map<Integer, HashMap> CreatingDocMap(IndexReader r, //creating DocMap
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
																			//joke of the day: i wrote this method. i have no goddamn idea how this worked :)
															
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

	private static Double multiplyDocVectors(Map<String, Double> firstVector, //multiplies two document vectors to return the cosine similarity value
			Map<String, Double> secondVector, Map<Integer, HashMap> DocMap) {

		
		HashMap<String, Double> TempMulVector = new HashMap<String, Double>();
		Iterator it = firstVector.entrySet().iterator();
		Iterator it1 = firstVector.entrySet().iterator();
		Iterator it2 = secondVector.entrySet().iterator();
		
		Double dotProdNumerator = 0.0;
		
		while (it.hasNext())
		{
	        Map.Entry entry = (Map.Entry)it.next();
	        if(secondVector.containsKey(entry.getKey()))
	        {
	        	Double mul1 = secondVector.get(entry.getKey());
	        	Double mul2 = firstVector.get(entry.getKey());
	        	Double mul = mul1*mul2;
	        	dotProdNumerator = dotProdNumerator + mul;
//	        	TempMulVector.put(entry.getKey(), mul);
	        }
		}
		
		double modfirstvector = 0.0;
		while (it1.hasNext())
		{
	        Map.Entry entry1 = (Map.Entry)it1.next();
	        modfirstvector = modfirstvector + Math.pow((double)(entry1.getValue()), 2);	        
		}
		modfirstvector = Math.sqrt(modfirstvector);
		
		double modsecondvector = 0;
		while (it2.hasNext())
		{
	        Map.Entry entry2 = (Map.Entry)it2.next();
	        modsecondvector = modsecondvector + Math.pow((double)(entry2.getValue()), 2);	        
		}
		
		modsecondvector = Math.sqrt(modsecondvector);
		Double modMultiplied = modfirstvector*modsecondvector;
		
		double cosSimilarityValue = dotProdNumerator/modMultiplied;
		
		
		return cosSimilarityValue;
		
	}


}
