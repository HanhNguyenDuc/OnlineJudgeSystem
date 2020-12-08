package executor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.sound.sampled.SourceDataLine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import config.Config;

public class ExecutionResult {
    private String exitcode, status, message;
    private float time;
    private int memory;
    private JSONObject additionalParams;
    private JSONObject additionalResults = new JSONObject();
    private Sandbox sandbox;
    private String additionalConfig;

    ExecutionResult(final String stdin, final String stdout, final String stderr, final String exitcode,
            final int memory, final String status, final String message, final float time, String config)
            throws IOException, ParseException {
        this.exitcode = exitcode;
        this.memory = memory;
        this.status = status;
        this.message = message;
        this.time = time;
        this.additionalConfig = config;
        Config config1 = new Config();
        this.additionalParams = config1.getResultConfigFile(additionalConfig);
    }



    ExecutionResult(ExecutionProfile executionProfile, Sandbox sandbox, String config) throws IOException,
            ParseException {
        // Get Process Infomation from executionProfile
        this.additionalConfig = config;
        Config config1 = new Config();
        this.additionalParams = config1.getResultConfigFile(additionalConfig);
        this.sandbox = sandbox;
        for (Iterator iterator = this.additionalParams.keySet().iterator(); iterator.hasNext();){
            String key = (String)iterator.next();
            String value = (String)this.additionalParams.get(key);
            String workdir = this.sandbox.getSandboxWorkDir();
            String filePath = workdir + "/" + value;
            String content = ExecutionResult.readFileContent(filePath);
            this.additionalResults.put(key, content);
        }
        HashMap<String, String> profileMap = executionProfile.toMap();
        String metaFilePath = profileMap.get("meta");
        // Get file content
        
        // Get metafile map
        HashMap<String, String> metaFileMap = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new FileReader(metaFilePath));
        String line;
        System.out.println("METAAAAA FILEEEEE: " + metaFilePath);
        while ((line = br.readLine()) != null){
            System.out.println(line);
            StringTokenizer sToken = new StringTokenizer(line, ":");
            String key = sToken.nextToken();
            String value = sToken.nextToken();
            metaFileMap.putIfAbsent(key, value);
        }
        br.close();

        this.exitcode = metaFileMap.get("exitcode");
        this.memory = Integer.valueOf(metaFileMap.get("cg-mem"));
        this.time = Float.valueOf(metaFileMap.get("time"));
    }

    public HashMap<String,String> toMap(){
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("exitcode", this.exitcode);
        map.put("memory", Integer.toString(this.memory));
        map.put("status", this.status);
        map.put("message", this.message);
        map.put("time", Float.toString(this.time));
        map.put("additionParams", this.additionalResults.toJSONString());
        // System.out.print("JSON PARAMS" + this.additionalResults.toJSONString());

        return map;
    }
    
    public String toString() {
        final HashMap<String, String> map = this.toMap();
        String jsonString;
        try {
            jsonString = new ObjectMapper().writeValueAsString(map);
            return jsonString;
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ExecutionResult fromProcessResult(final ProcessResult processResult){
        
        return null;
    }

    public JSONObject toJson(){
        HashMap<String, String> map = this.toMap();
        return new JSONObject(map);
    }

    public String toJsonString(){
        return this.toJson().toString();
    }

    private static String readFileContent(String filePath){
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null){
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return "";
        }
        return contentBuilder.toString();
    }

    // public static ExecutionResult fromExecutionProfile(final ExecutionProfile executionProfile){
    //     HashMap<String, String> profileMap = executionProfile.toMap();
        
    //     // read stdin, stdout, stderr, meta
    //     String stdin = ExecutionResult.readFileContent((String)profileMap.get("stdin"));
    //     String stdout = ExecutionResult.readFileContent((String)profileMap.get("stdout"));
    //     String stderr = ExecutionResult.readFileContent((String)profileMap.get("stderr"));
    //     String meta = ExecutionResult.readFileContent((String)profileMap.get("meta"));
    //     // read information from meta file
    //     return null;
    // }

    
}