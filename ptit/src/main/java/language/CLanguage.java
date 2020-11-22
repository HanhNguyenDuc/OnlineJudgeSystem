package language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CLanguage extends ProgramingLanguage {
    private static final String gccPath = "";

    @Override
    public String getName() {
        return "c";
    }

    @Override
    public String[] getSourceExtension() {
        String[] returnStrings = new String[] {"cpp", "c", "cxx", "c++", "cc"};
        return returnStrings;
    }

    @Override
    public String getDefaultSourceExtension() {
        return "/usr/bin/x86_64-linux-gnu-gcc-7";
    }

    @Override
    public String getDefaultCompiledExtension() {
        return "o";
    }

    @Override
    public Boolean requiredMultithreading() {
        return false;
    }

    @Override
    public ArrayList<String> getCompilationCommand(String sourceFilename, String executableFilename) {
        String[] commands = new String[] {
            CLanguage.gccPath,
            "-DEVAL",
            "-std=gnu++11",
            "-o",
            executableFilename,
            sourceFilename,
            "-lstdc++"
        };
        return new ArrayList<String>(Arrays.asList(commands));
        // return commands;
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
    public String[] getExecutionCommand(String executableFilename, HashMap<String, String> args) {
        ArrayList<String> commandList = new ArrayList<String>();
        commandList.add(executableFilename);
        for (Map.Entry<String, String> entry: args.entrySet()){
            commandList.add(String.format("%s = %s", entry.getKey(), entry.getValue()));
        }
        return (String[]) commandList.toArray();
    }

    @Override
    public ArrayList<String> getExecutionCommand(String executableFilename) {
        String [] commands = new String[]{
            "./" + executableFilename
        };
        return new ArrayList<String>(Arrays.asList(commands));
    }

    @Override
    public String getCodeFileName(String code){
        return "filename.cpp";
    }

    public String getExecutionFileName(String code){
        return "filename.out";
    }
}