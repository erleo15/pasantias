package amef.queue;

public interface QueueMessageBatch {

    void add(String message, long index);

    int size();

    void clear();

}
