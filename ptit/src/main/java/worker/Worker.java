package worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import dao.ProblemDAO;
import entity.Problem;
import executor.ExecutionProfile;
import executor.Executor;
import executor.ExecutorThread;
import language.CLanguage;
import language.ProgramingLanguage;

public class Worker {
    private final int extraTime = 2;
    private Executor executor;

    public static void main(String args[]) throws IOException {
        Worker worker = new Worker();
        // Read file code
        File file = new File("/home/OnlineJudgeSystem/ab.cpp");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String code = new String(data, "UTF-8");
        worker.judgeCode(code, new CLanguage(), 1);
    }

    Worker(){
        Random ranObj = new Random();
        this.executor = new Executor(ranObj.nextInt(999));
    }

    public void judgeCode(String code, ProgramingLanguage lang, int problemId){
        ProblemDAO problemDao = new ProblemDAO();
        Problem problem = problemDao.getProblemById(problemId);
        ExecutionProfile execProfile = new ExecutionProfile(
            lang,
            problem.getTimeLimit(),
            this.extraTime,
            problem.getMemLimit(),
            "meta.txt"
        );
        Thread thread = new Thread(new ExecutorThread(this.executor, code, problem.getTestPath(), execProfile));
        thread.start();
    }
}