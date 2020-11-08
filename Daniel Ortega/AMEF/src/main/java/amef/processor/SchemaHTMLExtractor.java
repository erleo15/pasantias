package amef.processor;

import amef.schema.SchemaItem;
import amef.schema.SchemaProperty;
import com.google.gson.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *  This class
 */

public class SchemaHTMLExtractor implements FileExtractor{


    private List<SchemaItem> items = new ArrayList<>();

    private Stack<SchemaItem> microdataStack = new Stack<>();
    private Stack<SchemaItem> rdfaStack = new Stack<>();

    private Stack<String> vocabStack = new Stack<>();

    private String section = "html";

    private Map<String,List<String>> search;

    public SchemaHTMLExtractor(Map<String,List<String>> search){
        this.search = search;
    }

    public List<SchemaItem> extract(Node root){
        traverse(root);
        if(!microdataStack.isEmpty()) System.out.println("microdataStack not empty");
        if(!rdfaStack.isEmpty()) System.out.println("rdfaStack not empty");
        if(!vocabStack.isEmpty()) System.out.println("vocabStack not empty");
        return items;
    }

    /**
     *  Performs a depth-first search of the graph
     */
    public void traverse(Node root) {
        Node node = root;
        int depth = 0;

        while (node != null) {
            head(node);
            if ( node.childNodeSize() > 0) {
                node = node.childNode(0);
                depth++;
            } else {
                while (node.nextSibling() == null && depth > 0) {
                    tail(node);
                    node = node.parentNode();
                    depth--;
                }
                tail(node);
                if (node == root)
                    break;
                node = node.nextSibling();
            }
        }
    }

    /**
     *  Called the first time the node is found
     * @param node
     */
    private void head(Node node){
        if( node instanceof Element){
            Element elem = (Element) node;
            if (elem.tagName().equals(HEAD) || elem.tagName().equals(BODY)) {
                section = elem.tagName();
            }

            headMicrodata(elem);
            headRDFa(elem);
            headJsonLD(elem);

            if(elem.tagName().equals(HTML)){
                if (microdataStack.isEmpty()) microdataStack.push(new SchemaItem(MICRODATA,NONE,NONE));
                if (rdfaStack.isEmpty()) rdfaStack.push(new SchemaItem(RDFa,NONE,NONE));
            }
        }
    }

    private void headMicrodata(Element elem){
        if(elem.hasAttr(ITEMSCOPE)){
            String itemtype = elem.attr(ITEMTYPE);
            if ((SCHEMAURL).matcher(itemtype).matches())
                microdataStack.push(new SchemaItem(MICRODATA,parseItemtype(itemtype),elem.tagName()));
        }
        if (elem.hasAttr(ITEMPROP)){
            String itemprop = elem.attr(ITEMPROP);
            if(search.keySet().contains(itemprop)){
                SchemaProperty prop = new SchemaProperty(itemprop, elem.attr(CONTENT));
                if(microdataStack.size()==1) prop = prop.section(section);
                microdataStack.peek().addProperty(prop);
            }
        }
    }

    private void headRDFa(Element elem){
        if (elem.hasAttr(VOCAB)){
            vocabStack.push(elem.attr(VOCAB));
        }
        if(!vocabStack.isEmpty() && (SCHEMAURL).matcher(vocabStack.peek()).matches() && elem.hasAttr(TYPEOF)) {
            rdfaStack.push(new SchemaItem(RDFa,elem.attr(TYPEOF),elem.tagName()));
        }
        if (elem.hasAttr(PROPERTY)){
            String property = elem.attr(PROPERTY);
            if(search.keySet().contains(property)){
                SchemaProperty prop = new SchemaProperty(property, elem.attr(CONTENT));
                if(rdfaStack.size()==1) prop = prop.section(section);
                rdfaStack.peek().addProperty(prop);
            }
        }

    }

    private void headJsonLD(Element elem){
        if(elem.tagName().equals(SCRIPT) && elem.attr(SCRIPTTYPE).contains(JSONTYPE)){
            try{
                JsonElement jsonElement = new JsonParser().parse(elem.text());
                if(jsonElement.isJsonObject()){
                    JsonObject object = jsonElement.getAsJsonObject();
                    if(object.has(CONTEXT) && (SCHEMAURL).matcher(object.get(CONTEXT).getAsString()).matches()){
                        extractJsonLD(object);
                    }
                }
            }catch (JsonSyntaxException mje){
                // void
            }
        }
    }

    private void extractJsonLD(JsonObject object){
        if(object.has(GRAPH) && object.get(GRAPH).isJsonArray()){
            JsonArray graph = object.get(GRAPH).getAsJsonArray();
            for (JsonElement graphElem: graph) {
                if(graphElem.isJsonObject()) extractJsonLD(graphElem.getAsJsonObject());
            }
        }
        if(object.has(TYPE)){
            SchemaItem item = new SchemaItem(JSON,object.get(TYPE).getAsString(),SCRIPT);
            for(String metaProperty : search.keySet()){
                if(object.has(metaProperty)){
                    JsonElement metaElem = object.get(metaProperty);
                    if(metaElem.isJsonArray()){
                        JsonArray metaArray = metaElem.getAsJsonArray();
                        for(JsonElement propElem : metaArray){
                            if(propElem.isJsonPrimitive()){
                                String prop = propElem.getAsString();
                                item.addProperty(new SchemaProperty(metaProperty,prop));
                            }
                        }
                    }else if(metaElem.isJsonPrimitive()){
                        String prop = metaElem.getAsString();
                        item.addProperty(new SchemaProperty(metaProperty,prop));
                    }
                }
            }
            if(!item.getProperties().isEmpty()) items.add(item);
        }
    }

    /**
     *  Called when the node is exited
     * @param node
     */
    private void tail(Node node) {
        if (node instanceof Element) {
            Element elem = (Element) node;
            if ((elem.hasAttr(ITEMSCOPE) && (SCHEMAURL).matcher(elem.attr(ITEMTYPE)).matches()) || elem.tagName().equals(HTML)) {
                SchemaItem microdataItem = microdataStack.pop();
                if(!microdataItem.getProperties().isEmpty()) items.add(microdataItem);
            }
            if ((!vocabStack.isEmpty() && (SCHEMAURL).matcher(vocabStack.peek()).matches() && elem.hasAttr(TYPEOF)) || elem.tagName().equals(HTML)) {
                SchemaItem rdfaItem = rdfaStack.pop();
                if(!rdfaItem.getProperties().isEmpty()) items.add(rdfaItem);
            }
            if (elem.hasAttr(VOCAB)){
                vocabStack.pop();
            }
        }

    }

    private String parseItemtype(String url){
        return url.substring(url.lastIndexOf('/')+1);
    }
}
