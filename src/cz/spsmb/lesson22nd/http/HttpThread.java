package cz.spsmb.lesson22nd.http;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Scanner;

public class HttpThread extends Thread {

    private Socket socket;

    public HttpThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String httpRequest = readHttpRequest(socket.getInputStream());
            System.out.println(httpRequest);
            // TODO Domaci ukol
            String filePath = getFilePath(httpRequest);
            String fileContent = getFileContent(filePath);
            HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder()
                    .setHttpVersion("HTTP/1.1")
                    .setStatusCode(200)
                    .addHeaderParam("Content-type", "text/html");
            httpResponseBuilder.setBody(fileContent);

            System.out.println(httpResponseBuilder.build());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            outputStreamWriter.write(httpResponseBuilder.build());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            System.out.println(getFileContent(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readHttpRequest(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            while (inputStream.available() > 0) {
                stringBuilder.append((char) inputStream.read());
            }
        } while (stringBuilder.length() == 0);

        return stringBuilder.toString();
    }

    private String getFilePath(String httpRequest){

        String[] requestField = httpRequest.split("\r\n");

        char[] charUrl = requestField[0].toCharArray();

        String stringUrl = "";

        for (int i = 4; i < charUrl.length-9; i++){
            stringUrl = stringUrl + charUrl[i];
        }

        return stringUrl;
    }

    private String getFileContent(String filePath) throws FileNotFoundException {

        File file = new File("http" + filePath + ".html");
        Scanner sc = new Scanner(file);

        String returnString = "";

        while (sc.hasNextLine()){
            returnString = returnString + sc.nextLine();
        }

        return returnString;
    }
}
