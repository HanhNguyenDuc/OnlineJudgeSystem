package entity;

public class Submission {
    private int id;
    private String code;
    private String judgeStatus;
    private Problem problem;
    private String judgeReport;

    public Submission(Problem problem,String code, String judgeStatus){
        this.problem = problem;
        this.code = code;
        this.judgeStatus = judgeStatus;
    }

    public Submission(Problem problem, String code){
        this.problem = problem;
        this.code = code;
    }

    public Submission(int id, Problem problem, String code){
        this.problem = problem;
        this.code = code;
        this.id = id;
    }

    public String getCode(){
        return this.code;
    }

    public String getJudgeStatus(){
        return this.judgeStatus;
    }

    public Problem getProblem(){
        return this.problem;
    }

    public int getId(){
        return this.id;
    }

    public void setCode(String code){
        this.code = code;
    }

    public void setJudgeStatus(String judgeStatus){
        this.judgeStatus = judgeStatus;
    }

    public void setProblem(Problem problem){
        this.problem = problem;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setJudgeReport(String judgeReport){
        this.judgeReport = judgeReport;
    }

    public String getJudgeReport(){
        return this.judgeReport;
    }
}