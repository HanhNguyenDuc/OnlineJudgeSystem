package worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import dao.ProblemDAO;
import entity.Problem;
import executor.ExecutionProfile;
import executor.Executor;
import executor.ExecutorThread;
import language.CLanguage;
import language.ProgramingLanguage;
import redis.clients.jedis.Jedis;

public class Worker {
    private final int extraTime = 2;

    public static void main(String args[]) throws IOException {
        // Worker worker = new Worker();
        // // Read file code
        // File file = new File("/home/OnlineJudgeSystem/ab.cpp");
        // FileInputStream fis = new FileInputStream(file);
        // byte[] data = new byte[(int) file.length()];
        // fis.read(data);
        // fis.close();
        // String code = new String(data, "UTF-8");
        // worker.judgeCode(code, new CLanguage(), 1);
        // Jedis jedis = new Jedis("localhost");
        // String value = jedis.get("judge-worker-1");
        // System.out.println(value);
        Worker worker = new Worker();

    }

    Worker() throws IOException {
        // Read file code
        File file = new File("/home/OnlineJudgeSystem/ab.cpp");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String code = new String(data, "UTF-8");
        // trash code above

        Jedis jedis = new Jedis("localhost");

        // find executor is not be used
        Executor curExecutor = null;
        for (int i=0; i<=10; i++){
            System.out.println("judge-worker-" + Integer.toString(i));
            String status = jedis.get("judge-worker-" + Integer.toString(i));
            if (status == null || !status.equals("inused")){
                curExecutor = new Executor(i);
                break;
            }
        }
        if (curExecutor != null){
            this.judgeCode(curExecutor, code, new CLanguage(), 1);
        }
        else{
            System.out.println("No available sandbox");
        }
    }

    public void judgeCode(Executor executor, String code, ProgramingLanguage lang, int problemId){
        ProblemDAO problemDao = new ProblemDAO();
        Problem problem = problemDao.getProblemById(problemId);
        ExecutionProfile execProfile = new ExecutionProfile(
            lang,
            problem.getTimeLimit(),
            this.extraTime,
            problem.getMemLimit(),
            "meta.txt"
        );
        Thread thread = new Thread(new ExecutorThread(executor, code, problem.getTestPath(), execProfile));
        thread.start();
    }
}