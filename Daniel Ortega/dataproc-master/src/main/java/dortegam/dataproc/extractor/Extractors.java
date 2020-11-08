package dortegam.dataproc.extractor;

import dortegam.dataproc.extractor.metadata.MetadataHTMLExtractor;
import dortegam.dataproc.extractor.metadata.search.MetadataSearch;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;

public enum Extractors {

    HTMLMetadata("HTMLMetadata", MetadataHTMLExtractor.class, MetadataSearch.class);

    private final String id;
    private final Class<? extends Extractor> extractor;
    private final Class<? extends Search> search;

    Extractors(String id, Class<? extends Extractor> extractor, Class<? extends Search> search) {
        this.id = id;
        this.extractor = extractor;
        this.search = search;
    }

    public static Constructor<? extends Extractor> fromId(String extractorId) {
        for(Extractors extractors : values()){
            if(extractors.id.equals(extractorId)){
                try {
                    return extractors.extractor.getConstructor(Search.class);
                }catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        throw new InvalidParameterException("No extractors with id " + extractorId + "exist" );
    }

    public static Class<? extends Search> searchFromId(String extractorId) {
        for(Extractors extractors : values()){
            if(extractors.id.equals(extractorId)){
                return extractors.search;
            }
        }
        throw new InvalidParameterException("No extractors with id " + extractorId + "exist" );
    }
}
