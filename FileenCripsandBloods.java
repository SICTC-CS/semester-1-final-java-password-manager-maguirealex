import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FileenCripsandBloods {
    private static final String ALGORITHM = "AES";
    private static final String ENV_KEY = "ENCRYPTION_SECRET_KEY";

    // Encrypt content
    public static byte[] encrypt(byte[] content, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    // Decrypt content
    public static byte[] decrypt(byte[] encryptedContent, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedContent);
    }

    // Generate a new SecretKey (use this for first-time setup)
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size: 128 bits
        return keyGen.generateKey();
    }

    // Save SecretKey to environment variable
    public static void saveKeyToEnv(SecretKey key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.printf("Run this command to set the environment variable:%nexport %s=%s%n", ENV_KEY, encodedKey);
    }

    // Load SecretKey from environment variable
    public static SecretKey loadKeyFromEnv() {
        String encodedKey = System.getenv(ENV_KEY);
        if (encodedKey == null || encodedKey.isEmpty()) {
            throw new IllegalStateException("Environment variable " + ENV_KEY + " is not set.");
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Convert SecretKey to Base64 string (for display or logging)
    public static String keyToBase64(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        try {
          
            System.out.println("Checking environment for an encryption key...");  // First-time setup: Generate and save a new key

            
            SecretKey key; // Check if the key exists in the environment
            try {
                key = loadKeyFromEnv();
                System.out.println("Encryption key loaded from environment.");
            } catch (IllegalStateException e) {
                System.out.println("Encryption key not found in environment. Generating a new key...");
                key = generateKey();
                saveKeyToEnv(key);
            }

            
            System.out.println("Current Encryption Key (Base64): " + keyToBase64(key));// Display the loaded/generated key (for testing purposes only; remove in production)

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
