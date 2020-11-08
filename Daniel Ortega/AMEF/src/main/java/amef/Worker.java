package amef;

import java.io.InputStream;
import java.util.*;

import amef.queue.QueueMessage;
import org.apache.log4j.Logger;
import amef.processor.FileProcessor;


/**
 * Worker implementation of the {@link ProcessingNode}, which builds up for each
 * core on the system one thread. Each thread connects to the SQS (AWS) and
 * requests a file. The file is retrieved, and processed by a Processor which
 * than stores the data back to S3. S3. The queue is signalized that the file is
 * done. In addition statistics about the processed files are store in SimpleDB
 * of AWS.
 * 
 * @author Robert Meusel
 * 
 */
public class Worker extends ProcessingNode {
	// the logger
	private static Logger log = Logger.getLogger(Worker.class);

	// the name of the processor class
	private final String processorClass = getOrCry("processorClass");
	// maximum number of threats
	private static int threadLimit = Integer.parseInt(getOrCry("parallelFiles"));

	// the actual worker thread.
	public static class WorkerThread extends Thread {
		private Timer timer = new Timer();
		int timeLimit = 0;

		public WorkerThread() {
		}

		public WorkerThread(int timeLimitMsec) {
			this.timeLimit = timeLimitMsec;
		}

		public void run() {
			Worker worker = new Worker();
			if (timeLimit < 1) {
				timeLimit = Integer.parseInt(worker.getOrCry("jobTimeLimit")) * 1000;
			}
			while (true) {

				// cancel all old timers
				boolean success = false;
				timer = new Timer();
				// set a new timer killing us after the specified time limit
				final WorkerThread t = this;
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						log.warn("Killing worker thread, timeout expired.");
						t.interrupt();
					}
				}, timeLimit);

				// start the worker - and let it work
				success = worker.getTaskAndProcess();

				// on failures sleep a bit
				timer.cancel();
				if (!success) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						log.warn("Interrupted", e);
					}
				}
			}
		}
	}

	public boolean getTaskAndProcess() {
		String inputFileKey = "";
		Thread interrupted = new Thread();
		try {

			// receive task message from queue
			QueueMessage message = getQueue().getOneMessage();
			if (message == null) {
				//log.warn("Queue is empty");
				return false;
			}

			interrupted = new Thread(){
				public void run()
				{
					getQueue().interrupted(message);
				}
			};

			Runtime.getRuntime().addShutdownHook(interrupted);

			inputFileKey = message.getBody();

			InputStream file = getInStorage().getFile(inputFileKey);

			FileProcessor processor = (FileProcessor) Class.forName(processorClass).newInstance();

			processor.process(file,inputFileKey);

			Runtime.getRuntime().removeShutdownHook(interrupted);

			/**
			 * remove message from queue. If an Exception is thrown or the node
			 * dies before finishing its task, this does not occur and the
			 * message is re-queued for another node
			 */
			getQueue().messageDone(message);

			log.debug("Finished processing file " + inputFileKey);

			return true;

		} catch (Exception e) {
			Runtime.getRuntime().removeShutdownHook(interrupted);
			log.warn("Unable to finish processing "+ inputFileKey +"\n("
					+ e.getClass().getSimpleName() + ": " + e.getMessage()
					+ ")");
			log.debug("Stracktrace", e.fillInStackTrace());

		}
		return false;
	}

	public static class ThreadGuard extends Thread {
		private List<Thread> threads = new ArrayList<Thread>();
		// can set thread limit to one for debugging
		private int threadLimit;
		private int threadSerial = 0;
		private int waitTimeSeconds = 1;



		private Class<? extends Thread> threadClass;

		public ThreadGuard(Class<? extends Thread> threadClass) {
			this.threadClass = threadClass;
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			this.threadLimit = Worker.threadLimit < availableProcessors ? Worker.threadLimit : availableProcessors;
		}

		public void run() {
			while (true) {
				List<Thread> threadsCopy = new ArrayList<Thread>(threads);
				for (Thread t : threadsCopy) {
					if (!t.isAlive()) {
						log.warn("Thread " + t.getName() + " died.");
						threads.remove(t);
					}

				}
				while (threads.size() < threadLimit) {
					Thread newThread;
					try {
						newThread = threadClass.newInstance();
						newThread.setName("#" + threadSerial);
						threads.add(newThread);
						newThread.start();
						log.info("Started new WorkerThread, "
								+ newThread.getName());
						threadSerial++;
					} catch (Exception e) {
						log.warn("Failed to start new Thread of class "
								+ threadClass);
					}

				}
				try {
					Thread.sleep(waitTimeSeconds * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new ThreadGuard(WorkerThread.class).start();
	}

}
