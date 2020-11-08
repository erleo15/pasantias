# Dataset Processor

Dataset Processor is a Java tool to extract information from large datasets using AWS.

## AWS

The Amazon Web Services used are:
- **IAM**
- **EC2**
- **S3**
- **SQS**

## Supported features

### Dataset location

Right now, only datasets located in **AWS S3** buckets are supported.

### Processing

This framework is built so it allows the addition of new processors (file formats) and extractors (content parsers).
 
The ones available are:
- **Processor**:
    - **WARC** (Web ARChive): focused in the [Common Crawl](https://commoncrawl.org) dataset. Calls an HTML extractor.
    
- **Extractor**:
    - **HTMLMetadata**: finds metadata nested in HTML as defined by [Schema.org](https://schema.org).
    
Search files allow the user to define a criteria to parse the files. 

Included search files:
- **accessibility.metadata**: retrieves accessibility properties in a metadata extractor.

### Output format

The result data is in **JSON** format so it can be introduced in a [**MongoDB**](https://mongodb.com/) instance to be queried.


## Guide

The Guide.md file contains the following sections:

- [Installation](Guide.md/#installation)

- [Actions](Guide.md/#actions)

- [Basic Steps](./Guide.md/#basic-steps)
    

## TODO

- Improve logging
  - Use log4j
  - Centralized logging for cloud instances?
    
- Cancel spot request automatically when done processing



## Contributing

If there is any format, dataset or extractor that you would want to see added don't doubt to ask 
and we will see what can be done.

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.


## License
[MIT](https://choosealicense.com/licenses/mit/)
