package keywords;

import java.sql.SQLException;
import java.util.List;
import Util.*;
import com.hankcs.hanlp.HanLP;

/**
 * Created by lenovo on 2016/8/24.
 */
public class CombineKeyword {
    public static void main(String[] args) throws SQLException{
        List<String> longtext = loadWords.loadWords();
        StringBuilder builder = new StringBuilder();
        for (String text : longtext){
            builder.append(text);
        }
        System.out.println(builder.toString().length());
        List<String> keywords = HanLP.extractKeyword(builder.toString(), 50);
        for (String word : keywords)
            System.out.print(word + " ");
        System.out.println();
    }
}
