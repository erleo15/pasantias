# Installation

1. Clone this repository 
    ```bash
    git clone https://github.com/dortegam/dataset-processor.git
    ```

2. Install Java 8 and Maven 3
    ```bash
    sudo apt install openjdk-8-jdk
    sudo apt install maven
    ```
   
3. *(optional)* [Install MongoDB](https://docs.mongodb.com/manual/tutorial/) in the machine where the data will be stored.

4. Create configuration, credentials and search files from the model versions
    ```bash
    cp config/credentials.properties.dist config/credentials.properties
    cp config/config.properties.dist config/config.properties
    cp config/(???).search.json.dist config/search.json
    ```

5. Edit these files with the desired configuration. Properties with value *\<edit\>* are to be set by the user. 

6. Compile the project
    ```bash
    mvn package
    ```

7. The project is ready to be used!




# Actions

All commands must be called from the root directory of the project.

```bash
./bin/master <action>
```

### Queue

- **add**: fill queue with the given parameters

    - **prefix**: add files starting with given prefix. (Mainly for testing)
    
        ```bash
        ./bin/master queue add prefix <prefix> [-l <fileLimit>]
        ```
      
        - *prefix*: prefix of the target file keys (without 'crawl-data/')
        
        - *fileLimit*: maximum number of files to add to queue
        
            Example: `./bin/master queue add prefix CC-MAIN-2019-26/segments/1560627998690.87/warc/ -l 2`
    
    - **file**: add all keys from paths file. These files (warc.paths) can be found 
        [here](https://commoncrawl.s3.amazonaws.com/crawl-data/index.html) selecting the location of the desired date.
    
        ```bash
        ./bin/master queue add file <file>
        ```
        - *file*: path to the warc.paths file
                
            Example: `./bin/master queue add file ./paths/2019-10.warc.paths`
         
- **delete**: remove all messages from queue

    ```bash
    ./bin/master queue delete
    ```
    
### Deploy

Uploads tool and config files to bucket to be used by EC2 instances

```bash
./bin/master deploy <files>
```

- *files*: files to be deployed. Must be like *( all | ( jar | conf | search )+ )*

    Example: `./bin/master deploy jar conf` or `./bin/master deploy all` 
    
### Launch

Launch EC2 spot instances that will start requesting and processing files automatically

```bash
./bin/master launch <amount> <maxPrice>
```

- *amount*: amount of instances to request.

- *maxPrice*: limit price for EC2 spot instances per hour in USD
    
    Example: `./bin/master launch 5 0.1`
      
### Monitor

Output number of instances requested and running, files left and estimated rate and finishing time

```bash
./bin/master monitor
```

### Stop

Stop all instances created by this tool

```bash
./bin/master stop
```    

### Results

- **list**: print dates of origin datasets of data stored in result bucket and amount of files for each one

    ```bash
    ./bin/master results list
    ```
  
- **retrieve**: introduces data stored in bucket to selected MongoDB instance

    ```bash
    ./bin/master results retrieve
    ```
  
- **delete**: deletes all data from bucket
    
    ```bash
    ./bin/master results delete
    ```
  
  
  
# Basic steps

This is a list of the basic steps (after installation) to process a CommonCrawl dataset of any given month (in this case September 2019).

1. Download and unzip the warc.paths file:
    ```bash
    wget https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2019-39/warc.paths.gz
    gunzip warc.paths.gz
    ```
   
2. When all the configuration files are ready, deploy all necessary files:
    ```bash
    ./bin/master deploy all
    ```
   
3. Fill queue from downloaded file:
    ```bash
    ./bin/master queue file warc.paths
    ```
   
4. Launch desired number of instances at the desired price (here a c5n.18xlarge will be the example):
    ```bash
    ./bin/master launch 4 0.7
    ```
   
5. Now you can see the development of the process:
   ```bash
   ./bin/master monitor
   ```
   
6. When the process is finished, terminate the instances:
   ```bash
   ./bin/master stop
   ```

7. Transfer the results from S3 to MongoDB
   ```bash
   ./bin/master results retrieve
   ```