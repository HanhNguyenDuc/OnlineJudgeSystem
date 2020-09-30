package executor;

public class ProcessResult {
    private String stdout, stderr;
    private int exitcode;

    ProcessResult(String stdout, String stderr, int exitcode){
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitcode = exitcode;
    }
    ProcessResult(){
        this.stdout = "";
        this.stderr = "";
        this.exitcode = 0;
    }

    public int getExitcode(){
        return this.exitcode;
    }
    public String getStderr(){
        return this.stderr;
    }
    public String getStdout(){
        return this.stdout;
    }
}