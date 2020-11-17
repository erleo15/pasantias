package dortegam.dataproc.framework.master;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.identitymanagement.model.*;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.services.s3.model.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;

import org.apache.commons.io.IOUtils;

import org.bson.Document;


class Master extends MasterNode {

	void queuePrefix(String prefix, Long limit){

		String bucket = getOrCry("dataBucket");
		String suffix = getOrCry("dataSuffix");

		List<String> files = new ArrayList<>();


		System.out.println("Queuing keys" + (limit != null ? " ( " + limit + " max ) " : " ") + "from bucket " + bucket
				+ " with prefix " + prefix + " and suffix " + suffix);

		try {
			prefix = getOrCry("dataPrefix") + "/" + prefix;
			ObjectListing objects = getS3().listObjects(bucket, prefix);

			int added = 0;
			for(S3ObjectSummary object : objects.getObjectSummaries()){
				if (object.getKey().endsWith(suffix)){
					files.add(object.getKey());
					if(limit != null && ++added >= limit) break;
				}

			}

			queue(files);

		} catch (Exception e) {
			System.out.println("Failed to obtain objects in bucket " + bucket
					+ " with prefix " + prefix);
			e.printStackTrace();
		}
	}

	void queueFile(String file, Long limit){

		List<String> files = new ArrayList<>();

		System.out.println("Queuing keys" + (limit != null ? " ( " + limit + " max ) " : " ") + "from file " + file);

		try (BufferedReader br = new BufferedReader(new FileReader(new File(file)))){

			int added = 0;
			while (br.ready()) {
				String line = br.readLine();
				if (line != null && line.trim().length() > 0) {
					files.add(line.trim());
					if(limit != null && ++added >= limit) break;
				}
			}

			queue(files);

		} catch (FileNotFoundException e) {
			System.err.println("Could not find file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not access file.");
			e.printStackTrace();
		}

	}

	private void queue(List<String> files){
		long totalQueued = 0;

		int batchSize = Integer.parseInt(getOrCry("batchSize"));

		long batchIndex = 0;

		String queueUrl = getQueue();

		if (queueUrl == null) {
			CreateQueueRequest req = new CreateQueueRequest();
			req.setQueueName(getOrCry("queueName"));

			Map<String, String> attributes = new HashMap<>();
			attributes.put("DelaySeconds", "0");
			attributes.put("MessageRetentionPeriod", "1209600");
			attributes.put("VisibilityTimeout", getOrCry("jobTimeLimit"));
			req.setAttributes(attributes);

			queueUrl = getSQS().createQueue(req).getQueueUrl();

		}

		SendMessageBatchRequest batchRequest = new SendMessageBatchRequest().withQueueUrl(queueUrl);

		System.out.print("\rAdded 0 files");

		for (String file : files) {

			SendMessageBatchRequestEntry batchEntry = new SendMessageBatchRequestEntry();
			batchEntry.setMessageBody(file);
			batchEntry.setId("task_" + batchIndex);
			batchRequest.getEntries().add(batchEntry);

			if (batchRequest.getEntries().size() >= batchSize) {
				getSQS().sendMessageBatch(batchRequest);
				batchIndex = 0;
				batchRequest.getEntries().clear();
			}
			batchIndex++;
			totalQueued++;
		}
		if (batchRequest.getEntries().size() > 0) {
			getSQS().sendMessageBatch(batchRequest);
		}

		System.out.print("\rAdded " + totalQueued + " files\n");
	}

	void clearQueue() {

		getSQS().purgeQueue(new PurgeQueueRequest(getQueue()));

		System.out.println("Purged queue " + getOrCry("queueName"));

	}

