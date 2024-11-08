import java.security.SecureRandom;

public class passJin {//get it, the name hahahahaha.

    // Define the set of characters to choose from
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!#$%&?";
    
    // Combine all the characters into a single string
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;

    // SecureRandom is recommended for cryptographic purposes
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        // Generate a random password with length 12
        String password = generateRandomPassword(12);
        System.out.println("Generated Password: " + password);
    }

    // Method to generate a random password
    public static String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);

        // Ensure the password includes at least one lowercase, uppercase, digit, and special character
        password.append(getRandomCharacter(LOWERCASE));  // At least one lowercase
        password.append(getRandomCharacter(UPPERCASE));  // At least one uppercase
        password.append(getRandomCharacter(DIGITS));     // At least one digit
        password.append(getRandomCharacter(SPECIAL_CHARS)); // At least one special character

        // Fill the remaining length of the password with random characters from the full set
        for (int i = password.length(); i < length; i++) {
            password.append(getRandomCharacter(ALL_CHARACTERS));
        }

        // Shuffle the password to ensure randomness
        return shuffleString(password.toString());
    }

    // Method to get a random character from a given string
    private static char getRandomCharacter(String str) {
        int index = random.nextInt(str.length());  // Get a random index
        return str.charAt(index);  // Return the character at that index
    }

    // Method to shuffle the string for randomness
    private static String shuffleString(String str) {
        char[] characters = str.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap characters[i] with the element at random index j
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
