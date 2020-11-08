package amef.queue.sqs;

import amef.queue.Queue;
import amef.queue.QueueMessage;
import amef.queue.QueueMessageBatch;
import amef.queue.QueueSize;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;

public class SQSQueue implements Queue {

    private String queueUrl;
    private AmazonSQS client;

    public SQSQueue (String queueUrl, AmazonSQS client){
        this.queueUrl = queueUrl;
        this.client = client;
    }

    @Override
    public QueueMessage getOneMessage() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(1);
        ReceiveMessageResult queueRes = client.receiveMessage(receiveMessageRequest);
        if (queueRes.getMessages().size() < 1) {
            return null;
        }
        return new SQSQueueMessage(queueRes.getMessages().get(0));
    }

    @Override
    public void messageDone(QueueMessage message) {
        client.deleteMessage(new DeleteMessageRequest(queueUrl, ((SQSQueueMessage)message).getReceiptHandle()));
    }

    @Override
    public void clearQueue() {
        client.deleteQueue(new DeleteQueueRequest(queueUrl));
    }

    @Override
    public void sendBatch(QueueMessageBatch batch) {
        SQSQueueMessageBatch b = (SQSQueueMessageBatch)batch;
        client.sendMessageBatch(b.getRequest().withQueueUrl(queueUrl));
    }

    @Override
    public QueueMessageBatch newbatch() {
        return new SQSQueueMessageBatch();
    }

    @Override
    public void interrupted(QueueMessage message) {
        client.changeMessageVisibility(new ChangeMessageVisibilityRequest(queueUrl,((SQSQueueMessage)message).getReceiptHandle(),0));
    }

    @Override
    public QueueSize size() {
        // get queue attributes
        GetQueueAttributesResult res = client.getQueueAttributes(
                new GetQueueAttributesRequest(queueUrl)
                        .withAttributeNames("All"));
        Long queueSize = Long.parseLong(res.getAttributes().get(
                "ApproximateNumberOfMessages"));
        Long inflightSize = Long.parseLong(res.getAttributes().get(
                "ApproximateNumberOfMessagesNotVisible"));
        return new QueueSize(queueSize,inflightSize);
    }


}
