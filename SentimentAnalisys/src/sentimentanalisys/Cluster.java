/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Reza
 */
public class Cluster {

    public double[] centroid;
    public List nodes;
    public List words;

    public Cluster() {
        centroid = new double[200];
        nodes = new LinkedList<double[]>();
        words = new LinkedList<String>();
    }

    public void refreshCentroid() {
        /*
        if (nodes.isEmpty()) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] = 100;

            }
            return;
        }
                */
        double[] dax = new double[centroid.length];
        for (int i = 0; i < nodes.size(); i++) {
            double[] vector = (double[]) nodes.get(i);
            for (int j = 0; j < dax.length; j++) {
                dax[j] = dax[j] + vector[j];
            }
        }
        for (int j = 0; j < dax.length; j++) {
            dax[j] = dax[j] / nodes.size();
        }
        this.centroid = dax;
    }

}
