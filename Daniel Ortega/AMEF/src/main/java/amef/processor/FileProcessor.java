package amef.processor;

import java.io.InputStream;


public interface FileProcessor {

	void process(InputStream fileStream, String inputFileKey) throws Exception;

}
