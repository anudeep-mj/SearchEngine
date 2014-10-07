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
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import java.lang.*;
import java.util.*;


public class VCompare implements Comparator<String> {
	Map<Integer, Double> base;
    public VCompare(Map<Integer, Double> base) {
        this.base = base;
    }
}
