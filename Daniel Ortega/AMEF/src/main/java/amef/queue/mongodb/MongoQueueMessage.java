package amef.queue.mongodb;

import amef.queue.QueueMessage;
import org.bson.Document;

public class MongoQueueMessage extends Document implements QueueMessage {

    public static String BODY = "body";
    public static String STATE = "state";

    public static int ready = 0;
    public static int inflifht = 1;

    public static Document readyFilter = new Document(STATE, ready);
    public static Document inflifhtFilter = new Document(STATE, inflifht);

    public MongoQueueMessage(Document document){
        this.append(BODY, document.get(BODY));
        this.append(STATE, document.get(STATE));
    }

    public MongoQueueMessage(String text){
        this.append(BODY, text);
        this.append(STATE, ready);
    }

    @Override
    public String getBody() {
        return this.getString(BODY);
    }

    public Document bodyFilter(){
        return new Document(BODY, this.get(BODY));
    }

}
