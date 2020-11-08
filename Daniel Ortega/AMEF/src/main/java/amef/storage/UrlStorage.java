package amef.storage;

import java.io.InputStream;
import java.net.URL;

public class UrlStorage implements InStorage {

    @Override
    public InputStream getFile(String fileKey) throws Exception{
        return new URL(fileKey).openStream();
    }
}
