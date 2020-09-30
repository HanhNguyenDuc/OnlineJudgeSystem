package executor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionResult {
    String stdin, stdout, stderr, exitcode, memory, status, message;

    ExecutionResult(final String stdin, final String stdout, final String stderr, final String exitcode, final String memory, final String status, final String message){
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitcode = exitcode;
        this.memory = memory;
        this.status = status;
        this.message = message;
    }

    ExecutionResult(String stdin, String stdout, String stderr){
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public HashMap<String,String> toMap(){
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("stdin", this.stdin);
        map.put("stdout", this.stdout);
        map.put("stderr", this.stderr);
        map.put("exitcode", this.exitcode);
        map.put("memory", this.memory);
        map.put("status", this.status);
        map.put("message", this.message);
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

    public static ExecutionResult fromExecutionProfile(final ExecutionProfile executionProfile){
        HashMap<String, String> profileMap = executionProfile.toMap();
        
        // read stdin, stdout, stderr, meta
        String stdin = ExecutionResult.readFileContent((String)profileMap.get("stdin"));
        String stdout = ExecutionResult.readFileContent((String)profileMap.get("stdout"));
        String stderr = ExecutionResult.readFileContent((String)profileMap.get("stderr"));
        String meta = ExecutionResult.readFileContent((String)profileMap.get("meta"));
        // read information from meta file
        return null;
    }
}