package chewyt;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static final int port = 12345;
    private static final String IP = "localhost";

    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String username;

    public Client(Socket socket, String username, String password) throws IOException {
        this.username = username;
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        // this.oos = new ObjectOutputStream(socket.getOutputStream());
        // this.ois = new ObjectInputStream(socket.getInputStream());

    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

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
        String loginStatus = client.in.readUTF();
        // System.out.println(loginStatus);
        if (loginStatus.equals("true")) {
            isLogin = true;
        } else if (loginStatus.equals("false")) {
            isLogin = false;
            System.out.println("Login failed");
        } else {
            isLogin = false;
            System.out.println("Login failed");
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
                    client.bw.write(input);
                    client.bw.newLine();
                    client.bw.flush();
                    System.out.println("[CLIENT] Your command is " + input);

                    switch (input) {
                        case "1":
                            System.out.println("Please enter deposit amount");
                            amount = scanner.nextFloat();
                            client.out.writeFloat(amount);
                            System.out.println("Deposit successful! Returning to Main Menu...");
                            break;

                        case "2":
                            System.out.println("Please enter withdrawal amount");// bankaccount.withdraw(amount);
                            // System.out.println(bankaccount.getcreateDate());
                            amount = scanner.nextFloat();
                            client.out.writeFloat(amount);
                            System.out.println("Withdrawal successful! Returning to Main Menu...");
                            break;

                        case "3":
                            /*
                             * ArrayList<String> transaction = (ArrayList) client.ois.readObject();
                             * for (String i : transaction) {
                             * System.out.println(i);
                             * }
                             */
                            break;

                        case "4":
                            System.out.println("Current Balance: " + client.in.readFloat());
                            break;

                        case "5":
                            // Break and do nothing
                            break;

                    }

                } else {

                    client.bw.write(input);
                    client.bw.newLine();
                    client.bw.flush();
                    System.out.println("[CLIENT] Your command is wrong. Please try again.");

                }

            } while (!input.equals("5"));
            System.out.println("Have a nice day! " + name);
            break;
        }

        socket.close();
        client.br.close();
        client.bw.close();
        scanner.close();
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
        System.out.println("1)\tDeposit");
        System.out.println("2)\tWithdraw");
        System.out.println("3)\tCheck Transaction");
        System.out.println("4)\tCheck Current Balance");
        System.out.println("5)\tExit");
        System.out.println("Enter the command:");

    }
}
