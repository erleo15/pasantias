package dortegam.dataproc.extractor.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dortegam.dataproc.extractor.metadata.model.MetadataItem;
import dortegam.dataproc.extractor.metadata.model.MetadataProperty;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dortegam.dataproc.extractor.metadata.search.MetadataSearch;


public class JSONLDHTMLParser extends MetadataFormatHTMLParser {

    private static final String SCRIPT = "script";
    private static final String SCRIPTTYPE = "type";
    private static final String SCRIPTJSONLD = "ld+json";

    private static final String CONTEXT = "@context";
    private static final String TYPE = "@type";

    private static final String JSONLD = "jsonld";


    JSONLDHTMLParser(MetadataSearch search) {
        super(search);
    }

    @Override
    public void head(Element elem) {
        //Ignore
    }

    @Override
    public List<MetadataItem> tail(Element elem) {
        List<MetadataItem> items = new ArrayList<>();
        if(elem.tagName().equals(SCRIPT) &&
                (elem.hasAttr(SCRIPTTYPE) && (elem.attr(SCRIPTTYPE).contains(SCRIPTJSONLD)))){

            JsonElement scriptElem = new Gson().toJsonTree(elem.text());
            if(scriptElem.isJsonObject()){
                JsonObject script = scriptElem.getAsJsonObject();
                String vocab = script.get(CONTEXT).getAsString();
                if(search.wantsVocab(parseVocab(vocab))){
                    recursiveParse(script, vocab, items);
                }
            }
        }
        return items;
    }

    private void recursiveParse(JsonElement elem, String vocab, List<MetadataItem> items){
        if(elem.isJsonArray()){
            for (JsonElement arrayElem : elem.getAsJsonArray()) {
                recursiveParse(arrayElem, vocab, items);
            }
        } else if(elem.isJsonObject()) {
            JsonObject object = elem.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                JsonElement objectElem = entry.getValue();
                if (objectElem.isJsonObject() || objectElem.isJsonArray()) {
                    recursiveParse(objectElem, vocab, items);
                }
            }
            parseObject(object, vocab, items);
        }
    }

    private void parseObject(JsonObject object, String vocab, List<MetadataItem> items){
        if(object.has(TYPE)) {
            String itemName = object.get(TYPE).getAsString();
            MetadataItem item = new MetadataItem(JSONLD,vocab,itemName, SCRIPT);
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (search.find(vocab, entry.getKey(), null) != null) {
                    JsonElement objectElem = entry.getValue();
                    if (objectElem.isJsonPrimitive()) {
                        MetadataProperty property = getProperty(vocab,entry.getKey(),object.getAsString());
                        if (property != null) item.addProperty(property);
                    } else if (objectElem.isJsonArray()){
                        for(JsonElement arrayElem : objectElem.getAsJsonArray()){
                            if (arrayElem.isJsonPrimitive()) {
                                MetadataProperty property = getProperty(vocab,entry.getKey(),arrayElem.getAsString());
                                if (property != null) item.addProperty(property);
                            }
                        }
                    }
                }
            }
            if(item.hasProperties()) items.add(item);
        }
    }

    private MetadataProperty getProperty(String vocab, String property, String value){

        MetadataSearch.Result result = search.find(vocab, property, value);

        if(result != null){

            return new MetadataProperty(result.searchId, property, value, result.correctValue);

        }

        return null;
    }


}
