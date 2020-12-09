package entity;

import language.ProgramingLanguage;

public class Submission {
    private int id;
    private String code;
    private String judgeStatus;
    private Problem problem;
    private String judgeReport;
    private ProgramingLanguage lang;
    private String judgeErr = null;
    private String verdict;
    

    public String getVerdict(){
        return this.verdict;
    }

    public void setVerdict(String verdict){
        this.verdict = verdict;
    }

    public Submission(Problem problem,String code, String judgeStatus){
        this.problem = problem;
        this.code = code;
        this.judgeStatus = judgeStatus;
    }

    public Submission(Problem problem, String code){
        this.problem = problem;
        this.code = code;
    }

    public Submission(Problem problem, String code, ProgramingLanguage lang){
        this.problem = problem;
        this.code = code;
        this.lang = lang;
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

    public void setProgramingLanguage(ProgramingLanguage lang){
        this.lang = lang;
    }

    public ProgramingLanguage getProgramingLanguage(){
        return this.lang;
    }

    public String getJudgeErr(){
        return this.judgeErr;
    }

    public void setJudgeErr(String judgeErr){
        this.judgeErr = judgeErr;
    }
}