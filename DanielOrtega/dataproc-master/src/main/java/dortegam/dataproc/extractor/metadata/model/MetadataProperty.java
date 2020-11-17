package dortegam.dataproc.extractor.metadata.model;

import com.mongodb.BasicDBObject;

public class MetadataProperty extends BasicDBObject {

    private static String SEARCHID = "searchId";
    private static String NAME = "name";
    private static String VALUE = "value";
    private static String VALID = "valid";

    public MetadataProperty(String searchId, String name, String value, boolean valid){
        this.append(SEARCHID,searchId);
        this.append(NAME,name);
        this.append(VALUE,value);
        this.append(VALID,valid);
    }

}
