package amef.queue;

public interface Queue {

    QueueMessage getOneMessage();

    void messageDone(QueueMessage message);

    void clearQueue();

    void sendBatch(QueueMessageBatch batch);

    QueueMessageBatch newbatch();

    void interrupted(QueueMessage message);

    QueueSize size();
}
