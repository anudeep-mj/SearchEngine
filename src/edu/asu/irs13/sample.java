package edu.asu.irs13;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.*;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import java.lang.*;
import java.util.*;



public class sample {

	int i, j = 0;
	
	public static void main( String args[] ) 
	{ 
	// create Scanner to obtain input from command window
	Scanner input = new Scanner( System.in ); 

	int number1; // first number to add 
	int number2; // second number to add 
	int sum; // sum of number1 and number2 

	System.out.print( "Enter first integer: " ); // prompt 
	number1 = input.nextInt(); // read first number from user 

	System.out.print( "Enter second integer: " ); // prompt 
	number2 = input.nextInt(); // read second number from user 

	sum = number1 + number2; // add numbers 

	System.out.printf( "Sum is %d\n", sum ); // display sum 

	} // end method main 
	
}
