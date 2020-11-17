package dortegam.dataproc.extractor;


import com.mongodb.BasicDBObject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;


public abstract class HTMLExtractor extends Extractor{

    protected List<BasicDBObject> items;

    public HTMLExtractor (Search search){
        super(search);
    }


    @Override
    public List<BasicDBObject> extract(Document root){
        items = new ArrayList<>();
        init();
        traverse(root);
        return items;
    }

    /**
     * Performs a depth-first search of the graph
     *
     * @param root Base node of the HTML document
     */
    private void traverse(Element root) {
        Element elem = root;
        int depth = 0;

        while (elem != null) {
            head(elem);
            if ( elem.children().size() > 0) {
                elem = elem.child(0);
                depth++;
            } else {
                while (elem.nextElementSibling() == null && depth > 0) {
                    tail(elem);
                    elem = elem.parent();
                    depth--;
                }
                tail(elem);
                if (elem == root)
                    break;
                elem = elem.nextElementSibling();
            }
        }
    }


    protected abstract void init();

    /**
     * Called when the node is entered
     *
     * @param elem Element being entered
     */
    protected abstract void head(Element elem);

    /**
     * Called when the node is abandoned
     *
     * @param elem Element being abandoned
     */
    protected abstract void tail(Element elem);


}
