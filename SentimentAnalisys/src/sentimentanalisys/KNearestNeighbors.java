/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.util.List;

/**
 *
 * @author Reza
 */
public class KNearestNeighbors {

    public KNearestNeighbors() {

    }
    
    public Cluster[] calculate(List<String> headWords, double[][] headVectors, ModelReader model){
        Cluster[] results = new Cluster[headWords.size()];
        for(int i = 0; i<headVectors.length;i++){
            results[i] = new Cluster();
            results[i].centroid = headVectors[i];
        }
        // fore asli
        
        
        
        return null;
    }

}
