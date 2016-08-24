package keywords;

import Util.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by lenovo on 2016/8/24.
 */
public class TFIDF_keywords {
    public static Map<String, Integer> idfs = new HashMap<String, Integer>();
    static{
        idfs = loadIDFs.loadIDFs();
    }

    public  static void main(String[] args) throws SQLException{
        Map<String, Double> wordsTFIDF = new HashMap<String, Double>();
        List<String> wordsList = loadWords.loadWords();
        Map<String, Integer> wordCounts = TF_keywords.wordCounts(wordsList);
        Map<String, Double> wordsIDF = IDF_keywords.wordsIDF(wordsList);

        for (String word : wordCounts.keySet()){
            wordsTFIDF.put(word, wordCounts.get(word) * wordsIDF.get(word));
        }

        List<Map.Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(wordsTFIDF.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue()) * (-1);
            }
        });

        for (int i = 0; i < 50; i ++)
            System.out.print(arrayList.get(i).getKey() + " ");
        System.out.println();


    }
}
