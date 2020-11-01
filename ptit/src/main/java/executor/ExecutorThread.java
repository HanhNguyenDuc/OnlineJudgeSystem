package executor;

import entity.Problem;

public class ExecutorThread implements Runnable {

    Executor executor;
    String code, testPath, solutionCode;
    ExecutionProfile execProfile;
    Problem problem;

    public ExecutorThread(Executor executor, Problem problem, String code, String testPath, ExecutionProfile execProfile){
        this.executor = executor;
        this.code = code;
        this.testPath = testPath;
        this.execProfile = execProfile;
        this.problem = problem;
    }

    @Override
    public void run() {
        this.executor.safetyRunCode(problem, code, testPath, execProfile);
    }

}