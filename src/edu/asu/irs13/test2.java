package edu.asu.irs13;

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.document.*;

import java.io.*;
import java.util.*; 


public class test2 {

	public static boolean validateCentroid(newCentroidMap, )
	{
		HashMap<String, ArrayList<Integer>> hmap1 = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> hmap2 = new HashMap<String, ArrayList<Integer>>();
		
		Iterator it1 = hmap1.entrySet().iterator();
		Iterator it2 = hmap2.entrySet().iterator();
		
		while(it1.hasNext() && it2.hasNext())
		{
			Map.Entry entry1 = (Map.Entry)it1.next();
			Map.Entry entry2 = (Map.Entry)it2.next();
			ArrayList<Integer> ar1 = new ArrayList<Integer>();
			ArrayList<Integer> ar2 = new ArrayList<Integer>();
			ar1 = (ArrayList<Integer>) entry1.getValue();
			ar2 = (ArrayList<Integer>) entry2.getValue();
			if (ar1.containsAll(ar2));
			{
				return false;
			}
		}
		
		
	}
	

}
