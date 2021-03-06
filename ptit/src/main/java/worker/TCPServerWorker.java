package worker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import dao.ProblemDAO;
import dao.SubmissionDAO;
import entity.Problem;
import entity.Submission;
import language.CLanguage;
import language.JavaLanguage;
import language.ProgramingLanguage;

public class TCPServerWorker {
    LinkedList<Submission> pq;

    public static void main(String args[]) throws IOException {
        TCPServerWorker worker = new TCPServerWorker();
    }

    public TCPServerWorker() throws IOException {
        this.pq = new LinkedList<Submission>();
        Thread thread = new Thread(new TaskSupplyThread(pq));
        thread.start();
        ServerSocket socketServer = new ServerSocket(1107);
        ProblemDAO problemDAO = new ProblemDAO();
        SubmissionDAO submissionDAO = new SubmissionDAO();
        while (true){
            Socket connection = socketServer.accept();
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            int programingLangId = bis.read();
            bos.write(1);
            bos.flush();
            ProgramingLanguage proLang = new CLanguage();
            if (programingLangId == 1){
                proLang = new CLanguage(); 
            }
            else if (programingLangId == 2){
                proLang = new JavaLanguage();
            }
            int problemId = bis.read();
            Problem problem = problemDAO.getProblemById(problemId);
            bos.write(1);
            bos.flush();
            
            // bos.write(1);
            // // bos.flush();
            String code = new String(bis.readAllBytes());
            // bos.write(1);
            // bos.flush();

            Submission submission = submissionDAO.createSubmission(problem, code, proLang);
            submission.setProgramingLanguage(proLang);
            System.out.println(submission.getId());
            pq.add(submission);
        }
    }
}