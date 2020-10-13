package executor;


/**
 * Hello world!
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import language.CLanguage;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class Sandbox {
    // private static final Logger LOGGER = LogManager.getLogger(Sandbox.class);
    private boolean isCreated = false;
    private int boxId;
    private static String isolatePath = "/opt/isolate/isolate";

    public static void main(String[] args) {
        // Executor exec = new Executor();
        // Code biên dịch c++
        Sandbox sandbox = new Sandbox(255);
        String codePath = "/home/OnlineJudgeSystem/ab.cpp";
        String inContent = "2 3\n";

        String codeExec = null;
        try {
            codeExec = new Scanner(new File(codePath)).useDelimiter("\\Z").next();
            FileWriter fw = new FileWriter(new File("/var/local/lib/isolate/255/box/ab.cpp"));
            fw.write(codeExec);
            fw.close();
            FileWriter fw2 = new FileWriter(new File("/var/local/lib/isolate/255/box/in.txt"));
            fw2.write(inContent);
            fw2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally{
        // System.out.println(content);
            ExecutionProfile execProfile = new ExecutionProfile(new CLanguage(), "in.txt", "out.txt", "err.txt", 2, 2, 256*1024, "meta.txt");
            CLanguage lang = new CLanguage();
            ArrayList<String> command = lang.getCompilationCommand("ab.cpp", "out.out");

            Executor.executeCode(command, sandbox, execProfile);
        }
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

    public ProcessResult safetyExecuteCommand(ArrayList<String> command, ExecutionProfile executionProfile, String[] additionalContextParams){
        /**
         * Cách run command với isolate: 
         * isolate options --run -- program arguments
         */
        System.out.println("Code has been executed!!!!");

        // LOGGER.debug("Code has been executed!!!!");
        ArrayList<String> validExecutableCommand = (ArrayList<String>)Arrays.asList(this.getContextCommandParams(additionalContextParams));
        
        HashMap<String, String> profileDict = executionProfile.toMap();
        for (Map.Entry<String, String> entry: profileDict.entrySet()){
            validExecutableCommand.add(String.format("--%s = %s", entry.getKey(), entry.getValue()));
        }
        validExecutableCommand.add("--run");
        validExecutableCommand.add("--");
        validExecutableCommand.addAll(command);
        String commandString = Sandbox.commandToString(validExecutableCommand);
        ProcessResult pr = new ProcessResult();
        try{
            pr = Sandbox.unsafetyExecuteCommand(commandString);
        }
        catch (Exception e){
            // LOGGER.debug("There is an Error when execute isolate command");
            e.printStackTrace();
        }
        return pr;
    }
    public ProcessResult safetyExecuteCommand(ArrayList<String> command, ExecutionProfile executionProfile){
        // ArrayList<String> validExecutableCommand = (ArrayList<String>)Arrays.asList(this.getContextCommandParams());
        System.out.println("Reach safetyExecuteCommand");
        ArrayList<String> validExecutableCommand = new ArrayList<>(Arrays.asList(this.getContextCommandParams()));
        
        HashMap<String, String> profileDict = executionProfile.toMap();
        for (Map.Entry<String, String> entry: profileDict.entrySet()){
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
        // START debug
        System.out.println("COMMAND STRING: " + commandString);
        // END debug
        ProcessResult pr = new ProcessResult();
        try{
            pr = Sandbox.unsafetyExecuteCommand(commandString);
        }
        catch (Exception e){
            // LOGGER.debug("There is an Error when execute isolate command");
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
