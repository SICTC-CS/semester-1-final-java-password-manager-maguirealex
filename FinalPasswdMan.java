import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class FinalPasswdMan {
      private static ArrayList<Account> accounts = new ArrayList<>();
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
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
                //modify Account
                break;

            case 4:
                //remove account           
                break;

            case 5:
                writeToCSV("accounts.csv");
                break;
            
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
        System.out.println("Enter the name, username, password, category seperated by a new line");
        String n = scanner.nextLine();
        String u = scanner.nextLine();
        String p = "";
        System.out.println("Would you like to generate a password? y/n");
        String passwordChoice = scanner.next();
       
            if (passwordChoice.equalsIgnoreCase("y")){
                p = passJin.generateRandomPassword(8);// the length of the password is dynamic 
            }else{
                System.out.println("Please type in your password!");
                p = scanner.nextLine();
            }
        
        String c = "Loo0oOser";        

        //create the animal
        Account a = new Account(n,u,p,c);
        accounts.add(a);
        System.out.print(a.toString());

        System.out.println("Account successfully added!");
    }











    private static void viewingAccount(){
             System.out.println("\n------ Account Information ------"); 
            for(Account account:accounts){ 
                System.out.println(account);  
                       
        }
         
    
        
    }

    private static void writeToCSV(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {  // 'true' enables append mode
            File file = new File(filename); // Check if the file already exists and is non-empty
            if (file.length() == 0) {
                writer.write("Name,Username,Password,Category\n"); // Write the header only if the file is empty
            }
    
            // Write account data
            for (Account account : accounts) {
                String csvLine = account.getName() + "," +
                                 account.getUsername() + "," +
                                 account.getPassword() + "," +
                                 account.getCategory();
                writer.write(csvLine + "\n");
            }
    
            System.out.println("Accounts saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error while writing to CSV: " + e.getMessage());
        }
    }
    
}
