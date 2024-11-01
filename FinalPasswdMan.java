import java.util.Scanner;

public class FinalPasswdMan {
    public static void main(args[] String){
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        
        while(run)
        
        printingMenu();
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                //add Account
                break;
            case 2:
                //view Account
                break;
            case 3:
                //modify Account
                break;

            case 4:
                //remove account           
                break;

            case 5:
                //save and exit 
                break;
            
            case 6:
                run = false;
                break;
        }


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


}
