package dortegam.dataproc.extractor.metadata.search;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.Set;

public class MetadataSearchEntry {

    String sector;

    String vocabulary;

    Set<MetadataSearchProperty> properties;

    private MetadataSearchEntry(String sector, String vocabulary, Set<MetadataSearchProperty> properties) {
        this.sector = sector;
        this.vocabulary = vocabulary;
        this.properties = properties;
    }

    public static JsonDeserializer<MetadataSearchEntry> deserializer = (jsonElement, type, context) -> {

        JsonObject object = jsonElement.getAsJsonObject();


        JsonElement idElem = object.get("id");

        if(idElem == null){
            throw new JsonSyntaxException("Id cannot be null");
        }
        if (!idElem.isJsonPrimitive()) {
            throw new JsonSyntaxException("Id must be a String");
        }

        String id = idElem.getAsString();


        JsonElement vocabularyElem = object.get("vocabulary");

        if(vocabularyElem == null){
            throw new JsonSyntaxException("Vocabulary cannot be null");
        }
        if (!vocabularyElem.isJsonPrimitive()) {
            throw new JsonSyntaxException("Vocabulary must be a String");
        }

        String vocabulary = vocabularyElem.getAsString();


        JsonElement propertiesElem = object.get("properties");

        if(propertiesElem == null){
            throw new JsonSyntaxException("Properties cannot be null");
        }
        if (!propertiesElem.isJsonArray()) {
            throw new JsonSyntaxException("Properties must be an array");
        }

        Set<MetadataSearchProperty> properties = context.deserialize(propertiesElem,new TypeToken<Set<MetadataSearchProperty>>(){}.getType());

        return new MetadataSearchEntry(id,vocabulary,properties);
    };
}