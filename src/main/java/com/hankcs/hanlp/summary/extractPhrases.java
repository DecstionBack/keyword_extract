package com.hankcs.hanlp.summary;

import Util.Utils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/1.
 */
public class extractPhrases {
    public static List<String> splitContetn(String content){
        StringBuilder builder = new StringBuilder();
        List<String> result = new LinkedList<String>();
        int number = 0;
        for (String paragraph : content.split("[。？！.!?]")) {
            if (paragraph.length() < 2)
                continue;
            //1500根据经验设置
            if (number + paragraph.length() > 1000){
                result.add(builder.toString());
                builder.delete(0, builder.length());
                builder.append(paragraph);
                number = 0;
            }
            else {
                number += paragraph.length();
                builder.append(paragraph);
            }
        }
        return result;
    }

    private static List<String> getPhrases(String text) {
        //下面这句话尽可能不要出现
        //System.out.println(text);
//        List<String> texts = new LinkedList<String>();
//        if (text2.length() > 1000){
//            texts = splitContetn(text2);
//        }
        List<String> phrases = new LinkedList<String>();
//        for (String text : texts){
            CoNLLSentence sentence = HanLP.parseDependency(text);
            StringBuilder candidatePhrases = new StringBuilder();
            CoNLLWord[] wordArray = sentence.getWordArray();
            for (CoNLLWord word : wordArray) {

                //System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
                if (!(text.contains(word.LEMMA + word.HEAD.LEMMA)))
                    continue;
                //System.out.println(word);
                if (word.DEPREL.equals("定中关系")) {
                    //System.out.println(word.LEMMA + word.HEAD.LEMMA);
                    if (candidatePhrases.toString().equals("")) {
                        if (text.contains(word.LEMMA + word.HEAD.LEMMA)) {
                            candidatePhrases.append(word.LEMMA + word.HEAD.LEMMA);
                            //System.out.println(candidatePhrases.toString());
                        }

                    } else{
                        //扩馆众筹类似的，虽然少见，但是会有
                        // System.out.println(candidatePhrases.toString() + word.LEMMA + word.HEAD.LEMMA);
                        if (text.contains(candidatePhrases.toString() + word.LEMMA + word.HEAD.LEMMA)) {
                            candidatePhrases.append(word.HEAD.LEMMA);
                        }
                        else if (text.contains(candidatePhrases.toString() + word.LEMMA + word.HEAD.LEMMA)){
                            candidatePhrases.append(word.LEMMA + word.HEAD.LEMMA);
                        }

                    }
                } else {
                    if (candidatePhrases.toString().length() > 1) {
                        phrases.add(candidatePhrases.toString());
                    }
                    candidatePhrases.delete(0, candidatePhrases.toString().length());
                }

            }
//        }

        return phrases;
    }

    public static List<String> getStopwords(){
        List<String> stopwords = new LinkedList<String>();
        String line = null;
        try {
            File file = new File("停用词表.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null){
                stopwords.add(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return stopwords;
    }

        public static List<Term> getNouns(String content){
            Segment defaultSegment = StandardTokenizer.SEGMENT;
            List<Term> termList = defaultSegment.seg(content);
            List<String> stopwords = getStopwords();
            List<Term> finalList = new LinkedList<Term>();
            List<String> phrases = extractPhrases.getPhrases(content);
            //phrases短语抽取正确[六四纪念馆、扩馆、众筹]
            //System.out.println("phrases:" + phrases);
            //用phrases替换其中的词语
            StringBuilder builder = new StringBuilder();

        for (Term term : termList){
            if (term.word.contains("/d"))
                continue;
            //判断一个词语是否是短语的一部分
            boolean contain = false;
            for (String phrase : phrases){
                //下面一句不可以省略
                builder.delete(0, builder.toString().length());
                builder.append(term.word);
                int k = 1;
                if (phrase.contains(term.word)){
                    boolean flag = true;
                    while (flag){
                        //判断是否越界
                        if (termList.indexOf(term) + k >= termList.size()){
                            //最后一个词为名词才加进去
                            if (k == 1){
                                if (term.nature.toString().contains("n")){
                                    finalList.add(new Term(builder.toString(), Nature.n));
                                    builder.delete(0, builder.toString().length());
                                    contain = true;
                                }
                            }
                            //否则后面的几个词和phrase相同才加进去
                            else if (phrase.equals(builder.toString())){
                                finalList.add(new Term(builder.toString(), Nature.n));
                                builder.delete(0, builder.toString().length());
                                contain = true;
                            }
                            //越界处理工作以后退出循环
                            break;
                        }
                        //加上k以后不越界执行下面操作
                        if (!(phrase.contains(termList.get(termList.indexOf(term) + k).word))){
                            //只是包含其中的一个词，那么就判断是否为名词，为名词就加入，不是名词就跳过，如果k>1则直接加入
                            if (k == 1){
                               // System.out.println("term:" + term);
                                if (termList.get(termList.indexOf(term)).word.contains("n")){
                                    finalList.add(new Term(builder.toString(), Nature.n));
                                    contain = true;
                                }
                            }
                            else {
                                finalList.add(new Term(builder.toString(), Nature.n));
                                contain = true;
                            }
                            builder.delete(0, builder.toString().length());
                            flag = false;
                        }
                        //包含term后面的第k个词语
                        else {
                            builder.append(termList.get(termList.indexOf(term) + k).word);
                            contain = true;
                            //如果被判定加入短语中，则后面加上/d不再参与后面的计算
                            termList.get(termList.indexOf(term) + k).word += "/d";
                            k++;
                        }

    }//while
}

}
        //都不包含短语
        if (!contain){
            if ((term.word.length() > 1) && (term.toString().contains("/n")))
            finalList.add(new Term(term.word, Nature.n));
            }
        }
        finalList.removeAll(stopwords);
    return finalList;
}

    public static List<Term> getNounsFromDatabase() throws SQLException{
        Connection conn = null;
        Statement statement = null;
        List<Term> words = new LinkedList<Term>();
        try{
            Class.forName(Utils.mysql_driver);
            conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            statement = conn.createStatement();
            String sql = "select id, titleNouns, contentNouns from samples_topic where topic='十九大'";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()){
                if (rs.getString("titleNouns") != null) {
                    for (String word : rs.getString("titleNouns").split(" "))
                        words.add(new Term(word, Nature.n));
                }
                if (rs.getString("contentNouns") != null) {
                    for (String word : rs.getString("contentNouns").split(" "))
                        words.add(new Term(word, Nature.n));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            statement.close();
            conn.close();
            return words;
        }
    }

    public static void main(String[] args){
        Segment defaultSegment = StandardTokenizer.SEGMENT;
        List<Term> termList = defaultSegment.seg("香港“六四纪念馆”周二起闭馆 扩馆众筹延续传递");
        System.out.println(termList);

        String text = "声明和呼吁";
        System.out.println(text.length());
        List<String> finalList = getPhrases(text);
        System.out.println(finalList);
//        for (Term term : finalList){
//            System.out.print(term.word + " ");
//        }
//        System.out.println();
    }
}

