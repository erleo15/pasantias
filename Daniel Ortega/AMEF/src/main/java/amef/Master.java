package amef;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import amef.queue.QueueMessageBatch;
import amef.queue.QueueSize;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.S3Object;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.UnspecifiedParameterException;


public class Master extends ProcessingNode {

	public Master(){

	}

	protected class DateSizeRecord {
		Date recordTime;
		Long queueSize;

		public DateSizeRecord(Date time, Long size) {
			this.recordTime = time;
			this.queueSize = size;
		}
	}

	private static Logger log = Logger.getLogger(Master.class);

	// command line parameters, different actions
	public static void main(String[] args) throws JSAPException {
		// command line parser
		JSAP jsap = new JSAP();
		UnflaggedOption actionParam = new UnflaggedOption("action")
				.setStringParser(JSAP.STRING_PARSER).setRequired(true)
				.setGreedy(false);

		actionParam
				.setHelp("Action to perform, can be 'queue', 'clearqueue', 'launch', 'shutdown', 'run', 'monitor', 'retievedata' and 'deletedata'");

		jsap.registerParameter(actionParam);

		JSAPResult config = jsap.parse(args);
		String action = config.getString("action");

		// Prefix path of objects to be queued from bucket
		if ("queue".equals(action)) {
			FlaggedOption prefix = new FlaggedOption("prefix")
					.setStringParser(JSAP.STRING_PARSER).setRequired(false)
					.setLongFlag("bucket-prefix").setShortFlag('p');

			prefix.setHelp("Prefix path of objects to be queued from bucket");
			jsap.registerParameter(prefix);

			FlaggedOption limit = new FlaggedOption("limit")
					.setStringParser(JSAP.LONG_PARSER).setRequired(false)
					.setLongFlag("file-number-limit").setShortFlag('l');

			limit.setHelp("Limits number of objects to be queued from bucket");

			jsap.registerParameter(limit);

			FlaggedOption prefixFile = new FlaggedOption("prefixFile")
					.setStringParser(JSAP.STRING_PARSER).setRequired(false)
					.setLongFlag("bucket-prefix-file").setShortFlag('f');

			prefixFile
					.setHelp("File including line based prefix paths of objects to be queued from bucket");
			jsap.registerParameter(prefixFile);

			JSAPResult queueResult = jsap.parse(args);
			// if parsing was not successful print usage of commands and exit
			if (!queueResult.success()) {
				printUsageAndExit(jsap, queueResult);
			}
			Long limitValue = null;
			try {
				limitValue = queueResult.getLong("limit");
			} catch (UnspecifiedParameterException e) {
				// do nothing
			}
			String filePath = null;
			try {
				filePath = queueResult.getString("prefixFile");
			} catch (UnspecifiedParameterException e) {
				// do nothing
			}

			new Master().queue(queueResult.getString("prefix"), limitValue,
						filePath);


			System.exit(0);
		}

		if ("clearqueue".equals(action)) {
			new Master().clearQueue();
			System.exit(0);
		}

		if ("monitor".equals(action)) {

			Switch autoShutdown = new Switch("autoShutdown").setLongFlag(
					"autoShutdown").setShortFlag('a');
			autoShutdown
					.setHelp("Indicates if shutdown method is called if not messages are left to process and ec2 system is still running.");
			jsap.registerParameter(autoShutdown);
			JSAPResult queueResult = jsap.parse(args);
			if (!queueResult.success()) {
				printUsageAndExit(jsap, queueResult);
			}
			boolean auto = queueResult.getBoolean("autoShutdown");
			new Master().monitorQueue(auto);
			System.exit(0);
		}

		if ("launch".equals(action)) {
			FlaggedOption jarfileP = new FlaggedOption("jarfile")
					.setStringParser(JSAP.STRING_PARSER).setRequired(false)
					.setLongFlag("jarfile").setShortFlag('j');
			jarfileP.setHelp("Jarfile to be executed on the worker instances");
			jsap.registerParameter(jarfileP);

			FlaggedOption amountP = new FlaggedOption("amount")
					.setStringParser(JSAP.INTEGER_PARSER).setRequired(true)
					.setLongFlag("worker-amount").setShortFlag('a');
			amountP.setHelp("Amount of worker instances to start in EC2");
			jsap.registerParameter(amountP);

			FlaggedOption priceP = new FlaggedOption("pricelimit")
					.setStringParser(JSAP.DOUBLE_PARSER).setRequired(true)
					.setLongFlag("pricelimit").setShortFlag('p');
			priceP.setHelp("Price limit for instances in US$");
			jsap.registerParameter(priceP);

			JSAPResult startParams = jsap.parse(args);
			if (!startParams.success()) {
				printUsageAndExit(jsap, startParams);
			}

			if(startParams.contains("jarfile")){
				File jarfile = new File(startParams.getString("jarfile"));
				if (!jarfile.exists() || !jarfile.canRead()) {
					log.warn("Unable to access JAR file at " + jarfile);
					System.exit(-1);
				}
				System.out.println("Deploying JAR file at " + jarfile);
				new Master().deploy(jarfile);
			}

			int amount = startParams.getInt("amount");
			new Master().createInstances(amount,
					startParams.getDouble("pricelimit"));
			System.out.println("done.");
			System.exit(0);
		}

		if ("shutdown".equals(action)) {
			System.out
					.print("Cancelling spot request and shutting down all worker instances in EC2...");
			new Master().shutdownInstances();
			System.out.println("done.");
			System.exit(0);
		}

		if ("retrievedata".equals(action)) {
			Switch list = new Switch("list").setLongFlag(
					"list").setShortFlag('l');
			list.setHelp("Print a list of the available data folders.");
			jsap.registerParameter(list);

			FlaggedOption dateP = new FlaggedOption("date")
					.setStringParser(JSAP.STRING_PARSER).setRequired(false)
					.setLongFlag("date").setShortFlag('d');
			dateP.setHelp("Date of the processed files (YYYY-MM)");
			jsap.registerParameter(dateP);

			JSAPResult retrieveParams = jsap.parse(args);
			if (!retrieveParams.success()) {
				printUsageAndExit(jsap, retrieveParams);
			}

			if(retrieveParams.getBoolean("list")){

				new Master().listData();
				System.exit(0);

			} else {
				String date = retrieveParams.getString("date");

				new Master().retrieveData(date);
				System.exit(0);
			}
		}

		if ("deletedata".equals(action)) {
			Switch awsP = new Switch("s3").setLongFlag(
					"s3").setShortFlag('s');
			awsP.setHelp("Delete data from S3 bucket.");
			jsap.registerParameter(awsP);

			Switch mongoP = new Switch("mongo").setLongFlag(
					"mongo").setShortFlag('m');
			mongoP.setHelp("Delete data from mongodb.");
			jsap.registerParameter(mongoP);

			FlaggedOption dateP = new FlaggedOption("date")
					.setStringParser(JSAP.STRING_PARSER).setRequired(false)
					.setLongFlag("date").setShortFlag('d');
			dateP.setHelp("Date of the processed files (YYYY-MM)");
			jsap.registerParameter(dateP);

			JSAPResult retrieveParams = jsap.parse(args);
			if (!retrieveParams.success()) {
				printUsageAndExit(jsap, retrieveParams);
			}

			if(!retrieveParams.getBoolean("s3") && !retrieveParams.getBoolean("mongo")){
				System.out.println("Select at least one storage tool (-s / -m)");
				printUsageAndExit(jsap, retrieveParams);
			}

			boolean aws = retrieveParams.getBoolean("s3");
			boolean mongo = retrieveParams.getBoolean("mongo");

			if(!retrieveParams.contains("date")){
				new Master().clearAllData(aws,mongo);
				System.exit(0);
			}else{
				new Master().clearData(retrieveParams.getString("date"),aws,mongo);
				System.exit(0);
			}
		}



		if("run".equals(action)){
			new Worker.ThreadGuard(Worker.WorkerThread.class).start();
		}else {
			printUsageAndExit(jsap, config);
		}
	}

