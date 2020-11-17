package dortegam.dataproc.framework.master;

import com.amazonaws.auth.*;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dortegam.dataproc.framework.Node;

public abstract class MasterNode extends Node {

    private static final String CREDFILENAME = "config/credentials.properties";

    private AWSCredentialsProvider awsCredentialsProvider = null;

    private MongoDatabase mongo = null;

    private AmazonIdentityManagement iam = null;

    @Override
    protected AWSCredentialsProvider getAwsCredentials(){
        if (awsCredentialsProvider == null){
            awsCredentialsProvider = new PropertiesFileCredentialsProvider(CREDFILENAME);
        }
        return awsCredentialsProvider;
    }

    AmazonIdentityManagement getIAM() {
        if (iam == null) {
            iam = AmazonIdentityManagementClientBuilder.standard()
                    .withCredentials(getAwsCredentials())
                    .withRegion(region)
                    .build();
        }
        return iam;
    }

    MongoDatabase getMongo(){

        if (mongo == null) {

            String username = getOrNull("userName");

            String password = getOrNull("password");

            String userDB = getOrNull("userDB");

            ServerAddress address = new ServerAddress(
                    getOrCry("mongoUrl"),
                    Integer.parseInt(getOrCry("mongoPort")));

            MongoClientOptions options = MongoClientOptions.builder().build();

            if(username!=null && password!=null && userDB!=null){

                System.out.println("Using MongoDB authenticated access");

                MongoCredential credential = MongoCredential.createCredential(username,userDB,password.toCharArray());

                mongo = new MongoClient(address,credential,options).getDatabase(getOrCry("database"));

            }else{

                System.out.println("MongoDB user, password and/or user database was not found. Using non-authenticated access");

                mongo = new MongoClient(address,options).getDatabase(getOrCry("database"));

            }

        }

        return mongo;
    }

}
