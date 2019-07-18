/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
//import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Reza
 */
public class ModelReader {

    public List<String> words;
    public double[][] vectors;
    public final int endOfSentenceIndex = 5;

    public List<String> positiveSentiments;
    public List<String> negativeSentiments;
    public List<String> defaultSentimentLexicon;
    public List<String> allPositiveSentiments;
    public List<String> allNegativeSentiments;
    public List<String> allSentiments;

    public Hashtable<String, Integer> aspectsTable;

    public List<String> headAspects;
    public List<String> allAspects;
    // final aspects shamele head aspects va khoroojihaye oon KNN hast
    public List<String> finalAspects;

    public ModelReader() {
        words = new LinkedList<String>();

        positiveSentiments = new LinkedList<>();
        negativeSentiments = new LinkedList<>();

        defaultSentimentLexicon = new LinkedList<>();

        allPositiveSentiments = new LinkedList<>();
        allNegativeSentiments = new LinkedList<>();

        allSentiments = new LinkedList<>();

    }

    public void loadModel(String wordsFileAddress, String vectorsFileAddress) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(wordsFileAddress));
        String currentLine = br.readLine();

        //System.out.println(currentLine);
        while (currentLine != null) {
            words.add(currentLine);
            currentLine = br.readLine();

        }

        int i = 0;
        //int j = 0;
        vectors = new double[words.size()][200];
        //br = new BufferedReader(new FileReader(vectorsFileAddress));
        Scanner scanner = new Scanner(new File(vectorsFileAddress));
        while (scanner.hasNext()) {
            currentLine = scanner.next();
            String[] line = currentLine.split(",");
            for (int j = 0; j < line.length; j++) {
                vectors[i][j] = Double.parseDouble(line[j]);
            }
            i++;

        }
        System.out.println("i = " + i);
        //System.out.println(scanner.nextLine());
        //System.out.println(scanner.nextLine().split(",")[0]);

    }

    public void loadSentiments(String SentimentsFileAddress) throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(SentimentsFileAddress);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("Synset");

        System.out.println("----------------------------");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            Element eElement = (Element) nNode;
            if (eElement.getAttribute("Label").equals("+1")) {
                String[] temp = eElement.getAttribute("Sense").split(",");
                for (int j = 0; j < temp.length; j++) {
                    //System.out.println(temp[j]);
                    temp[j] = temp[j].replaceAll("ي", "ی");
                    positiveSentiments.add(temp[j]);
                    defaultSentimentLexicon.add(temp[j]);
                    if (temp[j].split(" ").length > 1) {
                        String dax = temp[j].replaceAll(" ", "_");
                        positiveSentiments.add(dax);
                        defaultSentimentLexicon.add(dax);

                    }
                }
            } else if (eElement.getAttribute("Label").equals("-1")) {
                String[] temp = eElement.getAttribute("Sense").split(",");
                for (int j = 0; j < temp.length; j++) {
                    //System.out.println(temp[j]);
                    temp[j] = temp[j].replaceAll("ي", "ی");
                    negativeSentiments.add(temp[j]);
                    defaultSentimentLexicon.add(temp[j]);
                    if (temp[j].split(" ").length > 1) {
                        String dax = temp[j].replaceAll(" ", "_");
                        positiveSentiments.add(dax);
                        defaultSentimentLexicon.add(dax);
                    }
                }
            }

        }
        // inja oonayi ke kam dare to LexiPers va bayad dasti ezafe beshe
        positiveSentiments.add("عالی");
        positiveSentiments.add("بهتر");

    }

    public String[] findPositiveSentiments(double Similarity_Threshhold, int Min_Edit_Distance) {
        List<String> allSentiments = new LinkedList<String>();

        for (int i = 0; i < positiveSentiments.size(); i++) {
            String currentSentiment = positiveSentiments.get(i);
            System.out.println("curr = " + currentSentiment);
            allSentiments.add(currentSentiment);
            allPositiveSentiments.add(currentSentiment);
            this.allSentiments.add(currentSentiment);

            String[] similars = mostSimilar(currentSentiment, Similarity_Threshhold);
            if (similars != null) {
                for (int j = 0; j < similars.length; j++) {
                    if (isRoot4(currentSentiment, similars[j], Min_Edit_Distance)) {
                        if (!allPositiveSentiments.contains(similars[j])) {
                            allSentiments.add(similars[j]);
                            allPositiveSentiments.add(similars[j]);
                            this.allSentiments.add(similars[j]);
                            System.out.println(similars[j]);
                            for (int k = 0; k < similars.length; k++) {
                                if (isRoot4(similars[j], similars[k], Min_Edit_Distance)) {
                                    if (!allPositiveSentiments.contains(similars[k])) {
                                        allSentiments.add(similars[k]);
                                        allPositiveSentiments.add(similars[k]);
                                        this.allSentiments.add(similars[k]);
                                        System.out.println(similars[k]);
                                    }
                                }

                            }

                        }
                    }
                }
            }
            System.out.println("----");
        }
        String[] dax = (String[]) allSentiments.toArray(new String[allSentiments.size()]);
        return dax;

    }

    public String[] findNegativeSentiments(double Similarity_Threshhold, int Min_Edit_Distance) {
        List<String> allSentiments = new LinkedList<String>();

        for (int i = 0; i < negativeSentiments.size(); i++) {
            String currentSentiment = negativeSentiments.get(i);
            System.out.println("curr = " + currentSentiment);
            allSentiments.add(currentSentiment);
            allNegativeSentiments.add(currentSentiment);
            this.allSentiments.add(currentSentiment);

            String[] similars = mostSimilar(currentSentiment, Similarity_Threshhold);
            if (similars != null) {
                for (int j = 0; j < similars.length; j++) {
                    if (isRoot4(currentSentiment, similars[j], Min_Edit_Distance)) {
                        if (!allNegativeSentiments.contains(similars[j])) {
                            allSentiments.add(similars[j]);
                            allNegativeSentiments.add(similars[j]);
                            this.allSentiments.add(similars[j]);
                            System.out.println(similars[j]);
                            for (int k = 0; k < similars.length; k++) {
                                if (isRoot4(similars[j], similars[k], Min_Edit_Distance)) {
                                    if (!allNegativeSentiments.contains(similars[k])) {
                                        allSentiments.add(similars[k]);
                                        allNegativeSentiments.add(similars[k]);
                                        this.allSentiments.add(similars[k]);
                                        System.out.println(similars[k]);
                                    }
                                }

                            }

                        }
                    }
                }
            }
            System.out.println("----");
        }
        String[] dax = (String[]) allSentiments.toArray(new String[allSentiments.size()]);
        return dax;
    }

    public String[] mostSimilar(String word, double Similarity_Threshhold) {
        if (!words.contains(word)) {
            return null;
        }
        List<String> result = new LinkedList<String>();

        for (int i = 1; i < words.size(); i++) {
            if (cosDistance(vectors[words.indexOf(word)], vectors[i]) > Similarity_Threshhold) {
                result.add(words.get(i));

            }

        }

        String[] dax = (String[]) result.toArray(new String[result.size()]);
        return dax;

    }

    public String[] mostSimilar(double[] vector, double similarity) {
        List<String> result = new LinkedList<String>();
        for (int i = 1; i < words.size(); i++) {
            if (cosDistance(vector, vectors[i]) > similarity) {
                result.add(words.get(i));
            }
        }

        String[] dax = (String[]) result.toArray(new String[result.size()]);
        return dax;
    }

    public String[] mostSimilarAspects(double[] centriodVector, double similarity) {
        List<String> result = new LinkedList<String>();
        for (int i = 1; i < allAspects.size(); i++) {
            if (cosDistance(centriodVector, vectors[words.indexOf(allAspects.get(i))]) > similarity) {
                result.add(allAspects.get(i));
            }
        }

        String[] dax = (String[]) result.toArray(new String[result.size()]);
        return dax;
    }

    public double cosDistance(double[] vector1, double[] vector2) {
        double dotProdut = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < 200; i++) {
            dotProdut += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);

        }
        double dax = dotProdut / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return dax;
    }

    private boolean isRoot(String currentSentiment, String similar, int Min_Edit_Distance) {
        int index = 0;
        int mistake = 0;
        String temp = similar;
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }

        for (int i = 0; i < currentSentiment.length(); i++) {
            if (similar.indexOf(currentSentiment.charAt(i), index) == index + 1) {
                mistake++;
                if (mistake == 1 + (currentSentiment.length() / 5)) {
                    return false;
                }
            } else {
                index = similar.indexOf(currentSentiment.charAt(i), index) + 1;
            }
        }
        //String temp = similar;
        /// preprocess
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }

        temp = temp.replaceAll("تر", "");
        temp = temp.replaceAll("ترین", "");

        if (temp.endsWith("یه")) {
            temp = temp.replaceAll("یه", "ی");
        }
        if (temp.endsWith("ست")) {
            temp = temp.replaceAll("ست", "");
        }
        if (temp.endsWith("تون")) {
            temp = temp.replaceAll("تون", "");
        }
        if (temp.endsWith("ای")) {
            temp = temp.replaceAll("ای", "");
        }

        /// end of preprocess
        //if(temp.contains(currentSentiment)){
        //    return true;
        //}
        if (minDistance(temp, currentSentiment) < Min_Edit_Distance + (currentSentiment.length() / 5)) {
            return true;
        }

        return false;
    }

    private boolean isRoot2(String currentSentiment, String similar, int Min_Edit_Distance) {
        int index = 0;
        int mistake = 0;

        String temp = similar;
        /// preprocess
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }

        temp = temp.replaceAll("تر", "");
        temp = temp.replaceAll("ترین", "");

        if (temp.endsWith("یه")) {
            temp = temp.replaceAll("یه", "ی");
        }
        if (temp.endsWith("ست")) {
            temp = temp.replaceAll("ست", "ه");
        }
        if (temp.endsWith("تون")) {
            temp = temp.replaceAll("تون", "");
        }
        if (temp.endsWith("ای")) {
            temp = temp.replaceAll("ای", "");
        }

        /// end of preprocess
        if (temp.contains(currentSentiment) && minDistance(temp, currentSentiment) < Min_Edit_Distance + (currentSentiment.length() / 4)) {
            return true;
        }

        return false;
    }

    private boolean isRoot3(String currentSentiment, String similar, int Min_Edit_Distance) {
        int index = 0;
        int mistake = 0;
        String temp = similar;
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }
        //index = similar.indexOf(currentSentiment.charAt(0));
        for (int i = 0; i < currentSentiment.length(); i++) {
            if (similar.indexOf(currentSentiment.charAt(i), index) < index + 1) {
                mistake++;
                if (mistake == 1 + (currentSentiment.length() / 5)) {
                    return false;
                }
            } else {
                index = similar.indexOf(currentSentiment.charAt(i), index);
            }
        }
        //String temp = similar;
        /// preprocess
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }

        temp = temp.replaceAll("تر", "");
        temp = temp.replaceAll("ترین", "");

        if (temp.endsWith("یه")) {
            temp = temp.replaceAll("یه", "ی");
        }
        if (temp.endsWith("ست")) {
            temp = temp.replaceAll("ست", "");
        }
        if (temp.endsWith("تون")) {
            temp = temp.replaceAll("تون", "");
        }
        if (temp.endsWith("ای")) {
            temp = temp.replaceAll("ای", "");
        }

        /// end of preprocess
        //if(temp.contains(currentSentiment)){
        //    return true;
        //}
        if (minDistance(temp, currentSentiment) < Min_Edit_Distance + (currentSentiment.length() / 5)) {
            return true;
        }

        return false;
    }

    private boolean isRoot4(String currentSentiment, String similar, int Min_Edit_Distance) {
        int index = 0;
        int mistake = 0;
        String temp = similar;
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }
        //index = similar.indexOf(currentSentiment.charAt(0));

        int[] indexes = new int[currentSentiment.length()];
        for (int i = 0; i < currentSentiment.length(); i++) {
            indexes[i] = similar.indexOf(currentSentiment.charAt(i));
        }

        for (int i = 0; i < indexes.length - 1; i++) {
            if (indexes[i] == -1) {
                mistake++;
            } else if (indexes[i] > indexes[i + 1] && indexes[i + 1] > -1) {
                mistake++;
            }

        }

        if (mistake >= Min_Edit_Distance - 1 + (currentSentiment.length() / 5)) {
            return false;
        }

        //String temp = similar;
        /// preprocess
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceAll("اا", "ا");
            temp = temp.replaceAll("وو", "و");
            temp = temp.replaceAll("یی", "ی");
        }

        temp = temp.replaceAll("تر", "");
        temp = temp.replaceAll("ترین", "");

        if (temp.endsWith("یه")) {
            temp = temp.replaceAll("یه", "ی");
        }
        if (temp.endsWith("ست")) {
            temp = temp.replaceAll("ست", "");
        }
        if (temp.endsWith("تون")) {
            temp = temp.replaceAll("تون", "");
        }
        if (temp.endsWith("ای")) {
            temp = temp.replaceAll("ای", "");
        }

        /// end of preprocess
        //if(temp.contains(currentSentiment)){
        //    return true;
        //}
        if (minDistance(temp, currentSentiment) < Min_Edit_Distance + (currentSentiment.length() / 5)) {
            return true;
        }

        return false;
    }

    public int minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    // khodam gozashtam 2
                    int replace = dp[i][j] + 2;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }

    public String[][] sentimentTagger(String[] allPositiveSentiment, String[] allNegativeSentiment, String[][] comments) {
        List<String[]> result = new LinkedList<String[]>();
        for (int i = 0; i < comments.length; i++) {
            boolean end = false;
            String[] temp = {"null", "null", "null"};
            temp[0] = comments[i][0];
            // inja mohemme dige hamoonjayi ke 4 ta shodan 2ta
            temp[1] = comments[i][3];
            for (int j = 0; j < allPositiveSentiment.length; j++) {
                // && taze ezafe shode va faghat AJ ha ra entekhab mikonad

                if (comments[i][0].equals(allPositiveSentiment[j])) {
                    String[] posTaggs = comments[i][3].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("AJ") || posTaggs[k].equals("AJe")) {
                            temp[2] = "SPos";
                            end = true;
                            break;
                        }
                    }

                }
            }
            if (end) {
                result.add(temp);
                continue;
            }
            for (int j = 0; j < allNegativeSentiment.length; j++) {
                if (comments[i][0].equals(allNegativeSentiment[j])) {
                    String[] posTaggs = comments[i][3].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("AJ") || posTaggs[k].equals("AJe")) {
                            temp[2] = "SNeg";
                            //end = true;
                            break;

                        }
                    }
                }
            }

            result.add(temp);
        }

        String[][] dax = (String[][]) result.toArray(new String[result.size()][3]);
        return dax;
    }

    public String[][] findAspectCandidates(String[][] comments) {
        //These Comments have Sentiment Taggs
        List<String[]> result = new LinkedList<String[]>();
        for (int i = 0; i < comments.length; i++) {
            //System.out.println(i);
            boolean validSent = false;
            int j = 0;
            while (!comments[i + j][0].equals("null") && i + j < comments.length) {
                //System.out.println(j);
                //if (comments[i + j][2] != null) {
                //if (comments[i + j][2].equals("null")) {
                if (comments[i + j][2].equals("SNeg") || comments[i + j][2].equals("SPos")) {
                    validSent = true;
                }
                //}

                j++;

            }
            if (!validSent) {
                for (int index = 0; index < j; index++) {
                    result.add(comments[i]);
                    i++;

                }

            } else {
                for (int index = 0; index < j; index++) {
                    String[] posTaggs = comments[i][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i][2].equals("null")) {
                                comments[i][2] = "Aspect";

                            }
                            break;
                        }
                    }

                    result.add(comments[i]);
                    i++;

                }

            }
            result.add(comments[i]);

        }

        String[][] dax = (String[][]) result.toArray(new String[result.size()][3]);
        return dax;

    }

    public String[][] findAspectCandidates2(String[][] comments) {
        //These Comments have Sentiment Taggs
        //String[][] temp = new String[comments.length][comments[0].length];
        //List<String[]> result = new LinkedList<String[]>();
        for (int i = 0; i < comments.length; i++) {
            if (comments[i][2].equals("SNeg") || comments[i][2].equals("SPos")) {
                boolean aspectCandidateFound = false;
                if (i - 1 >= 0) {
                    String[] posTaggs = comments[i - 1][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i - 1][2].equals("null")) {
                                comments[i - 1][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }
                }

                if (i - 2 >= 0 && !aspectCandidateFound && !comments[i - 1][0].equals("null")) {
                    String[] posTaggs = comments[i - 2][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i - 2][2].equals("null")) {
                                comments[i - 2][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }

                }

                if (i + 1 < comments.length && !aspectCandidateFound) {
                    String[] posTaggs = comments[i + 1][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i + 1][2].equals("null")) {
                                comments[i + 1][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }

                }

                if (i + 2 < comments.length && !aspectCandidateFound && !comments[i + 1][0].equals("null")) {
                    String[] posTaggs = comments[i + 2][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i + 2][2].equals("null")) {
                                comments[i + 2][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }

                }

            }

        }

        //String[][] dax = (String[][]) result.toArray(new String[result.size()][3]);
        return comments;

    }

    // in yeki ham window e 5 ro check mikone va ham fasele cosinoosi beyne sentiment va aspect
    public String[][] findAspectCandidates3(String[][] comments) {
        //These Comments have Sentiment Taggs
        //String[][] temp = new String[comments.length][comments[0].length];
        //List<String[]> result = new LinkedList<String[]>();
        for (int i = 0; i < comments.length; i++) {
            if (comments[i][2].equals("SNeg") || comments[i][2].equals("SPos")) {
                boolean aspectCandidateFound = false;
                if (i - 1 >= 0) {
                    String[] posTaggs = comments[i - 1][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i - 1][2].equals("null")) {
                                comments[i - 1][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }
                }

                if (i - 2 >= 0 && !aspectCandidateFound && !comments[i - 1][0].equals("null")) {
                    String[] posTaggs = comments[i - 2][1].split("_");
                    for (int k = 0; k < posTaggs.length; k++) {
                        if (posTaggs[k].equals("N") || posTaggs[k].equals("Ne")) {
                            if (comments[i - 2][2].equals("null")) {
                                comments[i - 2][2] = "Aspect";
                                aspectCandidateFound = true;

                            }
                            //aspectCandidateFound = true;
                            break;
                        }
                    }
                }
            }

        }

        //String[][] dax = (String[][]) result.toArray(new String[result.size()][3]);
        return comments;

    }

    public String[][] deleteAspectsWithLowFereq(String[][] comments, int Min_Aspect_Frequency) {
        Hashtable<String, Integer> aspects = new Hashtable();
        List<String> aspectW = new LinkedList<String>();
        for (int i = 0; i < comments.length; i++) {
            if (comments[i][2].equals("Aspect")) {
                if (aspects.containsKey(comments[i][0])) {
                    int count = aspects.get(comments[i][0]) + 1;
                    aspects.remove(comments[i][0]);
                    aspects.put(comments[i][0], count);

                } else {
                    aspects.put(comments[i][0], 1);
                    aspectW.add(comments[i][0]);

                }
            }
        }
        aspectsTable = aspects;
        System.out.println("all Aspects: " + aspects.size());
        //int dax = aspects.size();
        allAspects = new LinkedList<String>();
        headAspects = new LinkedList<String>();
        finalAspects = new LinkedList<String>();
        for (int i = 0; i < aspectW.size(); i++) {
            //System.out.println(aspectW.get(i));
            if (aspects.get(aspectW.get(i)) >= Min_Aspect_Frequency) {
                allAspects.add(aspectW.get(i));

            }
            if (aspects.get(aspectW.get(i)) >= 700) {
                headAspects.add(aspectW.get(i));
                finalAspects.add(aspectW.get(i));

            }
            //System.out.println(aspects.get(aspectW.get(i)));
            //System.out.println("------------");
        }
        System.out.println("all Aspects: " + allAspects.size());
        System.out.println("head Aspects: " + headAspects.size());

        for (int i = 0; i < comments.length; i++) {
            if (comments[i][2].equals("Aspect")) {
                if (aspects.get(comments[i][0]) < Min_Aspect_Frequency) {
                    comments[i][2] = "null";

                }
            }
        }

        return comments;
    }

    public void findHeadAspects(String[][] commentsWithSentimentTaggs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void test(String[] evalComments) {

        System.out.println("========================");
        System.out.println("========================");
        System.out.println("========================");
        System.out.println("Test Started");
        double allAsp = 0;
        double correctOnAsp = 0;
        double allSen = 0;
        double correctOnSen = 0;
        for (int i = 0; i < evalComments.length; i++) {
            boolean aspFound = false;
            boolean sentFound = false;
            if (evalComments[i].contains("Aspect")) {
                String[] temp1 = evalComments[i].split(" ");
                System.out.println("curr Aspect:" + evalComments[i]);
                allAsp++;
                for (int k = 0; k < temp1.length; k++) {
                    for (int j = 0; j < allAspects.size(); j++) {
                        if (temp1[k].equals(allAspects.get(j))) {
                            correctOnAsp++;
                            System.out.println(allAspects.get(j));
                            aspFound = true;
                            break;
                        }
                    }
                    if (aspFound) {
                        break;
                    }
                }
                if (!aspFound) {

                    String temp2 = evalComments[i].substring(7, evalComments[i].length()).replaceAll(" ", "_");
                    for (int j = 0; j < allAspects.size(); j++) {
                        if (temp2.equals(allAspects.get(j))) {
                            correctOnAsp++;
                            System.out.println(allAspects.get(j));
                            break;

                        }
                    }
                }

            }
            if (evalComments[i].contains("Sentiment")) {
                String[] temp1 = evalComments[i].split(" ");
                System.out.println("curr Sentiment:" + evalComments[i]);
                allSen++;
                for (int k = 0; k < temp1.length; k++) {
                    for (int j = 0; j < allPositiveSentiments.size(); j++) {
                        if (temp1[k].equals(allPositiveSentiments.get(j))) {
                            correctOnSen++;
                            System.out.println(allPositiveSentiments.get(j));
                            sentFound = true;
                            break;
                        }
                    }
                    if (sentFound) {
                        break;
                    }
                }
                if (!sentFound) {
                    String temp2 = evalComments[i].substring(10, evalComments[i].length()).replaceAll(" ", "_");
                    for (int j = 0; j < allPositiveSentiments.size(); j++) {
                        if (temp2.equals(allPositiveSentiments.get(j))) {
                            correctOnSen++;
                            System.out.println(allPositiveSentiments.get(j));
                            break;
                        }
                    }
                }

                for (int k = 0; k < temp1.length; k++) {
                    for (int j = 0; j < allNegativeSentiments.size(); j++) {
                        if (temp1[k].equals(allNegativeSentiments.get(j))) {
                            correctOnSen++;
                            System.out.println(allNegativeSentiments.get(j));
                            sentFound = true;
                            break;
                        }
                    }
                    if (sentFound) {
                        break;
                    }
                }
                if (!sentFound) {
                    String temp2 = evalComments[i].substring(10, evalComments[i].length()).replaceAll(" ", "_");
                    for (int j = 0; j < allNegativeSentiments.size(); j++) {
                        if (temp2.equals(allNegativeSentiments.get(j))) {
                            correctOnSen++;
                            System.out.println(allNegativeSentiments.get(j));
                            break;
                        }
                    }
                }

            }
            if (!evalComments[i].contains("Aspect") && !evalComments[i].contains("Sentiment")) {

            }
        }
        System.out.println("correct on aspects= " + correctOnAsp);
        System.out.println("all aspects= " + allAsp);
        System.out.println("correct on sent= " + correctOnSen);
        System.out.println("all sent= " + allSen);
    }

    public void getPrecisionRecallForSentimens(String[][] evalCommentsNewFromat) {
        double truePositive = 0;
        double trueNegative = 0;
        double falsePoitive = 0;
        double falseNegative = 0;

        for (int i = 0; i < evalCommentsNewFromat.length; i++) {
            if (allSentiments.contains(evalCommentsNewFromat[i][0])) {
                if (!evalCommentsNewFromat[i][1].equals("Sentiment") && !evalCommentsNewFromat[i][1].equals("Sentimente")) {

                    boolean sentimentFound = false;
                    int dynamicIndex = -1;
                    while (i + dynamicIndex >= 0 && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex > -endOfSentenceIndex) {

                        if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect")) {
                            falsePoitive++;
                            sentimentFound = true;
                            break;
                        }
                        dynamicIndex--;
                    }
                    dynamicIndex = 1;
                    if (sentimentFound == false) {
                        while (i + dynamicIndex < evalCommentsNewFromat.length && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null")
                                && dynamicIndex < endOfSentenceIndex) {

                            if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect")) {
                                falsePoitive++;
                                sentimentFound = true;
                                break;
                            }
                            dynamicIndex++;
                        }
                    }

                    //falsePoitive++;
                } else {
                    truePositive++;
                    //inja ye while mikhad
                }
            }
            if (evalCommentsNewFromat[i][1].equals("Sentiment")) {
                if (!allSentiments.contains(evalCommentsNewFromat[i][0])) {
                    if (i + 1 < evalCommentsNewFromat.length && evalCommentsNewFromat[i + 1][1].equals("Sentiment")) {
                        if (!allSentiments.contains(evalCommentsNewFromat[i][0] + "_" + evalCommentsNewFromat[i + 1][0])
                                && !allSentiments.contains(evalCommentsNewFromat[i + 1][0])) {
                            falseNegative++;
                        }
                    } else {
                        falseNegative++;
                    }
                }
            }
        }
        double precision = truePositive / (truePositive + falsePoitive);
        double recall = truePositive / (truePositive + falseNegative);
        double f1Measure = (2 * precision * recall) / (precision + recall);

        System.out.println("=====================");
        System.out.println("ON Sentiments");

        System.out.println("true Positive = " + truePositive);
        System.out.println("fasle Positive = " + falsePoitive);
        System.out.println("false Negative = " + falseNegative);
        //System.out.println("true Positive = "+truePositive);
        System.out.println("recall = " + recall);
        System.out.println("precision = " + precision);
        System.out.println("f1 = " + f1Measure);

        truePositive = 0;
        trueNegative = 0;
        falsePoitive = 0;
        falseNegative = 0;
        for (int i = 0; i < evalCommentsNewFromat.length; i++) {
            if (defaultSentimentLexicon.contains(evalCommentsNewFromat[i][0])) {
                if (!evalCommentsNewFromat[i][1].equals("Sentiment") && !evalCommentsNewFromat[i][1].equals("Sentimente")) {

                    boolean sentimentFound = false;
                    int dynamicIndex = -1;
                    while (i + dynamicIndex >= 0 && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex > -endOfSentenceIndex) {

                        if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect")) {
                            falsePoitive++;
                            sentimentFound = true;
                            break;
                        }
                        dynamicIndex--;
                    }
                    dynamicIndex = 1;
                    if (sentimentFound == false) {
                        while (i + dynamicIndex < evalCommentsNewFromat.length && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null")
                                && dynamicIndex < endOfSentenceIndex) {

                            if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Aspect")) {
                                falsePoitive++;
                                sentimentFound = true;
                                break;
                            }
                            dynamicIndex++;
                        }
                    }

                    //falsePoitive++;
                } else {
                    truePositive++;
                    //inja ye while mikhad
                }
            }
            if (evalCommentsNewFromat[i][1].equals("Sentiment")) {
                if (!defaultSentimentLexicon.contains(evalCommentsNewFromat[i][0])) {
                    if (i + 1 < evalCommentsNewFromat.length && evalCommentsNewFromat[i + 1][1].equals("Sentiment")) {
                        if (!defaultSentimentLexicon.contains(evalCommentsNewFromat[i][0] + "_" + evalCommentsNewFromat[i + 1][0])
                                && !defaultSentimentLexicon.contains(evalCommentsNewFromat[i + 1][0])) {
                            falseNegative++;
                        }
                    } else {
                        falseNegative++;
                    }
                }
            }
        }
        precision = truePositive / (truePositive + falsePoitive);
        recall = truePositive / (truePositive + falseNegative);
        f1Measure = (2 * precision * recall) / (precision + recall);

        System.out.println("=====================");
        System.out.println("ON Sentiments Lexicon");

        System.out.println("true Positive = " + truePositive);
        System.out.println("fasle Positive = " + falsePoitive);
        System.out.println("false Negative = " + falseNegative);
        //System.out.println("true Positive = "+truePositive);
        System.out.println("recall = " + recall);
        System.out.println("precision = " + precision);
        System.out.println("f1 = " + f1Measure);

    }

    public void getPrecisionRecallForAspects(String[][] evalCommentsNewFromat) {
        double truePositive = 0;
        double trueNegative = 0;
        double falsePoitive = 0;
        double falseNegative = 0;
        for (int i = 0; i < evalCommentsNewFromat.length; i++) {
            if (allAspects.contains(evalCommentsNewFromat[i][0])) {
                if (!evalCommentsNewFromat[i][1].equals("Aspect") && !evalCommentsNewFromat[i][1].equals("Aspecte")) {

                    boolean sentimentFound = false;
                    int dynamicIndex = -1;
                    while (i + dynamicIndex >= 0 && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex > -endOfSentenceIndex) {

                        if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentiment") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentimente")) {
                            falsePoitive++;
                            sentimentFound = true;
                            break;
                        }
                        dynamicIndex--;
                    }
                    dynamicIndex = 1;
                    if (sentimentFound == false) {
                        while (i + dynamicIndex < evalCommentsNewFromat.length && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex < endOfSentenceIndex) {

                            if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentiment") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentimente")) {
                                falsePoitive++;
                                sentimentFound = true;
                                break;
                            }
                            dynamicIndex++;
                        }
                    }

                    //falsePoitive++;
                } else {
                    truePositive++;
                    //inja ye while mikhad
                }
            }
            if (evalCommentsNewFromat[i][1].equals("Aspect")) {
                if (!allAspects.contains(evalCommentsNewFromat[i][0])) {
                    if (i + 1 < evalCommentsNewFromat.length && evalCommentsNewFromat[i + 1][1].equals("Aspecte")) {
                        if (!allAspects.contains(evalCommentsNewFromat[i][0] + "_" + evalCommentsNewFromat[i + 1][0])) {
                            falseNegative++;
                        }
                    } else {
                        falseNegative++;
                    }
                }
            }
        }
        double precision = truePositive / (truePositive + falsePoitive);
        double recall = truePositive / (truePositive + falseNegative);
        double f1Measure = (2 * precision * recall) / (precision + recall);

        System.out.println("=====================");
        System.out.println("ON ALL ASPECTS");

        System.out.println("true Positive = " + truePositive);
        System.out.println("fasle Positive = " + falsePoitive);
        System.out.println("false Negative = " + falseNegative);
        //System.out.println("true Positive = "+truePositive);
        System.out.println("recall = " + recall);
        System.out.println("precision = " + precision);
        System.out.println("f1 = " + f1Measure);

        truePositive = 0;
        trueNegative = 0;
        falsePoitive = 0;
        falseNegative = 0;
        for (int i = 0; i < evalCommentsNewFromat.length; i++) {
            if (finalAspects.contains(evalCommentsNewFromat[i][0])) {
                if (!evalCommentsNewFromat[i][1].equals("Aspect") && !evalCommentsNewFromat[i][1].equals("Aspecte")) {
                    boolean sentimentFound = false;
                    int dynamicIndex = -1;
                    while (i + dynamicIndex >= 0 && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex > -endOfSentenceIndex) {

                        if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentiment") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentimente")) {
                            falsePoitive++;
                            sentimentFound = true;
                            break;
                        }
                        dynamicIndex--;
                    }
                    dynamicIndex = 1;
                    if (sentimentFound == false) {
                        while (i + dynamicIndex < evalCommentsNewFromat.length && !evalCommentsNewFromat[i + dynamicIndex][0].equals("null") && dynamicIndex < endOfSentenceIndex) {

                            if (evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentiment") || evalCommentsNewFromat[i + dynamicIndex][1].equals("Sentimente")) {
                                falsePoitive++;
                                sentimentFound = true;
                                break;
                            }
                            dynamicIndex++;
                        }
                    }

                    //falsePoitive++;
                } else {
                    truePositive++;
                    //inja ye while mikhad
                }
            }
            if (evalCommentsNewFromat[i][1].equals("Aspect")) {
                if (!finalAspects.contains(evalCommentsNewFromat[i][0])) {
                    if (i + 1 < evalCommentsNewFromat.length && evalCommentsNewFromat[i + 1][1].equals("Aspecte")) {
                        if (!finalAspects.contains(evalCommentsNewFromat[i][0] + "_" + evalCommentsNewFromat[i + 1][0])) {
                            falseNegative++;
                        }
                    } else {
                        falseNegative++;
                    }
                }
            }
        }
        precision = truePositive / (truePositive + falsePoitive);
        recall = truePositive / (truePositive + falseNegative);
        f1Measure = (2 * precision * recall) / (precision + recall);

        System.out.println("=====================");
        System.out.println("ON FINAL ASPECTS");

        System.out.println("true Positive = " + truePositive);
        System.out.println("fasle Positive = " + falsePoitive);
        System.out.println("false Negative = " + falseNegative);
        //System.out.println("true Positive = "+truePositive);
        System.out.println("recall = " + recall);
        System.out.println("precision = " + precision);
        System.out.println("f1 = " + f1Measure);

    }

    public void aspectSentimentClassification(String[] evalComments, String[][] evalCommentsNewFormat) {
        double correct = 0;
        double error = 0;
        System.out.println("===============================");
        int newFormatIndex = 0;
        for (int index = 0; index < evalComments.length; index++) {
            int sentenceLength = 0;
            while (!evalComments[index + sentenceLength].equals("null")) {
                sentenceLength++;
            }

            int newFormatSentenceLength = 0;
            while (!evalCommentsNewFormat[newFormatIndex + newFormatSentenceLength][0].equals("null")) {
                newFormatSentenceLength++;
            }

            for (int j = 0; j < sentenceLength; j++) {
                System.out.println(evalComments[index + j]);
            }
            System.out.println("-----");

            for (int j = 0; j < newFormatSentenceLength; j++) {
                System.out.println(evalCommentsNewFormat[newFormatIndex + j][0] + "\t" + evalCommentsNewFormat[newFormatIndex + j][1]);
            }
            System.out.println("-----");

            for (int j = 0; j < newFormatSentenceLength; j++) {
                if (evalCommentsNewFormat[newFormatIndex + j][1].equals("Sentiment")) {
                    String SentimentWord = evalCommentsNewFormat[newFormatIndex + j][0];
                    String AspectWord = "";
                    int dax = 1;
                    while (evalCommentsNewFormat[newFormatIndex + j + dax][1].equals("Sentimente")) {
                        SentimentWord = SentimentWord + " " + evalCommentsNewFormat[newFormatIndex + j + dax][0];
                        dax++;
                    }
                    System.out.println(SentimentWord);
                    boolean aspectFound = false;
                    int dynamicIndex = 1;
                    while (!aspectFound) {

                        if (evalCommentsNewFormat[newFormatIndex + j - dynamicIndex][1].equals("Aspect")) {
                            aspectFound = true;
                            AspectWord = evalCommentsNewFormat[newFormatIndex + j - dynamicIndex][0];
                            int dax2 = 1;
                            while (evalCommentsNewFormat[newFormatIndex + j - dynamicIndex + dax2][1].equals("Aspecte")) {
                                AspectWord = AspectWord + " " + evalCommentsNewFormat[newFormatIndex + j - dynamicIndex + dax2][0];
                                dax2++;
                            }
                            System.out.println(AspectWord);
                            break;

                        }
                        if (evalCommentsNewFormat[newFormatIndex + j + dynamicIndex][1].equals("Aspect")) {
                            aspectFound = true;
                            AspectWord = evalCommentsNewFormat[newFormatIndex + j + dynamicIndex][0];
                            int dax2 = 1;
                            while (evalCommentsNewFormat[newFormatIndex + j + dynamicIndex + dax2][1].equals("Aspecte")) {
                                AspectWord = AspectWord + " " + evalCommentsNewFormat[newFormatIndex + j + dynamicIndex + dax2][0];
                                dax2++;
                            }
                            System.out.println(AspectWord);
                            break;

                        }
                        dynamicIndex++;
                    }
                    //hala inja bayad binim doroste fasele ya na
                    for (int k = 1; k < sentenceLength; k++) {
                        if (evalComments[index + k].contains(SentimentWord)) {
                            int dax3 = -1;
                            while (!evalComments[index + k + dax3].contains("Aspect")) {
                                dax3--;
                            }
                            if (evalComments[index + k + dax3].contains(AspectWord)) {
                                System.out.println("yes");
                                correct++;
                            } else {
                                System.out.println("NO");
                                error++;
                            }
                        }

                    }
                }
            }
            System.out.println("===============");

            index = index + sentenceLength;
            newFormatIndex = newFormatIndex + newFormatSentenceLength;
            newFormatIndex++;
        }
        double acc = correct / (correct + error);
        System.out.println("Aspect Sentiment Classification = " + acc);

    }

    public void aspectSentimentClassification2(String[] evalComments, String[][] evalCommentsNewFormat) {
        System.out.println("===============================");

        double correct = 0;
        double error = 0;

        for (int index = 0; index < evalComments.length; index++) {
            int sentenceLength = 0;
            while (!evalComments[index + sentenceLength].equals("null")) {
                sentenceLength++;
            }
            // inja i + sentenceL dare null ro neshoon mide

            String[] sentenceWords = evalComments[index].split(" ");
            for (int j = 0; j < sentenceLength; j++) {
                System.out.println(evalComments[index + j]);
            }

            List sentimentList = new LinkedList<String>();
            List aspectList = new LinkedList<String>();

            for (int j = 0; j < sentenceLength; j++) {
                if (evalComments[index + j].contains("Aspect")) {
                    aspectList.add(evalComments[index + j].substring(7));
                }
                if (evalComments[index + j].contains("Sentiment")) {
                    sentimentList.add(evalComments[index + j].substring(10));
                }
            }

            int[] sentimentIndexes = new int[sentimentList.size()];
            int[] aspectIndexes = new int[aspectList.size()];

            index = index + sentenceLength;

        }

    }

}
