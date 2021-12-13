package chewyt;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static final int port = 12345;
    private static final String IP = "localhost";

    private Socket socket;
    private static BufferedWriter bw;
    private static BufferedReader br;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public Client(Socket socket, String username, String password) throws IOException {
        this.username = username;
        this.socket = socket;
        Client.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Client.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

    }

    public static void main(String[] args)
            throws UnknownHostException, IOException, ClassNotFoundException, EOFException {

        boolean isLogin = false;
        String input;
        float amount;

        // LOGIN
        System.out.println("Welcome to Chew Bank");
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket(IP, port);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        // System.out.println("A");

        // The moment socket created --> Server create CLienthandler obj
        Client client = new Client(socket, username, password);
        client.login(username, password);
        // System.out.println("B");

        // check login status
        String loginStatus = Client.br.readLine();
        // System.out.println(loginStatus);
        if (loginStatus.equals("true")) {
            isLogin = true;
        } else if (loginStatus.equals("false")) {
            isLogin = false;
            System.out.println("Login failed");
            client.closeEverything(socket, br, bw);

        } else {
            isLogin = false;
            System.out.println("Login failed");
            client.closeEverything(socket, br, bw);
        }
        // System.out.println("C");
        // System.out.println(socket.isConnected());
        // System.out.println(isLogin);

        String name = client.in.readUTF();

        // Show MENU SCREEN and USER INPUT
        while (socket.isConnected() && isLogin) {
            do {

                menuScreen(name);
                input = scanner.nextLine();

                if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4")
                        || input.equals("5")) {
                    Client.bw.write(input);
                    Client.bw.newLine();
                    Client.bw.flush();
                    System.out.println("[CLIENT] Your command is " + input);

                    switch (input) {
                        case "1":
                            System.out.println("Please enter deposit amount");
                            amount = scanner.nextFloat();
                            client.out.writeFloat(amount);
                            client.out.flush();
                            scanner.nextLine();
                            System.out.println("Deposit successful! Returning to Main Menu...");
                            break;

                        case "2":
                            System.out.println("Please enter withdrawal amount");// bankaccount.withdraw(amount);
                            // System.out.println(bankaccount.getcreateDate());
                            amount = scanner.nextFloat();
                            client.out.writeFloat(amount);
                            client.out.flush();
                            scanner.nextLine();
                            System.out.println("Withdrawal successful! Returning to Main Menu...");
                            break;

                        case "3":

                            String streamData = Client.br.readLine();
                            String[] transaction = streamData.split(",");
                            System.out.println(
                                    "============================\n\tTRANSACTION\n============================");
                            for (String i : transaction) {
                                System.out.println(i);
                            }

                            break;

                        case "4":
                            System.out.println("Current Balance: " + client.in.readFloat());
                            break;

                        case "5":
                            // Break and do nothing
                            break;

                    }

                } else {

                    Client.bw.write(input);
                    Client.bw.newLine();
                    Client.bw.flush();
                    System.out.println("[CLIENT] Your command is wrong. Please try again.");

                }

            } while (!input.equals("5"));
            System.out.println("Have a nice day! " + name);
            break;
        }

    }

    public void login(String username, String password) throws IOException {
        bw.write(username);
        bw.newLine();
        bw.flush();
        bw.write(password);
        bw.newLine();
        bw.flush();
    }

    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) throws IOException {
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void menuScreen(String user) {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        System.out.println("========================================");
        System.out.println("Welcome to your Bank account, " + user);
        System.out.println("========================================");
        System.out.println("1)\tDeposit");
        System.out.println("2)\tWithdraw");
        System.out.println("3)\tCheck Transaction");
        System.out.println("4)\tCheck Current Balance");
        System.out.println("5)\tExit");
        System.out.println("Enter the command:");

    }
}
