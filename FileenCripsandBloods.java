import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.IOException;

public class FileenCripsandBloods {

    private static final String ALGORITHM = "AES";
    private static final String ENV_KEY = "ENCRYPTION_SECRET_KEY"; // Name of the environment variable

    /**
     * Set the encryption key to the environment variable in Windows.
     * This will only affect the current session.
     */
    private static void setWindowsEnvVariable(String base64Key) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "setx", ENV_KEY, base64Key
        );
        Process process = builder.start();
        try {
            if (process.waitFor() == 0) {
                System.out.println("Environment variable set successfully in Windows.");
            } else {
                System.err.println("Failed to set environment variable in Windows.");
            }
        } catch (InterruptedException e) {
            throw new IOException("Error while waiting for `setx` command to complete.", e);
        }
    }

    /**
     * Loads the encryption key from the environment variable.
     * 
     * @return The loaded SecretKey
     * @throws IllegalStateException If the key is not found or invalid
     */
    public static SecretKey loadKeyFromEnv() throws IllegalStateException {
        String base64Key = System.getenv(ENV_KEY);
        if (base64Key == null) {
            throw new IllegalStateException("Encryption key not found in environment variable.");
        }

        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            return new SecretKeySpec(decodedKey, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Invalid encryption key format in environment variable.", e);
        }
    }

    /**
     * Encrypts content using the given SecretKey.
     */
    public static byte[] encrypt(byte[] content, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    /**
     * Decrypts content using the given SecretKey.
     */
    public static byte[] decrypt(byte[] encryptedContent, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedContent);
    }

    /**
     * Generates a new encryption key (useful for first-time setup).
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size: 128 bits
        return keyGen.generateKey();
    }

    /**
     * Saves the SecretKey to an environment variable as a Base64 string.
     */
    public static void saveKeyToEnv(SecretKey key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.printf("Run this command to set the environment variable:%nexport %s=%s%n", ENV_KEY, encodedKey);
    }

    /**
     * Converts the SecretKey to a Base64 string (for display or logging).
     */
    public static String keyToBase64(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        try {
            System.out.println("Checking environment for an encryption key...");

            // Check if the key exists in the environment
            SecretKey key;
            try {
                key = loadKeyFromEnv();
                System.out.println("Encryption key loaded from environment.");
            } catch (IllegalStateException e) {
                System.out.println("Encryption key not found in environment. Generating a new key...");
                key = generateKey();
                saveKeyToEnv(key); // Save the newly generated key to the environment
            }

           
            // System.out.println("Current Encryption Key (Base64): " + keyToBase64(key)); Display the current encryption key, possible useful for troubleshooting pew pew.

            // Example encryption and decryption
            System.out.println("Now completed!");


        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
