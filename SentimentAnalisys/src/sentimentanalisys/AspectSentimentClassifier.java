/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sentimentanalisys;

/**
 *
 * @author Reza
 */
public class AspectSentimentClassifier {
    
    public AspectSentimentClassifier(){
        
    }
    
    public void getPrecisionRecallforAspectSentimentClassification(String[] evalComments){
        for(int i = 0; i<evalComments.length ; i++){
            int counter = 0;
            while (!evalComments[i + counter].equals("null")) {
                counter++;
            }
            
            System.out.println(evalComments[i]);
        }
        
    }
    
}
