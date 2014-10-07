package edu.asu.irs13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

import org.apache.commons.math3.*;


public class kmeans {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException {

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
		
		
		
		returnIDFmap idfmap = new returnIDFmap();
		Map<Integer, Double> IDFMap = idfmap.getTFIDF();
		Iterator<Entry<Integer, Double>> IDFMapiter = IDFMap.entrySet().iterator();
//		int totaldocids[] = new int[IDFMap.size()];
		int totaldocids[] = new int[50];
		ArrayList cluster1 = new ArrayList();
		ArrayList cluster2 = new ArrayList();
		ArrayList centroidlist1 = new ArrayList();
		ArrayList centroidlist2 = new ArrayList();

		
		int i = 0;
		while(IDFMapiter.hasNext() && i!=50)
		{
			Map.Entry idfentry = (Map.Entry)IDFMapiter.next();
			Integer idfkey = (Integer) idfentry.getKey();
			totaldocids[i++] = idfkey;
		}
		
		
		DocumentVector[] docs = new DocumentVector[totaldocids.length];
		int j = 0;
		for (int docId : totaldocids)
		{
			System.out.println("docID:" + docId);

			TermFreqVector[] tfvs = r.getTermFreqVectors(docId);
//			TermFreqVector tfvtest = r.getTermFreqVector(8270, "path");
			System.out.println(tfvs);
			
			Assert.assertTrue(tfvs.length == 1);
		    docs[i] = new DocumentVector(terms);
		    for (TermFreqVector tfv : tfvs)
		    {
		    	String[] termTexts = tfv.getTerms();
		        int[] termFreqs = tfv.getTermFrequencies();
		        Assert.assertEquals(termTexts.length, termFreqs.length);
		        for (int k = 0; k < termTexts.length; k++)
		        {
		        	docs[j].setEntry(termTexts[k], termFreqs[k]);
		        }
		    }
		    docs[j].normalize();
		    j++;
		}
		double cosim01 = getCosineSimilarity(docs[0], docs[1]);
	    System.out.println("cosim(0,1)=" + cosim01);
	    double cosim02 = getCosineSimilarity(docs[0], docs[2]);
	    System.out.println("cosim(0,2)=" + cosim02);
	    double cosim03 = getCosineSimilarity(docs[0], docs[3]);
	    System.out.println("cosim(0,3)=" + cosim03);
	    r.close();
	}

	private static double getCosineSimilarity(DocumentVector documentVector,
			DocumentVector documentVector2) {
		return (documentVector.vector.dotProduct(documentVector2.vector)) /
			      (documentVector.vector.getNorm() * documentVector2.vector.getNorm());
		
	}		
}				
		
		
	/*	int cent1 = totaldocids[0];
		int cent2 = totaldocids[1];
		
		
		
		for(int k = 0; k<50; k++)
		{
			for(int j = 0; j<50; j++)
			{
				double firstSimilarity = similarity(cent1,totaldocids[j], r);
				double secondSimilarity = similarity(cent2, totaldocids[j], r);
				if(firstSimilarity > secondSimilarity)
				{
					cluster1.add(totaldocids[j]);
				}
				else
				{
					cluster2.add(totaldocids[j]);
				}
			}
			
			findnewcentroid();
			if(cent1 == newcent1 && cent2 == newcent2)
			{
				break;
			}
		}	
*/			
			
//	}

/*	private static double similarity(int cent1, int i, IndexReader r) throws IOException {
		
		TermFreqVector vector1 = r.getTermFreqVector(cent1, "contents");
		TermFreqVector vector2 = r.getTermFreqVector(i, "contents");
		System.out.println(vector1);
		System.out.println(vector2);
		
		
		
		return 0;
	}
*/

