package executor;

public class ExecutorThread implements Runnable{

    Executor executor;
    String code, testPath;
    ExecutionProfile execProfile;

    public ExecutorThread(Executor executor, String code, String testPath, ExecutionProfile execProfile){
        this.executor = executor;
        this.code = code;
        this.testPath = testPath;
        this.execProfile = execProfile;
    }

    @Override
    public void run() {
        this.executor.safetyRunCode(code, testPath, execProfile);
    }

}