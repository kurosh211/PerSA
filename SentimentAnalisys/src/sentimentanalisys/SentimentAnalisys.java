/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Reza
 */
public class SentimentAnalisys {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        final double Similarity_Threshhold = 0.3;
        final int Min_Edit_Distance = 10;
        final int Min_Aspect_Frequency = 50;
        final int Cluster_Num = 15;

        // TODO code application logic here
        ModelReader model = new ModelReader();
        FileReader fileReader = new FileReader();
        SentiPersReader sentiReader = new SentiPersReader();

        model.loadModel("words V4.txt", "vectors V4.csv");

        ///*
        model.loadSentiments("LexiPers_Adj.xml");
        System.out.println("PSen: " + model.positiveSentiments.size());
        System.out.println("NSen: " + model.negativeSentiments.size());

        System.out.println("=================");

        String[] allPositiveSentiment = model.findPositiveSentiments(Similarity_Threshhold, Min_Edit_Distance);
        System.out.println("=======================================");
        System.out.println("=======================================");
        String[] allNegativeSentiment = model.findNegativeSentiments(Similarity_Threshhold, Min_Edit_Distance);

        String[][] comments = fileReader.readCommentsFile("Comment Mobile all complete with Phrases V4.txt");
        String[][] commentsWithSentimentTaggs = model.sentimentTagger(allPositiveSentiment, allNegativeSentiment, comments);

        String[][] commentsWithAspectAndSentimentTaggs = model.findAspectCandidates2(commentsWithSentimentTaggs);

        String[][] finalComments = model.deleteAspectsWithLowFereq(commentsWithAspectAndSentimentTaggs, Min_Aspect_Frequency);
        //model.findHeadAspects(commentsWithSentimentTaggs);

        FileWriter writer = new FileWriter();
        //writer.writeComments("comments with taggs both v8 delete low fereq with all sentiments using AJ and AJe.txt", finalComments);

//*/
        // inja file arzyabio mikhoonim va dorost mikonim
        String[] evalComments = sentiReader.readxmlFile("SentiPers Data\\");
        String[][] evalCommentsNewFormat = sentiReader.changeFormat(evalComments);

        ///*
        double[][] headAspectVectors = new double[model.headAspects.size()][200];
        for (int i = 0; i < model.headAspects.size(); i++) {
            headAspectVectors[i] = model.vectors[model.words.indexOf(model.headAspects.get(i))];
        }

        KMeans kMeans = new KMeans();
        Cluster[] aspectClusters = kMeans.calculate(model.headAspects, headAspectVectors, Cluster_Num, 50);
        //age KMeans deterministin bashe aali mishe

        // Now We have Clusters and Aspect Tables as model.aspectTable
        System.out.println("======================================");
        System.out.println("Aspect Table:");

        for (int i = 0; i < model.allAspects.size(); i++) {
            System.out.println(model.allAspects.get(i) + " : " + model.aspectsTable.get(model.allAspects.get(i)));
            System.out.println("----------");
        }

        System.out.println("======================================");
        System.out.println("KMeans Clusters");

        for (int i = 0; i < aspectClusters.length; i++) {

            System.out.println("Cluster " + i + " in KMeans");
            for (int j = 0; j < aspectClusters[i].nodes.size(); j++) {
                System.out.println(aspectClusters[i].words.get(j));
            }
        }

        // clearly is KNN with Kmeans centroid
        System.out.println("======================================");
        System.out.println("Clusters with words near centroids");

        for (int i = 0; i < aspectClusters.length; i++) {

            System.out.println("Cluster " + i + " near center");
            String[] temp = model.mostSimilarAspects(aspectClusters[i].centroid, 0.45);
            for (int j = 0; j < temp.length; j++) {
                if (model.aspectsTable.get(temp[j]) > 150) {
                    System.out.println(temp[j]);
                    model.finalAspects.add(temp[j]);

                }

                // if count > 200 ??
            }
            temp = model.mostSimilar(aspectClusters[i].centroid, 0.8);
            for (int j = 0; j < temp.length; j++) {
                //if (model.aspectsTable.get(temp[j]) > 150) {
                    System.out.println(temp[j]);
                    model.finalAspects.add(temp[j]);
                //}
            }
        }

        // seri bad ke khasti faghat Aspect haro bedim behesh avval bayad vector haye final aspect haro bedast biarim va bad calculate konim
        // dar nahayat bayad test beshe
        // finalAspect ro darim AllSentiment ro ham darim evalComment ro ham darim
        //model.test(evalComments);
        model.getPrecisionRecallForAspects(evalCommentsNewFormat);
        model.getPrecisionRecallForSentimens(evalCommentsNewFormat);
       // */

        // model.aspectSentimentClassification(evalComments , evalCommentsNewFormat);
        //inja ye KNN sade ba seeded aspects
    }

}
