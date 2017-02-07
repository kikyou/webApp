package org.baidu.ecom.rpc;

/**
 * Created by baidu on 2017/2/6.
 */

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class PlainRpc {
    static class RpcServer extends Thread {
        public RpcServer(int port) {
            _port = port;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(_port);
                Socket client = serverSocket.accept();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                String request;
                while ((request = reader.readLine()) != null) {
                    System.out.println(String.format("server get message: %s", request));
                    writer.println(request);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {

            }
        }

        private int _port;
    }

    static class RpcClient extends Thread {
        public RpcClient(int port) {
            _port = port;
        }

        @Override
        public void run() {
            try {
                Socket client = new Socket("127.0.0.1", _port);
                //DataOutputStream writer = new DataOutputStream(client.getOutputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                for (int i = 0; i < 10; ++i) {
                    writer.println(String.format("Good luck %d", i + 1));
                    String request = reader.readLine();
                    System.out.println(String.format("client get message: %s", request));
                }

                client.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private int _port;
    }

    public static void main(String[] args) throws Exception{
        RpcServer server = new RpcServer(14567);
        RpcClient client = new RpcClient(14567);

        server.start();
        client.start();

        client.join();
        server.join();
    }
}
