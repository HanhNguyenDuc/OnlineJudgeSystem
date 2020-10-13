package executor;

public class FileStructure{
    int boxId;
    FileStructure(Sandbox sb){
        this.boxId = sb.getBoxId();
    }

    public String getBoxLocation(){
        return String.format("/var/local/lib/isolate/%s/box", this.boxId);
    }
    public String getTestCaseLocation(){
        return this.getBoxLocation() + "testcase";
    }

    public String getMetaFile(ExecutionProfile ep){
        return this.getBoxLocation() + ep.toMap().get("meta");
    }
}