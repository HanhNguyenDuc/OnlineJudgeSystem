package executor;

import entity.Problem;
import entity.Submission;

public class ExecutorThread implements Runnable {

    Executor executor;
    String code, testPath, solutionCode;
    ExecutionProfile execProfile;
    Problem problem;
    Submission submission;

    public ExecutorThread(Executor executor, Submission submission, String testPath, ExecutionProfile execProfile){
        this.executor = executor;
        this.submission = submission;
        this.testPath = testPath;
        this.execProfile = execProfile;
    }

    @Override
    public void run() {
        this.executor.safetyRunCode(submission, testPath, execProfile);
    }

}