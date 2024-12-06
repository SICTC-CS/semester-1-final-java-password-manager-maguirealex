import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.SecretKey;

public class FinalPasswdMan {

    private static final String LOGIN_FILE = ".login"; // File to store encrypted password
    private static final String USER_FILE = ".user"; // File to store user's First and Last Name + username
    private static final String HINT_FILE = ".hint"; // File to store the password hint
    private static final String ACCOUNTS_FILE = "accounts.csv"; // File to store accounts

    private static Set<String> pendingDeletions = new HashSet<>(); // Track accounts marked for deletion
    private static List<String> allAccounts = new ArrayList<>(); // Store all accounts from the file
    private static ArrayList<Account> accounts = new ArrayList<>(); // Account objects

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load the encryption key from the environment
        SecretKey key = null;
        try {
            key = FileenCripsandBloods.loadKeyFromEnv();
            System.out.println("Encryption key loaded from environment.");
        } catch (IllegalStateException e) {
            System.err.println("Error: Encryption key not found in environment variable.");
            System.err.println("Set the environment variable and try again.");
            return;
        }

        // Handle user setup/login
        if (!isFirstRun(key)) {
            if (!authenticate(key)) {
                System.out.println("Login failed. Exiting program.");
                return;
            }
        } else {
            setupUserAndPassword(key);
        }

        // Load accounts from the encrypted file
        loadAccounts(ACCOUNTS_FILE, key);

        boolean run = true;

        while (run) {
            printingMenu();
            int choice = getUserChoice(scanner);
            switch (choice) {
                case 1:
                    addAccount(scanner);
                    break;
                case 2: 
                    viewAccounts();
                    break;
                case 3:
                    viewAccounts();
                    modifyAccount(scanner);
                    break;
                case 4:
                    deleteAccount(scanner);
                    break;
                case 5:
                    saveAndClose(ACCOUNTS_FILE, key);
                    System.exit(0);
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

    private static boolean isFirstRun(SecretKey key) {
        return !Files.exists(Paths.get(LOGIN_FILE)) || !Files.exists(Paths.get(USER_FILE));
    }

    private static void setupUserAndPassword(SecretKey key) {
        Scanner scanner = new Scanner(System.in);

        // Set up username, password, and user details
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        setupPassword(key);

        // Set up user details
        System.out.println("Let's set up your user information:");
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        try {
            String userInfo = username + "," + firstName + "," + lastName;
            byte[] encryptedUserInfo = FileenCripsandBloods.encrypt(userInfo.getBytes(), key);
            Files.write(Paths.get(USER_FILE), encryptedUserInfo);
            System.out.println("User information saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving user information: " + e.getMessage());
        }
    }

    private static void setupPassword(SecretKey key) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please set up a password to secure this program:");

        String password;
        do {
            System.out.print("Enter a password: ");
            password = scanner.nextLine();
            if (!PasswordGood.isValidPassword(password)) {
                System.out.println("Password is invalid. It must contain at least:");
                System.out.println("- One uppercase letter");
                System.out.println("- One special character");
                System.out.println("- One digit");
                System.out.println("- At least 8 characters in length");
            }
        } while (!PasswordGood.isValidPassword(password));

        System.out.print("Enter a hint for your password (optional): ");
        String hint = scanner.nextLine();

        try {
            byte[] encryptedPassword = FileenCripsandBloods.encrypt(password.getBytes(), key);
            Files.write(Paths.get(LOGIN_FILE), encryptedPassword);

            if (!hint.isEmpty()) {
                byte[] encryptedHint = FileenCripsandBloods.encrypt(hint.getBytes(), key);
                Files.write(Paths.get(HINT_FILE), encryptedHint);
            }
            System.out.println("Password and hint saved successfully!");
        } catch (Exception e) {
            System.out.println("Error setting up password: " + e.getMessage());
        }
    }

    private static boolean authenticate(SecretKey key) {
        Scanner scanner = new Scanner(System.in);
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        // Load and display user information
        String username = "";
        try {
            byte[] encryptedUserInfo = Files.readAllBytes(Paths.get(USER_FILE));
            byte[] decryptedUserInfo = FileenCripsandBloods.decrypt(encryptedUserInfo, key);
            String[] userInfo = new String(decryptedUserInfo).split(",");

            if (userInfo.length != 3) {
                System.err.println("Error: User file corrupted. Please reset your setup.");
                return false;
            }

            username = userInfo[0];
            System.out.println("Welcome back, " + userInfo[1] + " " + userInfo[2] + "!");
        } catch (Exception e) {
            System.err.println("Error loading user information: " + e.getMessage());
            return false;
        }

        // Authenticate password
        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Enter your password (or type 'hint' to view the hint): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("hint")) {
                displayHint(key);
                continue;
            }

            try {
                byte[] encryptedPassword = Files.readAllBytes(Paths.get(LOGIN_FILE));
                byte[] decryptedPassword = FileenCripsandBloods.decrypt(encryptedPassword, key);
                String storedPassword = new String(decryptedPassword);

                if (storedPassword.equals(input)) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    attempts++;
                    System.out.println("Incorrect password. Attempts left: " + (MAX_ATTEMPTS - attempts));
                }
            } catch (Exception e) {
                System.out.println("Error during authentication: " + e.getMessage());
                return false;
            }
        }

        System.out.println("Maximum login attempts reached. Exiting program.");
        return false;
    }

    private static void displayHint(SecretKey key) {
        try {
            byte[] encryptedHint = Files.readAllBytes(Paths.get(HINT_FILE));
            byte[] decryptedHint = FileenCripsandBloods.decrypt(encryptedHint, key);
            String hint = new String(decryptedHint);
            System.out.println("Hint: " + hint);
        } catch (Exception e) {
            System.out.println("No hint available.");
        }
    }

    private static void printingMenu() {
        System.out.println("\n=== Password Manager ===");
        System.out.println("1. Add Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Modify Account");
        System.out.println("4. Remove Account");
        System.out.println("5. Save and Close");
        System.out.println("6. Exit");
    }

    private static int getUserChoice(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 6) {
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } else {
                scanner.nextLine(); // Consume invalid input
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
            }
        }
        return choice;
    }


