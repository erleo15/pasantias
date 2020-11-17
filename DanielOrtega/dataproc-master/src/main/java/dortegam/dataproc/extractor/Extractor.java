package dortegam.dataproc.extractor;

import com.mongodb.BasicDBObject;
import org.jsoup.nodes.Document;

import java.util.List;


public abstract class Extractor {

    protected Search search;

    public Extractor (Search search){
        this.search = search;
    }

    abstract public List<BasicDBObject> extract(Document root);

}
