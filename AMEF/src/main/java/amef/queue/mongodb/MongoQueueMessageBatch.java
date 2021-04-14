package amef.queue.mongodb;

import amef.queue.QueueMessageBatch;

import java.util.ArrayList;
import java.util.List;

public class MongoQueueMessageBatch implements QueueMessageBatch {

    private List<MongoQueueMessage> batch = new ArrayList<>();

    @Override
    public void add(String message, long index) {
        batch.add(new MongoQueueMessage(message));
    }

    @Override
    public int size() {
        return batch.size();
    }

    @Override
    public void clear() {
        batch.clear();
    }

    public List<MongoQueueMessage> getbatch() {
        return batch;
    }


}
