package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/12.
 */
public class loadIDFs {
    public static Map<String, Integer> loadIDFs(){
        Map<String, Integer> idfs = new HashMap<String, Integer>();
        try {
            Class.forName(Utils.mysql_driver);
            Connection conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            if (!conn.isClosed())
                System.out.println("Connecting to the Database successfully!");
            Statement statement = conn.createStatement();
            String sql = "select * from idf";

            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                idfs.put(rs.getString("word"), rs.getInt("count"));
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            return idfs;
        }
    }

    public static void main(String[] args){
        loadIDFs();
    }
}
