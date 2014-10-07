package edu.asu.irs13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.index.CorruptIndexException;
import Jama.Matrix;

public class CalAH {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {
		// TODO Auto-generated method stub
		writeIDF tfidf = new writeIDF();
		LinkAnalysis LA = new LinkAnalysis();
	
		ArrayList<Integer> list = new ArrayList<Integer>();

		HashMap<Integer, CL> baseSet = new HashMap<Integer, CL>();
		
		
		List<TreeMap<Integer, Double>> l = tfidf.getTFIDF();
		//calculating the AH only for first query.
		TreeMap<Integer, Double> tm = l.get(0);
		Set<Integer> set = tm.keySet();
		// Get an iterator 
		Iterator<Integer> i = set.iterator();              
		int count=0;
		int rootDocNum;
		while(i.hasNext() && count!=10) 
		{ 
			rootDocNum = i.next();
			System.out.println(rootDocNum);
			baseSet.put(rootDocNum, new CL(LA.getLinks(rootDocNum), LA.getCitations(rootDocNum)));
			count++;
		}
//		System.out.print("17650: ");
//		int[] cit3 = baseSet.get(17650).getCitations();
//		for(int pb:cit3)
//		{
//			System.out.print(pb + " ");
//		}
		
		
		int[][] MPR = new int[25054][25054];
		
		
		Iterator<Entry<Integer, CL>> it2 = baseSet.entrySet().iterator();
		while (it2.hasNext())
		{
	        Map.Entry val = (Map.Entry)it2.next();
	        int[] cit = ((CL) val.getValue()).getCitations();
	        int[] lin = ((CL) val.getValue()).getLinks();
	        for (int index = 0; index < cit.length; index++)
	        {
	            list.add(cit[index]);
	        }
	        for (int index = 0; index < lin.length; index++)
	        {
	            list.add(lin[index]);
	        }
		}
		Collections.sort(list);
		int size = list.get(list.size()-1);
		
		int[][] A = new int[size][size];

		
		Iterator<Entry<Integer, CL>> it = baseSet.entrySet().iterator();
		while (it.hasNext())
		{
	        Map.Entry val = (Map.Entry)it.next();
	        int[] b = ((CL) val.getValue()).getCitations();
	        for(int j = 0; j<b.length; j++)
	        {
	        	int k = b[j];
	        	int m = (int) val.getKey();
	        	A[k][m] = 1;
	        }
		}

		Iterator<Entry<Integer, CL>> it1 = baseSet.entrySet().iterator();
		while (it1.hasNext())
		{
	        Map.Entry val = (Map.Entry)it1.next();
	        int[] c = ((CL) val.getValue()).getLinks();
	        for(int j = 0; j<c.length; j++)
	        {
	        	int k1 = c[j];
	        	int m1 = (int) val.getKey();
	        	A[m1][k1] = 1;
	        }
		}
				
		
//		int n2 = A.length;
//		int m2 = A[0].length;
		int AT[][] = new int[size][size];
		int ATA[][] = new int[size][size];
		int AAT[][] = new int[size][size];

		for(int c1 = 0; c1<size; c1++)
		{
			for(int c2 = 0; c2<size; c2++)
			{
				AT[c1][c2] = A[c2][c1];
			}
		}
		
		int sum = 0;
		
		for ( int x = 0 ; x < size ; x++ )
        {
           for ( int y = 0 ; y < size ; y++ )
           {   
              for ( int z = 0 ; z < size ; z++ )
              {
                 sum = sum + AT[x][z]*A[z][y];
              }

              ATA[x][y] = sum;
              sum = 0;
           }
        }
		
		for ( int x = 0 ; x < size ; x++ )
        {
           for ( int y = 0 ; y < size ; y++ )
           {   
              for ( int z = 0 ; z < size ; z++ )
              {
                 sum = sum + A[x][z]*AT[z][y];
              }

              AAT[y][y] = sum;
              sum = 0;
           }
        }
		
		int zo = 0;
		int a[][] = new int[size][1];
		for(int x= 0; x<size; x++)
			a[x][zo] = 1;
		
		int amultip[][] = new int[size][1];
		int sum1 = 1;
		int sum2 = 2;

		while ((sum2-sum1)>.05)
		{
			for ( int x = 0 ; x < size ; x++ )
			{
				for ( int y = 0 ; y <= zo ; y++ )
				{   
					for ( int z = 0 ; z < size ; z++ )
					{
						sum = sum + ATA[x][z]*a[z][y];
					}

					amultip[x][y] = sum;
					sum = 0;
				}
			}
			for(int x = 0; x<size; x++)
			{
				sum1 = sum1+(amultip[x][0]*amultip[x][0]);
			}
			for(int x = 0; x<size; x++)
			{
				sum2 = sum2+(a[x][0]*a[x][0]);
			}
			
		
			for (int x = 0; x<size; x++)
			{	
				a[x][zo] = amultip[x][zo];
			
			}
		}
		
//		for(int x1 = 1; x1<25055; x1++)
//		{
//			int citations[] = LA.getCitations(x1);
//			for(int x2 = 0; x2<citations.length; x2++)
//			{
//				int x3 = citations[x2];
//				MPR[x3][x1] = 1;
//			}
//		}
//		
//		for(int x1 = 1; x1<25055; x1++)
//		{
//			int links[] = LA.getLinks(x1);
//			for(int x2 = 0; x2<links.length; x2++)
//			{
//				int x3 = links[x2];
//				MPR[x1][x3] = 1;
//			}
//		}

	}

	private static void CalcBaseset() {
		// TODO Auto-generated method stub
		
	}

}
