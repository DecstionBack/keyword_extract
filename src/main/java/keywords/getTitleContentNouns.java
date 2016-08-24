package keywords;

import Util.*;
import com.hankcs.hanlp.seg.common.Term;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.hankcs.hanlp.summary.*;

/**
 * Created by lenovo on 2016/8/24.
 */
public class getTitleContentNouns {
    public static void main(String[] args){
        Connection conn = null;
        Statement statement = null;
        Statement updatestatement = null;
        try{
            Class.forName(Utils.mysql_driver);
            conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            statement = conn.createStatement();
            updatestatement = conn.createStatement();
            String sql = "select id, title, content from samples_topic where id >= 2895 and id <= 3084 and titleNouns is null";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                List<Term> titleNouns = null;
                List<Term> contentNouns = null;
                int id = rs.getInt("id");
                System.out.println(id);
                try{
                    titleNouns =  extractPhrases.getNouns(rs.getString("title"));
                    contentNouns = extractPhrases.getNouns(rs.getString("content"));
                }catch(Exception e2){
                    e2.printStackTrace();
                    continue;
                }
                StringBuilder titleBuilder = new StringBuilder();
                StringBuilder contentBuilder = new StringBuilder();
                for (Term term : titleNouns){
                    titleBuilder.append(term.word + " ");
                }
                for (Term term : contentNouns){
                    contentBuilder.append(term.word + " ");
                }
                sql = "update samples_topic set titleNouns='" + titleBuilder.toString() + "' , contentNouns='" + contentBuilder.toString() + "' where id=" + id;
                try{
                    updatestatement.execute(sql);
                }catch(Exception e3){
                    e3.printStackTrace();
                }

                titleBuilder.delete(0, titleBuilder.toString().length());
                contentBuilder.delete(0, contentBuilder.toString().length());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
