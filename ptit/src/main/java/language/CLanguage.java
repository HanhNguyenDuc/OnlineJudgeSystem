package language;

import java.util.ArrayList;
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
        return null;
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
    public String[] getCompilationCommand(String sourceFilename, String executableFilename) {
        String[] commands = new String[] {
            CLanguage.gccPath,
            "-DEVAL",
            "-std=gnu11",
            "-o",
            executableFilename,
            sourceFilename
        };
        return commands;
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
    public String[] getExecutionCommand(String executableFilename) {
        String[] returnString = {executableFilename};
        return returnString;
    }

}