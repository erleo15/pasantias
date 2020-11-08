package amef.processor;

import amef.ProcessingNode;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;


import amef.schema.SchemaItem;
import org.jwat.warc.WarcRecord;

public class SchemaWarcProcessor extends ProcessingNode implements FileProcessor {

    private static Logger log = Logger.getLogger(SchemaWarcProcessor.class);

    @Override
    public void process(InputStream fileStream,
                        String inputFileKey) throws Exception {

        log.info("Extracting data from " + inputFileKey + " ...");
        WarcReader warcReader = WarcReaderFactory.getReaderCompressed(fileStream);

        long pagesTotal = 0;
        long foundTotal = 0;
        long start = System.currentTimeMillis();

        String date = getDate(inputFileKey);

        // read all entries in the ARC file
        RecordWithOffsetsAndURL item;
        item = getNextResponseRecord(warcReader);
        List<Document> result = new ArrayList<>();
        while (item != null) {
            List<SchemaItem> items;
            try {
                org.jsoup.nodes.Document doc;
                try {
                    // try parsing with charset detected from doc
                    doc = Jsoup.parse(new ByteArrayInputStream(item.bytes),
                            null, "");
                    items =  new SchemaHTMLExtractor(search()).extract(doc);
                } catch (IllegalCharsetNameException
                        | UnsupportedCharsetException e) {
                    try {
                        // didnt work, try parsing with utf-8 as charset
                        doc = Jsoup.parse(new ByteArrayInputStream(item.bytes),
                                "UTF-8", "");
                        items =  new SchemaHTMLExtractor(search()).extract(doc);
                    } catch (IllegalCharsetNameException
                            | UnsupportedCharsetException e2) {
                        // didnt work either, no result
                        items = new ArrayList<>();
                    }
                }
                if (!items.isEmpty()) {
                    result.add(getDocument(inputFileKey,items,item.url,date));
                    foundTotal++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pagesTotal++;
            }

            // next record with one retry
            item = getNextResponseRecord(warcReader);
        }
        warcReader.close();

        //Store statistics in a document
        double duration = (System.currentTimeMillis() - start) / 1000.0;
        double rate = (pagesTotal * 1.0) / duration;

        Document fileStats = new Document();
        fileStats.append("file",inputFileKey);
        fileStats.append("total", date);
        fileStats.append("ready",pagesTotal);
        fileStats.append("found",foundTotal);
        fileStats.append("duration",duration);
        fileStats.append("rate",rate);

        //Upload results to database
        getOutStorage().store(getOutputFileKey(date,inputFileKey),fileStats,result);

        log.info("Extracted data from " + inputFileKey + " - parsed "
                + pagesTotal + " pages in " + duration + " seconds, " + rate
                + " pages/sec");
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


    private String getDate(String fileName){
        int slashIndex = fileName.lastIndexOf('/');
        String date = fileName.substring(slashIndex+9, slashIndex+15);
        return date.substring(0,4).concat("-").concat(date.substring(4));
    }

    protected String getDomain(String url){
        String domain = url.substring(url.indexOf(':')+3);
        return domain.contains("/") ? domain.substring(0, domain.indexOf('/')) : domain;
    }

    private String getOutputFileKey(String date, String inputFileKey){
        int idx = inputFileKey.indexOf(".warc");
        String key = inputFileKey.substring(0, idx) + ".json";
        return date + "/" + key;
    }




    private static final String WARC_TARGET_URI = "WARC-Target-URI";

    protected static class RecordWithOffsetsAndURL {
        public byte[] bytes;
        public String url;

        public RecordWithOffsetsAndURL(byte[] bytes, String url) {
            super();
            this.bytes = bytes;
            this.url = url;
        }
    }

    protected RecordWithOffsetsAndURL getNextResponseRecord(WarcReader warcReader)
            throws IOException {
        WarcRecord wr;
        while (true) {
            try {
                wr = warcReader.getNextRecord();
            } catch (IOException e) {
                continue;
            }
            if (wr == null)
                return null;

            String type = wr.getHeader("WARC-Type").value;
            if (type.equals("response")) {
                byte[] rawContent = IOUtils.toByteArray(wr.getPayloadContent());
                String url = wr.getHeader(WARC_TARGET_URI).value;
                return new RecordWithOffsetsAndURL(rawContent, url);
            }
        }
    }
}
