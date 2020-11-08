package amef.storage;

import org.bson.Document;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.json.JSONArray;

import java.io.InputStream;
import java.util.List;

public class S3Storage extends RestS3Service implements OutStorage, InStorage {

    private String resultBucket;

    private String dataBucket;


    public S3Storage (org.jets3t.service.security.AWSCredentials credentials, String resultBucket, String dataBucket) throws S3ServiceException {
        super(credentials);
        this.resultBucket = resultBucket;
        this.dataBucket = dataBucket;
    }

    @Override
    public void store(String fileKey, Document stats, List<Document> data) {
        try{
            System.out.println("S3 Storage " + resultBucket+ "\n" + new S3Bucket(resultBucket).getLocation());
            S3Object statsObject = new S3Object("stats/"+fileKey,stats.toJson());
            putObject(resultBucket, statsObject);

            if(!data.isEmpty()) {
                S3Object dataObject = new S3Object("pages/" + fileKey, new JSONArray(data).toString());
                putObject(resultBucket, dataObject);
            }

        }catch (Exception e){

        }

    }

    public void deleteAll() throws ServiceException{
        S3Object[] objects;
        while((objects = listObjects(resultBucket)).length > 0 ){
            for (S3Object object : objects) {
                if (object.getKey() != null
                        && (object.getKey().startsWith("pages/") || object
                        .getKey().startsWith("stats/"))) {
                    deleteObject(resultBucket, object.getKey());
                }
            }
        }
    }

    public void delete(String date) throws ServiceException{
        S3Object[] objects;
        while((objects = listObjects(resultBucket)).length > 0 ){
            for (S3Object object : objects) {
                if (object.getKey() != null
                        && (object.getKey().startsWith("pages/"+date) || object
                        .getKey().startsWith("stats/"+date))) {
                    deleteObject(resultBucket, object.getKey());
                }
            }
        }
    }

    @Override
    public InputStream getFile(String fileKey) throws Exception {
        return getObject(dataBucket,fileKey).getDataInputStream();
    }
}
