package dortegam.dataproc.extractor.metadata;


import dortegam.dataproc.extractor.metadata.model.MetadataItem;
import dortegam.dataproc.extractor.metadata.model.MetadataProperty;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import dortegam.dataproc.extractor.metadata.search.MetadataSearch;


public class RDFaHTMLParser extends MetadataFormatHTMLParser {

    private static final String VOCAB = "vocab";
    private static final String TYPEOF = "typeof";
    private static final String PROPERTY = "property";

    private static final String RDFa = "rdfa";

    private static final String CONTENT = "content";


    private Stack<MetadataItem> items = new Stack<>();
    private Stack<String> vocabs = new Stack<>();



    RDFaHTMLParser(MetadataSearch search) {
        super(search);
    }

    @Override
    public void head(Element elem) {

        if (elem.hasAttr(VOCAB)) {
            vocabs.push(parseVocab(elem.attr(VOCAB)));
        }

        MetadataItem item = wantedItem(elem);

        //Parse node looking for items.
        if (item != null) {
            items.push(item);
        }

        if (notHTML(elem.tagName())) {

            //Parse node looking for properties
            MetadataProperty property = wantedProperty(elem);

            if (property != null) {
                items.peek().addProperty(property);
            }
        }

    }

    @Override
    public List<MetadataItem> tail(Element elem) {
        if(elem.hasAttr(VOCAB)){
            vocabs.pop();
        }

        if(wantedItem(elem) != null || hasForcedItem(elem.tagName())){
            MetadataItem item = items.pop();
            if(item.hasProperties()) return Collections.singletonList(item);
        }
        return null;
    }

    //Returns a metadata item if this node contains a searched one or has to be forced. Else returns null
    private MetadataItem wantedItem(Element elem){

        if(elem.hasAttr(TYPEOF) && !vocabs.isEmpty()){

            if(search.wantsVocab(vocabs.peek())){
                return new MetadataItem(RDFa,vocabs.peek(),elem.attr(TYPEOF),elem.tagName());
            }

        }
        if (forceItem(items,elem.tagName())){
            return new MetadataItem(RDFa,null,null, elem.tagName());
        }

        return null;

    }


    //Returns a metadata property if this node contains a searched one. Else returns null
    private MetadataProperty wantedProperty(Element elem){
        if (elem.hasAttr(PROPERTY)) {

            String value = null;

            if(elem.hasAttr(CONTENT)){
                value = elem.attr(CONTENT);
            } else if (elem.textNodes().size() > 0){
                value = elem.textNodes().get(0).text();
            }

            MetadataSearch.Result result = search.find(items.peek().getVocab(),elem.attr(PROPERTY),value);

            if(result != null){

                return new MetadataProperty(result.searchId, elem.attr(PROPERTY), value, result.correctValue );

            }

        }
        return null;
    }

}
