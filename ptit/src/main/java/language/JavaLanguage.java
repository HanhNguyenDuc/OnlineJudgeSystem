package language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaLanguage extends ProgramingLanguage {
    private static final String javaPath = "";

    @Override
    public String getName() {
        return "java";
    }

    @Override
    public Boolean requiredMultithreading() {
        return false;
    }

    @Override
    public String getDefaultSourceExtension(){
        return "/usr/lib/jvm/java-11-openjdk-amd64/bin/javac";
    }

    @Override
    public ArrayList<String> getCompilationCommand(String sourceFilename, String executableFilename) {
        String[] commands = new String[]{
            JavaLanguage.javaPath,
            sourceFilename
        };
        return new ArrayList<String>(Arrays.asList(commands));
    }

    @Override
    public String[] getExecutionCommand(String executableFilename, HashMap<String, String> args) {
        ArrayList<String> commandList = new ArrayList<String>();
        String javaPath = "/usr/lib/jvm/java-11-openjdk-amd64/bin/java";
        commandList.add(javaPath);
        commandList.add(executableFilename);
        for (Map.Entry<String, String> entry: args.entrySet()){
            commandList.add(String.format("%s = %s", entry.getKey(), entry.getValue()));
        }
        return (String[]) commandList.toArray();
    }

    @Override
    public ArrayList<String> getExecutionCommand(String executableFilename) {
        ArrayList<String> commandList = new ArrayList<String>();
        String javaPath = "/usr/lib/jvm/java-11-openjdk-amd64/bin/java";
        commandList.add(javaPath);
        // remove .class from excutableFilename
        executableFilename = executableFilename.replace(".class", "");
        commandList.add(executableFilename);
        commandList.add("-Xmx256M");
        return commandList;
    }

    @Override
    public String[] getValidSourceFilenameAndExecFilename(String sourceCode) {
        String[] returnValues = {
            String.format("src.%s", this.getDefaultSourceExtension()),
            String.format("src.%s", this.getDefaultCompiledExtension())
        };
        return returnValues;
    }

    @Override
    public String getDefaultCompiledExtension(){
        return "class";
    }

    private String findMainClass(String code){
        String regex = "[^{}]*public\\s+(final)?\\s*class\\s+(\\w+).*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(code);
        
        String mainClassField = "mc";
        Boolean isFound = m.find();
        if (isFound){
            mainClassField = code.substring(m.start(), m.end());
        }
        
        String[] mainClassElement = mainClassField.split("\\s+");
        String mainClass = "";
        for (String i : mainClassElement){
            if (i.equals("{")){
                break;
            }
            mainClass = i;
        }
        
        return mainClass;
    }

    @Override
    public String getCodeFileName(String code){
        String mainClass = findMainClass(code);
        return mainClass + ".java";
    }

    @Override
    public String getExecutionFileName(String code){
        String mainClass = findMainClass(code);
        return mainClass + ".class";
    }



}