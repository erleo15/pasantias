package amef.schema;

import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.List;

public class SchemaItem extends BasicDBObject {

    private static String FORMAT = "format";
    private static String NAME = "name";
    private static String TAG = "tag";
    private static String PROPERTIES = "properties";

    public SchemaItem(String format, String name, String tag){
        this.append(FORMAT,format);
        this.append(NAME,name);
        this.append(TAG,tag);
        this.append(PROPERTIES,new ArrayList<SchemaProperty>());
    }

    public SchemaItem(String format, String name){
        this.append(FORMAT,format);
        this.append(NAME,name);
        this.append(PROPERTIES,new ArrayList<SchemaProperty>());
    }

    public SchemaItem addProperty(SchemaProperty prop){
        ((ArrayList<SchemaProperty>)this.get(PROPERTIES)).add(prop);
        return this;
    }

    public List<SchemaProperty> getProperties(){
        return (List<SchemaProperty>)this.get(PROPERTIES);
    }

}
