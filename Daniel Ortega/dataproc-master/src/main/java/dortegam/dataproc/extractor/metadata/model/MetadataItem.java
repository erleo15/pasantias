package dortegam.dataproc.extractor.metadata.model;

import com.mongodb.BasicDBObject;

import java.util.ArrayList;

public class MetadataItem extends BasicDBObject {

    private static final String FORMAT = "format";
    private static final String VOCAB = "vocabulary";
    private static final String NAME = "name";
    private static final String TAG = "tag";
    private static final String PROPERTIES = "properties";

    public MetadataItem(String format, String vocabulary, String name, String tag){
        this.append(FORMAT,format);
        this.append(VOCAB,vocabulary);
        this.append(NAME,name);
        this.append(TAG,tag);
        this.append(PROPERTIES,new ArrayList<MetadataProperty>());
    }

    public String getVocab(){
        return this.getString(VOCAB);
    }

    public void addProperty(MetadataProperty prop){
        ((ArrayList<MetadataProperty>)this.get(PROPERTIES)).add(prop);
    }

    public boolean hasProperties(){
        return !((ArrayList)this.get(PROPERTIES)).isEmpty();
    }

}
