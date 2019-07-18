/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Reza
 */
public class FileWriter {

    public FileWriter() {

    }

    public void writeComments(String outputAddress, String[][] comments) throws FileNotFoundException, IOException {
        File fout = new File(outputAddress);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < comments.length; i++) {
            if (!comments[i][0].equals("null") && !comments[i][2].equals("null")) {
                String temp = comments[i][0] + "\t" + comments[i][1] + "\t" + comments[i][2];
                bw.write(temp);
                bw.newLine();
                continue;
            }
            if (!comments[i][0].equals("null") && comments[i][2].equals("null")) {
                String temp = comments[i][0] + "\t" + comments[i][1];
                bw.write(temp);
                bw.newLine();
                continue;
            }
            if (comments[i][0].equals("null")) {
                String temp = "";
                bw.write(temp);
                bw.newLine();
                //System.out.println("daaaaaaaaaaaaaaaaaaaaaaax");

            }
        }

        bw.close();
    }

}
