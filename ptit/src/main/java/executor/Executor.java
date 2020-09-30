package executor;

import java.util.ArrayList;
import java.util.Arrays;

public class Executor {
    public static ExecutionResult executeCode(String codeExec, Sandbox sandbox, ExecutionProfile executionProfile){
        ArrayList<String> command = new ArrayList<String>(Arrays.asList(executionProfile.language.getDefaultSourceExtension()));
        command.addAll(Arrays.asList(executionProfile.language.getExecutionCommand(codeExec)));
        
        
        ProcessResult execResult = sandbox.safetyExecuteCommand(command, executionProfile);
        
        
        return null;
    }
}