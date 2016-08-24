package demo;

/**
 * Created by lenovo on 2016/8/8.
 */
import java.util.LinkedList;
import java.util.List;

public class Content {
    private Integer id;
    private String title;
    private String content;
    private List<String> titleSeg = new LinkedList<String>();
    private List<String> contentSeg = new LinkedList<String>();
    private List<String> titleKeywords = new LinkedList<String>();
    private List<String> contentKeywords = new LinkedList<String>();
    private List<String> finalKeywords = new LinkedList<String>();

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public List<String> getTitleSeg(){
        return titleSeg;
    }

    public void setTitleSeg(List<String> titleSeg){
        this.titleSeg = titleSeg;
    }

    public List<String> getContentSeg(){
        return contentSeg;
    }

    public void setContentSeg(List<String> contentSeg){
        this.contentSeg = contentSeg;
    }

    public List<String> getTitleKeywords(){
        return titleKeywords;
    }

    public void setTitleKeywords(List<String> titleKeywords){
        this.titleKeywords =  titleKeywords;
    }

    public List<String> getContentKeywords(){
        return contentKeywords;
    }

    public void setContentKeywords(List<String> contentWords){
        this.contentKeywords = contentWords;
    }

    public List<String> getFinalKeywords(){
        return finalKeywords;
    }

    public void setFinalKeywords(List<String> finalKeywords){
        this.finalKeywords = finalKeywords;
    }


}
