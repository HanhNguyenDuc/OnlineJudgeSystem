package config;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    private static JSONParser jsonParser = new JSONParser();

    public JSONObject getProfileConfigFile(String configFileName) throws IOException, ParseException {
        URL url = getClass().getResource(configFileName + "Profile.json");
        FileReader fileReader = new FileReader(url.getPath());
        JSONObject configJson = (JSONObject) jsonParser.parse(fileReader);

        return configJson;
    }

    public JSONObject getResultConfigFile(String configFileName) throws IOException, ParseException {
        URL url = getClass().getResource(configFileName + "Result.json");
        FileReader fileReader = new FileReader(url.getPath());
        JSONObject configJson = (JSONObject)jsonParser.parse(fileReader);

        return configJson;
    }
}