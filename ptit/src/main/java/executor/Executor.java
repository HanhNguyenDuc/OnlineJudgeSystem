package executor;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

import language.ProgramingLanguage;
import utils.UnzipUtility;

public class Executor {
    /**
     * Executor create a sandbox to use
     * Executor can exec different code and input
     * Executor work as a micro worker for a worker
     */
    Sandbox sandbox;
    String meta = "meta.txt";
    String codeFileName = "file.cpp";
    String inputFileName;
    String executeFileName = "out.out";
    ProgramingLanguage lang;

    Executor(int id) {
        this.sandbox = new Sandbox(id);
    }

    public void safetyRunCode(String code, String testPath, ExecutionProfile execProfile){
        /**
         * write code String to a file in isolate box
         * compile and run
         */
        try{
            FileWriter writer = new FileWriter(this.sandbox.getSandboxWorkDir() + "/" + this.codeFileName);
            writer.write(code);
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                // compile
                ExecutionResult execResCom = this.compileCodeFile(execProfile);
                System.out.println(execResCom.toString());
                // run test
                String testDir = this.sandbox.getSandboxWorkDir() + "/test";
                UnzipUtility uzipUtil = new UnzipUtility();
                uzipUtil.unzip(testPath, testDir);

                String[] files;
                File f = new File(testDir);
                files = f.list();
                for (String filename : files){
                    // Get list of file, move current file to "in.txt" and run execute code
                    String filePath = this.sandbox.getSandboxWorkDir() + "/test/" + filename;
                    String inputFilePath = this.sandbox.getSandboxWorkDir() + "/" + "in.txt";
                    copyFile(filePath, inputFilePath);
                    ExecutionResult execRes = this.execCodeFile(execProfile);
                    System.out.println(execRes.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void copyFile(String srcPath, String desPath) throws IOException {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = new FileInputStream(new File(srcPath));
            outStream = new FileOutputStream(new File(desPath));
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
            }
            System.out.println("File is copied successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            inStream.close();
            outStream.close();
        }
    }


    public ExecutionResult compileCodeFile(ExecutionProfile execProfile)
            throws FileNotFoundException, ParseException, IOException {
        /**
         * write compile ExecutionResult to Database
         */
        execProfile.setAdditionalConfig("compile");
        execProfile.setSandbox(this.sandbox);
        
        ArrayList<String> command = execProfile.getLanguage().getCompilationCommand(this.codeFileName, this.executeFileName);
        return this.executeCode(command, execProfile, true);
    }


    public ExecutionResult execCodeFile(ExecutionProfile execProfile)
            throws FileNotFoundException, ParseException, IOException {
        execProfile.setAdditionalConfig("execute");
        execProfile.setSandbox(this.sandbox);
        ArrayList<String> command = execProfile.getLanguage().getExecutionCommand(this.executeFileName);
        return this.executeCode(command, execProfile, false);
    }



    public ExecutionResult executeCode(ArrayList<String> codeExec,
            ExecutionProfile executionProfile, boolean needExPath) throws IOException, ParseException {

        // get source extension, Eg: /var/lib/gnu/g++
        ArrayList<String> command = null;
        if (!needExPath){
            command = new ArrayList<String>();
        }
        else{
            command = new ArrayList<String>(Arrays.asList(executionProfile.getLanguage().getDefaultSourceExtension()));
        }
        command.addAll(codeExec);
        sandbox.safetyExecuteCommand(command, executionProfile);
        ExecutionResult execResult = new ExecutionResult(executionProfile, this.sandbox, "compile");
        return execResult;
    }
}