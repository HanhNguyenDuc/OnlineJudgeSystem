package config;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import redis.clients.jedis.Jedis;

public class Config {
    // private static JSONParser jsonParser = new JSONParser();

    public JSONObject getProfileConfigFile(String configFileName) throws IOException, ParseException {
        Jedis jedis = new Jedis("localhost");
        String configKey = "config-file--"+configFileName + "Profile.json";
        while (true){
            if (jedis.get(configKey) == null || !jedis.get(configKey).equals("inused")){
                break;
            }
        }
        JSONObject configJson = null;
        try{
            jedis.set(configKey, "inused");
            URL url = getClass().getResource(configFileName + "Profile.json");
            FileReader fileReader = new FileReader(url.getPath());
            JSONParser jsonParser = new JSONParser();
            configJson = (JSONObject) jsonParser.parse(fileReader);
            fileReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            jedis.set(configKey, "free");
        }
        jedis.close();
        return configJson;
    }

    public JSONObject getResultConfigFile(String configFileName) throws IOException, ParseException {

        Jedis jedis = new Jedis("localhost");
        JSONObject configJson = null;
        String configKey = "config-file--"+configFileName + "Result.json";
        try{
            while (true){
                if (jedis.get(configKey) == null || !jedis.get(configKey).equals("inused")){
                    break;
                }
            }
            jedis.set(configKey, "inused");
            URL url = getClass().getResource(configFileName + "Result.json");
            FileReader fileReader = new FileReader(url.getPath());
            JSONParser jsonParser = new JSONParser();
            configJson = (JSONObject)jsonParser.parse(fileReader);
            fileReader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            jedis.set(configKey, "free");
        }
        jedis.close();
        return configJson;
    }
}