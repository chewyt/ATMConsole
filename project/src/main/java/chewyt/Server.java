package chewyt;

import java.io.*;
import java.net.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int port = 12345;
    private ServerSocket server;

    // Constructor
    public Server(ServerSocket server) {
        this.server = server;
    }

    void startServer() {

        try {
            System.out.println("[SERVER] Server ready. Listening for client...");
            ExecutorService threadPool = Executors.newFixedThreadPool(3);
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("A client connected. Asking for user credentials..");
                // checkCredentials(socket);

                ClientHandler clienthandler = new ClientHandler(socket);
                // Code for auto running and scheduling of threads from Threadpool by Executor
                // Service
                threadPool.submit(clienthandler);
            }

        } catch (Exception e) {
            closeServerSocket();
        }

    }

    public void closeServerSocket() {

        try {
            if (server != null) {
                server.close();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Setup the server
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket); // Create Server object
        server.startServer();
    }
}
