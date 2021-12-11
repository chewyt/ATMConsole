package chewyt;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clienthandlers = new ArrayList<>();
    private static ArrayList<String[]> userAccount = new ArrayList<String[]>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;
    private String password;
    private String bankAccountNo;

    ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.username = br.readLine();
            System.out.println("[SERVER] Username entered for Socket ID " + socket.getPort() + " : " + username);
            this.password = br.readLine();
            System.out.println("[SERVER] Password entered for Socket ID " + socket.getPort() + " : " + password);
            // Adding client to arraylist, to know how many users are logged in to server
            clienthandlers.add(this);

        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }
    }

    public void removeClientHandler() {
        clienthandlers.remove(this);
        // System.out.println("How many active threads after user left: " +
        // Thread.activeCount());
    }

    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {
        removeClientHandler();
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
    }

    public boolean userLogin(String username, String password) throws IOException {

        // Finding file existence, else create new
        File usersList = new File("usersList.txt");
        if (!usersList.exists()) {
            usersList.createNewFile();
        }

        // Loading from DB to static ArrayList
        userAccount.clear();
        try (FileReader DBfile = new FileReader(usersList)) {
            BufferedReader reader = new BufferedReader(DBfile);
            reader.readLine(); // Ignoring first line
            String line = null;
            while ((line = reader.readLine()) != null) {
                // System.out.println("line: " + line);
                // System.out.println(line.split(",")[0]);
                userAccount.add(line.split(","));
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("A File not found error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An IO error occurred.");
            e.printStackTrace();
        }

        // Checking user login credential against ArrayList
        // System.out.println("userAccount: " + userAccount);

        for (String[] user : userAccount) {
            // System.out.println(user[0] + " = " + username + " ?");
            if (user[0].equals(username)) {
                System.out.println("User exists");
                if (user[1].equals(password)) {
                    System.out.println("Password correct");
                    bankAccountNo = user[2];
                    return true;
                } else {
                    System.out.println("Password incorrect.");
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {

        try {
            if (userLogin(username, password)) {
                System.out.println("Bank Account number retrieved for " + username + " : " + bankAccountNo);
                bw.write("true");
                bw.newLine();
                bw.flush();

            } else {

                System.out.println("Login failed");
                bw.write("false");
                bw.newLine();
                bw.flush();
                closeEverything(socket, br, bw);
            }

            String input = "";
            BankAccount bankaccount = new BankAccount(username, bankAccountNo);
            while (!input.equals("5")) {

                input = br.readLine();
                System.out.println("[SERVER] Client's input(" + username + "): " + input);

                switch (input) {
                    case "1":
                        System.out.println("[SERVER] Commmand: " + input);// bankaccount.deposit(amount);
                        System.out.println(bankaccount.getAccNo());
                        ;
                        break;

                    case "2":
                        System.out.println("[SERVER] Commmand: " + input);// bankaccount.withdraw(amount);
                        System.out.println(bankaccount.getcreateDate());
                        break;

                    case "3":
                        System.out.println("[SERVER] Commmand: " + input);// bankaccount.getTransaction();
                        System.out.println(bankaccount.getBalance());
                        break;

                    case "4":
                        System.out.println("[SERVER] Commmand: " + input);// bankaccount.checkBalance();
                        System.out.println(bankaccount.getName());
                        break;

                    case "5":
                        // Break and do nothing
                        break;

                    default:
                        System.out.println("[SERVER] Wrong line command");
                        break;
                }

            }
            System.out.println("[SERVER] " + username + " has logged off from ATM console.");
            System.out.println("[SERVER] Server ready. Listening for client...");
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }

    }
}
