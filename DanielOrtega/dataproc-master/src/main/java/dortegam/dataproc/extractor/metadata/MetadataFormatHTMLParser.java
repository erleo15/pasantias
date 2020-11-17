package dortegam.dataproc.extractor.metadata;

import dortegam.dataproc.extractor.metadata.model.MetadataItem;
import dortegam.dataproc.extractor.metadata.search.MetadataSearch;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Stack;

abstract class  MetadataFormatHTMLParser {

    private static final String HTML = "html";
    private static final String HEAD = "head";
    private static final String BODY = "body";


    MetadataSearch search;


    MetadataFormatHTMLParser(MetadataSearch search){
        this.search = search;
    }

    abstract void head(Element elem);

    abstract List<MetadataItem> tail(Element elem);


    private boolean forced = false;


    //Determines if an item should be forced in head or body so the stack is not empty
    boolean forceItem(Stack items, String tag){
        if(items.isEmpty() && (tag.equals(HEAD) || tag.equals(BODY))){
            return forced = true;
        }else {
            return false;
        }
    }

    //Determines if the item of head or body has been forced
    boolean hasForcedItem(String tag){
        return forced && (tag.equals(HEAD) || tag.equals(BODY));
    }



    boolean notHTML(String tag){
        return !tag.equals(HTML);
    }




    //Parse item name from url
    String parseItem(String url){
        return url.replaceAll("https?://"+parseVocab(url)+"/?", "");
    }

    //Parse vocabulary from vocabulary or item url
    String parseVocab(String url){
        url = url.replaceAll("https?://", "");
        if(url.contains("/")){
            url = url.substring(0,url.lastIndexOf('/'));
        }
        return url;
    }
}
