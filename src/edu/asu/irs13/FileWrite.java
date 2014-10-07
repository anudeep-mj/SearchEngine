package edu.asu.irs13;

import java.io.*;

public class FileWrite {
	public static void writeFile(String text) {
        try {
                BufferedWriter bwriter = new BufferedWriter(new FileWriter(new File("out2.txt"), true));
                bwriter.write(text);
                bwriter.newLine();
                bwriter.close();
        } catch (Exception e) {
        }
}
}

