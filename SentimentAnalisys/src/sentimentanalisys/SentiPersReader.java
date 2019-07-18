/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalisys;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Reza
 */
public class SentiPersReader {

    public SentiPersReader() {

    }

    public String[] readxmlFile(String folderAddress) throws ParserConfigurationException, SAXException, IOException {
        List<String> result = new LinkedList<String>();

        //Set opinion = new HashSet<String>();
        //Set targetI = new HashSet<String>();
        File folder = new File(folderAddress);
        File[] listOfFiles = folder.listFiles();
        System.out.println(listOfFiles.length);

        for (int indexFile = 0; indexFile < listOfFiles.length; indexFile++) {
            File fXmlFile = listOfFiles[indexFile];
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            if (doc.getDocumentElement().getAttribute("Type").equals("Mobile")) {
                System.out.println(fXmlFile.getName());
                //System.out.println(doc.getDocumentElement().getAttribute("Type"));

                //inja avval tagga peyda mishan
                NodeList tags = doc.getElementsByTagName("Tag");
                //System.out.println(tags.getLength());
                Element[] tagElems = new Element[tags.getLength()];
                for (int i = 0; i < tags.getLength(); i++) {
                    tagElems[i] = (Element) tags.item(i);
                    //System.out.println(tagElems[i].getAttribute("Coordinate"));
                }

                //inja ham miaym yeki yeki kalame haro mikhoonim ta binim chi dare toosh
                NodeList generalReviews = doc.getElementsByTagName("General_Review");
                //System.out.println(generalReviews.getLength());

                for (int i = 0; i < generalReviews.getLength(); i++) {
                    //System.out.println("i=" + i);
                    Element elem = (Element) generalReviews.item(i);
                    NodeList sentences = elem.getElementsByTagName("Sentence");
                    for (int j = 0; j < sentences.getLength(); j++) {
                        //System.out.println("j=" + j);
                        Element sentElem = (Element) sentences.item(j);
                        //System.out.println(sentElem.getTextContent());

                        result.add(sentElem.getTextContent().replaceAll("ي", "ی"));

                        String currSentID = sentElem.getAttribute("ID");

                        for (int k = 0; k < tagElems.length; k++) {
                            if (tagElems[k].getAttribute("Type").equals("Target(I)") || tagElems[k].getAttribute("Type").equals("Opinion")) {

                                String currTagCoordinate = tagElems[k].getAttribute("Coordinate");
                                currTagCoordinate = currTagCoordinate.substring(1, currTagCoordinate.length() - 1);
                                //System.out.println(currTagCoordinate);
                                String temp[] = currTagCoordinate.split(",");
                                //System.out.println(temp[0]);
                                if (temp[0].equals(currSentID)) {
                                    //System.out.println("YES");
                                    String keyWord = sentElem.getTextContent().substring(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
                                    //System.out.println(keyWord);
                                    if (tagElems[k].getAttribute("Type").equals("Target(I)")) {
                                        result.add("Aspect " + keyWord.replaceAll("ي", "ی"));
                                    }
                                    if (tagElems[k].getAttribute("Type").equals("Opinion")) {
                                        result.add("Sentiment " + keyWord.replaceAll("ي", "ی"));
                                    }
                                    // bayad index haro ham ye joori zakhire konim ke bedoonim masalan kodoom aali sentimente
                                    // na hamash doroste na inke avvalish
                                }
                            }
                        }

                        //String[] words = sentElem.getTextContent().split(" ");
                        //for(int k = 0; k<words.length ; k++){
                        //    String[] temp = {"null", "null", "null"};
                        //    temp[0] = words[k];
                        //}
                        result.add("null");
                    }

                }

                //generalReviews.
            }
        }

        String[] dax = (String[]) result.toArray(new String[result.size()]);
        return dax;

    }

    public String[][] changeFormat(String[] evalComments) {
        List<String[]> result = new LinkedList<String[]>();

        for (int index = 0; index < evalComments.length; index++) {
            int counter = 0;
            while (!evalComments[index + counter].equals("null")) {
                counter++;
            }
            String[] words = evalComments[index].split(" ");
            String[][] tempRes = new String[words.length][2];
            for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                tempRes[wordIndex][0] = words[wordIndex];
                tempRes[wordIndex][1] = "null";
            }
            //inja male masalan 2 khatte bade ke shamele aspect va sentimente
            for (int i = 1; i < counter; i++) {
                if (evalComments[index + i].contains("Aspect")) {
                    String temp2 = evalComments[index + i].substring(7);
                    String[] temp1 = temp2.split(" ");
                    for (int j = 0; j < tempRes.length; j++) {
                        if (tempRes[j][0].equals(temp2)) {
                            tempRes[j][1] = "Aspect";
                            break;
                        }

                        if (tempRes[j][0].equals(temp1[0])) {
                            tempRes[j][1] = "Aspect";
                            for (int k = 1; k < temp1.length; k++) {
                                if (j + k < tempRes.length) {
                                    tempRes[j + k][1] = "Aspecte";
                                }

                            }
                            break;
                        }
                    }
                }
                if (evalComments[index + i].contains("Sentiment")) {
                    String temp2 = evalComments[index + i].substring(10);
                    String[] temp1 = temp2.split(" ");
                    for (int j = 0; j < tempRes.length; j++) {
                        if (tempRes[j][0].equals(temp2)) {
                            tempRes[j][1] = "Sentiment";
                            break;
                        }
                        if (tempRes[j][0].equals(temp1[0])) {
                            tempRes[j][1] = "Sentiment";
                            for (int k = 1; k < temp1.length; k++) {
                                if (j + k < tempRes.length) {
                                    tempRes[j + k][1] = "Sentimente";
                                }

                            }
                            break;
                        }
                    }
                }
            }

            //inja akharesh hamaro miriizm toye result
            for (int i = 0; i < tempRes.length; i++) {
                result.add(tempRes[i]);

            }
            String[] dax = {"null", "null"};
            result.add(dax);
            index = index + counter;
        }

        String[][] dax = (String[][]) result.toArray(new String[result.size()][2]);
        return dax;
    }