	private static void printUsageAndExit(JSAP jsap, JSAPResult result) {
		@SuppressWarnings("rawtypes")
		Iterator it = result.getErrorMessageIterator();
		while (it.hasNext()) {
			System.err.println("Error: " + it.next());
		}

		System.err.println("Usage: " + Master.class.getName() + " "
				+ jsap.getUsage());
		System.err.println(jsap.getHelp());

		System.exit(1);
	}

	public void clearQueue() {
		getQueue().clearQueue();
		log.info("Deleted job queue");
	}

	public void createInstances(int count, double priceLimitDollars) {
		String startupScript = "#!/bin/bash \n echo 1 > /proc/sys/vm/overcommit_memory \n"
				+ "sudo apt-get update\n"
				+ "sudo apt-get install openjdk-8-jdk -y\n"
				+ "wget -O /tmp/start.jar \"" + getJarUrl() + "\" \n"
				+ "java -Xmx"
				+ getOrCry("javamemory").trim()
				+ " -jar /tmp/start.jar > /tmp/start.log & \n";

		AmazonEC2 ec2 = new AmazonEC2Client(getAwsCredentials());
		ec2.setEndpoint(getOrCry("ec2endpoint"));

		log.info("Requesting " + count + " instances of type "
				+ getOrCry("ec2instancetype") + " with price limit of "
				+ priceLimitDollars + " US$");

		try {
			// our bid
			RequestSpotInstancesRequest runInstancesRequest = new RequestSpotInstancesRequest()
					.withSpotPrice(Double.toString(priceLimitDollars))
					.withInstanceCount(count).withType("persistent");

			// what we want
			LaunchSpecification workerSpec = new LaunchSpecification()
					.withInstanceType(getOrCry("ec2instancetype"))
					.withImageId(getOrCry("ec2ami"))
					.withKeyName(getOrCry("ec2keypair"))
					.withUserData(
							new String(Base64.encodeBase64(startupScript
									.getBytes())));

			runInstancesRequest.setLaunchSpecification(workerSpec);

			// place the request
			ec2.requestSpotInstances(runInstancesRequest);
			log.info("Request placed, now use 'monitor' to check how many instances are running. Use 'shutdown' to cancel the request and terminate the corresponding instances.");
		} catch (Exception e) {
			log.warn("Failed to start instances - ", e);
		}
	}

