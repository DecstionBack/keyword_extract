package rest_webservice;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.extractPhrases;

import Util.*;

@Path("keyword")
public class Keyword_extract {
    public static int maxContentNumber = 10;
    public static Map<String, String> topicList = new HashMap<String, String>();
    public static Map<String, Integer> idfs = new HashMap<String, Integer>();
    static {
        try {
            topicList = loadTopics.loadTopics();
            if (topicList != null){
                System.out.println("Load topicList successfully");
                System.out.println("size:" + topicList.size());
            }
            idfs = loadIDFs.loadIDFs();
            if (idfs != null){
                System.out.println("Load idfs successfully");
                System.out.println("size:" + idfs.size());
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    @GET
    @Path("/get")
    public String getSingleKeyword(@QueryParam("title") String title, @QueryParam("content")String content, @QueryParam("num") Integer num){
        JSONObject json = new JSONObject();
        if (title !=null)
            json.put("title", title);
        if (content != null)
            json.put("content", content);
        if (num == null)
            num = 10;
        json.put("num",num);
        List<String> keywords = getKeywords_rest.getKeywords(json, idfs);
        return keywords.toString();
    }

    @POST
    @Path("/batch")
    @Produces("application/json")
    public JSONArray getJsonBatch(JSONObject json){
        //System.out.println("batch");
        Integer number = -1;
        if(json.containsKey("num")) {
            number = json.getInt("num");
            System.out.println("number:" + number);
        }


        JSONArray jsonArray = JSONArray.fromObject(json.get("contents"));
        //System.out.println(jsonArray);
        JSONArray jsonResult = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
           // System.out.println(jsonObject);
            //System.out.println(jsonObject.get("title"));

            JSONObject result = new JSONObject();
            //keywords可能为null
            List<String> keywords = getKeywords_rest.getKeywords(jsonObject, idfs);

            result.put("keywords",keywords);
            if(keywords != null)
                result.put("topic", getKeywords_rest.getTopics(keywords, topicList));

            jsonResult.add(result);
        }
        return jsonResult;

    }

    @POST
    @Path("/json")
    @Produces("application/json")
    public JSONObject getJson(JSONObject jsonData) throws IOException{
//        if (jsonData.get("content").toString().length() > 1200)
//            return null;
        System.out.println("title:" + jsonData.get("title"));
        System.out.println("content:" + jsonData.get("content"));
        System.out.println("url:" + jsonData.get("url"));
        System.out.println("num:" + jsonData.get("num"));

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<String> keywords = new LinkedList<String>();

        List<Term> termList = extractPhrases.getNouns(jsonData.get("title").toString());
        List<String> titleWords = new LinkedList<String>();

        for (Term term : termList){
            if (!titleWords.contains(term.word))
                titleWords.add(term.word);
        }
        System.out.println("titleWords:" + titleWords);

        List<String> contentWords = HanLP.extractKeyword(jsonData.get("content").toString(), (Integer)jsonData.get("num"));
        //标题和正文的放在一起
        titleWords.addAll(contentWords);

        //查找所属类别
        List<String> topic = new LinkedList<String>();
        for (String word : titleWords){
            if (topicList.containsKey(word))
                if (!topic.contains(topicList.get(word)))
        topic.add(topicList.get(word));

    }

    JSONObject json = new JSONObject();
    json.put("keywords",titleWords);
    json.put("topic", topic);
    return json;



}

    // The Java method will produce content identified by the MIME Media type "text/plain"
//    @Produces("text/plain")
//    public String sayHello(){
//        return "say hello";
//    }
//    public String getClichedMessage() {
//        // Return some cliched textual content
//        return "Hello World";
//    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://localhost:8088/");
        server.start();

        System.out.println("Server running");
        System.out.println("Visit: http://localhost:8088/keyword");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
