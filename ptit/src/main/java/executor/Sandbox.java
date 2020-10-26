package executor;


/**
 * Hello world!
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import language.CLanguage;


import org.json.simple.parser.ParseException;


public class Sandbox {
    // private static final Logger LOGGER = LogManager.getLogger(Sandbox.class);
    private boolean isCreated = false;
    private int boxId;
    private static String isolatePath = "/opt/isolate/isolate";

    public static void main(String[] args) throws IOException, ParseException {
        // Sandbox sandbox = new Sandbox(255);
        // ExecutionProfile execProfile = new ExecutionProfile(
        //     new CLanguage(), 
        //     2, 
        //     2, 
        //     256*1024, 
        //     "meta.txt", 
        //     sandbox,
        //     "compile"
        // );
        // CLanguage lang = new CLanguage();
        // ArrayList<String> command = lang.getCompilationCommand("ab.cpp", "out1.out");

        // ExecutionResult execResult = Executor.executeCode(command, sandbox, execProfile);
        // System.out.println(execResult);

        // // Run executable File
        // ExecutionProfile execProfile2 = new ExecutionProfile(
        //     new CLanguage(),
        //     2,
        //     2,
        //     256*1024,
        //     "meta.txt",
        //     sandbox,
        //     "exec"
        // );

        // // ExecutionResult execResult = new ExecutionResult(execProfile, sandbox);

        Executor executor = new Executor(230);
        // Read file code
        File file = new File("/home/OnlineJudgeSystem/ab.cpp");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String code = new String(data, "UTF-8");
        System.out.println(code);

        String testPath = "/home/OnlineJudgeSystem/zipped.zip";

        ExecutionProfile execProfile = new ExecutionProfile(
            new CLanguage(),
            2,
            2,
            256 * 1024,
            "meta.txt"
        );

        Thread thread = new Thread(new ExecutorThread(executor, code, testPath, execProfile));
        thread.start();
    }

    Sandbox(int id){
        this.isCreated = false;
        this.boxId = id;
        try{
            // Clean old sandbox
            Sandbox.cleanSandboxWithId(this.boxId);
            // Clean old sandbox
            Sandbox.initSandBoxWithId(this.boxId);
            this.isCreated = true;
            // LOGGER.debug(String.format("Sandbox %s is successfully created", Integer.toString(this.boxId)));
        }
        catch(Exception e){
            // LOGGER.debug(String.format("There is an error when create sandbox %s: "));
            // LOGGER.debug(e);
        }
    }

    public int getBoxId(){
        return this.boxId;
    }

    public boolean getIsCreated(){
        return this.isCreated;
    }

    public static String commandToString(ArrayList<String> command){
        String result = "";
        for (String eleString : command){
            result += String.format(" %s",eleString);
        }

        return result;
    }

    

    public ProcessResult safetyExecuteCommand(ArrayList<String> command, ExecutionProfile executionProfile){
        // ArrayList<String> validExecutableCommand = (ArrayList<String>)Arrays.asList(this.getContextCommandParams());
        System.out.println("Reach safetyExecuteCommand");
        ArrayList<String> validExecutableCommand = new ArrayList<>(Arrays.asList(this.getContextCommandParams()));
        
        HashMap<String, String> profileDict = executionProfile.toMap();
        for (Map.Entry<String, String> entry: profileDict.entrySet()){
            if (entry.getValue() == null){
                continue;
            }
            if (entry.getKey().charAt(0) == '_'){
                String newKey = entry.getKey().substring(1);
                validExecutableCommand.add(String.format("--%s=%s", newKey, entry.getValue()));
            }
            else{
                validExecutableCommand.add(String.format("--%s %s", entry.getKey(), entry.getValue()));
            }
        }
        validExecutableCommand.add("--run");
        validExecutableCommand.add("--");
        validExecutableCommand.addAll(command);
        String commandString = Sandbox.commandToString(validExecutableCommand);
        System.out.print(validExecutableCommand);
        // START debug
        System.out.println("COMMAND STRING: " + commandString);
        // END debug
        ProcessResult pr = new ProcessResult();
        try{
            pr = Sandbox.unsafetyExecuteCommand(commandString);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return pr;
    }

    public String getSandboxWorkDir(){
        return String.format("/var/local/lib/isolate/%s/box", Integer.toString(this.boxId));
    }


    public String[] getContextCommandParams(String[] additionalContextParams){
        String[] contextParams = {
            Sandbox.isolatePath, 
            "--cg", 
            "--silent", 
            String.format("--box-id %s", Integer.toString(this.boxId))
        };
        ArrayList<String> additionalContextParamsArrList = new ArrayList<String>(Arrays.asList(additionalContextParams));
        ArrayList<String> contextParamsArrList = new ArrayList<String>(Arrays.asList(contextParams));
        additionalContextParamsArrList.addAll(contextParamsArrList);
        return (String[]) additionalContextParamsArrList.toArray();
    }

    public String[] getContextCommandParams(){
        String[] contextParams = {
            Sandbox.isolatePath, 
            "--cg", 
            "--silent", 
            "-e",
            String.format("--box-id %s", Integer.toString(this.boxId))
        };
        return contextParams;
    }


    private static ProcessResult unsafetyExecuteCommand(String command) throws IOException, InterruptedException {
        Process p;
        // Execute
        p = Runtime.getRuntime().exec(command);
        InputStream errorStream = p.getErrorStream();
        int exitcode = p.waitFor();

        // Read shell output
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String result = "";
        while ((line = reader.readLine()) != null) {
            result += line + "\n";
        }
        // Convert InputStream to String
        String errorString = "";
        try{
            InputStreamReader isReader = new InputStreamReader(errorStream);
            //Creating a BufferedReader object
            BufferedReader errorReader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = errorReader.readLine())!= null){
                sb.append(str);
            }
            errorString = sb.toString();
            System.out.println(errorString);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        ProcessResult pr = new ProcessResult(result, errorString, exitcode);
        return pr;
    }

    protected static void initSandBoxWithId(int id) throws IOException, InterruptedException {
        String command = String.format("/opt/isolate/isolate --init --cg --box-id %s", Integer.toString(id));
        Sandbox.unsafetyExecuteCommand(command);
    }

    protected static void cleanSandboxWithId(int id) throws IOException, InterruptedException {
        String command = String.format("/opt/isolate/isolate --cleanup --box-id %s", Integer.toString(id));
        Sandbox.unsafetyExecuteCommand(command);
    } 
}
