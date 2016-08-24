package Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	/**
	 * 连接数据库参数 
	 * @driver 表示是mysql数据库
	 * @url 192.168.140.200换成对应的ip，3306端口号不换 sensitive指数据库名称 
	 */
	public static String post_driver = "org.postgresql.Driver";
	public static String post_url  ="jdbc:postgresql://192.168.140.200:5432/keyword_2.0?useSSL=false&characterEncoding=UTF-8";
	public static String post_user = "postgres";
	public static String post_password = "xlive911";

	public static String mysql_driver = "com.mysql.jdbc.Driver";
	public static String mysql_url = "jdbc:mysql://192.168.140.200:3306/keyword2.0?useSSL=false&characterEncoding=UTF-8";
	public static String mysql_user = "root";
	public static String mysql_password = "ekgdebs";

	
	public static String wxb_driver = "com.mysql.jdbc.Driver";
	public static String wxb_url  ="jdbc:postgresql://111.202.27.164:3306/wx?useSSL=false&characterEncoding=UTF-8";
	public static String wxb_user = "root";
	public static String wxb_password = "ekgdebs";
	
	/**
	 * 获取当天日期
	 */
	public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	public static String datetime = df.format(new Date());
}
