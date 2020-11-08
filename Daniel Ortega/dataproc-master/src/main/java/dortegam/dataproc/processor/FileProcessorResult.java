package dortegam.dataproc.processor;

import org.bson.Document;

import java.util.List;

public class FileProcessorResult {

    private String fileKey;
    private Document stats;
    private List<Document> data;

    public FileProcessorResult(String fileKey, Document stats, List<Document> data) {
        this.fileKey = fileKey;
        this.stats = stats;
        this.data = data;
    }

    public String getFileKey() {
        return fileKey;
    }

    public Document getStats() {
        return stats;
    }

    public List<Document> getData() {
        return data;
    }
}
