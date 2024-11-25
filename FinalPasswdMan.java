import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.SecretKey;

public class FinalPasswdMan {

    private static final String LOGIN_FILE = ".login"; // File to store encrypted password
    private static Set<String> pendingDeletions = new HashSet<>(); // Track accounts marked for deletion
    private static List<String> allAccounts = new ArrayList<>();  // Store all accounts from the file
    private static ArrayList<Account> accounts = new ArrayList<>();

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

        // Handle password setup/login
        if (!isFirstRun(key)) {
            if (!authenticate(key)) {
                System.out.println("Incorrect password. Exiting program.");
                return;
            }
        } else {
            setupPassword(key);
        }

        // Load accounts from the encrypted file
        loadAccounts("accounts.csv", key);

        boolean run = true;

        while (run) {
            printingMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    addAccount(scanner);
                    break;
                case 2:
                    viewingAccount();
                    break;
                case 3:
                    modifyAccount(scanner);
                    break;
                case 4:
                    System.out.println("Which account would you like to delete?");
                    String accountName = scanner.nextLine();
                    markForDeletion(accountName);
                    break;
                case 5:
                    saveAndClose("accounts.csv", key);
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

    private static boolean isFirstRun(SecretKey key) {
        return !Files.exists(Paths.get(LOGIN_FILE));
    }

    private static void setupPassword(SecretKey key) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome! Please set up a password to secure this program:");

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

        try {
            byte[] encryptedPassword = FileenCripsandBloods.encrypt(password.getBytes(), key);
            Files.write(Paths.get(LOGIN_FILE), encryptedPassword);
            System.out.println("Password set up successfully!");
        } catch (Exception e) {
            System.out.println("Error setting up password: " + e.getMessage());
        }
    }

    private static boolean authenticate(SecretKey key) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your password:");

        String inputPassword = scanner.nextLine();
        try {
            byte[] encryptedPassword = Files.readAllBytes(Paths.get(LOGIN_FILE));
            byte[] decryptedPassword = FileenCripsandBloods.decrypt(encryptedPassword, key);
            String storedPassword = new String(decryptedPassword);

            return storedPassword.equals(inputPassword);
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    // --- Existing functionality follows ---

    private static void printingMenu() {
        System.out.println("\n=== Password Manager ===");
        System.out.println("1. Add Account");
        System.out.println("2. View Account");
        System.out.println("3. Modify Account");
        System.out.println("4. Remove Account");
        System.out.println("5. Save and Close");
        System.out.println("6. Exit");
    }

    private static void addAccount(Scanner scanner) {
        System.out.println("Enter the category, name, username, and password separated by a new line:");
        String c = scanner.nextLine();
        String n = scanner.nextLine();
        String u = scanner.nextLine();
        String p = "";

        System.out.println("Would you like to generate a password? (y/n)");
        String passwordChoice = scanner.next();
        scanner.nextLine(); // Consume newline

        if (passwordChoice.equalsIgnoreCase("y")) {
            p = passJin.generateRandomPassword(8);
        } else {
            System.out.println("Please type in your password:");
            while (!PasswordGood.isValidPassword(p)) {
                p = scanner.nextLine();
                if (!PasswordGood.isValidPassword(p)) {
                    System.out.println("Invalid password. Try again!");
                }
            }
        }

        Account account = new Account(c, n, u, p);
        accounts.add(account);
        allAccounts.add(String.format("%s,%s,%s,%s", c, n, u, p));
        System.out.println("Account successfully added!");
    }

    private static void viewingAccount() {
        System.out.println("\n------ Account Information ------");
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
        } else {
            for (Account account : accounts) {
                System.out.println(account);
            }
        }
    }

    private static void modifyAccount(Scanner scanner) {
        System.out.println("Which account would you like to modify? (name)");
        for (Account account : accounts) {
            System.out.println(account);
        }

        String name = scanner.nextLine();
        System.out.println("Choose a property to modify:\n1. Category\n2. Name\n3. Username\n4. Password");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newInfo;
        switch (choice) {
            case 1:
                System.out.println("Enter new category:");
                newInfo = scanner.nextLine();
                accounts.stream().filter(a -> a.getName().equals(name)).forEach(a -> a.setCategory(newInfo));
                break;
            case 2:
                System.out.println("Enter new name:");
                newInfo = scanner.nextLine();
                accounts.stream().filter(a -> a.getName().equals(name)).forEach(a -> a.setName(newInfo));
                break;
            case 3:
                System.out.println("Enter new username:");
                newInfo = scanner.nextLine();
                accounts.stream().filter(a -> a.getName().equals(name)).forEach(a -> a.setUsername(newInfo));
                break;
            case 4:
                System.out.println("Enter new password:");
                newInfo = scanner.nextLine();
                accounts.stream().filter(a -> a.getName().equals(name)).forEach(a -> a.setPassword(newInfo));
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }

    public static void saveAndClose(String filename, SecretKey key) {
        updateAllAccounts();
        try {
            StringBuilder csvContent = new StringBuilder();
            for (String line : allAccounts) {
                String accountName = line.split(",")[1]; // Assuming Name is the second column
                if (!pendingDeletions.contains(accountName)) {
                    csvContent.append(line).append("\n");
                }
            }

            byte[] encryptedContent = FileenCripsandBloods.encrypt(csvContent.toString().getBytes(), key);
            Files.write(Paths.get(filename), encryptedContent);

            pendingDeletions.clear();
            System.out.println("Changes saved and file encrypted.");
        } catch (Exception e) {
            System.err.println("Error saving and encrypting file: " + e.getMessage());
        }
    }

    public static void loadAccounts(String filename, SecretKey key) {
        allAccounts.clear();
        accounts.clear();

        try {
            byte[] encryptedContent = Files.readAllBytes(Paths.get(filename));
            byte[] decryptedContent = FileenCripsandBloods.decrypt(encryptedContent, key);
            String content = new String(decryptedContent);

            for (String line : content.split("\n")) {
                allAccounts.add(line);
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    accounts.add(new Account(parts[0], parts[1], parts[2], parts[3]));
                }
            }

            System.out.println("Accounts loaded and decrypted.");
        } catch (Exception e) {
            System.err.println("Error loading and decrypting file: " + e.getMessage());
        }
    }

    private static void updateAllAccounts() {
        allAccounts.clear();
        for (Account account : accounts) {
            allAccounts.add(String.format("%s,%s,%s,%s", account.getCategory(), account.getName(), account.getUsername(), account.getPassword()));
        }
    }

    public static void markForDeletion(String accountName) {
        pendingDeletions.add(accountName);
        System.out.println("Account '" + accountName + "' has been marked for deletion.");
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
