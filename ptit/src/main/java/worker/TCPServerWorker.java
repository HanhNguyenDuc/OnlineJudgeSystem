package worker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.PriorityQueue;

import dao.ProblemDAO;
import entity.Problem;
import entity.Submission;
import language.CLanguage;
import language.ProgramingLanguage;

public class TCPServerWorker {
    PriorityQueue<Submission> pq;

    public static void main(String args[]) throws IOException {
        TCPServerWorker worker = new TCPServerWorker();
    }

    public TCPServerWorker() throws IOException {
        this.pq = new PriorityQueue<Submission>();
        Thread thread = new Thread(new TaskSupplyThread(pq));
        ServerSocket socketServer = new ServerSocket(1107);
        ProblemDAO problemDAO = new ProblemDAO();
        while (true){
            Socket connection = socketServer.accept();
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            
            int problemId = bis.read();
            Problem problem = problemDAO.getProblemById(problemId);
            bos.write(1);
            bos.flush();
            int programingLangId = bis.read();
            ProgramingLanguage proLang = null;
            if (programingLangId == 1){
                proLang = new CLanguage(); 
            }
            bos.write(1);
            bos.flush();
            String code = new String(bis.readAllBytes());


            Submission submission = new Submission(problem, code, proLang);
            pq.add(submission);
        }
    }
}