	public void deploy(File jarFile) {
		String deployBucket = getOrCry("deployBucket");
		String deployFilename = getOrCry("deployFilename");

		try {
			getS3Storage().getOrCreateBucket(deployBucket);
			AccessControlList bucketAcl = getS3Storage().getBucketAcl(
					deployBucket);
			bucketAcl.grantPermission(GroupGrantee.ALL_USERS,
					Permission.PERMISSION_READ);

			S3Object statFileObject = new S3Object(jarFile);
			statFileObject.setKey(deployFilename);
			statFileObject.setAcl(bucketAcl);

			getS3Storage().putObject(deployBucket, statFileObject);

			log.info("File " + jarFile + " now accessible at " + getJarUrl());
		} catch (Exception e) {
			log.warn("Failed to deploy or set permissions in bucket "
					+ deployBucket + ", key " + deployFilename, e);
		}
	}

	private String getJarUrl() {
		return "http://s3.amazonaws.com/" + getOrCry("deployBucket") + "/"
				+ getOrCry("deployFilename");
	}



	public void monitorQueue(boolean autoShutDown) {
		System.out
				.println("Monitoring job queue, extraction rate and running instances. AutoShutdown is: "
						+ (autoShutDown ? "on" : "off"));
		System.out.println();

		List<DateSizeRecord> sizeLog = new ArrayList<DateSizeRecord>();
		DecimalFormat twoDForm = new DecimalFormat("#.##");

		AmazonEC2 ec2 = new AmazonEC2Client(getAwsCredentials());
		ec2.setEndpoint(getOrCry("ec2endpoint"));

		long emptyQueueTimerMS = 0;
		long maxEmptyQueueTimeMS = 60000;
		long sleepMS = 1000;

		while (true) {
			try {

				QueueSize size = getQueue().size();

				int requestedInstances = 0;
				int runningInstances = 0;

                DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
                DescribeSpotInstanceRequestsResult describeResult = ec2
                        .describeSpotInstanceRequests(describeRequest);
                List<SpotInstanceRequest> describeResponses = describeResult
                        .getSpotInstanceRequests();

                for (SpotInstanceRequest describeResponse : describeResponses) {
                    if ("active".equals(describeResponse.getState())) {
                        runningInstances++;
                        requestedInstances++;
                    }
                    if ("open".equals(describeResponse.getState())) {
                        requestedInstances++;
                    }
                }

                // in case the queueSize and the inflightSize are 0 for a longer
                // time, the ec2s are automatically shut down.
                if (autoShutDown) {
                    if (requestedInstances + runningInstances > 0) {
                        if (size.ready + size.inflight > 0) {
                            emptyQueueTimerMS = 0;
                        } else {
                            emptyQueueTimerMS += sleepMS;
                        }

                        if (emptyQueueTimerMS > maxEmptyQueueTimeMS) {

                            System.out
                                    .println(new Date() + " No more messages to process. Shutting down instances.");
                            shutdownInstances();
                            try {
                                // lets wait a little bit.
                                System.out.println("Waiting for shutdown.");
                                Thread.sleep(sleepMS * 10);
                            } catch (InterruptedException e) {
                                // who cares if we get interrupted here
                            }
                        }
                    }
                }


				// add the new value to the tail, now remove too old stuff from
				// the
				// head
				DateSizeRecord nowRecord = new DateSizeRecord(Calendar
						.getInstance().getTime(), size.ready + size.inflight);
				sizeLog.add(nowRecord);

				int windowSizeSec = 120;

				// remove outdated entries
				for (DateSizeRecord rec : new ArrayList<DateSizeRecord>(sizeLog)) {
					if (nowRecord.recordTime.getTime()
							- rec.recordTime.getTime() > windowSizeSec * 1000) {
						sizeLog.remove(rec);
					}
				}
				// now the first entry is the first data point, and the entry
				// just
				// added the last;
				DateSizeRecord compareRecord = sizeLog.get(0);
				double timeDiffSec = (nowRecord.recordTime.getTime() - compareRecord.recordTime
						.getTime()) / 1000;
				long sizeDiff = compareRecord.queueSize - nowRecord.queueSize;

				double rate = sizeDiff / timeDiffSec;

				System.out.print('\r');

				if(requestedInstances == 0 && runningInstances == 0) {
					System.out.print("Instances requested: " + requestedInstances + "\tRunning instances: " + runningInstances + "\t");
				}
				if (rate > 0) {
					System.out.print("Remaining messages: " + size.ready + "\tMessages in progess: " + size.inflight
							+ "\trate: " + twoDForm.format(rate * 60)
							+ " m/min\t ETA: "
							+ twoDForm.format((size.ready / rate) / 3600) + " h");
				} else {
					System.out.print("Queue size: " + size.ready + "\tMessages in progess: " + size.inflight);
				}

			} catch (AmazonServiceException e) {
				System.out.print("\r! // ");
			}
			try {
				Thread.sleep(sleepMS);
			} catch (InterruptedException e) {
				// who cares if we get interrupted here
			}
		}
	}