private static void addAccount(Scanner scanner) {
    System.out.println("Enter the category, name, username, and password separated by a new line:");
    String category = scanner.nextLine();
    String name = scanner.nextLine();
    String username = scanner.nextLine();
    String password = "";

    System.out.println("Generate a password? (y/n)");
    String passwordChoice = scanner.nextLine();

    if (passwordChoice.equalsIgnoreCase("y")) {
        password = passJin.generateRandomPassword(12); // Randomly generated password
    } else {
        while (!PasswordGood.isValidPassword(password)) {
            System.out.println("Enter a password:");
            password = scanner.nextLine();
            if (!PasswordGood.isValidPassword(password)) {
                System.out.println("Invalid password. Try again.");
            }
        }
    }

    // Add to the mutable accounts and allAccounts lists
    Account newAccount = new Account(category, name, username, password);
    accounts.add(newAccount); // Add to accounts
    allAccounts.add(String.format("%s,%s,%s,%s", category, name, username, password)); // Add to allAccounts

    System.out.println("Account successfully added.");
}


    private static void viewAccounts() {
        System.out.println("\nYour Accounts\n");
        for (Account account : accounts) {
            System.out.println("Account:" +account.getName());
            System.out.print(account+"\n");
        }
    }

    private static void modifyAccount(Scanner scanner) {
        System.out.println("Enter the name of the account to modify:");
        String name = scanner.nextLine();
    
        Optional<Account> accountOpt = accounts.stream()
                .filter(account -> account.getName().equalsIgnoreCase(name))
                .findFirst();
    
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            System.out.println("Modify details (press Enter to skip a field):");
    
            System.out.print("New Category (" + account.getCategory() + "): ");
            String category = scanner.nextLine();
            if (!category.isEmpty()) {
                account.setCategory(category);
            }
    
            System.out.print("New Name (" + account.getName() + "): ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                account.setName(newName);
            }
    
            System.out.print("New Username (" + account.getUsername() + "): ");
            String username = scanner.nextLine();
            if (!username.isEmpty()) {
                account.setUsername(username);
            }
    
            System.out.print("Change Password? (y/n): ");
            String changePassword = scanner.nextLine();
            if (changePassword.equalsIgnoreCase("y")) {
                System.out.println("Generate a new password? (y/n)");
                String passwordChoice = scanner.nextLine();
    
                if (passwordChoice.equalsIgnoreCase("y")) {
                    String newPassword = passJin.generateRandomPassword(12); // Generate random password
                    account.setPassword(newPassword);
                    System.out.println("Password updated successfully: " + newPassword);
                } else {
                    String newPassword;
                    do {
                        System.out.print("Enter new password: ");
                        newPassword = scanner.nextLine();
                        if (!PasswordGood.isValidPassword(newPassword)) {
                            System.out.println("Invalid password. It must contain at least:");
                            System.out.println("- One uppercase letter");
                            System.out.println("- One special character");
                            System.out.println("- One digit");
                            System.out.println("- At least 8 characters in length");
                        }
                    } while (!PasswordGood.isValidPassword(newPassword));
                    account.setPassword(newPassword);
                    System.out.println("Password updated successfully.");
                }
            }
    
            System.out.println("Account updated successfully.");
        } else {
            System.out.println("Account not found.");
        }
    }
    

    private static void deleteAccount(Scanner scanner) {
        System.out.println("Enter the name of the account to delete:");
        String name = scanner.nextLine();

        boolean removed = accounts.removeIf(account -> account.getName().equalsIgnoreCase(name));
        if (removed) {
            System.out.println("Account successfully deleted.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void saveAndClose(String filename, SecretKey key) {
        try {
            StringBuilder csvContent = new StringBuilder();
            for (String account : allAccounts) {
                csvContent.append(account).append("\n");
            }

            byte[] encryptedContent = FileenCripsandBloods.encrypt(csvContent.toString().getBytes(), key);
            Files.write(Paths.get(filename), encryptedContent);

            System.out.println("Data saved successfully. Goodbye!");
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private static void loadAccounts(String filename, SecretKey key) {
        try {
            if (!Files.exists(Paths.get(filename))) {
                System.out.println("No account file found. Starting fresh.");
                return;
            }
    
            byte[] encryptedContent = Files.readAllBytes(Paths.get(filename));
            byte[] decryptedContent = FileenCripsandBloods.decrypt(encryptedContent, key);
    
            // Use ArrayList to allow modifications
            allAccounts = new ArrayList<>(Arrays.asList(new String(decryptedContent).split("\n")));
    
            // Clear the current accounts list before adding the new ones
            accounts.clear();
    
            for (String line : allAccounts) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    accounts.add(new Account(parts[0], parts[1], parts[2], parts[3]));
                }
            }
    
            System.out.println("Accounts loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }
    

    public static class PasswordGood {

        public static boolean isValidPassword(String password) {
            if (password.length() < 8) {
                return false; // Password is too short
            }

            boolean hasUppercase = false;
            boolean hasSpecialChar = false;
            boolean hasDigit = false;

            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUppercase = true;
                } else if (Character.isDigit(c)) {
                    hasDigit = true;
                } else if (!Character.isLetterOrDigit(c)) {
                    hasSpecialChar = true;
                }

                if (hasUppercase && hasSpecialChar && hasDigit) {
                    return true;
                }
            }

            return false; // If any condition is not met
        }
    }
}
