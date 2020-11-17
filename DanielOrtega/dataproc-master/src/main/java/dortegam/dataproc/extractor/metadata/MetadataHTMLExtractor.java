package dortegam.dataproc.extractor.metadata;


import dortegam.dataproc.extractor.Search;
import dortegam.dataproc.extractor.metadata.model.MetadataItem;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

import dortegam.dataproc.extractor.metadata.search.MetadataSearch;
import dortegam.dataproc.extractor.HTMLExtractor;


public class MetadataHTMLExtractor extends HTMLExtractor {

    private List<MetadataFormatHTMLParser> parsers;

    public MetadataHTMLExtractor(Search search){
        super(search);
    }


    public void init(){
        parsers = Arrays.asList(
                new MicrodataHTMLParser((MetadataSearch) search),
                new RDFaHTMLParser((MetadataSearch) search),
                new JSONLDHTMLParser((MetadataSearch) search)
        );
    }

    public void head(Element elem){

        //Search for items and properties in all formats
        for (MetadataFormatHTMLParser parser : parsers){
            parser.head(elem);
        }

    }


    public void tail(Element elem) {

        //Store items with results
        for (MetadataFormatHTMLParser parser : parsers){
            List<MetadataItem> foundItems = parser.tail(elem);
            if(foundItems != null){
                items.addAll(foundItems);
            }
        }

    }


}
