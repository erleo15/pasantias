package dortegam.dataproc.framework.master;

import com.martiansoftware.jsap.*;

import java.util.*;

public class CLI {

    private static JSAP jsap;
    private static String[] params;
    private static Master master;

    public static void main(String[] args) throws JSAPException {

        jsap = new JSAP();

        params = args;

        master = new Master();

        UnflaggedOption actionParam = new UnflaggedOption("action")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);

        actionParam
                .setHelp("Action to perform \n(queue | launch | stop | monitor | results | search )");

        jsap.registerParameter(actionParam);

        JSAPResult result = jsap.parse(params);
        String action = result.getString("action");

        actionParam.setUsageName(action);

        //Queue related operations
        if ("queue".equals(action)) {
            queue();
            System.exit(0);
        }

        //Deploy
        if ("deploy".equals(action)) {
            deploy();
            System.exit(0);
        }

        //Launch
        if ("launch".equals(action)) {
            launch();
            System.exit(0);
        }

        //Monitor processing progress
        if ("monitor".equals(action)) {
            monitor();
        }

        //Shutdown instances
        if ("stop".equals(action)) {
            stop();
            System.exit(0);
        }

        //Retrieve data from S3 to MongoDB
        if ("results".equals(action)) {
            results();
            System.exit(0);
        }

        actionParam.setUsageName(null);