	void deploy(boolean jar, boolean conf, boolean search) {

		String bucket = getOrCry("deployBucket");

		if(!getS3().doesBucketExistV2(bucket)){

			try {

				getS3().createBucket(bucket);

				System.out.println("Created new bucket " + bucket);

			} catch (AmazonServiceException e){

				System.err.println("Failed to create bucket " + bucket);
				e.printStackTrace();
				System.exit(1);

			}

		}

		if(jar){

			String jarFilePath = "target/dataproc.jar";

			try{

				File jarFile = new File(jarFilePath);

				getS3().putObject(bucket, getOrCry("deployFilename"), jarFile);

				System.out.println("File " + jarFile + " now accessible at "
						+ getS3().getUrl(getOrCry("deployBucket"),getOrCry("deployFilename")) );

			}catch (AmazonServiceException e){

				System.out.println("Failed to find, open or deploy file " + jarFilePath);

			}

		}

		if(conf) {

			try {

				File confFile = new File(PROPFILENAME);

				getS3().putObject(bucket, PROPFILENAME, confFile);

				System.out.println("File " + confFile + " now accessible at "
						+ getS3().getUrl(getOrCry("deployBucket"),PROPFILENAME));

			} catch (AmazonServiceException e) {

				System.out.println("Failed to find, open or deploy file " + PROPFILENAME);

			}

		}

		if(search){

			try{

				File searchFile = new File(SEARCHFILENAME);

				getS3().putObject(bucket, SEARCHFILENAME, searchFile);

				System.out.println("File " + searchFile + " now accessible at "
						+ getS3().getUrl(getOrCry("deployBucket"),SEARCHFILENAME) );

			}catch (AmazonServiceException e){

				System.out.println("Failed to find, open or deploy file " + SEARCHFILENAME);

			}

		}

	}

	void launch(int count, double priceLimit) {

		// Check parameters
		if(count <= 0){
			System.err.println("Instance count must be greater than 0");
			System.exit(1);
		}

		if(priceLimit <= 0){
			System.err.println("Price limit must be greater than 0");
			System.exit(1);
		}

		try {

			String deploybucket = getOrCry("deployBucket");

			// Script to be executed by worker nodes
			String startupScript = "#!/bin/bash \n echo 1 > /proc/sys/vm/overcommit_memory \n"
					+ "sudo apt-get update\n"
					+ "sudo apt-get install openjdk-8-jdk -y\n"
					+ "sudo apt-get install awscli -y\n"
					+ "cd /tmp\n"
					+ "mkdir " + CONFDIR + "\n"

					+ "aws s3api get-object --bucket " + deploybucket
					+ " --key " + getOrCry("deployFilename") + " " + getOrCry("deployFilename") + "\n"

					+ "aws s3api get-object --bucket " + deploybucket
					+ " --key " + PROPFILENAME + " " + PROPFILENAME + "\n"

					+ "aws s3api get-object --bucket " + deploybucket
					+ " --key " + SEARCHFILENAME + " " + SEARCHFILENAME + "\n"

					+ "java -cp " + getOrCry("deployFilename")
					+ " dortegam.dataproc.framework.worker.Worker > dataproc.log 2> dataproc.error & \n";

			System.out.println("Requesting " + count + " " + getOrCry("ec2instancetype")
					+ " instances with price limit of " + priceLimit + " US$");

			// Define spot instance request
			RequestSpotInstancesRequest runInstancesRequest = new RequestSpotInstancesRequest()
					.withSpotPrice(Double.toString(priceLimit))
					.withInstanceCount(count)
					.withType(SpotInstanceType.Persistent);

			LaunchSpecification workerSpec = new LaunchSpecification()
					.withInstanceType(getOrCry("ec2instancetype"))
					.withImageId(getOrCry("ec2ami"))
					.withKeyName(getOrCry("ec2keypair"))
					.withIamInstanceProfile(getInstanceProfile())
					.withUserData(Base64.getEncoder().encodeToString(startupScript.getBytes()));

			runInstancesRequest.setLaunchSpecification(workerSpec);

			// Launch request
			RequestSpotInstancesResult result = getEC2().requestSpotInstances(runInstancesRequest);

			// Add purpose tag to requests
			List<String> resources = new ArrayList<>();
			for(SpotInstanceRequest request : result.getSpotInstanceRequests()){
				resources.add(request.getSpotInstanceRequestId());
			}

			List<Tag> tags = new ArrayList<>();
			tags.add(new Tag("dataproc", ""));

			getEC2().createTags(new CreateTagsRequest(resources, tags));

		} catch (AmazonServiceException e) {

			System.err.println("Failed to start instances");
			e.printStackTrace();

		}
	}

