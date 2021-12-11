package chewyt;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static final int port = 12345;
    private static final String IP = "localhost";

    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private BufferedReader keyboard;
    private String username;

    public Client(Socket socket, String username, String password) throws IOException {
        this.username = username;
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.keyboard = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void main(String[] args) throws UnknownHostException, IOException {

        boolean isLogin = false;

        System.out.println("Welcome to Chew Bank");
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket(IP, port);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        isLogin = false;

        // The moment socket created --> Server create CLienthandler obj
        Client client = new Client(socket, username, password);
        client.login(username, password);

        // check login status
        String loginStatus = client.br.readLine();
        if (loginStatus.equals("true")) {
            isLogin = true;
        } else if (loginStatus.equals("false")) {
            isLogin = false;
            System.out.println("Login failed");
        }

        int command = 0;
        String input;

        while (socket.isConnected() && isLogin) {
            do {
                menuScreen(username);
                input = client.keyboard.readLine();

                if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")
                        || input.equals("5")) {
                    client.bw.write(input);
                    client.bw.newLine();
                    client.bw.flush();
                    System.out.println("[CLIENT] Your command is " + input);
                } else {

                    client.bw.write(input);
                    client.bw.newLine();
                    client.bw.flush();
                    System.out.println("[CLIENT] Your command is wrong. Please try again BITCH.");

                }

            } while (!input.equals("5"));
            System.out.println("Have a nice day! " + username);
            break;
        }

        socket.close();
        client.br.close();
        client.bw.close();
        System.exit(0);

    }

    public void login(String username, String password) throws IOException {
        bw.write(username);
        bw.newLine();
        bw.flush();
        bw.write(password);
        bw.newLine();
        bw.flush();
    }

    public static void menuScreen(String user) {
        System.out.println("========================================");
        System.out.println("Welcome to your Bank account, " + user);
        System.out.println("========================================");
        System.out.println("1)\tdeposit");
        System.out.println("2)\twithdraw");
        System.out.println("3)\ttransaction");
        System.out.println("4)\tcheck balance");
        System.out.println("5)\tExit");
        System.out.println("Enter the command:");

    }
}
