package amef.storage;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;

import java.util.List;

public class MongoStorage implements OutStorage {

    private MongoCollection statsCollection;
    private MongoCollection dataCollection;

    public MongoStorage (MongoDatabase database){
        // get globals collection and create unique index on the field date if it doesn't exist
        statsCollection = database.getCollection("stats");
        BasicDBObject statsIndex = new BasicDBObject()
                .append("date",1)
                .append("file",1);
        statsCollection.createIndex(statsIndex,new IndexOptions().name("file_date_index").unique(true));

        // get websites collection and create unique index on the fields date and url if it doesn't exist
        dataCollection = database.getCollection("pages");
        BasicDBObject pagesIndex = new BasicDBObject()
                .append("date",1)
                .append("url",1);
        dataCollection.createIndex(pagesIndex, new IndexOptions().name("url_date_index").unique(true));
    }

    @Override
    public void store(String fileKey, Document stats, List<Document> data) {
        try{
            storeStats(stats);
            storeData(data);
        } catch (MongoWriteException e){

        }
    }

    public void storeStats(Document stats){
        statsCollection.insertOne(stats);
    }

    public void storeData(List<Document> data){
        if(!data.isEmpty()) dataCollection.insertMany(data, new InsertManyOptions().ordered(false));
    }

    public void deleteAll(){
        dataCollection.drop();
        statsCollection.drop();
    }

    public void delete(String date){
        dataCollection.deleteMany(new BasicDBObject("date",date));
        statsCollection.deleteMany(new BasicDBObject("date",date));
    }
}
