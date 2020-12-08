package executor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import config.Config;

import org.json.simple.JSONObject;

import language.ProgramingLanguage;

public class ExecutionProfile {
    /**
     * Được sử dụng để chứa các tham số hợp lệ khi thực thi process (dùng để quản lý
     * process được tạo không có tác dụng thực thi lệnh)
     */
    private ProgramingLanguage language;
    private String meta;
    private int time, extraTime, memory, processes;
    private JSONObject additionalParams;
    private static JSONParser jsonParser = new JSONParser();
    private Sandbox sandbox;
    private String additionalConfig;
    private String filename;

    ExecutionProfile(ProgramingLanguage language, int time, int extraTime, int memory, String meta,
            Sandbox sandbox, String config) throws ParseException, FileNotFoundException, IOException {
                /**
                 * Init ExecutionProfile with config param
                 */
        this.language = language;
        this.time = time;
        this.extraTime = extraTime;
        this.memory = memory;
        this.sandbox = sandbox;
        this.meta = sandbox.getSandboxWorkDir() + "/" + meta;
        this.processes = 55;
        this.additionalConfig = config;
        Config config1 = new Config();
        this.additionalParams = config1.getProfileConfigFile(config);
    }


    ExecutionProfile(ProgramingLanguage language, int time, int extraTime, int memory, String meta,
            Sandbox sandbox, String config, String filename) throws ParseException, FileNotFoundException, IOException {
                /**
                 * Init ExecutionProfile with config param
                 */
        this.language = language;
        this.time = time;
        this.extraTime = extraTime;
        this.memory = memory;
        this.sandbox = sandbox;
        this.meta = sandbox.getSandboxWorkDir() + "/" + meta;
        this.processes = 55;
        this.additionalConfig = config;
        Config config1 = new Config();
        this.additionalParams = config1.getProfileConfigFile(config);
        this.filename = filename;
    }


    public ExecutionProfile(ProgramingLanguage language, int time, int extraTime, int memory, String meta, Sandbox sandbox){
                /**
                 * Init ExecutionProfile with config param
                 */
        this.language = language;
        this.time = time;
        this.extraTime = extraTime;
        this.sandbox = sandbox;
        this.memory = memory;
        this.meta = sandbox.getSandboxWorkDir() + "/" + meta;
        this.processes = 55;
    }

    public void setAdditionalConfig(String config) throws IOException, ParseException {
        this.additionalConfig = config;
        Config config1 = new Config();
        this.additionalParams = config1.getProfileConfigFile(config);
    }

    public void setSandbox(Sandbox sandbox){
        this.sandbox = sandbox;
    }

    ExecutionProfile(ProgramingLanguage language, int time, int extraTime, int memory, String meta,
            String additionalParamsPath, Sandbox sandbox) throws ParseException, FileNotFoundException, IOException {
                /**
                 * Init ExecutionProfile with absoulute path to config file
                 */
        this.language = language;
        this.time = time;
        this.extraTime = extraTime;
        this.memory = memory;
        this.sandbox = sandbox;
        this.meta = sandbox.getSandboxWorkDir() + "/" + meta;
        this.processes = 55;
        this.additionalParams = (JSONObject)jsonParser.parse(new FileReader(additionalParamsPath));
    }

    public HashMap<String, String> objectToMap(){
        HashMap<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("language", this.language.getName());
        returnMap.put("time", Integer.toString(this.time));
        returnMap.put("extra-time", Integer.toString(this.extraTime));
        returnMap.put("mem", Integer.toString(this.memory));
        returnMap.put("meta", this.meta);
        returnMap.put("processes", Integer.toString(this.processes));
        // Get additional params
        for (Iterator iterator = this.additionalParams.keySet().iterator(); iterator.hasNext();){
            String key = (String)iterator.next();
            String value = ((String)this.additionalParams.get(key)).replace("{workdir}", this.sandbox.getSandboxWorkDir());
            returnMap.put(key, value);
        }
        

        return returnMap;
    }

    public HashMap<String, String> toMap() {
        System.out.println("Reach toMap of ExecutionProfile");
        HashMap<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("time", Integer.toString(this.time));
        returnMap.put("extra-time", Integer.toString(this.extraTime));
        returnMap.put("mem", Integer.toString(this.memory));
        returnMap.put("meta", this.meta);
        returnMap.put("_processes", Integer.toString(this.processes));
        for (Iterator iterator = this.additionalParams.keySet().iterator(); iterator.hasNext();){
            String key = (String)iterator.next();
            String value = ((String)this.additionalParams.get(key)).replace("{workdir}", this.sandbox.getSandboxWorkDir());
            returnMap.put(key, value);
        }
        System.out.println(returnMap.toString());
        return returnMap;
    }

    public String toString() {
        HashMap<String, String> map = this.toMap();
        String jsonString;
        try {
            jsonString = new ObjectMapper().writeValueAsString(map);
            return jsonString;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ProgramingLanguage getLanguage(){
        return this.language;
    }

    public JSONObject getAdditionalParams(){
        return this.additionalParams;
    }

    // public String getFileName(){
        
    // }

}