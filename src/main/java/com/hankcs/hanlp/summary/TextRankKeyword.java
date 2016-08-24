package com.hankcs.hanlp.summary;


import com.hankcs.hanlp.algoritm.MaxHeap;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * 基于TextRank算法的关键字提取，适用于单文档
 * @author hankcs
 */
public class TextRankKeyword extends KeywordExtractor
{
    /**
     * 提取多少个关键字
     */
    int nKeyword = 10;
    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    final static float d = 0.85f;
    /**
     * 最大迭代次数
     */
    final static int max_iter = 200;
    final static float min_diff = 0.001f;

    /**
     * 提取关键词
     * @param document 文档内容
     * @param size 希望提取几个关键词
     * @return 一个列表
     */
    public static List<String> getKeywordList(String document, int size)
    {
        TextRankKeyword textRankKeyword = new TextRankKeyword();
        textRankKeyword.nKeyword = size;

        return textRankKeyword.getKeyword(document);
    }

    /**
     * 提取关键词
     * @param content
     * @return
     */
    public List<String> getKeyword(String content)
    {
        Set<Map.Entry<String, Float>> entrySet = getTermAndRank(content, nKeyword).entrySet();
        List<String> result = new ArrayList<String>(entrySet.size());
        for (Map.Entry<String, Float> entry : entrySet)
        {
            result.add(entry.getKey());
        }
        return result;
    }

    public static List<Term> splitContetn(String content){
        StringBuilder builder = new StringBuilder();
        List<Term> result = new LinkedList<Term>();
        int number = 0;
        for (String paragraph : content.split("[。？！.!?]")) {
            if (paragraph.length() < 2)
                continue;
            //1500根据经验设置
            if (number + paragraph.length() > 1500){
                List<Term> nounsTerm = extractPhrases.getNouns(builder.toString());
                //System.out.println("nounsTerm:" + nounsTerm);
                result.addAll(nounsTerm);
                number = 0;
                builder.delete(0, builder.toString().length());
                //如果大于1500，则需要将paragraph置于下一次的最前端
                builder.append(paragraph);
}
else {
        number += paragraph.length();
        builder.append(paragraph);
        }
        //System.out.println(paragraph);
        }
        //判断是否最后读取完
        if (builder.toString().length() > 0){
        List<Term> nounsTerm = extractPhrases.getNouns(builder.toString());
        //System.out.println("nounsTerm:" + nounsTerm);
        for (Term term : nounsTerm){
        result.add(term);
        }
        number = 0;
        builder.delete(0, builder.toString().length());
        }
        //System.out.println("result:" + result);
        return result;
        }

    /**
     * 返回全部分词结果和对应的rank
     * @param content
     * @return
     */
    public Map<String,Float> getTermAndRank(String content)
    {
        assert content != null;

       // List<Term> termList = defaultSegment.seg(content);
        //System.out.println("termList:" + termList);
        //根据长度来做选择
        if (content.length() < 1500)
            return getRank(extractPhrases.getNouns(content));
        else
            return getRank(splitContetn(content));
//      return getRank(extractPhrases.getNouns(content));
        //从数据库中得到名词和名词短语]
//        Map<String, Float> termList = null;
//        try {
//            termList= getRank(extractPhrases.getNounsFromDatabase());
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally {
//            return termList;
//        }
    }

    /**
     * 返回分数最高的前size个分词结果和对应的rank
     * @param content
     * @param size
     * @return
     */
    public Map<String,Float> getTermAndRank(String content, Integer size)
    {
        Map<String, Float> map = getTermAndRank(content);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>()
        {
           // @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 使用已经分好的词来计算rank
     * //@param termList
     * @return
     */
    public Map<String,Float> getRank(List<Term> termList)
    {
        List<String> wordList = new ArrayList<String>(termList.size());
        //System.out.println(extractPhrases.getPhrases());
       // wordList = extractPhrases.getPhrases();
        for (Term t : termList)
        {
            if (shouldInclude(t))
            {
                wordList.add(t.word);
            }
        }
//        System.out.println(wordList);
        Map<String, Set<String>> words = new TreeMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        //System.out.println("wordList:" + wordList);
        for (String w : wordList)
        {
            if (!words.containsKey(w))
            {
                words.put(w, new TreeSet<String>());
            }
            que.offer(w);
            if (que.size() > 5)
            {
                que.poll();
            }

            for (String w1 : que)
            {
                for (String w2 : que)
                {
                    if (w1.equals(w2))
                    {
                        continue;
                    }

                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
//        System.out.println(words);
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String element : value)
                {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(element) == null ? 0 : score.get(element)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        System.out.println("score:" + score);
        return score;
    }
}
