package amef.schema;

import com.mongodb.BasicDBObject;

public class SchemaProperty extends BasicDBObject {

    private String NAME = "name";
    private String VALUE = "value";
    private String SECTION = "section";

    public SchemaProperty(String name, String value){
        this.append(NAME,name);
        this.append(VALUE,value);
    }

    public SchemaProperty section(String section){
        this.append(SECTION,section);
        return this;
    }
}
