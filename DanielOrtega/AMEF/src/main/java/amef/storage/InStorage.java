package amef.storage;

import java.io.InputStream;

public interface InStorage {

    InputStream getFile(String fileKey) throws Exception;

}
