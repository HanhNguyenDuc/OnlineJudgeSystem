package worker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String arg[]) throws UnknownHostException, IOException {
        for (int i=0; i<=25; i++){
            Socket client = new Socket("localhost", 1107);
            BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(client.getInputStream());

            // Gửi mã ngôn ngữ: C++ là 1, Java là 2
            bos.write(1);
            bos.flush();
            System.out.println(bis.read());

            // Gửi mã bài (id)
            bos.write(1);
            bos.flush();
            System.out.println(bis.read());

            // Gửi code
            File file = new File("/home/OnlineJudgeSystem/ab.cpp");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            
            
            fis.read(data);
            fis.close();

            bos.write(data);
            bos.flush();
            // int returnCode = bis.read();
            // System.out.println("Return Code: " + Integer.toString(returnCode));
            bis.close();
            bos.close();
        }
        // fis.read(data);

        // Socket client = new Socket("localhost", 1107);
        // BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
        // BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
        
        // bos.write(1);
        // bos.flush();
        // bos.write(1);
        // bos.flush();
        // // System.out.println(bis.read());

        // File file = new File("/home/OnlineJudgeSystem/ab.cpp");
        // FileInputStream fis = new FileInputStream(file);
        // byte[] data = new byte[(int) file.length()];

        // fis.read(data);
        // fis.close();
        // bos.write(data);
        // bos.flush();
        // bis.close();
        // bos.close();
        // fis.read(data);

    }
}