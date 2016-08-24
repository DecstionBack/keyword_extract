package keywords;

import Util.*;

import javax.rmi.CORBA.Util;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by lenovo on 2016/8/24.
 */
public class TF_keywords {

    public static Map<String, Integer> wordCounts(List<String> wordsList){
                    Map<String, Integer> wordCounts = new HashMap<String, Integer>();
                    for (String words : wordsList){
                        for (String word : words.split(" ")){
                            if (word.contains("月日") || word.contains("年月"))
                                continue;
                            if (!wordCounts.containsKey(word))
                                wordCounts.put(word, 1);
                            else {
                                wordCounts.put(word, wordCounts.get(word) + 1);
                            }
            }
        }
        return wordCounts;
    }

    public static void main(String[] args) throws SQLException{
        List<String> wordsList = loadWords.loadWords();
        Map<String, Integer> wordCounts = wordCounts(wordsList);
        List<Map.Entry<String, Integer>> arrayList = new ArrayList<Map.Entry<String, Integer>>(wordCounts.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue()) * (-1);
            }
        });

        for (int i = 0; i < 50; i ++)
            System.out.print(arrayList.get(i).getKey() + " ");
        System.out.println();
    }
}
