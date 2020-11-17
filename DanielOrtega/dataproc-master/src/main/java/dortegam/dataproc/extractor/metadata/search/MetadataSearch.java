package dortegam.dataproc.extractor.metadata.search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dortegam.dataproc.extractor.Search;

import java.io.Reader;
import java.util.*;

public class MetadataSearch extends ArrayList<MetadataSearchEntry> implements Search {

    public boolean wantsVocab (String sVocabulary){
        for(MetadataSearchEntry entry : this){
            if(entry.vocabulary.equals(sVocabulary)) return true;
        }
        return false;
    }

    public Result find (String sVocabulary, String sProperty, String sValue){
        for(MetadataSearchEntry entry : this){
            if(entry.vocabulary.equals(sVocabulary)){
                for(MetadataSearchProperty property : entry.properties){
                    if(property.name.equals(sProperty)){
                        if(property.values == null){
                            return new Result(entry.sector, true);
                        }else{
                            for(String value : property.values){
                                if(sValue.startsWith(value))
                                    return new Result(entry.sector, true);
                            }
                            return new Result(entry.sector, false);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static MetadataSearch fromJson(Reader sReader){
        Gson gson =  new GsonBuilder().registerTypeAdapter(MetadataSearchEntry.class, MetadataSearchEntry.deserializer).create();
        return gson.fromJson(sReader, MetadataSearch.class);
    }

    public static class Result{

         public String searchId;

         public boolean correctValue;

         Result(String searchId, boolean correctValue) {
             this.searchId = searchId;
             this.correctValue = correctValue;
         }

    }

}



