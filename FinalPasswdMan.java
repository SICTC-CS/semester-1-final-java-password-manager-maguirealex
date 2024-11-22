import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

public class FinalPasswdMan {

    private static Set<String> pendingDeletions = new HashSet<>(); // To track accounts marked for deletion
    private static List<String> allAccounts = new ArrayList<>();  // To store all accounts from the file

    // Mark an account for deletion
    
    
    private static ArrayList<Account> accounts = new ArrayList<>();
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        loadAccounts("accounts.csv");
        
        boolean run = true;

        while(run){
        
        printingMenu();
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                //add Account //alex
                addAccount(scanner);
                break;
            case 2:
                viewingAccount(); //maguire
                break;
            case 3:
                modifyAccount(scanner); //alex
                break;

            case 4:
                System.out.println("Which account would you like to delete?");
                String accountName = scanner.nextLine();
                markForDeletion(accountName); // Add the account name to pending deletions
            break;

            case 5:
                saveAndClose("accounts.csv");;
                System.exit(0);
            
            case 6:
                run = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            }
        }
        scanner.close();
    }
    









    private static void printingMenu() {
        System.out.println("\n=== Password Manager ===");
        System.out.println("1. Add Account");
        System.out.println("2. View Account");
        System.out.println("3. Modify Account");
        System.out.println("4. Remove Account");
        System.out.println("5. Save and Close");
        System.out.println("6. Exit");
    }

    private static void addAccount(Scanner scanner){
        
        //ask the user for details
        System.out.println("Enter the category, name, username, password seperated by a new line");
        String c = scanner.nextLine();
        String n = scanner.nextLine();
        String u = scanner.nextLine();
        String p = "";
        System.out.println("Would you like to generate a password? y/n");
        String passwordChoice = scanner.next();
       
            if (passwordChoice.equalsIgnoreCase("y")){
                p = passJin.generateRandomPassword(8);// the length of the password is dynamic 
            }else{
                System.out.println("Please type in your password!");
                while (PasswordGood.isValidPassword(p) == false) {
                    p = scanner.next();
                    System.out.println("Invalid password try again!");

                    // if (PasswordGood.isValidPassword(p) == false) {
                    //     System.out.println("Invalid password try again!");
                    // }
                }
            }
        
                

        //create the account
        Account a = new Account(c,n,u,p);
        accounts.add(a);
        System.out.print(a.toString());
        String csvLine = c + "," + n + "," + u + "," + p; // Add to allAccounts as CSV
        allAccounts.add(csvLine);
        System.out.println("\nAccount successfully added!");



    }



    private static void modifyAccount(Scanner scanner){
        ///////////    THIS IS WHEN SWITCH CASES < Conditional Statements   /////////////////////////
        int type;
        String name;
        String newInfo;
            //choose the account
            System.out.println("Which account? (name)");
            for(Account account:accounts){
                System.out.println(account);
            }
            name = scanner.next();
            //choose the property
            System.out.println("Choose a property: \n\t1. Category\t2. Name\t 3. Username\t4. Password\t");
            type = scanner.nextInt();
            switch (type){
                case 1:
                    //update info
                    newInfo = scanner.next();
                    //find an object in an ArrayList
                    for(Account account:accounts){
                        if (account.getCategory().equals(name)){
                            //set the new data
                            account.setCategory(newInfo);
                        }
                    }
                    break;
                case 2:
                    //update info
                    newInfo = scanner.next();
                    //find an object in an ArrayList
                    for(Account account:accounts){
                        if (account.getName().equals(name)){
                            //set the new data
                            account.setName(newInfo);
                        }
                    }
                    break;
                case 3:
                    //update info
                    newInfo = scanner.next();
                    //find an object in an ArrayList
                    for(Account account:accounts){
                        if (account.getUsername().equals(name)){
                            //set the new data
                            account.setUsername(newInfo);
                        }
                    }
                    break;
                case 4:
                    //update info
                    newInfo = scanner.next();
                    //find an object in an ArrayList
                    for(Account account:accounts){
                        if (account.getPassword().equals(name)){
                            //set the new data
                            account.setPassword(newInfo);
                        }
                    }
                    break;
            }
    }







    private static void viewingAccount() {
        System.out.println("\n------ Account Information ------");
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
        } else {
            for (Account account : accounts) {
                System.out.println(account); // Use the Account class's toString method
            }
        }
    }
    


    public static void saveAndClose(String filename) {
        updateAllAccounts(); // Sync allAccounts with accounts list
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : allAccounts) {
                // Exclude accounts marked for deletion
                String accountName = line.split(",")[1]; // Assuming Name is the second column
                if (!pendingDeletions.contains(accountName)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            pendingDeletions.clear(); // Clear pending deletions after saving
            System.out.println("Changes saved and file closed.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private static void updateAllAccounts() {
        allAccounts.clear();
        for (Account account : accounts) {
            String csvLine = account.getCategory() + "," + account.getName() + "," + account.getUsername() + "," + account.getPassword();
            allAccounts.add(csvLine);
        }
    }    
    public static void markForDeletion(String accountName) {
        pendingDeletions.add(accountName);
        System.out.println("Account '" + accountName + "' has been marked for deletion.");
    }

    public static void loadAccounts(String filename) {
        allAccounts.clear();
        accounts.clear(); // Clear in-memory accounts to avoid duplicates
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allAccounts.add(line); // Add the raw CSV line
    
                // Parse the line and add an Account object to the accounts list
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Account account = new Account(parts[0], parts[1], parts[2], parts[3]);
                    accounts.add(account);
                }
            }
            System.out.println("Accounts loaded from file.");
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }


    public class PasswordGood {

    // Method to validate password
    public static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false; // Password is too short
        }

        boolean hasUppercase = false;
        boolean hasSpecialChar = false;
        boolean hasDigit = false;

        // Loop through each character in the password
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }

            // If all conditions are met, we can stop checking
            if (hasUppercase && hasSpecialChar && hasDigit) {
                return true;
            }
        }

        return false; // If any condition is not met
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (isValidPassword(password)) {
            System.out.println("Password is valid!");
        } else {
            System.out.println("Password is invalid. It must contain at least:");
            System.out.println("- One uppercase letter");
            System.out.println("- One special character");
            System.out.println("- One digit");
            System.out.println("- At least 8 characters in length");
        }

        scanner.close();
    }
}
    
}
    


