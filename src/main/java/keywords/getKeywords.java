/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:25</create-date>
 *
 * <copyright file="DemoChineseNameRecoginiton.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014+ 上海林原信息科技有限公司. All Right Reserved+ http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package keywords;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.extractPhrases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import Util.*;

/**
 * 关键词提取
 * @author hankcs
 */
public class getKeywords
{

    public static void main(String[] args)
    {
        try{
            Class.forName(Utils.mysql_driver);
            Connection conn =DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            if (!conn.isClosed())
                System.out.println("Connecting to the Database successfully!");

            Statement statement = conn.createStatement();
            Statement updatestatement = conn.createStatement();
            /**
             * 选取关键词
             */
            String sql = "select id, content from samples_topic where ";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                int id = rs.getInt("id");
                System.out.println(id);
                //String title = rs.getString("title");
                //System.out.println(title);
                String content = rs.getString("content");
                //List<Term> titleWords = null;
                List<String> contentWords = null;
                try {
                 //   titleWords = extractPhrases.getNouns(title.replaceAll("[0123456789]",""));
                    contentWords = HanLP.extractKeyword(content.replaceAll("[--_——\n]",""), 50);
                }catch(Exception e){
                    continue;
                }
                StringBuilder builder = new StringBuilder();
//                for (Term term : titleWords){
//                        if (!builder.toString().contains(term.word))
//                            builder.append(term.word + " ");
//                }
                for (String term : contentWords){
                if (!builder.toString().contains(term))
                    if(!builder.toString().contains(term))
                        builder.append(term + " ");
            }
            sql = "update samples_topic set contentWordsNumber='" + builder.toString() + "' where id=" + id;
            builder.delete(0, builder.toString().length());
            try{
                updatestatement.execute(sql);
            }catch(Exception e2){
                e2.printStackTrace();
            }

            }
            rs.close();
            statement.close();
            updatestatement.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