    public String[][] changeFormat2(String[] evalComments) {
        List<String[]> result = new LinkedList<String[]>();

        for (int index = 0; index < evalComments.length; index++) {
            int counter = 0;
            while (!evalComments[index + counter].equals("null")) {
                counter++;
            }
            String[] words = evalComments[index].split(" ");
            String[][] tempRes = new String[words.length][2];
            for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                tempRes[wordIndex][0] = words[wordIndex];
                tempRes[wordIndex][1] = "null";
            }
            //inja male masalan 2 khatte bade ke shamele aspect va sentimente
            // aspecta hatman badesh daghighan sentimente
            // sentimenta 1 ya 2ta ghablesh aspecte
            int jstartforAspects = 0;
            int jstartforSents = 0;
            for (int i = 1; i < counter; i++) {
                if (evalComments[index + i].contains("Aspect")) {
                    String temp2 = evalComments[index + i].substring(7);
                    String[] temp1 = temp2.split(" ");
                    for (int j = jstartforAspects; j < tempRes.length; j++) {
                        if (tempRes[j][0].contains(temp2)) {
                            tempRes[j][1] = "Aspect";
                            jstartforAspects = j;
                            break;
                        }
                    }
                    if (temp1.length == 2) {
                        for (int j = jstartforAspects; j < tempRes.length - 1; j++) {
                            if (tempRes[j][0].contains(temp1[0]) && tempRes[j + 1][0].contains(temp1[1])) {
                                tempRes[j][1] = "Aspect";
                                tempRes[j + 1][1] = "Aspecte";
                                jstartforAspects = j + 2;
                                break;
                            }
                        }

                    }
                    if (temp1.length == 3) {
                        for (int j = jstartforAspects; j < tempRes.length - 2; j++) {
                            if (tempRes[j][0].contains(temp1[0]) && tempRes[j + 1][0].contains(temp1[1]) && tempRes[j + 2][0].contains(temp1[2])) {
                                tempRes[j][1] = "Aspect";
                                tempRes[j + 1][1] = "Aspecte";
                                tempRes[j + 2][1] = "Aspecte";
                                jstartforAspects = j + 3;
                                break;
                            }
                        }

                    }

                }
                if (evalComments[index + i].contains("Sentiment")) {
                    String temp2 = evalComments[index + i].substring(10);
                    String[] temp1 = temp2.split(" ");
                    for (int j = jstartforSents; j < tempRes.length; j++) {
                        if (tempRes[j][0].contains(temp2)) {
                            tempRes[j][1] = "Sentiment";
                            jstartforSents = j;
                            break;
                        }
                    }
                    if (temp1.length == 2) {
                        for (int j = jstartforSents; j < tempRes.length - 1; j++) {
                            if (tempRes[j][0].contains(temp1[0]) && tempRes[j + 1][0].contains(temp1[1])) {
                                tempRes[j][1] = "Sentiment";
                                tempRes[j + 1][1] = "Sentimente";
                                jstartforSents = j + 2;
                                break;
                            }
                        }

                    }
                    if (temp1.length == 3) {
                        for (int j = jstartforSents; j < tempRes.length - 2; j++) {
                            if (tempRes[j][0].contains(temp1[0]) && tempRes[j + 1][0].contains(temp1[1]) && tempRes[j + 2][0].contains(temp1[2])) {
                                tempRes[j][1] = "Sentiment";
                                tempRes[j + 1][1] = "Sentimente";
                                tempRes[j + 2][1] = "Sentimente";
                                jstartforSents = j + 3;
                                break;
                            }
                        }

                    }

                }
            }

            //inja akharesh hamaro miriizm toye result
            for (int i = 0; i < tempRes.length; i++) {
                result.add(tempRes[i]);

            }
            String[] dax = {"null", "null"};
            result.add(dax);
            index = index + counter;
        }

        String[][] dax = (String[][]) result.toArray(new String[result.size()][2]);
        return dax;
    }

}
