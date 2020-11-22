package language;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ProgramingLanguage {
    /**
     * 
     * @return Programing Language
     */
    String[] sourceExtension = {};
    public abstract String getName();

    public String[] getSourceExtension(){
        return this.sourceExtension;
    }

    public String getCodeFileName(String code){
        return "filename";
    }
    public String getExecutionFileName(String code){
        return "filename";
    }

    public String getDefaultSourceExtension(){
        String[] sourceExtension = this.getSourceExtension();
        if (sourceExtension.length > 0){
            return sourceExtension[0];
        }
        return null;
    }

    public String getDefaultCompiledExtension(){
        return null;
    }

    public abstract Boolean requiredMultithreading();

    public abstract ArrayList<String> getCompilationCommand(String sourceFilename, String executableFilename);

    /**
     * Được dùng để lấy ra lệnh thực thi ứng với từng ngôn ngữ
     * @param executableFilename
     * @param args
     * @return
     */
    public abstract String[] getExecutionCommand(String executableFilename, HashMap<String, String> args);

    /**
     * Được dùng để lấy ra lệnh thực thi ứng với từng ngôn ngữ
     * @param executableFilename
     * @param args
     * @return
     */
    public abstract ArrayList<String> getExecutionCommand(String executableFilename);

    public abstract String[] getValidSourceFilenameAndExecFilename(String sourceCode);
}