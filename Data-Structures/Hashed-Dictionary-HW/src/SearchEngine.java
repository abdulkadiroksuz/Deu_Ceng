import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SearchEngine {

    private Set<String> stopWordsSet = new HashSet<String>(); //hashsets have O(1) complexity for contains() method
    private HashedDictionary<String,AList<Integer>> hashedDict; //where all the words contain
    //private String[] fileNames; //faster traverse
    private Dictionary<String, Integer> files; //holds file name and number of the words for each document

    //performance monitoring
    private long indexingTime;

    public SearchEngine() {
        this.indexingTime = 0;

        fillStopWords(); //inserts stop words to hashSet
        hashedDict = new HashedDictionary<>();
        files=new Dictionary<>();
        ReadFile();

        while (true) {
            String[] query = Prompt().trim().split(" "); //to enter a query


            if(query[0].equalsIgnoreCase("end"))
                break;

            System.out.println("The most related file is: "+searchQuery(query));
            printNumsOfWordFound(query); //does not print frequencies (prints occurences)

        }
    }

    private String searchQuery(String[] query) {

        //returns the most related document's name ONLY! (search.txt operations cannot be included here.
        // search.txt operations are removed from the program after the performance monitoring completed)
        //if you mind looking how I did search look for the functions in HashedDictionary class named "something"..Collision()

        //prioritization is determined  by calculating frequency sum of each word in each text
        //thus, although text1 has more in total words, if text2 is more frequent in the number of occurrences for the query, text2 will be suggested

        double maxFreq = 0;
        String mostRelatedFile = "an error occured"; // going to be re-assigned


        Iterator<Integer> valueIterator = files.getValueIterator(); //hold word number of file
        Iterator<String> keyIterator = files.getKeyIterator();

        int i = 0;
        while (valueIterator.hasNext()) { //traverses all the documents (texts in this project)
            int wordNumberOfFile = Integer.parseInt(valueIterator.next().toString());
            String nameOfFile = (String)  keyIterator.next();
            double freq = 0;

            int counter = 0; // to hold number of error while searching
            for(String word : query) { //to calculate total frequency
                AList<Integer> aListOfKey;
                try {
                    aListOfKey = hashedDict.getValue(word); // returns array list of given word that stores occurrence count for each document
                }catch (Exception e) {
                    continue;
                }
                if(aListOfKey==null){
                    counter++;
                    continue;
                }

                Object[] countValues = aListOfKey.toArray(); //holds word number in text

                try {
                    freq += Double.parseDouble( countValues[i].toString() )  / wordNumberOfFile;
                }catch (ArrayIndexOutOfBoundsException e){ // means word does not include
                    freq = 0;
                }catch (Exception e) {
                    System.out.println("an error occured");
                }
            }
            if(counter == query.length)
                return "No documents found";

            if(maxFreq<=freq) {
                maxFreq = freq;
                mostRelatedFile = nameOfFile;
            }
            i++;
        }
        return mostRelatedFile;


    }

    private void printNumsOfWordFound(String[] query) {
        //can be used to see how many times the word counted for each word in each document

        printDocNames();

        for(String word : query) {
            System.out.print(String.format("%-18s   ",word));

            if(!hashedDict.contains(word)) {
                for(int i = 0 ; i< files.getSize();i++) {
                    System.out.print(String.format("%-11d",0));
                }
                System.out.println();
                continue;
            }

            AList<Integer> aListOfKey = hashedDict.getValue(word); // returns array list of given word that stores occurrence count for each document
            Object[] countValues = aListOfKey.toArray();
            printCounts(countValues);
        }
        System.out.println();
    }

    private void printCounts(Object[] countValues) {

        for(Object count : countValues) {
            if(count != null)
                System.out.print(String.format("%-11d",(int) count)); //printing document names
            else
                System.out.print(String.format("%-11d",0));
        }
        System.out.println();
    }

    private void printDocNames() { //print all the document's name

        Iterator keyIterator = files.getKeyIterator();

        System.out.print("                  ");
        while (keyIterator.hasNext()) {
            System.out.print(String.format("%s    ", keyIterator.next()));
        }
        System.out.println();

    }

    private String Prompt() {
        //prompts the user to enter a query
        //returns query string
        System.out.println("Enter your query");
        System.out.println("(enter \"end\" word to shut down)");
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();

    }

    private void fillStopWords() {
        // this procedure is used to fill the set named "stopWordsSet" only

        try {
            Scanner myReader = new Scanner(new FileReader("stop_words_en.txt"));
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                stopWordsSet.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private int removeStopWords(String lineToClean, String fileName, int dirLength) {
        //for the texts named with numbers 001.txt etc.
        //returns number of non-stop word of the line
        int count= 0;
        for(String wordToCompare : lineToClean.split(" ")){
            if(!stopWordsSet.contains(wordToCompare)) {

                wordToCompare= wordToCompare.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ").trim();

                for(String word : wordToCompare.split(" ")) {

                    if (word.length() > 1) {
                        count++;
                        addToHashedDict(fileName, dirLength, word);
                    }
                }

            }
        }
        return count;
    }

    private void addToHashedDict(String fileName, int dirLength, String word) {

        int fileNum = 1;
        try { // for number named texts (001.txt etc.)
            fileNum = Integer.parseInt(fileName.substring(0, fileName.length() - 4));
        }catch (NumberFormatException e){ //do not anything if file was not named with numbers (necessary for search.txt)
            ;
        }catch (Exception e) {
            System.out.println("an error occurred");
            e.printStackTrace();
        }
        AList<Integer> value;

        if (hashedDict.contains(word)) {
            value = hashedDict.getValue(word);
            try {
                value.replace(fileNum, value.getEntry(fileNum)+1); //if the word is found, then simply increase the number of occurence in related file name by 1.

            } catch (Exception e) {
                System.out.println("an error occured");
                e.printStackTrace();
            }
        } else {
            value = new AList<>();

            for (int i = 1; i <= dirLength; i++) {
                if (i == fileNum) {
                    value.add(1);
                    continue;
                }
                value.add(0); //
            }
            if(fileNum!=1)
                hashedDict.add(word, value); //insert words to the hash table here
            else {//used in performance monitoring
                //hashedDict.addCollision(word, value); //search the words in search.txt

                //this section is used for performance monitoring.
                //all the functions named ...Collision (probeCollision etc.) is used for performance monitoring
                // (independent body from program and the user may use for checking if he/she wants)
            }
        }

    }

    private void ReadFile(){
        //to read files

        FileReader fileReader;
        BufferedReader bufferedReader = null;
        try {
            File folder = new File("sport");
            if (folder.isDirectory()) {
                File[] filesInDir = folder.listFiles();
                assert files != null;
                long start = System.nanoTime();
                for (int i = 0 ; i<filesInDir.length ; i++) {

                    bufferedReader = new BufferedReader(new FileReader(filesInDir[i]));

                    String line;
                    int numOfCleanWords = 0;
                    while (null != (line = bufferedReader.readLine())) {
                        line=line.toLowerCase(Locale.ENGLISH);

                        if(line.length()>0)
                            numOfCleanWords += removeStopWords(line,filesInDir[i].getName(),filesInDir.length);
                    }
                    files.add(filesInDir[i].getName(), numOfCleanWords);
                }
                this.indexingTime = System.nanoTime()-start;
                //searchTxt(new File("search.txt")); // for performance monitoring
            }else if(folder.isFile()) {

                bufferedReader = new BufferedReader(new FileReader(folder));

                String line;
                int numOfCleanWords = 0;
                while (null != (line = bufferedReader.readLine())) {
                    line=line.toLowerCase(Locale.ENGLISH);

                    if(line.length()>0)
                        numOfCleanWords += removeStopWords(line,folder.getName(),1);
                }
                files.add(folder.getName(), numOfCleanWords);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    private void searchTxt(File folder) throws IOException {
        // this function copied from ReadFile function
        BufferedReader bufferedReader = new BufferedReader(new FileReader(folder));

        String line;
        int numOfCleanWords = 0;
        while (null != (line = bufferedReader.readLine())) {
            line=line.toLowerCase(Locale.ENGLISH);

            if(line.length()>0)
                numOfCleanWords += removeStopWords(line,folder.getName(),1);
        }
        files.add(folder.getName(), numOfCleanWords);

    }




}
