/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.util.List;
import java.util.Random;

/**
 *
 * @author Reza
 */
public class KMeans {

    public KMeans() {

    }

    public Cluster[] calculate(List<String> words, double[][] vectors, int k, int maxIter) {
        Cluster[] clusters = new Cluster[k];
        //set k ta random vector baraye centriod ha
        Random rndom = new Random();
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster();
            clusters[i].centroid = vectors[rndom.nextInt(vectors.length - 1)];
        }

        // hala mirim soraghe halghe asli
        boolean endCond = true;
        int iteration = 0;
        while (endCond) {
            //asle majara
            System.out.println("iteration = " + iteration);
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].nodes.clear();
                clusters[i].words.clear();
            }
            for (int i = 0; i < vectors.length; i++) {
                // be dalile inke cosDist NaN ziad dare 0 mikonim index shoroo ra
                int maxIndex = -1;
                double minDist = 10000;
                double temp = 0;
                for (int j = 0; j < clusters.length; j++) {

                    temp = euclideanDistance(vectors[i], clusters[j].centroid);
                    //System.out.println(temp);
                    if (temp < minDist) {
                        minDist = temp;
                        maxIndex = j;
                    }
                }
                clusters[maxIndex].nodes.add(vectors[i]);
                clusters[maxIndex].words.add(words.get(i));

            }

            // jadid shodan centriod ha
            double[][] oldCenteroids = new double[k][clusters[0].centroid.length];
            for (int i = 0; i < oldCenteroids.length; i++) {
                oldCenteroids[i] = clusters[i].centroid;
            }

            for (int i = 0; i < clusters.length; i++) {
                clusters[i].refreshCentroid();
            }

            //sharte payan
            boolean contCond = false;
            for (int i = 0; i < clusters.length; i++) {
                if (!clusters[i].nodes.isEmpty()) {
                    System.out.println(euclideanDistance(oldCenteroids[i], clusters[i].centroid));
                    if (euclideanDistance(oldCenteroids[i], clusters[i].centroid) != 0) {
                        contCond = true;
                        break;

                    }

                }

            }
            /*
            for (int i = 0; i < clusters.length; i++) {
                System.out.println("cluster " + i + ";");
                for (int j = 0; j < clusters[i].words.size(); j++) {
                    System.out.println(clusters[i].words.get(j));
                }
            }
            */
            if (iteration > maxIter) {
                System.out.println("be tahe tedad resid");

                break;
            }
            if (contCond == false) {
                endCond = false;
                System.out.println("be khatere hamgerayi");

            }
            iteration++;

        }
        return clusters;

    }

    public void calculateWithCosDistance(List<String> words, double[][] vectors, int k, int maxIter) {
        Cluster[] clusters = new Cluster[k];
        //set k ta random vector baraye centriod ha
        Random rndom = new Random();
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster();
            clusters[i].centroid = vectors[rndom.nextInt(vectors.length - 1)];
        }

        // hala mirim soraghe halghe asli
        boolean endCond = true;
        int iteration = 0;
        while (endCond) {
            //asle majara
            System.out.println("iteration = " + iteration);
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].nodes.clear();
                clusters[i].words.clear();
            }
            for (int i = 0; i < vectors.length; i++) {
                // be dalile inke cosDist NaN ziad dare 0 mikonim index shoroo ra
                int maxIndex = -1;
                double minDist = 1000;
                double temp = 0;
                for (int j = 0; j < clusters.length; j++) {

                    temp = cosDistance(vectors[i], clusters[j].centroid);
                    //if(Double.isNaN(temp)){
                    //    temp = 1;
                    //}
                    //System.out.println(temp);
                    if (temp < minDist) {
                        minDist = temp;
                        maxIndex = j;
                    }
                }
                if (maxIndex < 0) {
                    clusters[rndom.nextInt(clusters.length)].nodes.add(vectors[i]);
                    clusters[rndom.nextInt(clusters.length)].words.add(words.get(i));
                } else {

                    clusters[maxIndex].nodes.add(vectors[i]);
                    clusters[maxIndex].words.add(words.get(i));
                }

            }

            // jadid shodan centriod ha
            double[][] oldCenteroids = new double[k][clusters[0].centroid.length];
            for (int i = 0; i < oldCenteroids.length; i++) {
                oldCenteroids[i] = clusters[i].centroid;
            }

            for (int i = 0; i < clusters.length; i++) {
                clusters[i].refreshCentroid();
            }

            //sharte payan
            boolean contCond = false;
            for (int i = 0; i < clusters.length; i++) {
                if (!clusters[i].nodes.isEmpty()) {
                    if (euclideanDistance(oldCenteroids[i], clusters[i].centroid) != 0) {
                        System.out.println("hanooz edame darad");
                        contCond = true;
                        break;
                    }
                }

            }

            for (int i = 0; i < clusters.length; i++) {
                System.out.println("cluster " + i + ";");
                for (int j = 0; j < clusters[i].words.size(); j++) {
                    System.out.println(clusters[i].words.get(j));
                }
            }

            if (iteration > maxIter) {
                System.out.println("be tahe tedad resid");
                break;
            }
            if (contCond == false) {
                endCond = false;
                System.out.println("be khatere hamgerayi");
            }
            iteration++;

        }

    }

    public double cosDistance(double[] vector1, double[] vector2) {
        double dotProdut = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dotProdut += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        double dax = 1 - ((dotProdut) / (Math.sqrt(norm1) * Math.sqrt(norm2)));

        //System.out.println(dax);
        return dax;
    }

    public double euclideanDistance(double[] vector1, double[] vector2) {
        double result = 0;
        for (int i = 0; i < vector1.length; i++) {
            result += Math.pow(vector1[i] - vector2[i], 2);
        }
        double dax = Math.sqrt(result);

        return dax;
    }

}
