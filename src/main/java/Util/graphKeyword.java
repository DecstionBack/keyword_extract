package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by decstionback on 16-8-22.
 */
public class graphKeyword {

    public static List<LinkedList<String>> loadWords(String columnName){
        List<LinkedList<String>> wordsList = new LinkedList<LinkedList<String>>();
        try {
            Class.forName(Utils.mysql_driver);
            Connection conn = DriverManager.getConnection(Utils.mysql_url, Utils.mysql_user, Utils.mysql_password);
            Statement statement = conn.createStatement();
            String sql = "select id, titleWords, " + columnName + " from samples_topic where topic='G20'";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                LinkedList<String> words = new LinkedList<String>();
                if (rs.getString(columnName) == null)
                    continue;
                for (String word : rs.getString("titleWords").split(" "))
                    words.add(word);
                int i = 0;
                for (String word : rs.getString(columnName).split(" ")){
                    if (!words.contains(word))
                        words.add(word);
                    i++;
                    if (i >= 10)
                        break;
                }
                wordsList.add(words);
            }
            //System.out.println(wordsList);
        }catch(Exception e){
            e.printStackTrace();
        }

        return wordsList;

    }

    public static Map<String, LinkedList<Node>> buildGraph(Map<String, LinkedList<Node>> graphs, List<LinkedList<String>> wordsList){
        String firstNode = null, secondNode = null;
        int index = 0;
        for (LinkedList<String> words : wordsList){
            //System.out.println(index++);
            for (int i = 0; i < words.size() - 1; i++){
                for (int j = i + 1; j < words.size(); j++){
                    firstNode = words.get(i);
                    secondNode = words.get(j);
                   // System.out.println(firstNode + " " + secondNode);
                    if (graphs.containsKey(firstNode)){
                        List<Node> firstNodeToWords = graphs.get(firstNode);
                        //true表示firstNodeToWords中不包含，后面需要添加，为false不用添加
                        boolean flag = true;
                        for (Node node : firstNodeToWords){
                            if (node.getToNode().equals(secondNode)){
                                node.addWeight();
                                flag = false;
                            }
                        }
                        if (flag)
                            firstNodeToWords.add(new Node(secondNode, 1));


                    }
                    else{
                        if (graphs.containsKey(secondNode)){
                            List<Node> secondNodeToWords = graphs.get(secondNode);
                            //true表示firstNodeToWords中不包含，后面需要添加，为false不用添加
                            boolean flag = true;
                            for (Node node : secondNodeToWords){
                                if (node.getToNode().equals(firstNode)){
                                    node.addWeight();
                                    flag = false;
                                }
                            }
                            if (flag)
                                secondNodeToWords.add(new Node(firstNode, 1));
                        }

                        else{
                            LinkedList<Node> newToNodes = new LinkedList<Node>();
                            newToNodes.add(new Node(secondNode, 1));
                            graphs.put(firstNode, newToNodes);
                        }
                    }

                }
            }
        }

        out(graphs);



        return graphs;
    }

    public static void out(Map<String, LinkedList<Node>> graphs){
        for (String word : graphs.keySet()){
            for (Node node : graphs.get(word)){
                if (node.getWeight() > 1)
                    System.out.print(word + ":\n" + node);
            }

        }
    }

    public static void writeFile(Map<String, LinkedList<Node>> graphs) throws IOException{
        File file =new File("Graph_G20.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (String word : graphs.keySet()){
            LinkedList<Node> toNodes = graphs.get(word);
            for (Node node : toNodes){
                bufferedWriter.write(word + " " + node.getToNode() + " " + node.getWeight());
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }
    public static void main(String[] args) throws IOException{
        List<LinkedList<String>> wordsList = loadWords("contentWords");
        Map<String, LinkedList<Node>> graphs= new HashMap<String, LinkedList<Node>>();
        graphs = buildGraph(graphs, wordsList);
        writeFile(graphs);

    }




}
