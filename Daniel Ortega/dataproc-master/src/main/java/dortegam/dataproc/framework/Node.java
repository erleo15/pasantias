package dortegam.dataproc.framework;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.*;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.*;


public abstract class Node {

    protected static final String CONFDIR = "config/";

    protected static final String PROPFILENAME = CONFDIR + "config.properties";

    protected static final String SEARCHFILENAME = CONFDIR + "search.json";

    private PropertiesConfiguration config = null;

    private AmazonS3 s3 = null;

    private AmazonSQS sqs = null;

    private AmazonEC2 ec2 = null;

    private String queue = null;

    protected Regions region = Regions.US_EAST_2;


    // Configuration
    private PropertiesConfiguration config() {
        if (config == null) {
            config = loadConfig();
        }
        return config;
    }

    private PropertiesConfiguration loadConfig() {
        PropertiesConfiguration p = new PropertiesConfiguration();
        try (InputStream pStream = new FileInputStream(new File(PROPFILENAME))) {
            p.load(pStream);
        } catch (IOException e){
            System.err.println("Unable to find property file " + PROPFILENAME);
            System.exit(1);
        } catch (ConfigurationException e){
            System.err.println("Unable to load property file " + PROPFILENAME);
            System.exit(1);
        }
        return p;
    }


    protected String getOrCry(String key) {
        if (key == null || key.trim().equals("")) {
            throw new IllegalArgumentException("No key given for config lookup!");
        }
        String value = config().getString(key);
        if (value == null || value.equals("")) {
            System.err.println("Value not found in configuration for key " + key);
            System.exit(1);
        }
        return value.trim();
    }

    protected String getOrNull(String key) {
        if (key == null || key.trim().equals("")) {
            throw new IllegalArgumentException("No key given for config lookup!");
        }
        String value = config().getString(key);
        if (value == null || value.trim().equals("")) {
            return null;
        }
        return value.trim();
    }


    // AWS Services
    protected abstract AWSCredentialsProvider getAwsCredentials();

    protected AmazonS3 getS3() {
        if (s3 == null) {
            s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(getAwsCredentials())
                    .withRegion(Regions.US_EAST_1)
                    .build();
        }
        return s3;
    }

    protected AmazonEC2 getEC2(){
        if (ec2 == null) {
            ec2 = AmazonEC2ClientBuilder.standard()
                    .withCredentials(getAwsCredentials())
                    .withRegion(getOrCry("ec2region"))
                    .build();
        }
        return ec2;
    }

    protected AmazonSQS getSQS(){
        if (sqs == null) {
            sqs = AmazonSQSClientBuilder.standard()
                    .withCredentials(getAwsCredentials())
                    .withRegion(getOrCry("queueRegion"))
                    .build();
        }
        return sqs;
    }

    protected String getQueue() {

        if (queue == null) {

            try {

                GetQueueUrlResult res = getSQS().getQueueUrl(new GetQueueUrlRequest(getOrCry("queueName")));
                queue = res.getQueueUrl();

            } catch (AmazonServiceException e) {

                if (e.getErrorCode().equals("AWS.SimpleQueueService.NonExistentQueue")) return null;

                System.err.println("Failed retrieving queue URL");
                e.printStackTrace();
                System.exit(1);

            }
        }
        return queue;
    }



}