	private IamInstanceProfileSpecification getInstanceProfile() {

		String roleName = getOrCry("roleName");

		try {

			getIAM().getRole(new GetRoleRequest().withRoleName(roleName));

		}catch (NoSuchEntityException e){

			String assumePolicyFilename = "assumePolicy.json";
			String assumePolicy = "";

			try{
				assumePolicy = IOUtils.toString(
						Master.class.getResourceAsStream("/"+assumePolicyFilename), StandardCharsets.UTF_8);
			} catch (IOException ioe){
				System.err.println("Could not open file " + assumePolicyFilename);
				e.printStackTrace();
				System.exit(1);
			}

			getIAM().createRole(new CreateRoleRequest()
					.withRoleName(roleName)
					.withAssumeRolePolicyDocument(assumePolicy)
					.withDescription("Enables dataproc workers to access SQS and S3"));

			System.out.println("Created role " + roleName);

		}

		String rolePolicyDocumentName = "DataprocWorkerAccess";

		try {

			getIAM().getRolePolicy(new GetRolePolicyRequest()
					.withRoleName(roleName)
					.withPolicyName(rolePolicyDocumentName));

		}catch (NoSuchEntityException e){

			String rolePolicyFilename = "rolePolicy.json";
			String rolePolicy = "";

			try{

				rolePolicy = IOUtils.toString(Master.class
						.getResourceAsStream("/"+rolePolicyFilename),StandardCharsets.UTF_8);

			} catch (IOException ioe){

				System.err.println("Could not open file " + rolePolicyFilename);
				e.printStackTrace();
				System.exit(1);

			}

			getIAM().putRolePolicy(new PutRolePolicyRequest()
					.withRoleName(roleName)
					.withPolicyName(rolePolicyDocumentName)
					.withPolicyDocument(rolePolicy));

			System.out.println("Created policy " + rolePolicyDocumentName + " for role " + roleName);

		}

		String instanceProfileName = "dataprocworker";

		InstanceProfile instanceProfile;

		try {

			instanceProfile = getIAM().getInstanceProfile(new GetInstanceProfileRequest()
					.withInstanceProfileName(instanceProfileName))
					.getInstanceProfile();

		}catch (NoSuchEntityException e){

			instanceProfile = getIAM().createInstanceProfile(new CreateInstanceProfileRequest()
					.withInstanceProfileName(instanceProfileName)).getInstanceProfile();

			System.out.println("Created instance profile " + instanceProfileName);

		}

		boolean roleFound = false;

		for(Role role : instanceProfile.getRoles()){
			if(role.getRoleName().equals(roleName)){
				roleFound = true;
				break;
			}
		}

		if(!roleFound){

			getIAM().addRoleToInstanceProfile(new AddRoleToInstanceProfileRequest()
					.withInstanceProfileName(instanceProfileName)
					.withRoleName(roleName));

			System.out.println("Added instance profile " + instanceProfileName + " to role " + roleName);

			System.out.println("Waiting for configuration to take effect...");

			// This configuration needs some time to make effect
			// If sleep time is too short (unknown) instance request will fail due to instanceProfile not valid
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ignored){}

		}

