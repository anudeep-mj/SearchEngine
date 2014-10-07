package edu.asu.irs13;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

	import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

	import java.lang.*;
import java.util.*;


	public class modD {

		public static void main(String[] args) throws Exception
		{
			IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
			HashMap<String,String> moddMap = new HashMap<String,String>();
			String[] array = new String[2];
			System.out.println("The number of documents in this index is: " + r.maxDoc());
			int i = 0;
			int j = 0;
			double k = 0;
			double l = 0;
			int m = 0;
			int n =0;
			String str = new String();
			String a = new String();
			String b = new String();
			TermEnum t = r.terms();

			
			FileWriter moddTXT = new FileWriter("modd.txt");
			
			while(t.next())
			{
				if (i >= 0)
				{ 
					String st1= t.term().text();					
					moddTXT.write(st1 +"\r\n");
				
				}
			}
			
			moddTXT.close();

			try{
				BufferedReader in = new BufferedReader(new FileReader("out3.txt"));
				String line = "";
				 while ((line = in.readLine()) != null) {
			          
			            moddMap.put(parts[0], parts[1]);
			        }
			        in.close();
			        System.out.println(moddMap.toString());

				String strLine;
				//Close the input stream
				in.close();
			}catch (Exception e){//Catch exception if any
				e.printStackTrace();		
			}

			
			
			
			
			
			
			
/*			while(t.next())
			{
				
			
				while(t.next())
				{
				 
						
				
				
				}
			}
			moddTXT.close();
*/
     }
}
	

	