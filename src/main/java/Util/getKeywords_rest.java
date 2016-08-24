package Util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.extractPhrases;
import net.sf.json.JSONObject;
import java.util.*;

/**
 * Created by lenovo on 2016/8/12.
 */
public class getKeywords_rest {
    public static List<String> getTopics(List<String> keywords, Map<String, String> topics){
        List<String> topicResult = new LinkedList<String>();
        for (String word : keywords){
            for (String item : topics.keySet()){
                if (word.contains(item))
                    if (!topicResult.contains(topics.get(item)))
                        topicResult.add(topics.get(item));
            }
        }

        return topicResult;
    }

    private static double calContentIDF(String word, Map<String, Integer> idfs){
        //简化计算，递减函数，所以N可以不写，因为是个固定值
        if (idfs.containsKey(word))
            return Math.log( 1.0 / idfs.get(word));
        else return 1.0;
    }

    public static List<String> getKeywords(JSONObject jsonObject, Map<String, Integer> idfs){
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<Term> termList = new LinkedList<Term>();
        try {
            if (jsonObject.containsKey("title"))
                termList = extractPhrases.getNouns(jsonObject.getString("title").replaceAll("[0-9-\n]",""));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        List<String> titleWords = new LinkedList<String>();

        for (Term term : termList){
            if (!titleWords.contains(term.word))
                titleWords.add(term.word);
        }
        System.out.println("titleWords:" + titleWords);

        List<String> contentWords = new LinkedList<String>();
//        if (jsonObject.getString("content").length() > 1500)
//            return titleWords;
        //每次都取前50个词语，最后根据需要进行返回即可
        //System.out.println("titleWords:" + titleWords.size());
        try {
            if (titleWords.size() < jsonObject.getInt("num") && jsonObject.containsKey("content"))
                contentWords = HanLP.extractKeyword(jsonObject.getString("content").replaceAll("[0-9-\n]",""), 50);
            else return titleWords;
        }catch(Exception e){
            e.printStackTrace();
            return titleWords;
        }
        //去除标题和正文包含的重复的关键词
        contentWords.removeAll(titleWords);
        titleWords.remove("年月日");
        contentWords.remove("年月日");
        //正文如果含有关键词，则根据idf进行排序后再输出
        Map<String, Double> contentWordsIDF = new HashMap<String, Double>();
        for (String word : contentWords){
            contentWordsIDF.put(word, calContentIDF(word, idfs));
        }
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(contentWordsIDF.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue()) * (-1);
            }
        });
        //标题和正文的放在一起
        int titleWordsNum = titleWords.size();
        int conentKeywordNumber = jsonObject.getInt("num") - titleWordsNum > list.size() ? list.size() : jsonObject.getInt("num") - titleWordsNum;
        for (int i = 0; i < conentKeywordNumber; i++){
            //if (!titleWords.contains(list.get(i).getKey()))
                titleWords.add(list.get(i).getKey());
        }
        System.out.println("keywords:" + titleWords);
        return titleWords;
    }
}
