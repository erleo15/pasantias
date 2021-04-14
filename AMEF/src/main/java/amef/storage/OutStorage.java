package amef.storage;

import org.bson.Document;

import java.util.List;

public interface OutStorage {

    void store(String fileKey, Document stats, List<Document> data);

}
