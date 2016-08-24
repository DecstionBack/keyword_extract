package keywords;

import Util.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by lenovo on 2016/8/24.
 */
public class IDF_keywords {
        public static Map<String, Integer> idfs = new HashMap<String, Integer>();
    static{
        idfs = loadIDFs.loadIDFs();
    }

    public static Map<String, Double> wordsIDF(List<String> wordsList){
        Map<String, Double> wordsIDF = new HashMap<String, Double>();
        for (String words : wordsList){
            for (String word : words.split(" ")){
                if (word.contains("年月") || word.contains("月日"))
                    continue;
                if (!wordsIDF.containsKey(word)){
                    if (!idfs.containsKey(word))
                        wordsIDF.put(word, 1.0);
                    else
                        wordsIDF.put(word, 1.0 / (idfs.get(word) + 1));
                }
            }
        }
        return wordsIDF;
    }
    public static void main(String[] args) throws SQLException{
        List<String> wordsList = loadWords.loadWords();
        Map<String, Double> wordsIDF = wordsIDF(wordsList);

        List<Map.Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(wordsIDF.entrySet());
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
