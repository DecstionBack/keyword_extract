package Util;


import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/12.
 */
public class loadTopics {
    public static Map<String, String> loadTopics() throws SQLException{
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> topics = new HashMap<String, String>();
        try {
            Class.forName(Utils.mysql_driver);
            conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            if (!conn.isClosed())
                System.out.println("Connecting to the Database successfully!");
            statement = conn.createStatement();
            String sql = "select sample_keyword, first_name from sample_keywords, t_subject where sample_keywords.sample_topics_id=t_subject.id";
            rs= statement.executeQuery(sql);
            int num = 0;
            while (rs.next()){
               topics.put(rs.getString("sample_keyword"), rs.getString("first_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (!conn.isClosed()){
                rs.close();
                statement.close();
                conn.close();
                return topics;
            }
        }
        return null;

    }
}
