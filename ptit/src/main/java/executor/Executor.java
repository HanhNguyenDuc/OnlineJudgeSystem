package executor;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dao.SubmissionDAO;
import entity.Problem;
import entity.Submission;
import language.ProgramingLanguage;
import redis.clients.jedis.Jedis;
import utils.UnzipUtility;

public class Executor{
    /**
     * Executor create a sandbox to use
     * Executor can exec different code and input
     * Executor work as a micro worker for a worker
     */
    Sandbox sandbox;
    String meta = "meta.txt";
    String codeFileName = "SubmissionFile.java";
    String inputFileName;
    String executeFileName = "SubmissionFile.class";
    ProgramingLanguage lang;

    public Sandbox getSandBox(){
        return this.sandbox;
    }

    public Executor(int id) {
        this.sandbox = new Sandbox(id);
    }

    public int getId(){
        return this.sandbox.getBoxId();
    }

    public void safetyRunCode(Submission submission, String testPath, ExecutionProfile execProfile){
        /**
         * write code String to a file in isolate box
         * compile and run
         */
        Problem problem = submission.getProblem();
        String code = submission.getCode();
        String solutionCode = problem.getSolution();
        ArrayList<String> submissionResult = new ArrayList<String>();
        ArrayList<String> solutionResult = new ArrayList<String>();
        
        JSONArray submissionReport = new JSONArray();
        JSONObject fullSubmissionReport = new JSONObject(); 
        fullSubmissionReport.put("tests", submissionReport);
        SubmissionDAO submissionDAO = new SubmissionDAO();
        String err = "";
        
        
        
        
        
        
        try {
            submission.setJudgeStatus("JUDGING");
            submissionDAO.updateSubmissionStatus(submission);



            // Write code to file
            String codeFileName = execProfile.getLanguage().getCodeFileName(code);
            String executeFileName = execProfile.getLanguage().getExecutionFileName(code);
            FileWriter writer = new FileWriter(this.sandbox.getSandboxWorkDir() + "/" + codeFileName);
            writer.write(code);
            writer.close();





            // compile
            ExecutionResult execResCom = this.compileCodeFile(execProfile, codeFileName, executeFileName);
            fullSubmissionReport.put("compile", execResCom.toJson());
            System.out.println(execResCom.toJson().toJSONString());
            // run test
            String testDir = this.sandbox.getSandboxWorkDir() + "/test";
            
            Jedis jedis = new Jedis("localhost");
            while (true){
                // System.out.println("file-path--zippedzip");
                if (jedis.get("file-path--zippedzip") == null || !jedis.get("file-path--zippedzip").equals("inused")){
                    break;
                }
            }

            String[] files = null;
            try{
                jedis.set("file-path--zippedzip", "inused");
                UnzipUtility uzipUtil = new UnzipUtility();
                uzipUtil.unzip(testPath, testDir);
                File f = new File(testDir);
                files = f.list();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                jedis.set("file-path--zippedzip", "free");
            }

            jedis.close();









            for (String filename : files){
                // Get list of file, move current file to "in.txt" and run execute code
                String filePath = this.sandbox.getSandboxWorkDir() + "/test/" + filename;
                String inputFilePath = this.sandbox.getSandboxWorkDir() + "/" + "in.txt";
                copyFile(filePath, inputFilePath);
                ExecutionResult execRes = this.execCodeFile(execProfile, executeFileName);
                // System.out.println("JSON VALUE: ");
                // System.out.println(execRes.toJson().get("additionParams"));

                /**
                 * Get output from execResult
                 */
                JSONObject testCaseReport = new JSONObject();
                submissionReport.add(testCaseReport);
                testCaseReport.put("exitcode", null);
                testCaseReport.put("judgeVerdict", null);
                testCaseReport.put("memory", execRes.toJson().get("memory"));
                testCaseReport.put("time", execRes.toJson().get("time"));
                if (execRes.toJson().get("exitcode") == null){
                    testCaseReport.put("judgeVerdict", "TLE");
                }else{
                    testCaseReport.put("exitcode", execRes.toJson().get("exitcode"));
                }
                System.out.println(execRes.toJson().toJSONString());
                String additionParamsString = (String)execRes.toJson().get("additionParams");
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse(additionParamsString);
                submissionResult.add((String)jsonObject.get("stdout"));
                // System.out.println(jsonObject.get("stdout"));
            }







            /**
             * re-write code to this.codeFileName and execute code
             */
            ExecutionProfile solutionProfile = problem.getSolutionProfile(sandbox);
            codeFileName = solutionProfile.getLanguage().getCodeFileName(solutionCode);
            executeFileName = solutionProfile.getLanguage().getExecutionFileName(solutionCode);
            writer = new FileWriter(this.sandbox.getSandboxWorkDir() + "/" + codeFileName);
            writer.write(solutionCode);
            writer.close();
            ExecutionResult solutionResCom = this.compileCodeFile(solutionProfile, codeFileName, executeFileName);

            for (String filename: files){
                // Get list of file, move current file to "in.txt" and run execute code
                String filePath = this.sandbox.getSandboxWorkDir() + "/test/" + filename;
                String inputFilePath = this.sandbox.getSandboxWorkDir() + "/" + "in.txt";
                copyFile(filePath, inputFilePath);
                ExecutionResult execRes = this.execCodeFile(solutionProfile, executeFileName);
                /**
                 * Get output from execResult
                 */
                String additionParamsString = (String)execRes.toJson().get("additionParams");
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse(additionParamsString);
                solutionResult.add((String)jsonObject.get("stdout"));
            }
            


            String verdict = "AC";

            boolean flag = true;
            System.out.println("Start comparing code");
            for (int i=0; i<solutionResult.size(); i++){
                String trueOutput = solutionResult.get(i);
                String predOutput = submissionResult.get(i);
                JSONObject testCaseReport = (JSONObject)submissionReport.get(i);
                if (testCaseReport.get("judgeVerdict") == null){
                    if (trueOutput.equals(predOutput)){
                        testCaseReport.replace("judgeVerdict", "AC");
                        
                    }
                    else{
                        testCaseReport.replace("judgeVerdict", "WA");
                        flag = false;
                        break;
                    }
                }
                else if (testCaseReport.get("judgeVerdict") == "TLE"){
                    verdict = "TLE";
                    break;
                }
            }
            System.out.println(submissionReport.toJSONString());
            
            if (flag == false){
                verdict = "WA";
            }
            submission.setVerdict(verdict);
            
        } catch (Exception e) {
            e.printStackTrace();
            err = e.toString();
        }
        finally{
            submission.setJudgeReport(fullSubmissionReport.toJSONString());
            submissionDAO.updateSubmissionReport(submission);
            if (err.equals("")){
                submission.setJudgeStatus("DONE");
            }
            else{
                submission.setJudgeStatus("FAIL");
                submission.setJudgeErr(err);
                submission.setVerdict("ERR");
            }
            submissionDAO.updateSubmissionStatus(submission);
            
            Jedis jedis = new Jedis("localhost");
            jedis.set("judge-worker-" + Integer.toString(this.getId()), "free");
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


    public ExecutionResult compileCodeFile(ExecutionProfile execProfile, String codeFileName, String executeFileName)
            throws FileNotFoundException, ParseException, IOException {
        /**
         * write compile ExecutionResult to Database
         */
        execProfile.setAdditionalConfig("compile");
        execProfile.setSandbox(this.sandbox);
        
        ArrayList<String> command = execProfile.getLanguage().getCompilationCommand(codeFileName, executeFileName);
        return this.executeCode(command, execProfile, true);
    }


    public ExecutionResult execCodeFile(ExecutionProfile execProfile, String executeFileName)
            throws FileNotFoundException, ParseException, IOException {
        execProfile.setAdditionalConfig("execute");
        execProfile.setSandbox(this.sandbox);
        ArrayList<String> command = execProfile.getLanguage().getExecutionCommand(executeFileName);
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