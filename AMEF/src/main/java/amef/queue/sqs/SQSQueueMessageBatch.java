package amef.queue.sqs;

import amef.queue.QueueMessageBatch;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;

public class SQSQueueMessageBatch implements QueueMessageBatch {

    private SendMessageBatchRequest smbr = new SendMessageBatchRequest();

    @Override
    public void add(String message, long index) {
        SendMessageBatchRequestEntry smbre = new SendMessageBatchRequestEntry();
        smbre.setMessageBody(message);
        smbre.setId("task_" + index);
        smbr.getEntries().add(smbre);
    }

    @Override
    public int size() {
        return smbr.getEntries().size();
    }

    @Override
    public void clear() {
        smbr.getEntries().clear();
    }

    public SendMessageBatchRequest getRequest(){
        return smbr;
    }
}