        printUsageAndExit(result);

    }

    private static void queue() throws JSAPException {

        UnflaggedOption queueOperationParam = new UnflaggedOption("operation")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);
        queueOperationParam.setHelp("Operation to perform in the queue (add | clear)");

        jsap.registerParameter(queueOperationParam);

        JSAPResult result = jsap.parse(params);

        String queueOperation = result.getString("operation");
        queueOperationParam.setUsageName(queueOperation);

        if("add".equals(queueOperation)){
            queueAdd();
        }else if("clear".equals(queueOperation)){
            queueClear();
        } else {
            queueOperationParam.setUsageName(null);
            System.out.println("Invalid queue operation");
            printUsageAndExit(result);
        }

    }

    private static void queueAdd() throws JSAPException {

        UnflaggedOption addModeParam = new UnflaggedOption("addMode")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);
        addModeParam.setHelp("Mode to add elements to queue (prefix | file)");

        jsap.registerParameter(addModeParam);

        JSAPResult result = jsap.parse(params);

        String addMode = result.getString("addMode");
        addModeParam.setUsageName(addMode);

        if("prefix".equals(addMode)){
            queueAddPrefix();
        }else if("file".equals(addMode)){
            queueAddFile();
        } else {
            addModeParam.setUsageName(null);
            System.out.println("Invalid queue add mode");
            printUsageAndExit(result);
        }

    }

    private static void queueAddPrefix() throws JSAPException {

        UnflaggedOption prefixParam = new UnflaggedOption("prefix")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);
        prefixParam.setHelp("Prefix of objects to be added to the queue");

        FlaggedOption limitParam = new FlaggedOption("limit")
                .setStringParser(JSAP.LONG_PARSER).setRequired(false)
                .setLongFlag("limit").setShortFlag('l');
        limitParam.setHelp("Limits number of objects added to the queue");

        jsap.registerParameter(prefixParam);
        jsap.registerParameter(limitParam);

        JSAPResult result = jsap.parse(params);

        if (!result.success()){
            printUsageAndExit(result);
        }

        String prefix = result.getString("prefix");

        if(result.contains("limit")){
            master.queuePrefix(prefix,result.getLong("limit"));
        }else{
            master.queuePrefix(prefix,null);
        }

    }

    private static void queueAddFile() throws JSAPException {

        UnflaggedOption fileParam = new UnflaggedOption("file")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);
        fileParam.setHelp("File with objects to be added to the queue");

        FlaggedOption limitParam = new FlaggedOption("limit")
                .setStringParser(JSAP.LONG_PARSER).setRequired(false)
                .setLongFlag("limit").setShortFlag('l');
        limitParam.setHelp("Limits number of objects added to the queue");

        jsap.registerParameter(fileParam);
        jsap.registerParameter(limitParam);

        JSAPResult result = jsap.parse(params);

        String file = result.getString("prefix");

        if(result.contains("limit")){
            master.queueFile(file,result.getLong("limit"));
        }else{
            master.queueFile(file,null);
        }
    }

    private static void queueClear() {
        master.clearQueue();
    }

    private static void deploy() throws JSAPException{

        UnflaggedOption filesParam = new UnflaggedOption("file")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(true);
        filesParam.setHelp("List of files to be deployed on the worker instances ( all | [ jar | conf | search ] )");

        jsap.registerParameter(filesParam);

        JSAPResult result = jsap.parse(params);

        if (!result.success()){
            printUsageAndExit(result);
        }

        List files = Arrays.asList(result.getStringArray("file"));

        if(files.contains("all")) {

            if(files.size() > 1){

                System.err.println("Option 'all' must appear alone");
                System.exit(1);

            }

            master.deploy(true, true,true);

        }else{

            boolean jar = false, conf = false, search = false;

            if(files.contains("jar")) jar = true;
            if(files.contains("conf")) conf = true;
            if(files.contains("search")) search = true;

            master.deploy(jar,conf,search);

        }

    }

    private static void launch() throws JSAPException{

        FlaggedOption deployParam = new FlaggedOption("jarfile")
                .setStringParser(JSAP.STRING_PARSER).setRequired(false)
                .setLongFlag("deploy").setShortFlag('d');
        deployParam.setHelp("Jarfile to be deployed on the worker instances");

        UnflaggedOption amountParam = new UnflaggedOption("amount")
                .setStringParser(JSAP.INTEGER_PARSER).setRequired(true)
                .setGreedy(false);
        amountParam.setHelp("Amount of worker instances to start in EC2");

        UnflaggedOption priceParam = new UnflaggedOption("pricelimit")
                .setStringParser(JSAP.DOUBLE_PARSER).setRequired(true)
                .setGreedy(false);
        priceParam.setHelp("Price limit for instances in US$");

        FlaggedOption monitorParam = new FlaggedOption("monitor")
                .setStringParser(JSAP.STRING_PARSER).setRequired(false)
                .setLongFlag("monitor").setShortFlag('m');
        monitorParam.setHelp("Start monitoring extraction");

        jsap.registerParameter(deployParam);
        jsap.registerParameter(amountParam);
        jsap.registerParameter(priceParam);
        jsap.registerParameter(monitorParam);

        JSAPResult result = jsap.parse(params);

        if (!result.success()) {
            printUsageAndExit(result);
        }

        if(result.contains("jarfile")){
            master.deploy(true,true,true);
        }else{
            master.deploy(false,true,true);
        }

        master.launch(result.getInt("amount"),result.getDouble("pricelimit"));

        if(result.contains("monitor")){
            monitor();
        }
    }


    private static void monitor() {
        master.monitor();
    }

    private static void stop() {
        master.stop();
    }

    private static void results() throws JSAPException {

        UnflaggedOption dataOperationParam = new UnflaggedOption("operation")
                .setStringParser(JSAP.STRING_PARSER).setRequired(true)
                .setGreedy(false);
        dataOperationParam.setHelp("Operation to perform with the results ( list | retrieve | delete )");

        jsap.registerParameter(dataOperationParam);

        JSAPResult result = jsap.parse(params);

        String dataOperation = result.getString("operation");
        dataOperationParam.setUsageName(dataOperation);

        if("list".equals(dataOperation)){
            listData();
        }else if("retrieve".equals(dataOperation)){
            retrieveData();
        }else if("delete".equals(dataOperation)){
            deleteData();
        } else {
            dataOperationParam.setUsageName(null);
            System.out.println("Invalid data operation");
            printUsageAndExit(result);
        }

    }

    private static void listData(){
        master.listData();
    }

    private static void retrieveData(){
        master.retrieveData();
    }

    private static void deleteData(){
        master.deleteData();
    }

    private static void printUsageAndExit(JSAPResult result) {
        @SuppressWarnings("rawtypes")
        Iterator it = result.getErrorMessageIterator();
        while (it.hasNext()) {
            System.err.println("Error: " + it.next());
        }

        System.err.println("Usage: ./bin/Master " + jsap.getUsage());
        System.err.println(jsap.getHelp());

        System.exit(1);
    }
}
