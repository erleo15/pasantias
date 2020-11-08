package dortegam.dataproc.framework.worker;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;

import com.google.gson.JsonSyntaxException;
import dortegam.dataproc.extractor.Extractor;
import dortegam.dataproc.extractor.Extractors;
import dortegam.dataproc.extractor.Search;
import dortegam.dataproc.framework.Node;
import dortegam.dataproc.processor.FileProcessor;
import dortegam.dataproc.processor.FileProcessors;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;


abstract class WorkerNode extends Node {

    private Search search = null;

    @Override
    protected AWSCredentialsProvider getAwsCredentials(){
        return new EC2ContainerCredentialsProviderWrapper();
    }

    protected Search search() {
        if(search == null){
            search = loadSearch();
        }
        return search;
    }

    private Search loadSearch() {
        Search search = null;
        try (Reader sReader = new FileReader(new File(SEARCHFILENAME))) {
            Class<? extends Search> searchClass = Extractors.searchFromId(getOrCry("extractor"));
            search = (Search) searchClass.getMethod("fromJson",Reader.class).invoke(null,sReader);
        } catch (IOException e) {
            System.err.println("Unable to find property file " + PROPFILENAME);
            System.exit(1);
        } catch (JsonSyntaxException e){
            System.err.println("Wrong search file format");
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            System.exit(1);
        }
        return search;
    }

    protected Constructor<? extends FileProcessor> getProcessor() throws InvalidParameterException {
        return FileProcessors.fromId(getOrCry("processor"));
    }

    protected Constructor<? extends Extractor> getExtractor() throws InvalidParameterException{
        return Extractors.fromId(getOrCry("extractor"));
    }

}
