package amef.queue.mongodb;

import amef.queue.Queue;
import amef.queue.QueueMessage;
import amef.queue.QueueMessageBatch;
import amef.queue.QueueSize;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoQueue implements Queue {

    MongoCollection queue;

    public MongoQueue(MongoClient client, String queue){
        // get target database
        MongoDatabase database = client.getDatabase("queues");

        // get globals collection and create unique index on the field date if it doesn't exist
        this.queue = database.getCollection(queue);
    }

    @Override
    public synchronized MongoQueueMessage getOneMessage(){
        Object message = queue.findOneAndUpdate(MongoQueueMessage.readyFilter,set(MongoQueueMessage.inflifhtFilter));
        if(message == null) return null;
        return new MongoQueueMessage((Document) message);
    }

    @Override
    public void messageDone(QueueMessage message){
        queue.deleteOne(((MongoQueueMessage)message).bodyFilter());
    }

    @Override
    public void clearQueue(){
        queue.deleteMany(new Document());
    }

    @Override
    public void sendBatch(QueueMessageBatch batch) {
        MongoQueueMessageBatch b = (MongoQueueMessageBatch) batch;
        queue.insertMany(b.getbatch());
    }

    @Override
    public QueueMessageBatch newbatch() {
        return new MongoQueueMessageBatch();
    }


    @Override
    public void interrupted(QueueMessage message) {
        queue.updateOne(((MongoQueueMessage)message).bodyFilter(),set(MongoQueueMessage.readyFilter));
    }

    public void retakeInFlight(){
        System.out.println("retakeInflight");
        queue.updateMany(MongoQueueMessage.inflifhtFilter,set(MongoQueueMessage.readyFilter));
    }

    @Override
    public QueueSize size() {
        return new QueueSize(queue.countDocuments(MongoQueueMessage.readyFilter), queue.countDocuments(MongoQueueMessage.inflifhtFilter));
    }


    public Document set(Document doc){
        return new Document("$set",doc);
    }
}
