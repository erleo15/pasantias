package dortegam.dataproc.processor;

import dortegam.dataproc.extractor.Extractor;
import dortegam.dataproc.extractor.HTMLExtractor;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;

public enum FileProcessors {

    WARC("WARC", WarcFileProcessor.class, HTMLExtractor.class);


    private final String id;
    private final Class<? extends FileProcessor> processor;
    private final Class<? extends Extractor> extractor;


    FileProcessors(String id, Class<? extends FileProcessor> processor, Class<? extends HTMLExtractor> extractor){
        this.id = id;
        this.processor = processor;
        this.extractor = extractor;
    }

    public static Constructor<? extends FileProcessor> fromId(String processorId){
        for(FileProcessors processors : values()){
            if(processors.id.equals(processorId)){
                try {
                    return processors.processor.getConstructor(processors.extractor);
                }catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        throw new InvalidParameterException("No processors with id " + processorId + " exist.");
    }

}
