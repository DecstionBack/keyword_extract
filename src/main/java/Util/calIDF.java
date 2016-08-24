package Util;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/14.
 */
public class calIDF {
    public static void calIDF() throws SQLException{
        List<String> contents = new LinkedList<String>();
        Map<String, Integer> counts = new HashMap<String, Integer>();
        Connection conn = null;
        try{
            Class.forName(Utils.mysql_driver);
            conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            if (!conn.isClosed())
                System.out.println("Connecting to the Database successfully!");
            Statement statement = conn.createStatement();
            String sql = "select titleWords, contentWords ,sample_content  from samples_yzlj";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                contents.add(rs.getString("sample_content"));
                for (String word : (rs.getString("titleWords") + rs.getString("contentWords")).split(" ")){
                    if (!counts.containsKey(word))
                        counts.put(word, 0);
                }
            }
            System.out.println("Read database successfully!");
            for (String word : counts.keySet()){
                int num  = 0;
                for (String content : contents){
                    if (content.contains(word)) {
                        num++;
                        continue;
                    }
                }
                counts.put(word, num);
            }

            rs.close();
            statement.close();

            Statement insertstatement = conn.createStatement();
            sql = null;
            if (counts.containsKey(" "))
                counts.remove(" ");
            for (String word : counts.keySet()){
                sql = "insert into idf(word, count) values('" + word + "'," + counts.get(word) + ")";
                try {
                    insertstatement.execute(sql);
                }catch(Exception e2){
                    e2.printStackTrace();
                    continue;
                }
            }
            insertstatement.close();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (!conn.isClosed())
                conn.close();
            System.out.println("calIDF complete!");
        }
    }

    public static void main(String[] args) throws  SQLException{
        calIDF();
    }
}
