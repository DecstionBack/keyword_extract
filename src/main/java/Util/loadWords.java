package Util;


import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/24.
 */
public class loadWords {
    public static List<String> loadWords() throws SQLException{
        Connection conn = null;
        Statement statement = null;
        List<String> wordsList = new LinkedList<String>();
        try{
            Class.forName(Utils.mysql_driver);
            conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            statement = conn.createStatement();
            String sql = "select titleWords, contentWords from samples_topic where topic='北戴河' and updatetime<='2016-08-17' and updatetime > '2016-08-10'";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                wordsList.add(rs.getString("titleWords") + rs.getString("contentWords"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (!conn.isClosed()){
                statement.close();
                conn.close();
            }
            return wordsList;
        }
    }
}
