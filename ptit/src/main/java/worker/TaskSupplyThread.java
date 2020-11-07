package worker;

import java.util.PriorityQueue;

import dao.ProblemDAO;
import entity.Problem;
import entity.Submission;
import executor.ExecutionProfile;
import executor.Executor;
import executor.ExecutorThread;
import language.ProgramingLanguage;
import redis.clients.jedis.Jedis;

public class TaskSupplyThread implements Runnable{
    private PriorityQueue<Submission> pq;
    private final int extraTime = 2;

    public TaskSupplyThread(PriorityQueue<Submission> pq){
        this.pq = pq;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Executor curExecutor = null;
        Jedis jedis = new Jedis("localhost");
        while(true){
            for (int i=0; i<=10; i++){
                System.out.println("judge-worker-" + Integer.toString(i));
                String status = jedis.get("judge-worker-" + Integer.toString(i));
                if (status == null || !status.equals("inused")){
                    curExecutor = new Executor(i);
                    break;
                }
            }
            if (curExecutor != null && this.pq.peek() != null){
                Submission submission = this.pq.poll();
                this.judgeCode(curExecutor, submission);
            }
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
        Thread thread = new Thread(new ExecutorThread(executor, problem, code, problem.getTestPath(), execProfile));
        thread.start();
    }

    public void judgeCode(Executor executor, Submission submission){
        /**
         * This method gonna be update soon
         */
    }
}