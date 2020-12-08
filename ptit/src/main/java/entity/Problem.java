package entity;

import executor.ExecutionProfile;
import executor.Sandbox;
import language.CLanguage;

public class Problem {
    private int id, timeLimit, memLimit, numSolved;
    private String name, statement, solution, testPath;
    
    public Problem(int id, String name, String statement, String solution, int numSolved,
            String testPath, int timeLimit, int memLimit){
        this.id = id;
        this.timeLimit = timeLimit;
        this.memLimit = memLimit;
        this.numSolved = numSolved;
        this.name = name;
        this.statement = statement;
        this.solution = solution;
        this.testPath = testPath;
    }

    public ExecutionProfile getSolutionProfile(Sandbox sandbox){
        ExecutionProfile execProfile = new ExecutionProfile(
            new CLanguage(),
            this.timeLimit,
            2,
            this.memLimit,
            "meta.txt",
            sandbox
        );

        return execProfile;
    }

    public int getId(){
        return this.id;
    }

    public int getTimeLimit(){
        return this.timeLimit;
    }

    public int getMemLimit(){
        return this.memLimit;
    }

    public int getNumSolved(){
        return this.numSolved;
    }

    public String getName(){
        return this.name;
    }

    public String getStatement(){
        return this.statement;
    }

    public String getSolution(){
        return this.solution;
    }

    public String getTestPath(){
        return this.testPath;
    }
}