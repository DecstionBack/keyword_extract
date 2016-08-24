package Util;

/**
 * Created by decstionback on 16-8-23.
 */
public class Node {
    private String toNode;
    private int weight;

    public Node(){

    }

    public Node(String toNode, int weight){
        this.toNode = toNode;
        this.weight = weight;
    }

    public void setToNode(String toNode){
        this.toNode = toNode;
    }

    public String getToNode(){
        return toNode;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public int getWeight(){
        return weight;
    }

    public void addWeight(){
        weight = weight + 1;
    }

    @Override
    public String toString(){
        return toNode + ":" + weight;
    }
}
