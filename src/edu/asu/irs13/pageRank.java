package edu.asu.irs13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.lucene.index.CorruptIndexException;

class MyComparator3 implements Comparator {

Map map;

public MyComparator3(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    if(map.get(o2).equals(map.get(o1)))
    	return 1;
    else
    	return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
}
}


public class pageRank {

	/**
	 * @param args
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws CorruptIndexException,
			IOException {
		
		pageRank pr = new pageRank();
		pr.calcPageRank();

	}

	private void calcPageRank() throws CorruptIndexException, IOException {
		Map<Integer, CL> baseSet = new HashMap<Integer, CL>();
		List<Integer> linksList = new ArrayList<Integer>();
		List<Integer> citaList = new ArrayList<Integer>();
				
		Map<Integer, Double> R1Map = new HashMap<Integer, Double>();
		Map<Integer, Double> R0Map = new HashMap<Integer, Double>();
		Map<Integer, Double> RFinalMap = new HashMap<Integer, Double>();
	
		Map<Integer, Double> RMapFinalUnsorted = new HashMap<Integer, Double>();
		MyComparator3 comp=new MyComparator3(RMapFinalUnsorted);
		Map<Integer,Double> RFinalMapCompared = new TreeMap(comp);
		
		double w = 0;
		Scanner input = new Scanner(System.in);
		System.out.print( "Enter w as you want: " ); // prompt 
		w = input.nextDouble(); // read w from user 
		
		
		for (int i = 0; i < LinkAnalysis.numDocs; i++)
			R0Map.put(i,(double) 1);

		double sum1 = 0;
		double sum3 = 1;
		double sum2 = 0;
		int k = 0;

		CalcBaseset(baseSet, linksList, citaList); //calculate base set
		long startTime2 = System.nanoTime();
		for (k = 0; k < 28; k++) {
//		while (sum1< 0.001) {
			System.out.println("iteration: "+ k);
			System.out.println("here");
			int count = 0, counter = 0;
			double value = 0;
			long startTime1 = System.nanoTime();
			Iterator<Entry<Integer, CL>> baseSetIterator = baseSet.entrySet()
					.iterator();
			while (baseSetIterator.hasNext()) {
				Map.Entry val = (Map.Entry) baseSetIterator.next();
				int[] lin = ((CL) val.getValue()).getLinks();
				List<Integer> arraylinklist = new ArrayList<Integer>();
				for (int i = 0; i < lin.length; i++) {
					arraylinklist.add(lin[i]);
				}
				int key = (int) val.getKey();

				if (lin.length == 0
						&& (Collections.frequency(citaList, key) == 0)) {
					for (int i = 0; i < LinkAnalysis.numDocs; i++) {
						value = (double) (1 / LinkAnalysis.numDocs);
						if (counter != 0)
							R1Map.put(i, ((value * R0Map.get(counter)) + R1Map
									.get(i)));
						else
							R1Map.put(i, value * R0Map.get(counter));
					}
				} else {
					count = lin.length + Collections.frequency(citaList, key);
					for (int i = 0; i < LinkAnalysis.numDocs; i++) {
						if (arraylinklist.contains(i)) {
							value = (double) ((0.9 + (0.1 / LinkAnalysis.numDocs)) / count);
							if (counter != 0)
								R1Map.put(i,
										((value * R0Map.get(counter)) + R1Map
												.get(i))); //the calculations here compute pagerank matrix after iteration
							else
								R1Map.put(i, ((value * R0Map.get(counter))));
						} else {
							value = (double) (0.1 / LinkAnalysis.numDocs);
							if (counter != 0)
								R1Map.put(i,
										((value * R0Map.get(counter)) + R1Map
												.get(i)));
							else
								R1Map.put(i, ((value * R0Map.get(counter))));
						}
					}
				}
				counter++;
			}

			sum1 = 0;
			for(int R0iterator = 0, R1iterator = 0; R0iterator<25054; R0iterator++, R1iterator++)
			{
				sum1 = sum1 + (R0Map.get(R0iterator)-R1Map.get(R1iterator)); //check the convergence
				while( R1iterator == 25054)
				{
					break;
				}
			}
			
			System.out.println("sum1: "+sum1);
			R0Map.clear(); //clear R0 for next iteration
			R0Map.putAll(R1Map);
			R1Map.clear(); //clear R1 map computed from R0 for next iteration
			long endTime1 = System.nanoTime();
			long iterationtime =  ((endTime1 - startTime1) /1000);
			System.out.println("time taken for the iteration: " + iterationtime);

		}
		long endTime2 = System.nanoTime();
		long totalRtime =  ((endTime2 - startTime2) /1000);
		System.out.println("time taken for the final PageRank matrix calculation: " + totalRtime);

		
		System.out.println("ROMap: " + R0Map);
//		RFinalMap.putAll(R0Map);
		
		Entry<Integer,Double> maxEntry = null;

		for(Entry<Integer, Double> entry : R0Map.entrySet()) { 		//calculate the document with the highest page rank
		    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
		        maxEntry = entry;
		    }
		}
		System.out.println("\nThe max page rank is in:" + maxEntry.getKey() + "with rank:" + maxEntry.getValue());

		for(int tot = 0;tot<7; tot++)
		{	
		returnIDFmap idfmap = new returnIDFmap();
		Map<Integer, Double> IDFMap = idfmap.getTFIDF(); //get the IDF map with the cosine similarity	
		System.out.println("IDF Map:" + IDFMap);
		
		Iterator<Entry<Integer, Double>> IDFMapiter = IDFMap.entrySet().iterator();
		while(IDFMapiter.hasNext())
		{
			Map.Entry idfentry = (Map.Entry)IDFMapiter.next();
			Integer idfkey = (Integer) idfentry.getKey();
			Double idfval = (Double) idfentry.getValue();
			double v1 = (1-w)*idfval;
			double v2 = w*R0Map.get(idfkey);
			double v3 = v1+v2;
			RMapFinalUnsorted.put(idfkey, v3); //this makes the unsorted final map which combines the values of page rank and vector similarity
		}
		System.out.println("RMapFinalUnsorted: " + RMapFinalUnsorted);
		
		RFinalMapCompared.putAll(RMapFinalUnsorted); //sorts the final map which combines the values of page rank and vector similarity
		Set set = RFinalMapCompared.entrySet();
		// Get an iterator 
		System.out.println("top pages of pagerank: ");
		System.out.println("RMapFinalsorted: " + RFinalMapCompared);

		Iterator<Entry<Integer, Double>> RFMapiter = IDFMap.entrySet().iterator();
		
		// Display elements 
		while(RFMapiter.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)RFMapiter.next(); 
			System.out.print(me.getKey()+ " "); 
		}
		
		RMapFinalUnsorted.clear();
		RFinalMapCompared.clear();
//		System.out.println(RFinalMapCompared);
		}
	}

	public void CalcBaseset(Map<Integer, CL> baseSet, List<Integer> linksList,
			List<Integer> citaList) throws CorruptIndexException, IOException {
		LinkAnalysis LA = new LinkAnalysis();
		int count = 0;
		while (count < 25054) {
			int[] links = LA.getLinks(count);
			int[] citations = LA.getCitations(count);
			for (int i = 0; i < links.length; i++)
				linksList.add(links[i]);
			for (int i = 0; i < citations.length; i++)
				citaList.add(citations[i]);
			baseSet.put(count,
					new CL(LA.getLinks(count), LA.getCitations(count)));
			count++;
		}
	}

}
