package amef;

import java.io.*;
import java.util.*;

import amef.storage.*;
import org.apache.commons.configuration.*;

import amef.queue.Queue;
import amef.queue.sqs.SQSQueue;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import amef.queue.mongodb.MongoQueue;

public abstract class ProcessingNode {
	private static Logger log = Logger.getLogger(ProcessingNode.class);

	protected static final String PFILENAME = "/amef.properties";

	protected static final String SEARCHFILENAME = "/search.txt";

	private static PropertiesConfiguration config = null;

	private static Map<String,List<String>> search = null;

	private S3Storage s3 = null;

	private MongoStorage mongoStorage = null;

	private UrlStorage urlStorage = null;

	private Queue queue = null;

	private MongoClient mongoClient = null;

	private String queueUrl = null;

	protected static PropertiesConfiguration config() {
		if (config == null) {
			config = loadConfig(PFILENAME);
		}
                
		return config;
	}

	protected static PropertiesConfiguration loadConfig(String f) {
		PropertiesConfiguration p = new PropertiesConfiguration();
		InputStream pStream = ProcessingNode.class
				.getResourceAsStream(f);
                
                
                
		if (pStream == null) {
			log.warn("Unable to find property file " + f);
			return p;
		}
		try {
			p.load(pStream);
		} catch (ConfigurationException e) {
			log.warn("Unable to load property file " + f);
		}
		return p;
	}

	protected static Map<String,List<String>> search() {
		if (search == null) {
			search = loadSearch(SEARCHFILENAME);
		}
		return search;
	}


	private static Map<String,List<String>> loadSearch(String f) {
		Map<String,List<String>> searchTerms = new HashMap<>();
		InputStream sStream = ProcessingNode.class
				.getResourceAsStream(f);
		if (sStream == null) {
			log.warn("Unable to find property file " + f);
			return searchTerms;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(sStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					String prop;
					if(line.contains(":")) {
						prop = line.substring(0, line.indexOf(':'));
						List<String> values = new ArrayList<>();
						String valuesStr = line.substring(line.indexOf(":")+1);
						StringTokenizer st = new StringTokenizer(valuesStr,",");
						while (st.hasMoreTokens()){
							String value = st.nextToken();
							values.add(value);
						}
						searchTerms.put(prop.trim(),values);
					} else {
						searchTerms.put(line.trim(),null);
					}
				}
			}
			return searchTerms;
		} catch (IOException e) {
			log.warn("Unable to find search file " + f);
			return null;
		}
	}

	/**
	 * Returns property value for a certain key.
	 *
	 * @param key
	 * @return property value, empty string if property could not be found
	 * @throws IllegalArgumentException
	 *             if no key is set.
	 */
	public static String getOrCry(String key) {
		if (key == null || key.trim().equals("")) {
			throw new IllegalArgumentException(
					"No key given for config lookup!");
		}
		String value = config().getString(key);
		if (value == null || value.trim().equals("")) {
			log.warn("Value not found in configuration for key " + key);
			return "";
		}
		return value.trim();
	}

	protected AWSCredentials getAwsCredentials() {
		return new AWSCredentials() {
			public String getAWSAccessKeyId() {
				return config().getString("awsAccessKey");
			}

			public String getAWSSecretKey() {
				return config().getString("awsSecretKey");
			}
		};
	}

	protected org.jets3t.service.security.AWSCredentials getJetS3tCredentials() {
		AWSCredentials cred = getAwsCredentials();
		return new org.jets3t.service.security.AWSCredentials(
				cred.getAWSAccessKeyId(), cred.getAWSSecretKey());
	}

	protected synchronized Queue getQueue(){
		if(queue == null){
			if(getOrCry("queueService").equals("sqs")){
				queue = new SQSQueue(getQueueUrl(),getSQSQueue());
			}else if(getOrCry("queueService").equals("mongo")){
				queue = getMongoQueue();
			}else{
				log.warn("Queue service provided is not valid");
			}
		}
		return queue;
	}

	private AmazonSQS getSQSQueue() {
		AmazonSQSClient sqs = new AmazonSQSClient(getAwsCredentials());
		sqs.setEndpoint(config().getString("queueEndpoint"));
		return sqs;
	}

	private MongoQueue getMongoQueue() {
		MongoQueue queue = new MongoQueue(getMongo(), getOrCry("jobQueueName"));
		return queue;
	}

	public synchronized MongoClient getMongo(){
		if (mongoClient == null) {
			Logger.getLogger("org.mongodb.driver").setLevel(Level.WARN);
			StringBuilder mongoPath = new StringBuilder();
			mongoPath.append("mongodb://");
			mongoPath.append(getOrCry("mongoUrl"));
			mongoPath.append(":");
			mongoPath.append(getOrCry("mongoPort"));

			mongoClient = MongoClients.create(mongoPath.toString());
		}
		return mongoClient;

	}

	protected OutStorage getOutStorage(){
		if(getOrCry("storageService").equals("s3")){
			return getS3Storage();
		} else if(getOrCry("storageService").equals("mongo")){
			return getMongoStorage();
		} else {
			log.warn("Provided environment is not valid");
		}
		return null;
	}

	protected InStorage getInStorage(){
		if(getOrCry("fileOrigin").equals("s3")){
			return getS3Storage();
		} else if(getOrCry("fileOrigin").equals("url")){
			return getUrlStorage();
		} else {
			log.warn("Provided file origin is not valid");
		}
		return null;
	}

	protected UrlStorage getUrlStorage() {
		if (urlStorage == null) {
			urlStorage = new UrlStorage();
		}
		return urlStorage;
	}

	protected S3Storage getS3Storage() {
		if (s3 == null) {
			try {
				s3 = new S3Storage(getJetS3tCredentials(),getOrCry("resultBucket"),getOrCry("dataBucket"));
			} catch (S3ServiceException e1) {
				log.warn("Unable to connect to S3", e1);
			}
		}
		return s3;
	}

	protected MongoStorage getMongoStorage(){
		if(mongoStorage == null){
			mongoStorage = new MongoStorage(getMongo().getDatabase(getOrCry("mongoDB")));
		}
		return mongoStorage;
	}

	private String getQueueUrl() {
		if (queueUrl == null) {
			String jobQueueName = config().getString("jobQueueName");
			if (jobQueueName == null || jobQueueName.trim().equals("")) {
				log.warn("No job queue given");
				return "";
			}
			try {
				GetQueueUrlResult res = getSQSQueue().getQueueUrl(
						new GetQueueUrlRequest(jobQueueName));
				queueUrl = res.getQueueUrl();

				// create the queue if it should be missing for some reason
			} catch (AmazonServiceException e) {
				if (e.getErrorCode().equals(
						"AWS.SimpleQueueService.NonExistentQueue")) {

					CreateQueueRequest req = new CreateQueueRequest();
					req.setQueueName(jobQueueName);

					Map<String, String> qattr = new HashMap<String, String>();
					qattr.put("DelaySeconds", "0");

					qattr.put("MessageRetentionPeriod", "1209600");
					qattr.put("VisibilityTimeout", getOrCry("jobTimeLimit"));
					req.setAttributes(qattr);

					CreateQueueResult res = getSQSQueue().createQueue(req);
					queueUrl = res.getQueueUrl();
				}
			}
		}
		return queueUrl;
	}

}