		return new IamInstanceProfileSpecification().withArn(instanceProfile.getArn());

	}

	void monitor() {

		class DateSizeRecord {
			private Date recordTime;
			private Long queueSize;

			private DateSizeRecord(Date time, Long size) {
				this.recordTime = time;
				this.queueSize = size;
			}
		}

		List<DateSizeRecord> sizeLog = new ArrayList<>();

		DecimalFormat twoDForm = new DecimalFormat("#.##");


		long sleepMS = 5000;
		int windowSizeSec = 120;

		Runtime.getRuntime().addShutdownHook(new Thread(System.out::println));

		while (true) {
			try {

				// Create filtered description request
                DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest()
						.withFilters(new Filter("tag-key",Collections.singletonList("dataproc")));

                // Request request descriptions
                DescribeSpotInstanceRequestsResult describeResult = getEC2()
						.describeSpotInstanceRequests(describeRequest);

                List<SpotInstanceRequest> describeResponses = describeResult.getSpotInstanceRequests();

                // Count total and active requests
				int requestedInstances = 0;
				int runningInstances = 0;

                for (SpotInstanceRequest describeResponse : describeResponses) {
                    if ("active".equals(describeResponse.getState())) {
                        runningInstances++;
                        requestedInstances++;
                    }
                    if ("open".equals(describeResponse.getState())) {
                        requestedInstances++;
                    }
                }

                String queue = getQueue();
                if(queue == null){
					System.err.println("Queue " + getOrCry("queueName") + " does not exist");
					System.exit(1);
				}

                // Get queue attributes (messages left)
				GetQueueAttributesResult queueAttributes = getSQS().getQueueAttributes(
						new GetQueueAttributesRequest(getQueue()).withAttributeNames("All"));

				Long queueSize = Long.parseLong(queueAttributes.getAttributes().get(
						"ApproximateNumberOfMessages"));
				Long inflightSize = Long.parseLong(queueAttributes.getAttributes().get(
						"ApproximateNumberOfMessagesNotVisible"));

				long totalMessagesLeft = queueSize + inflightSize;

				// Create timed record
				DateSizeRecord nowRecord = new DateSizeRecord(
						Calendar.getInstance().getTime(), queueSize + inflightSize);

				sizeLog.add(nowRecord);

				// Remove records out of time window
				for (DateSizeRecord rec : new ArrayList<DateSizeRecord>(sizeLog)) {
					if (nowRecord.recordTime.getTime()
							- rec.recordTime.getTime() > windowSizeSec * 1000) {
						sizeLog.remove(rec);
					}
				}

				// Get first record inside window
				DateSizeRecord lastRecord = sizeLog.get(0);

				// Calculate rates
				double timeDiffSec =
						(nowRecord.recordTime.getTime() - lastRecord.recordTime.getTime()) / 1000.0;

				long sizeDiff = lastRecord.queueSize - nowRecord.queueSize;

				double rate = sizeDiff / timeDiffSec;

				// Print results
				System.out.print("\rInstances: " + runningInstances + "/" + requestedInstances  + "    ");

				System.out.print("Messages: " + inflightSize + "/" + totalMessagesLeft + "    ");

				if (rate > 0) {
					System.out.print("Rate: " + twoDForm.format(rate * 60) + " m/min   " +
							"ETA: " + twoDForm.format((totalMessagesLeft / rate) / 3600) + " h");
				} else {
					System.out.print("Rate: Unknown       ");
				}

			} catch (AmazonServiceException e) {
				System.out.print("\rError retrieving data");
			}

			// Sleep
			try {
				Thread.sleep(sleepMS);
			} catch (InterruptedException ignored) { }
		}
	}



	void stop() {

		try {

			// Get requests with desired tag and active|open
			DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest()
					.withFilters(new Filter("tag-key",Collections.singletonList("dataproc")),
								 new Filter("state",  Arrays.asList("open","active")));

			DescribeSpotInstanceRequestsResult describeResult = getEC2()
					.describeSpotInstanceRequests(describeRequest);

			List<SpotInstanceRequest> requests = describeResult.getSpotInstanceRequests();

			if (requests.size() <= 0) {
				System.out.println("There are no active spot requests by this program");
				System.exit(0);
			}

			// Get requests and instances IDs
			List<String> spotRequestIds = new ArrayList<>();

			List<String> instanceIds = new ArrayList<>();

			for (SpotInstanceRequest request : requests) {

				spotRequestIds.add(request.getSpotInstanceRequestId());

				if ("active".equals(request.getState())) {
					instanceIds.add(request.getInstanceId());
				}

			}

			// Cancel request
			getEC2().cancelSpotInstanceRequests(
					new CancelSpotInstanceRequestsRequest().withSpotInstanceRequestIds(spotRequestIds));

			System.out.println("Shutting down spot requests");

			// Stop running instances
			if (instanceIds.size() > 0) {
				try {

					getEC2().terminateInstances(new TerminateInstancesRequest(
							instanceIds));

					System.out.println("Shutting down " + instanceIds.size() + " spot instances");

				} catch(AmazonServiceException e){
					System.err.println("Failed to terminate spot instance(s)");
					e.printStackTrace();
				}
			}

		} catch (AmazonServiceException e) {
			System.err.println("Failed to cancel spot request(s)");
			e.printStackTrace();
		}

	}

	void retrieveData() {
		try {

			String resultBucket = getOrCry("resultBucket");

			JsonParser parser = new JsonParser();

			MongoCollection<Document> dataCollection = getMongo().getCollection("data");
			MongoCollection<Document> statsCollection = getMongo().getCollection("stats");

			// List data objects
			List<S3ObjectSummary> dataObjectSummaries = getS3().listObjects(new ListObjectsRequest()
					.withBucketName(resultBucket)
					.withPrefix("data/"))
					.getObjectSummaries();

			int i = 1;

			// Get data objects and insert them into database
			for(S3ObjectSummary dataObjectSummary : dataObjectSummaries) {

				System.out.print("\rRetrieving data file " + i + " of " + dataObjectSummaries.size());

				try {

					S3Object object = getS3().getObject(new GetObjectRequest(resultBucket, dataObjectSummary.getKey()));

					JsonArray dataDocArray = parser.parse(new InputStreamReader(object.getObjectContent())).getAsJsonArray();

					List<Document> data = new ArrayList<>();

					for (JsonElement dataDoc : dataDocArray) {
						data.add(Document.parse(dataDoc.toString()));
					}

					if(!data.isEmpty()) dataCollection.insertMany(data, new InsertManyOptions().ordered(false));

				} catch (Exception e) {
					System.err.println("\nError retrieving " + dataObjectSummary.getKey());
					e.printStackTrace();
				}
				++i;
			}

			System.out.println("\nData files retrieved");


			// List stats objects
			List<S3ObjectSummary> statsObjectSummaries = getS3().listObjects(new ListObjectsRequest()
					.withBucketName(resultBucket)
					.withPrefix("stats/"))
					.getObjectSummaries();

			int j = 1;

			// Get stats objects and insert them into database
			for(S3ObjectSummary statsObjectSummary : statsObjectSummaries) {

				System.out.print("\rRetrieving stats file " + j + " of " + statsObjectSummaries.size());

				try {

					S3Object statsObject = getS3().getObject(new GetObjectRequest(resultBucket, statsObjectSummary.getKey()));

					JsonObject statsDoc = parser.parse(new InputStreamReader(statsObject.getObjectContent())).getAsJsonObject();

					statsCollection.insertOne(Document.parse(statsDoc.toString()));

				} catch (Exception e) {
					System.err.println("\nError retrieving " + statsObjectSummary.getKey());
					e.printStackTrace();
				}
				++j;

			}

			System.out.println("\nStat files retrieved");


		} catch (Exception e) {
			System.err.println("Error retrieving files");
			e.printStackTrace();
		}

	}

	void listData(){
		try {

			Map<String, Long> dates = new HashMap<>();

			ListObjectsV2Result objectListing = getS3().listObjectsV2(new ListObjectsV2Request()
					.withBucketName(getOrCry("resultBucket"))
					.withPrefix("data/")
					.withDelimiter("/"));

			if (objectListing.getCommonPrefixes().isEmpty()){
				System.out.println("No objects available");
				System.exit(0);
			}

			for(String commonPrefix : objectListing.getCommonPrefixes()){

				ObjectListing dateListing = getS3().listObjects(new ListObjectsRequest()
						.withBucketName(getOrCry("resultBucket"))
						.withPrefix(commonPrefix));

				long amount = dateListing.getObjectSummaries().size();

				dates.put(commonPrefix.replaceAll("data/",""), amount);

			}

			System.out.println("Dates available to retrieve data:");

			for(Map.Entry<String,Long> date : dates.entrySet()){
				System.out.println("\t" + date.getKey() + "\t" + date.getValue() + " file(s)");
			}


		} catch (Exception e) {
			System.err.println("Failed retrieving data");
			e.printStackTrace();
			System.exit(1);
		}
	}

	void deleteData(){

		String bucket = getOrCry("resultBucket");

		ListObjectsV2Result objectListing = getS3().listObjectsV2(new ListObjectsV2Request()
				.withBucketName(bucket));

		while (true) {

			for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
				getS3().deleteObject(bucket, s3ObjectSummary.getKey());
			}

			if (objectListing.isTruncated()) {
				objectListing = getS3().listObjectsV2(new ListObjectsV2Request()
						.withBucketName(bucket)
						.withContinuationToken(objectListing.getNextContinuationToken()));
			} else {
				break;
			}
		}

	}

}