	public void queue(String singlePrefix, Long limit, String filePath) {

		String dataBucket = getOrCry("dataBucket");

		String prefix = "";
		List<String> files = new ArrayList<>();

		boolean dataSuffixSet = true;
		String dataSuffix = "";
		try {
			dataSuffix = getOrCry("dataSuffix");
		} catch (IllegalArgumentException e) {
			dataSuffixSet = false;
		}

		if (filePath == null) {
			if (singlePrefix == null || singlePrefix.trim().equals("")) {
				log.warn("No prefix given");
				return;
			}
			prefix = singlePrefix;

			log.info("Queuing all keys from bucket " + dataBucket
					+ " with prefix " + prefix);

			try {
				prefix = getOrCry("dataPrefix") + "/" + prefix;
				S3Object[] objects = getS3Storage().listObjects(dataBucket,
						prefix, null);

				for(S3Object object : objects){
					if (dataSuffixSet && object.getKey().endsWith(dataSuffix))
						files.add(object.getKey());
				}

			} catch (Exception e) {
				log.warn("Failed to obtain objects in bucket " + dataBucket
						+ " with prefix " + prefix, e);
			}
		} else {

			try {//parte revisada
				FileReader fis = new FileReader(new File(filePath));
				BufferedReader br = new BufferedReader(fis);
				while (br.ready()) {
					String line = br.readLine();
					if (line != null && line.trim().length() > 0) {
						if (dataSuffixSet && line.trim().endsWith(dataSuffix))
							files.add(line.trim());
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				log.warn("Could not find file.");
				log.debug(e);
			} catch (IOException e) {
				log.warn("Could not access file.");
				log.debug(e);
			}

			log.info("Queuing all keys in " + filePath);
		}
		if (files.isEmpty()) {
			log.warn("No files included");
			return;
		}
		if (limit != null) {
			log.info("Setting limit of files to: " + limit);
		} else {
			log.info("Selecting all included files.");
		}

		long globalQueued = 0;

		int batchSize = Integer.parseInt(getOrCry("batchsize"));
		System.out.println();
		QueueMessageBatch batch = getQueue().newbatch();

		long batchIndex = 0;
		for (String file : files) {

			System.out.print("\rAdded " + globalQueued + " files");

			if(limit != null && globalQueued >= limit) break;

			batch.add(file,batchIndex);
			if (batch.size() >= batchSize) {
				getQueue().sendBatch(batch);
				batchIndex = 0;
				// having send into queue - reset entries.
				batch.clear();
			}
			batchIndex++;
			globalQueued++;
			// send the rest
		}
		if (batch.size() > 0) {
			getQueue().sendBatch(batch);
		}

		System.out.print("\rAdded " + globalQueued + " files\n");
	}

	public void shutdownInstances() {
		AmazonEC2 ec2 = new AmazonEC2Client(getAwsCredentials());
		ec2.setEndpoint(getOrCry("ec2endpoint"));

		try {
			// cancel spot request, so no new instances will be launched
			DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
			DescribeSpotInstanceRequestsResult describeResult = ec2
					.describeSpotInstanceRequests(describeRequest);
			List<SpotInstanceRequest> describeResponses = describeResult
					.getSpotInstanceRequests();
			List<String> spotRequestIds = new ArrayList<String>();
			List<String> instanceIds = new ArrayList<String>();

			for (SpotInstanceRequest describeResponse : describeResponses) {
				spotRequestIds.add(describeResponse.getSpotInstanceRequestId());
				if ("active".equals(describeResponse.getState())) {
					instanceIds.add(describeResponse.getInstanceId());
				}
			}
			ec2.cancelSpotInstanceRequests(new CancelSpotInstanceRequestsRequest()
					.withSpotInstanceRequestIds(spotRequestIds));
			log.info("Cancelled spot request");

			if (instanceIds.size() > 0) {
				ec2.terminateInstances(new TerminateInstancesRequest(
						instanceIds));
				log.info("Shut down " + instanceIds.size() + " instances");
			}

		} catch (Exception e) {
			log.warn("Failed to shutdown instances - ", e);
		}
	}

	public void retrieveData(String date) {
		try {
			S3Object[] dataObjects = getS3Storage().listObjects(getOrCry("resultBucket"),
				"data/"+date, null);
			int i = 1;

			for(S3Object object : dataObjects) {
				System.out.print("\rRetrieving data file " + i + " of " + dataObjects.length);
				try {
					S3Object dataObject = getS3Storage().getObject(getOrCry("resultBucket"), object.getKey());

					JsonArray docArray = new JsonParser().parse(new InputStreamReader(dataObject.getDataInputStream())).getAsJsonArray();
					List<Document> data = new ArrayList<>();
					for (JsonElement doc : docArray) {
						data.add(Document.parse(doc.toString()));
					}
					getMongoStorage().storeData(data);

				} catch (Exception e) {
					log.warn("Error retrieving " + object.getKey() + e);
				}
				++i;
			}
			System.out.print("\rAll data files retrieved     \n");

			S3Object[] statObjects = getS3Storage().listObjects(getOrCry("resultBucket"),
					"stats/"+date, null);
			int j = 1;

			for(S3Object object : statObjects) {
				System.out.print("\rRetrieving stats file " + j + " of " + statObjects.length);
				try {
					S3Object statsObject = getS3Storage().getObject(getOrCry("resultBucket"), object.getKey());

					JsonObject doc = new JsonParser().parse(new InputStreamReader(statsObject.getDataInputStream())).getAsJsonObject();
					getMongoStorage().storeStats(Document.parse(doc.toString()));

				} catch (Exception e) {
					log.warn("Error retrieving " + object.getKey() + e);
				}
				++j;
			}
			System.out.print("\rAll stat files retrieved     \n");


		} catch (Exception e) {
			log.warn("Error: ", e);
		}
	}

	public void listData(){
		try {
			AmazonS3Client client = new AmazonS3Client(getAwsCredentials());

			ListObjectsRequest listObjectsRequest =
					new ListObjectsRequest().withBucketName(getOrCry("resultBucket"))
							.withPrefix("data/")
							.withDelimiter("/");

			ObjectListing objects = client.listObjects(listObjectsRequest);

			System.out.println("Dates avlailable for retrievedata:");
			for(String date : objects.getCommonPrefixes()){
				System.out.println("\t"+date.substring(5,12));
			}


		} catch (Exception e) {
			log.warn("Error: ", e);
		}
	}

	public void clearAllData(boolean aws, boolean mongo){
		if(aws) {
			System.out.println("Deleting all data from S3");
			try{
				getS3Storage().deleteAll();
			}catch (ServiceException e){
				log.warn(e);
			}
		}
		if (mongo){
			System.out.println("Deleting all data from mongodb");
			getMongoStorage().deleteAll();
		}
	}

	public void clearData(String target, boolean aws, boolean mongo){
		if(aws) {
			System.out.println("Deleting data with date " + target + " from S3");
			try{
				getS3Storage().delete(target);
			}catch (ServiceException e){
				log.warn(e);
			}
		}
		if (mongo){
			System.out.println("Deleting data with date " + target + " from mongodb");
			getMongoStorage().delete(target);
		}
	}

}