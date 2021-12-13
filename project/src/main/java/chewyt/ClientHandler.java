package chewyt;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clienthandlers = new ArrayList<>();
    private static List<String[]> userAccount = new ArrayList<String[]>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private DataInputStream in;
    private DataOutputStream out;

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
            this.in = new DataInputStream(socket.getInputStream()); // for getting float variables from client
            this.out = new DataOutputStream(socket.getOutputStream()); // for sending float variables to client

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
            reader.close(); // is it redundant?? @chuk

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
            if (!userLogin(username, password)) {

                System.out.println("Login failed");
                bw.write("false");
                bw.newLine();
                bw.flush();
                closeEverything(socket, br, bw);

            } else {

                System.out.println("Bank Account number retrieved for " + username + " : " + bankAccountNo);
                bw.write("true"); // Work similarly good
                bw.newLine();
                bw.flush();

                String input = "";
                float amount;
                BankAccount bankaccount = new BankAccount(username, bankAccountNo);
                out.writeUTF(bankaccount.getName());
                out.flush();
                while (!input.equals("5")) {
                    input = br.readLine();
                    System.out.println("[SERVER] Client's input(" + username + "): " + input);

                    switch (input) {
                        case "1":
                            System.out.println("[SERVER] Commmand: " + input + " - DEPOSIT");
                            amount = in.readFloat();
                            bankaccount.deposit(amount);
                            System.out.println("[SERVER] Deposit of " + amount + " is successful.");

                            ;
                            break;

                        case "2":
                            System.out.println("[SERVER] Commmand: " + input + " - WITHDRAW");// bankaccount.withdraw(amount);
                            amount = in.readFloat();
                            bankaccount.withdraw(amount);
                            break;

                        case "3":
                            System.out.println("[SERVER] Commmand: " + input + " - TRANSACTION");// bankaccount.getTransaction();
                            ArrayList<String> transactions = bankaccount.getTransactions();
                            System.out.println("Size of transaction list: " + transactions.size());
                            String streamData = "";
                            for (String string : transactions) {
                                streamData += string + ",";
                            }
                            streamData = streamData.substring(0, streamData.length() - 1);
                            bw.write(streamData);
                            bw.newLine();
                            bw.flush();
                            break;

                        case "4":
                            System.out.println("[SERVER] Commmand: " + input + " - BALANCE");// bankaccount.checkBalance();
                            out.writeFloat(bankaccount.getBalance());
                            out.flush();
                            break;

                        case "5":
                            closeEverything(socket, br, bw);
                            break;

                        default:
                            System.out.println("[SERVER] Wrong line command");
                            break;
                    }

                }
                System.out.println("[SERVER] " + username + " has logged off from ATM console.");
                System.out.println("[SERVER] Server ready. Listening for client...");
            }

        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }

    }
}
