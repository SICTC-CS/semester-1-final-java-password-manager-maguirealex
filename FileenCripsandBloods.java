import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.IOException;

public class FileenCripsandBloods {

    private static final String ALGORITHM = "AES";
    private static final String ENV_KEY = "ENCRYPTION_SECRET_KEY"; // Name of the environment variable

    public static SecretKey loadKeyFromEnv() throws IllegalStateException {//Loads the encryption key from the environment variable.
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

    public static byte[] encrypt(byte[] content, SecretKey key) throws Exception {//Encrypts content using the given SecretKey.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(byte[] encryptedContent, SecretKey key) throws Exception {//Decrypts content using the given SecretKey.
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedContent);
    }

    
    public static SecretKey generateKey() throws Exception { 
        // Generates a new encryption key (useful for first-time setup).
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size: 128 bits
        return keyGen.generateKey();
    }

    public static String keyToBase64(SecretKey key) {    //Converts the SecretKey to a Base64 string (for display or logging).
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        try {
            // Generate a new encryption key
            SecretKey key = generateKey();
            String base64Key = keyToBase64(key);
    
            // Print the command for the user to run
            System.out.println("Please run the following command to set the environment variable:");
            System.out.printf("Windows (PowerShell): [Environment]::SetEnvironmentVariable(\"%s\", \"%s\", \"User\")%n", ENV_KEY, base64Key);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}

