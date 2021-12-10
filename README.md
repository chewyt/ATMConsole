# ATM Console

Making a server to host database of clients bank account
Bank account class to support basic functions
Client login to check balance, deposit, withdraw, etc...

Steps

Create Server class
-with Clienthandler(socket) and Executor service
Create Client class

- Constructor for client objects (socket, username)

prebuilt --> accounts.txt --> linked to HashMap

username for login if Key exist
password for login if Value is similar

HashMap ok --> login

loop console unless command is closed

second step

create console menu

1. deposit
2. withdraw
3. check balance
4. Transaction
5. exit

client chooses option send command to socket terminal
Server got the option and run OOP for BankAccount.class
depending on option
1 > Deposit cash amount: int // deposit method + update balance + Write to fileDB
2 > Withdraw cash amount : 50,80,100,200,500, others //withdraw method + update balance + Write to fileDB
3 > Check balance // balance method
4 > Transaction //print report method >> write to
