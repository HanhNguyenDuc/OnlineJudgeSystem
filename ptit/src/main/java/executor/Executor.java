package executor;

import java.util.ArrayList;
import java.util.Arrays;

public class Executor {

    

    public static ExecutionResult executeCode(ArrayList<String> codeExec, Sandbox sandbox, ExecutionProfile executionProfile){
        ArrayList<String> command = new ArrayList<String>(Arrays.asList(executionProfile.language.getDefaultSourceExtension()));
        command.addAll(codeExec);
        System.out.println(command.toString());

        ProcessResult execResult = sandbox.safetyExecuteCommand(command, executionProfile);
        
        return null;
    }
}