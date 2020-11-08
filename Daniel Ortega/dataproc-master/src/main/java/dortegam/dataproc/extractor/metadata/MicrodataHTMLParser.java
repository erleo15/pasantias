package dortegam.dataproc.extractor.metadata;


import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import dortegam.dataproc.extractor.metadata.search.MetadataSearch;
import dortegam.dataproc.extractor.metadata.model.MetadataItem;
import dortegam.dataproc.extractor.metadata.model.MetadataProperty;


public class MicrodataHTMLParser extends MetadataFormatHTMLParser{

    private static final String ITEMSCOPE = "itemscope";
    private static final String ITEMTYPE = "itemtype";
    private static final String ITEMPROP = "itemprop";

    private static final String MICRODATA = "microdata";

    private static final String CONTENT = "content";


    private Stack<MetadataItem> items = new Stack<>();


    MicrodataHTMLParser(MetadataSearch search) {
        super(search);
    }


    @Override
    public void head(Element elem) {

        MetadataItem item = wantedItem(elem);

        //Parse node looking for items.
        if(item != null){
            items.push(item);
        }

        if(notHTML(elem.tagName())){
            //Parse node looking for properties
            MetadataProperty property = wantedProperty(elem);

            if (property != null){
                items.peek().addProperty(property);
            }
        }



    }

    @Override
    public List<MetadataItem> tail(Element elem) {
        if(wantedItem(elem) != null || hasForcedItem(elem.tagName())){
            MetadataItem item = items.pop();
            if(item.hasProperties()) return Collections.singletonList(item);
        }
        return null;
    }




    //Returns a metadata item if this node contains a searched one or has to be forced. Else returns null
    private MetadataItem wantedItem(Element elem){

        if(elem.hasAttr(ITEMSCOPE) || elem.hasAttr(ITEMTYPE)){

            String itemtype = elem.attr(ITEMTYPE);

            if(search.wantsVocab(parseVocab(elem.attr(ITEMTYPE)))){
                return new MetadataItem(MICRODATA,parseVocab(itemtype),parseItem(itemtype),elem.tagName());
            }

        }

        if (forceItem(items,elem.tagName())){
            return new MetadataItem(MICRODATA,null,null, elem.tagName());
        }

        return null;

    }


    //Returns a metadata property if this node contains a searched one. Else returns null
    private MetadataProperty wantedProperty(Element elem){
        if (elem.hasAttr(ITEMPROP)) {

            String value = null;

            if(elem.hasAttr(CONTENT)){
                value = elem.attr(CONTENT);
            } else if (elem.textNodes().size() > 0){
                value = elem.textNodes().get(0).text();
            }

            MetadataSearch.Result result = search.find(items.peek().getVocab(),elem.attr(ITEMPROP),value);

            if(result != null){

                return new MetadataProperty(result.searchId, elem.attr(ITEMPROP), value, result.correctValue );

            }

        }
        return null;
    }





}
