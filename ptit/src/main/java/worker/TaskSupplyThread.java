package worker;

import java.util.LinkedList;
import entity.Problem;
import entity.Submission;
import executor.ExecutionProfile;
import executor.Executor;
import executor.ExecutorThread;
import language.ProgramingLanguage;
import redis.clients.jedis.Jedis;

public class TaskSupplyThread implements Runnable{
    private LinkedList<Submission> pq;
    private final int extraTime = 2;

    public TaskSupplyThread(LinkedList<Submission> pq){
        this.pq = pq;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Executor curExecutor = null;
        Jedis jedis = new Jedis("localhost");
        while(true){
            for (int i=0; i<=10; i++){
                // System.out.println("judge-worker-" + Integer.toString(i));
                String status = jedis.get("judge-worker-" + Integer.toString(i));
                
                if (status == null || !status.equals("inused")){
                    Submission submission = this.pq.poll();
                    
                    if (submission == null){
                        continue;
                    }
                    jedis.set("judge-worker-" + Integer.toString(i), "inused");
                    curExecutor = new Executor(i);
                    this.judgeCode(curExecutor, submission);
                    break;
                }
            }
        }
    }

    // public void judgeCode(Executor executor, Submission submission){
    //     ProblemDAO problemDao = new ProblemDAO();
    //     ExecutionProfile execProfile = new ExecutionProfile(
    //         lang,
    //         submission.getProblem().getTimeLimit(),
    //         this.extraTime,
    //         submission.getProblem().getMemLimit(),
    //         "meta.txt"
    //     );
    //     Thread thread = new Thread(new ExecutorThread(executor, submission, submission.getProblem().getTestPath(), execProfile));
    //     thread.start();
    // }

    public void judgeCode(Executor executor, Submission submission){
        Problem problem = submission.getProblem();
        ProgramingLanguage lang = submission.getProgramingLanguage();
        ExecutionProfile execProfile = new ExecutionProfile(
            lang,
            problem.getTimeLimit(),
            this.extraTime,
            problem.getMemLimit(),
            "meta.txt",
            executor.getSandBox()
        );
        Thread thread = new Thread(new ExecutorThread(executor, submission, problem.getTestPath(), execProfile));
        thread.start();
    }
}