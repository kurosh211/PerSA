/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sentimentanalisys;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Reza
 */
public class FileReader {
    public FileReader(){
        
    }
    
    public String[][] readCommentsFile(String adress) throws FileNotFoundException, IOException {
        List<String[]> result = new LinkedList<String[]>();
        int i = 0;
        int j = 0;
        BufferedReader br = new BufferedReader(new java.io.FileReader(adress));
        String currentLine = br.readLine();

        //System.out.println(currentLine);
        while (currentLine != null) {
            StringTokenizer tokenizer = new StringTokenizer(currentLine);
            String currToken = null;
            //if (tokenizer.hasMoreTokens()) {
            //    currToken = tokenizer.nextToken("\t");
            //}
            
            //String[] temp = new String[4];
            String[] temp = {"null","null","null","null"};
            //System.out.println(currToken);
            while (tokenizer.hasMoreTokens()) {
                //result[i][j] = currToken;
                currToken = tokenizer.nextToken("\t");
                temp[j] = currToken;

                //System.out.println(currToken);
                j++;
            }
            //temp[j] = currToken;
            j = 0;
            result.add(temp);
            if (i % 10000 == 0) {
                System.out.println(i);

            }

            

            currentLine = br.readLine();
            i++;
            // System.out.println(i);
        }
        
        

        //String currentLine = br.readLine();
        //System.out.println(currentLine);
        //String[][] dax = (String[][]) result.toArray();
        String[][] dax = (String[][]) result.toArray(new String[result.size()][4]);

        return dax;

    }
    
}
