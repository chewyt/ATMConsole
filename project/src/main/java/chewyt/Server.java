package chewyt;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int port = 12345;
    private ServerSocket server;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;
    private String password;

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

    public void checkCredentials(Socket socket) throws IOException {

        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        username = br.readLine();
        // password = br.readLine();
        System.out.println(username);
        /*
         * HashMap<String, String> userAccounts = new HashMap<>();
         * userAccounts.put(username, password);
         * System.out.println(userAccounts);
         */
    }

    public static void main(String[] args) throws IOException {
        // Setup the server
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket); // Create Server object
        server.startServer();
    }
}
