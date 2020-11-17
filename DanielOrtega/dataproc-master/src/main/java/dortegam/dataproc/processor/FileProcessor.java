package dortegam.dataproc.processor;

import java.io.InputStream;


public interface FileProcessor {

	FileProcessorResult process(InputStream fileStream, String inputFileKey) throws Exception;

}
