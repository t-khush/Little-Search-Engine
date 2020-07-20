package lse;

import java.io.*;
import java.util.*;

public class Driver {

    LittleSearchEngine lse;

    public Driver() {
        lse = new LittleSearchEngine();
    }

    public void loadNoise() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("noisewords.txt"));
        while (sc.hasNext()) {
            String word = sc.next();
            this.lse.noiseWords.add(word);
        }
        sc.close();
    }

    public void getWordTester() throws FileNotFoundException {
        this.loadNoise();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter input: ");
        String st = lse.getKeyword(sc.next());
        System.out.println();
        System.out.println(st);
        sc.close();
    }

    public void loadKeyWordsTester() throws FileNotFoundException {
        this.loadNoise();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter input file: ");
        HashMap<String,Occurrence> keyHash = lse.loadKeywordsFromDocument(sc.next());
        sc.close();
        Set<String> keySet = keyHash.keySet();
        Iterator<String> keyIt = keySet.iterator();
        while (keyIt.hasNext()) {
            String st = keyIt.next();
            System.out.print(st + " " + keyHash.get(st).frequency + "\n");
        }
    }

    public void mergeKeywordsTester() throws FileNotFoundException {
        this.loadNoise();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter input file: ");

        String target = sc.next();
        while (!"quit".equals(target)) {
            HashMap<String,Occurrence> keyHash = lse.loadKeywordsFromDocument(target);
            lse.mergeKeywords(keyHash);
            HashMap<String,ArrayList<Occurrence>> totalHash = lse.keywordsIndex;

            for(Map.Entry<String, ArrayList<Occurrence>> entry : totalHash.entrySet()) {
                String key = entry.getKey();
                ArrayList<Occurrence> occur = entry.getValue();
                System.out.println(key);
                for(Occurrence occ : occur) {
                    System.out.println("\t" + occ.frequency);
                }

            }
            System.out.print("Enter input file: ");
            target = sc.next();
        }
        sc.close();
    }

    public void makeIndexTester() throws FileNotFoundException {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the docFile: ");
        String docFile = sc.next();
        System.out.println();
        System.out.print("Enter the noiseFile: ");
        String noiseWordsFile = sc.next();
        System.out.println();

        this.lse.makeIndex(docFile, noiseWordsFile);

        HashMap<String, ArrayList<Occurrence>> keyHash = this.lse.keywordsIndex;

        Set<String> allKeys = keyHash.keySet();

        Iterator<String> keyIterator = allKeys.iterator();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            ArrayList<Occurrence> occList = keyHash.get(key);
            System.out.print(key + "\t: ");
            for (int i = 0; i < occList.size(); i++) {
                Occurrence occObj = occList.get(i);
                System.out.print("(" + occObj.document + ", " + occObj.frequency + ") --> ");
            }
            System.out.println();
        }

        sc.close();
    }

    public void insertLastOccTester() {

        Scanner sc = new Scanner(System.in);

        int size = (int) (Math.random() * 11)+1;

        ArrayList<Occurrence> occArr = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Occurrence temp = new Occurrence(null, (int)(Math.random()*31));
            occArr.add(temp);
            lse.insertLastOccurrence(occArr);
        }

        System.out.print("Here is the occurrence list: [");
        for (int i = 0; i < occArr.size(); i++) {
            if (i==occArr.size() - 1) {
                System.out.print(occArr.get(i).frequency);
            }
            else System.out.print(occArr.get(i).frequency + ", ");
        }
        System.out.println("]");

        System.out.print("Enter a frequency: ");

        String input = sc.next();

        System.out.println();

        while (!"quit".equals(input)) {
            int freq = Integer.parseInt(input);
            Occurrence temp = new Occurrence(null, freq);
            occArr.add(temp);
            ArrayList<Integer> midPts = lse.insertLastOccurrence(occArr);
            System.out.print("Here is the occurrence list: [");
            for (int i = 0; i < occArr.size(); i++) {
                if (i==occArr.size() - 1) {
                    System.out.print(occArr.get(i).frequency);
                }
                else System.out.print(occArr.get(i).frequency + ", ");
            }
            System.out.println("]");
            System.out.print("Here are the midpoints: [");
            for (int i = 0; i < midPts.size(); i++) {
                if (i==midPts.size() - 1) {
                    System.out.print(midPts.get(i));
                }
                else System.out.print(midPts.get(i) + ", ");
            }
            System.out.println("]");
            System.out.print("Enter another frequency or quit: ");
            input = sc.next();
            System.out.println();
        }

        sc.close();
    }

    public void top5Tester() throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the docFile: ");
        String docFile = sc.next();
        System.out.println();
        System.out.print("Enter the noiseFile: ");
        String noiseWordsFile = sc.next();
        System.out.println();

        this.lse.makeIndex(docFile, noiseWordsFile);

        String quit = "no";

        while (!"quit".equals(quit)) {
            System.out.print("Enter kw1: ");
            String kw1 = sc.next();
            System.out.println();
            System.out.print("Enter kw2: ");
            String kw2 = sc.next();
            System.out.println();

            ArrayList<String> result = this.lse.top5search(kw1, kw2);

            for (int i = 0; i < result.size(); i++) {
                System.out.print(result.get(i) + "\t");
            }
            System.out.println();
            System.out.print("Enter 'quit' to quit, anything else to continue: ");
            quit = sc.next();
            System.out.println();
        }
        sc.close();
    }

    public static void main(String[] args) throws FileNotFoundException {

        Driver driver = new Driver();

        Scanner sc = new Scanner(System.in);

        System.out.print("(a)test getWord\t(b)test loadKeyWords\t(c)test makeIndex\t(d)test top5search\t(e)test insertLastOccurrence: \t(f)test mergeKeywords ");
        String option = sc.next();
        System.out.println();

        switch (option) {

            case "a" :
                driver.getWordTester();
                break;
            case "b" :
                driver.loadKeyWordsTester();
                break;
            case "c" :
                driver.makeIndexTester();
                break;
            case "d" :
                driver.top5Tester();
                break;
            case "e" :
                driver.insertLastOccTester();
                break;
            case "f" :
                driver.mergeKeywordsTester();
                break;
            default :
                sc.close();
        }
    }

}