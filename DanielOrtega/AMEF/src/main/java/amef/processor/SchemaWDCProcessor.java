package amef.processor;

import amef.ProcessingNode;
import amef.schema.SchemaItem;
import amef.schema.SchemaProperty;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class SchemaWDCProcessor extends ProcessingNode implements FileProcessor, FileExtractor {

    private static Logger log = Logger.getLogger(SchemaWDCProcessor.class);

    @Override
    public void process(InputStream fileStream, String inputFileKey) throws Exception {

        log.info("Extracting data from " + inputFileKey + " ...");

        String date = "";
        Matcher dateMatcher = Pattern.compile("\\d{4}-\\d{2}").matcher(inputFileKey);
        if(dateMatcher.find()){
            date = dateMatcher.group();
        }

        long pagesTotal = 0;
        long foundTotal = 0;
        long start = System.currentTimeMillis();

        List<Document> result = new ArrayList<>();
        String format = "unknown";
        if(inputFileKey.contains("html-microdata")){
            format = MICRODATA;
        }else if (inputFileKey.contains("html-rdfa")){
            format = RDFa;
        }else if (inputFileKey.contains("html-embedded-jsonld")){
            format = JSON;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fileStream)))) {
            String currentUrl = "";
            Map<String,SchemaItem> items = new HashMap<>();
            while (reader.ready()){
                String line;
                try{
                    line = reader.readLine();
                } catch (EOFException e){
                    break;
                }
                StringTokenizer st = new StringTokenizer(line);
                String ref, type, value = "", url;
                try{
                    ref = removeMarks(st.nextToken());
                    type = removeMarks(st.nextToken());
                    while (st.countTokens() > 2){
                        value+=st.nextToken();
                    }
                    value = removeMarks(removeQuotes(value));
                    url = removeMarks(st.nextToken());
                } catch (NullPointerException e){
                    continue;
                }
                if(!url.equals(currentUrl)){
                    List<SchemaItem> foundItems = new ArrayList<>();
                    for(SchemaItem item : items.values()){
                        if(!item.getProperties().isEmpty()) foundItems.add(item);
                    }
                    if(!foundItems.isEmpty()){
                        result.add(getDocument(inputFileKey,foundItems,currentUrl,date));
                        foundTotal++;
                    }
                    pagesTotal++;
                    currentUrl = url;
                    items.clear();
                }
                if(type.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                    if(SCHEMAURL.matcher(value).matches())
                        items.put(ref,new SchemaItem(format,value.substring(value.lastIndexOf('/')+1)));
                } else {
                    if (!items.containsKey(ref)){
                        items.put(ref,new SchemaItem(format,"none"));
                    }
                    if(SCHEMAURL.matcher(type).matches()){
                        String prop = type.substring(type.lastIndexOf('/')+1);
                        if(search().keySet().contains(prop)){
                            if(value.contains("@")) value = value.substring(0,value.indexOf('@'));
                            StringTokenizer values = new StringTokenizer(value,",");
                            while (values.hasMoreTokens()){
                                String val = values.nextToken();
                                if(search().get(prop) == null) items.get(ref).addProperty(new SchemaProperty(prop,val));
                                else {
                                    for(String acceptedValue : search().get(prop)){
                                        if(val.startsWith(acceptedValue)) items.get(ref).addProperty(new SchemaProperty(prop,val));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Store statistics in a document
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            double rate = (pagesTotal * 1.0) / duration;

            Document fileStats = new Document();
            fileStats.append("file",inputFileKey);
            fileStats.append("date", date);
            fileStats.append("total",pagesTotal);
            fileStats.append("found",foundTotal);
            fileStats.append("duration",duration);
            fileStats.append("rate",rate);

            if(!result.isEmpty()) getOutStorage().store(inputFileKey, fileStats, result);
        }
    }

    private Document getDocument(String file, List<SchemaItem> items, String url, String date){
        Document result = new Document();
        result.append("file",file);
        result.append("date",date);
        result.append("domain", getDomain(url));
        result.append("url", url);
        result.append("items", items);
        return result;
    }

    protected String getDomain(String url){
        String domain = url.substring(url.indexOf(':')+3);
        return domain.contains("/") ? domain.substring(0, domain.indexOf('/')) : domain;
    }

    private String removeMarks(String s){
        return s.replace("<","").replace(">","");
    }

    private String removeQuotes(String s){
        return s.replace("\"","");
    }
}