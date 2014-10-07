package edu.asu.irs13;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.index.CorruptIndexException;

class MyComparator2 implements Comparator {

Map map;

public MyComparator2(Map map) {
    this.map = map;
}

public int compare(Object o1, Object o2) {

    if(map.get(o2).equals(map.get(o1)))
    	return 1;
    else
    	return ((Double) map.get(o2)).compareTo((Double) map.get(o1));
}
}


public class CalcAuHu {

	public static void main(String[] args) throws CorruptIndexException,
			IOException {

		CalcAuHu calc = new CalcAuHu();
		calc.calculate();

	}

	public void calculate() throws CorruptIndexException, IOException {
		
		TreeSet<Integer> tset = new TreeSet<Integer>();
		
		HashMap<Integer, CL> baseSet = new HashMap<Integer, CL>();
		CalcBaseset(baseSet);	//calculate base set
		System.out.println("part1");

		int size = CalcSizeMat(baseSet, tset); // calculate size of matrix
		System.out.println("part2");

		HashMap<Integer, Integer> IndMat = new HashMap<Integer, Integer>();
		CalcIndexmat(IndMat, tset);

		int[][] AdjMat = new int[size][size];

		AdjMat = CalcAdjMat(baseSet, AdjMat, IndMat); //calculate adjacency matrix
		System.out.println("part3");
		for (int x = 0; x < size; x++) {
			for (int y=0; y< size; y++)
				System.out.print(AdjMat[x][y] + " ");
			System.out.print("\n");
		}

		int AdjMatTranspose[][] = new int[size][size];
		int ATA[][] = new int[size][size];
		int AAT[][] = new int[size][size];

		AdjMatTranspose = CalcTranspose(AdjMat, size, AdjMatTranspose); //calculate transpose of adj matrix
		System.out.println("part5");
		for (int x = 0; x < size; x++) {
			for (int y=0; y< size; y++)
				System.out.print(AdjMatTranspose[x][y] + " ");
			System.out.print("\n");
		}

		ATA = CalcATransposeA(AdjMat, AdjMatTranspose, size, ATA); //calculate A-Transpose* A
		System.out.println("part6");

		AAT = CalcAATranspose(AdjMat, AdjMatTranspose, size, AAT); //calculate A * A-Transpose
		System.out.println("part7");

		System.out.println("ATA matrix");
		for (int x = 0; x < size; x++) {
			for (int y=0; y< size; y++)
				System.out.print(ATA[x][y] + " ");
			System.out.print("\n");
		}
		
		System.out.println("AAT matrix");
		for (int x = 0; x < size; x++) {
			for (int y=0; y< size; y++)
				System.out.print(AAT[x][y] + " ");
			System.out.print("\n");
		}
		
		int a0[][] = new int[size][1];
		for (int x = 0; x < size; x++)
			a0[x][0] = 1;

		double auth[][] = new double[size][1];
		double hub[][] = new double[size][1];

		long startTime2 = System.nanoTime();
		auth = CalcAuthAndHub(ATA, size); //calculate authority
		long endTime2 = System.nanoTime();
		long authtime =  ((endTime2 - startTime2) /1000);
		System.out.println("time for authority calculation: " + authtime);
		System.out.println("part8");
		
		HashMap<Integer, Double> authmap = new HashMap<Integer, Double>();
		Iterator<Integer> it3 = tset.iterator();
		int ind1 = 0;
		while (it3.hasNext())
		{
			authmap.put((Integer) it3.next(), auth[ind1][0]);
			ind1++;
		}
		
		MyComparator2 comp=new MyComparator2(authmap);
		Map<Integer,Double> TREEMap = new TreeMap(comp);
		
		long startTime3 = System.nanoTime();
		TREEMap.putAll(authmap);  //compared authority
		long endTime3 = System.nanoTime();
		long authsorttime =  ((endTime3 - startTime3) /1000);
		System.out.println("time for authority sorting calculation: " + authsorttime);

		
		System.out.println("auth before sort: " + TREEMap);
		Set set = TREEMap.entrySet();
		// Get an iterator 
		Iterator iter = set.iterator();
		int a4=0;
		// Display elements 
		System.out.println("top authorities: ");
		while(iter.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)iter.next(); 
			System.out.print(me.getKey()+ " "); 
			a4++;
			if(a4 == 10)
			{
				break;
			}
		}

		long startTime4 = System.nanoTime();
		hub = CalcAuthAndHub(AAT, size); //calculate hub
		long endTime4 = System.nanoTime();
		long hubtime =  ((endTime4 - startTime4) /1000);
		System.out.println("time for hub calculation: " + hubtime);

		System.out.println("part9");
		
		HashMap<Integer, Double> hubmap = new HashMap<Integer, Double>();
		Iterator<Integer> it4 = tset.iterator();
		int indx2 = 0;
		while (it4.hasNext())
		{
			hubmap.put((Integer) it4.next(), hub[indx2][0]);
			indx2++;
		}
		
		System.out.println("hubmap: " + hubmap);
		
		MyComparator2 compforhub=new MyComparator2(hubmap);
		Map<Integer,Double> TREEMap1 = new TreeMap(compforhub);
		
		long startTime5 = System.nanoTime();
		TREEMap1.putAll(hubmap); //calculate sorted hub values
		long endTime5 = System.nanoTime();
		long hubsorttime =  ((endTime5 - startTime5) /1000);
		System.out.println("\ntime for hub sorting calculation: " + hubsorttime);

