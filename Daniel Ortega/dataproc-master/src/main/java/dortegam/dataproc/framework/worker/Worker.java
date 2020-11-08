package dortegam.dataproc.framework.worker;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.*;

import dortegam.dataproc.processor.FileProcessorResult;


public class Worker extends WorkerNode {

	public static void main(String[] args) {
		new Worker().start();
	}

	private List<Thread> threads = new ArrayList<>();

	private int availableProcessors = Runtime.getRuntime().availableProcessors();

	private int threadCount = availableProcessors;


	private void start() {

		int threadSerial = 0;

		while (true) {
			if(threadCount <= 0){
				// TODO: stopInstance();
			}
			List<Thread> threadsCopy = new ArrayList<>(threads);
			for (Thread t : threadsCopy) {
				if (!t.isAlive()) {
					System.err.println("Thread " + t.getName() + " died.");
					threads.remove(t);
				}
			}
			while (threads.size() < threadCount) {
				try {
					Thread newThread = newWorker();
					newThread.setName("#" + threadSerial);
					threads.add(newThread);
					newThread.start();
					System.out.println("Started new WorkerThread, " + newThread.getName());
					threadSerial++;
				} catch (Exception e) {
					System.err.println("Failed to start new Thread");
				}

			}
			try {
				int waitTimeSeconds = 10;
				Thread.sleep(waitTimeSeconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	private Thread newWorker(){

		return new Thread() {

			int timeLimit = Integer.parseInt(getOrCry("jobTimeLimit")) * 1000;

			public void run() {

				while (true) {

					final Thread t = this;

					// Wait for a new task for timeLimit * 1.1
					// If a message is not retrieved in that time, thread is killed
					Timer taskRequestTimer = new Timer();

					taskRequestTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							threadCount--;
							t.interrupt();
						}
					}, new Double(timeLimit*1.1).longValue());

					Message taskMessage;
					do{
						taskMessage = requestTask();
						if(taskMessage == null) {
							try{
								sleep(5000);
							}catch (Exception ignored){};
						}
					} while (taskMessage == null);

					taskRequestTimer.cancel();


					Timer taskProcessTimer = new Timer();

					taskProcessTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							System.err.println("Killing worker thread, timeout expired.");
							t.interrupt();
						}
					}, timeLimit);

					processTask(taskMessage.getBody());

					completedTask(taskMessage.getReceiptHandle());

					taskProcessTimer.cancel();

				}
			}
		};
	}

	private Message requestTask(){

		try {

			String queue = getQueue();
			if(queue == null){
				System.err.println("Queue " + getOrCry("queueName") + " does not exist");
				return null;
			}

			// Receive message from queue
			ReceiveMessageResult messageResult = getSQS().receiveMessage(new ReceiveMessageRequest()
					.withQueueUrl(queue)
					.withMaxNumberOfMessages(1));

			if (messageResult.getMessages().size() < 1) {
				return null;
			} else {
				return messageResult.getMessages().get(0);
			}

		} catch (AmazonServiceException e) {
			System.err.println("Failed to retrieve message from queue");
			return null;
		}

	}

	private void processTask(String taskFileKey) {

		try {

			InputStream taskFile = getS3().getObject(new GetObjectRequest(getOrCry("dataBucket"),taskFileKey)).getObjectContent();

			FileProcessorResult result = getProcessor().newInstance(getExtractor().newInstance(search())).process(taskFile,taskFileKey);

			storeResult(result);

		} catch (InvalidParameterException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private void storeResult(FileProcessorResult result){

		String bucket = getOrCry("resultBucket");

		if(!getS3().doesBucketExistV2(bucket)){

			try {

				getS3().createBucket(bucket);

				System.out.println("Created new bucket " + bucket);

			} catch (AmazonServiceException e){

				System.err.println("Failed to create bucket " + bucket);
				e.printStackTrace();

			}

		}

		try{

			getS3().putObject(
					bucket,
					"stats/" + result.getFileKey(),
					new JSONObject(result.getStats()).toString());

			getS3().putObject(
					bucket,
					"data/" + result.getFileKey(),
					new JSONArray(result.getData()).toString());

		} catch (AmazonServiceException e){
			System.err.println("Unable to store data of file " + result.getFileKey());
		}

	}

	private void completedTask(String receiptHandle){

		getSQS().deleteMessage(new DeleteMessageRequest().withQueueUrl(getQueue()).withReceiptHandle(receiptHandle));

	}

}
