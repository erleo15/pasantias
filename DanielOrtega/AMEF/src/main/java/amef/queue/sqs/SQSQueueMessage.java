package amef.queue.sqs;

import amef.queue.QueueMessage;
import com.amazonaws.services.sqs.model.Message;

public class SQSQueueMessage implements QueueMessage {

    private Message message;

    protected SQSQueueMessage(Message message){
        this.message = message;
    }

    @Override
    public String getBody() {
        return message.getBody();
    }

    protected String getReceiptHandle(){
        return message.getReceiptHandle();
    }

}
