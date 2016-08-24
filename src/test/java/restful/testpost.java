package restful;

import Util.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by lenovo on 2016/8/11.
 */
public class testpost {
                public static String ip = "http://localhost:8088/keyword/batch";
            public static void main(String[] args) throws Exception{
                HttpURLConnection connection = null;
                try{
                    Class.forName(Utils.post_driver);
                    Connection conn = DriverManager.getConnection(Utils.post_url, Utils.post_user, Utils.post_password);
                    Statement statement= conn.createStatement();
                    Statement updatestatement= conn.createStatement();
                    String sql = "select id, title, sample_content from temp20";
                    ResultSet rs = statement.executeQuery(sql);
                    int number = 0;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("num","10");
                    JSONArray array = new JSONArray();
                    while(rs.next()){
                        number++;
                        //添加请求内容
                        Integer id = rs.getInt("id");
                        String title = rs.getString("title");
                        String content = rs.getString("sample_content");
                        JSONObject user = new JSONObject();
                        user.put("title",title);

                        user.put("content",content);
                        user.put("num",10);
                        array.add(user);

                        //System.out.println(title);
                    // System.out.println(content);
                    if (number % 1 ==0){
                        System.out.println(number);
                        //创建连接
                        URL url = new URL(ip);
                        connection = (HttpURLConnection) url.openConnection();


                        //设置http连接属性
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(true);

                    //设置http头 消息
                    connection.setRequestProperty("Content-Type","application/json");  //设定 请求格式 json
                    connection.setRequestProperty("Accept","application/json");//设定响应的信息的格式为 json
                    connection.connect();



                    jsonObject.put("contents",array);
                    OutputStream out = connection.getOutputStream();
                    out.write(jsonObject.toString().getBytes());
                    out.flush();
                    out.close();

                    array.clear();
                    jsonObject.clear();


                    //读取响应
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while((line = reader.readLine())!=null){
                        builder.append(new String(line.getBytes(),"utf-8"));
                    }
                    System.out.println(builder.toString());

                        JSONArray resultArray = JSONArray.fromObject(builder.toString());

                        System.out.println(resultArray.getJSONObject(0).getString("keywords"));

                        String result = resultArray.getJSONObject(0).getString("keywords");
                        sql = "update temp20 set keywords='" + result + "' where id=" + id;
                        try{
                            updatestatement.execute(sql);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    reader.close();
                    connection.disconnect();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