		Set set1 = TREEMap1.entrySet();
		// Get an iterator 
		Iterator iter1 = set1.iterator();
		int a5=0;
		// Display elements 
		System.out.println("top hubs: ");
		while(iter1.hasNext()) 
		{ 
			Map.Entry me1 = (Map.Entry)iter1.next(); 
			System.out.print(me1.getKey()+ " "); 
			a5++;
			if(a5 == 10)
			{
				break;
			}
		}


		System.out.println("authority matrix");
		for (int indx = 0; indx < size; indx++) {
			System.out.print(auth[indx][0] + " ");
		}
		
		System.out.println("hub matrix");
		for (int indx = 0; indx < size; indx++) {
			System.out.print(hub[indx][0] + " ");
		}

	}

	public void CalcIndexmat(HashMap<Integer, Integer> IndMat, TreeSet<Integer> tset) {
		Iterator<Integer> it2 = tset.iterator();
		int ind = 0;
		while (it2.hasNext()) {
			IndMat.put((Integer) it2.next(), ind);
			ind++;
		}
	}

	public double[][] CalcAuthAndHub(int[][] AH, int size) {
		double sum = 0;
		int zo = 0;
		double a0[][] = new double[size][1];
		for (int x = 0; x < size; x++)
			a0[x][0] = 1;
		double a1[][] = new double[size][1];
		
		for (int diff = 1; diff < 5000; diff++) {
			for (int x = 0; x < size; x++) {
				for (int z = 0; z < size; z++) {
					sum = sum + AH[x][z] * a0[z][0];
				}

				a1[x][0] = sum;
				sum = 0;
			}

			double summ = 0;
			for (int x = 0; x < size; x++) {
				summ = summ + a1[x][0] * a1[x][0];
			}
			summ =  Math.sqrt(summ);
			
			for (int x = 0; x < size; x++) {
				a0[x][zo] = (a1[x][zo]/(double)summ);
			}
		}
		return a0;
	}

	
	public int[][] CalcAATranspose(int[][] AdjMat, int[][] AdjMatTranspose,
			int size, int[][] AAT) {
		int sum = 0;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					sum = sum + AdjMat[x][z] * AdjMatTranspose[z][y];
				}

				AAT[x][y] = sum;
				sum = 0;
			}
		}
		return AAT;
	}

	public int[][] CalcATransposeA(int[][] AdjMat, int[][] AdjMatTranspose,
			int size, int[][] ATA) {
		int sum = 0;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					sum = sum + AdjMatTranspose[x][z] * AdjMat[z][y];
				}

				ATA[x][y] = sum;
				sum = 0;
			}
		}
		return ATA;
	}

	public int[][] CalcTranspose(int[][] AdjMat, int size,
			int[][] AdjMatTranspose) {
		for (int c1 = 0; c1 < size; c1++) {
			for (int c2 = 0; c2 < size; c2++) {
				AdjMatTranspose[c1][c2] = AdjMat[c2][c1];
			}
		}
		return AdjMatTranspose;
	}

	public int[][] CalcAdjMat(HashMap<Integer, CL> baseSet, int[][] AdjMat,
			HashMap<Integer, Integer> IndMat) {
		Iterator<Entry<Integer, CL>> it = baseSet.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry val = (Map.Entry) it.next();
			int docNum = (int) val.getKey();
			int docNumInd = IndMat.get(docNum);

			int[] citations = ((CL) val.getValue()).getCitations();
			for (int j = 0; j < citations.length; j++) {
				AdjMat[IndMat.get(citations[j])][docNumInd] = 1;
			}

			int[] links = ((CL) val.getValue()).getLinks();
			for (int j = 0; j < links.length; j++) {
				AdjMat[docNumInd][IndMat.get(links[j])] = 1;
			}
		}
		return AdjMat;
	}

	public int CalcSizeMat(HashMap<Integer, CL> baseSet, TreeSet<Integer> tset) {
		Iterator<Entry<Integer, CL>> it2 = baseSet.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry val = (Map.Entry) it2.next();
			int[] cit = ((CL) val.getValue()).getCitations();
			int[] lin = ((CL) val.getValue()).getLinks();
			for (int index = 0; index < cit.length; index++) {
				tset.add(cit[index]);
			}
			for (int index = 0; index < lin.length; index++) {
				tset.add(lin[index]);
			}
			tset.add((Integer) val.getKey());
		}

		int size = tset.size();
		return size;
	}

	public void CalcBaseset(HashMap<Integer, CL> baseSet)
			throws CorruptIndexException, IOException {
		writeIDF tfidf = new writeIDF();
		LinkAnalysis LA = new LinkAnalysis();
		long startTime6 = System.nanoTime();
		List<TreeMap<Integer, Double>> l = tfidf.getTFIDF();
		long endTime6 = System.nanoTime();
		long tfidftime =  ((endTime6 - startTime6) /1000);
		System.out.println("time for tfidf calc: " + tfidftime);
		// calculating the AH only for first query.
		TreeMap<Integer, Double> tm = l.get(0);
		Set<Integer> set = tm.keySet();
		// Get an iterator
		Iterator<Integer> i = set.iterator();
		int count = 0;
		int rootDocNum;
		long startTime1 = System.nanoTime();
		while (i.hasNext() && count != 10) {
			rootDocNum = i.next();
			System.out.println(rootDocNum);
			baseSet.put(
					rootDocNum,
					new CL(LA.getLinks(rootDocNum), LA.getCitations(rootDocNum)));
			count++;
		}
		long endTime1 = System.nanoTime();
		long basesettime =  ((endTime1 - startTime1) /1000);
		System.out.println("time for base set: " + basesettime);
	}

}
