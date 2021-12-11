package chewyt;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class BankAccount {
    private static final String path = "BankDB";
    private String name; // FINAL
    private String username; // FINAL
    private final String accountNumber; // FINAL
    private float accountBalance;
    private ArrayList<String> transactions;
    private LocalDate createDate;
    private LocalDate closingDate;

    Random random = new Random();

    BankAccount(String name) {
        // constructor
        this.name = name;
        this.username = name;
        int tempNumber = random.nextInt(100000000) + 1;
        this.accountNumber = String.format("%08d", tempNumber);
        this.accountBalance = 0;
        this.createDate = LocalDate.now();
        this.closingDate = null;
        this.transactions = new ArrayList<String>();
    }

    BankAccount(String name, float initialBalance) {
        // overloaded constructor
        this.name = name;
        this.username = name;
        int tempNumber = random.nextInt(99999998) + 1;
        this.accountNumber = String.format("%08d", tempNumber);

        this.accountBalance = initialBalance;
        this.createDate = LocalDate.now();
        this.closingDate = null;
        this.transactions = new ArrayList<String>();
    }

    BankAccount(String username, String accountNumber) {
        // overloaded constructor - Upload object from existing data

        this.accountNumber = accountNumber;
        this.closingDate = null;
        this.transactions = new ArrayList<String>();

        // How to load the account details from file DB
        createDirectory(path); // create in case DBfolder is empty
        System.out.println("Account number: " + accountNumber);
        loadUser(path, accountNumber); // info loaded plus transactions
    }

    public static void createDirectory(String path) {

        File folder = new File(path);

        try {
            if (folder.mkdir()) {
                System.out.println("[SERVER] Status: BankDB created successfully");
            } else {
                System.out.println("[SERVER] Status: BankDB folder already exists and loaded.");
            }
        } catch (Exception e) {
            System.out.println("[SERVER] ERROR: BankDB Folder not created");
            e.printStackTrace();
        }

    }

    public void loadUser(String path, String accountNumber) {

        try (FileReader userInfo = new FileReader(path + "/" + accountNumber + ".db")) {
            BufferedReader reader = new BufferedReader(userInfo);
            this.username = reader.readLine(); // Line 1: username
            this.name = reader.readLine(); // Line 2: FULL NAME
            this.accountBalance = Float.parseFloat(reader.readLine()); // Line 3: Current balance
            this.createDate = LocalDate.parse(reader.readLine()); // Line 4: Start date
            transactions.clear();
            String line = null;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("A File not found error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An IO error occurred.");
            e.printStackTrace();
        }

    }

    // Getters

    public String getName() {
        return this.name;
    }

    public String getAccNo() {
        return this.accountNumber;
    }

    public float getBalance() {
        return this.accountBalance;
    }

    public LocalDate getcreateDate() {
        return this.createDate;
    }

    public LocalDate getclosingDate() {
        if (this.closingDate == null) {
            System.out.println("The account is active");
        }
        return this.closingDate;
    }

    public void updateDB() {
        try {
            FileWriter fileWriter = new FileWriter((path + "/" + accountNumber + ".db"), false);
            fileWriter.write(username);
            fileWriter.append("\n" + name);
            fileWriter.append("\n" + accountBalance);
            fileWriter.append("\n" + createDate);
            for (String i : transactions) {
                fileWriter.append("\n" + i);
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An IO error occurred.");
            e.printStackTrace();
        } // overwrites file

    }

    // Setters

    public boolean deposit(double cash) {
        if (cash > 0 && this.closingDate == null) {
            this.accountBalance += cash;
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formatDateTime = now.format(format);
            String transactionLine = String.format("Deposited $%.2f at %s", cash, formatDateTime);
            transactions.add(transactionLine);
            updateDB();
            return true;
        } else if (this.closingDate != null) {
            System.out.println("Account already closed.");
            return false;
        } else {
            System.out.println("[SERVER] Transaction invalid. Please enter positive amount.");
            return false;
        }
    }

    public boolean withdraw(double cash) {
        if (cash > 0 && cash < this.accountBalance && this.closingDate == null) {
            this.accountBalance -= cash;
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formatDateTime = now.format(format);
            String transactionLine = String.format("withdrawn $%.2f at %s", cash, formatDateTime);
            transactions.add(transactionLine);
            updateDB();
            return true;
        } else if (this.closingDate != null) {
            System.out.println("Account already closed.");
            return false;
        }

        else if (cash > this.accountBalance) {
            System.out.println("Transaction invalid. Balance lower than amount to be withdrawn.");
            return false;
        }

        else {
            System.out.println("Transaction invalid. Please enter positive amount.");
            return false;
        }
    }

    public ArrayList<String> getTransactions() {
        /*
         * System.out.println("Transactions");
         * System.out.println("-------------------------------------");
         * for (String i : this.transactions) {
         * System.out.println(i);
         * }
         */
        return this.transactions;
    }

    public boolean cancelAccount() {

        if (this.closingDate == null) {
            this.closingDate = LocalDate.now();
            return true;
        } else {
            System.out.println("Account already closed");
            return false;
        }

    }
}
