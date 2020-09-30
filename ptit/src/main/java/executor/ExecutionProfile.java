package executor;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import language.ProgramingLanguage;

public class ExecutionProfile {
    /**
     * Được sử dụng để chứa các tham số hợp lệ khi thực thi process (dùng để quản lý process được tạo
     * không có tác dụng thực thi lệnh)
     */
    ProgramingLanguage language;
    private String stdin, stdout, stderr, meta;
    private int time, extraTime, memory;

    ExecutionProfile(ProgramingLanguage language, String stdin, String stdout, String stderr, int time, int extraTime,
            int memory, String meta) {
                /**
                 * meta: path to meta file
                 * stdin: path to stdin
                 * stdout: path to return stdout
                 * stderr: path to return stderr
                 */
        this.language = language;
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
        this.time = time;
        this.extraTime = extraTime;
        this.memory = memory;
        this.meta = meta;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("language", this.language.getName());
        returnMap.put("stdin", this.stdin);
        returnMap.put("stderr", this.stderr);
        returnMap.put("stdout", this.stdout);
        returnMap.put("time", Integer.toString(this.time));
        returnMap.put("extra-time", Integer.toString(this.extraTime));
        returnMap.put("memory", Integer.toString(this.memory));
        returnMap.put("meta", this.meta);
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

}