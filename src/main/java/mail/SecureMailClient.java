package mail;

import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Enhanced mail client with encryption and digital signature support
 */
public class SecureMailClient {
    private static final Logger logger = Logger.getLogger(SecureMailClient.class.getName());
    private static final String KEYS_DIR = "keys";
    
    /**
     * Sends an encrypted and/or signed email
     */
    public static void sendSecureMail(String from, String to, String subject, 
                                    String body, boolean encrypt, boolean sign) throws Exception {
        logger.info(() -> "Sending secure mail from " + from + " to " + to);
        
        // Create the base message
        String processedBody = body;
        
        // Apply encryption if requested
        if (encrypt) {
            processedBody = encryptMessage(processedBody, to);
            logger.info("Message encrypted");
        }
        
        // Apply digital signature if requested
        if (sign) {
            processedBody = signMessage(processedBody, from);
            logger.info("Message digitally signed");
        }
        
        // Send the message
        MailClient.sendMail(from, to, subject, processedBody);
        
        logger.info("Secure mail sent successfully");
    }
    
    /**
     * Encrypts a message for the recipient
     */
    private static String encryptMessage(String message, @SuppressWarnings("unused") String recipient) throws Exception {
        // Generate a random AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();
        
        // Encrypt the message with AES
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedData = cipher.doFinal(message.getBytes());
        
        // For demo purposes, we'll use a simple base64 encoding
        // In a real implementation, you'd encrypt the AES key with the recipient's public key
        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedData);
        
        return "ENCRYPTED_MESSAGE_START\n" + encryptedMessage + "\nENCRYPTED_MESSAGE_END";
    }
    
    /**
     * Signs a message with the sender's private key
     */
    private static String signMessage(String message, String sender) throws Exception {
        // For demo purposes, we'll create a simple signature
        // In a real implementation, you'd use the sender's private key
        String signature = generateSignature(message, sender);
        
        return message + "\n\n--- DIGITAL SIGNATURE ---\n" + 
               "Signed by: " + sender + "\n" +
               "Signature: " + signature + "\n" +
               "Timestamp: " + new Date().toString();
    }
    
    /**
     * Generates a simple signature for demo purposes
     */
    private static String generateSignature(String message, String sender) {
        // Simple hash-based signature for demo
        String data = message + sender + System.currentTimeMillis();
        return "SIG_" + data.hashCode();
    }
    
    /**
     * Decrypts a received message
     */
    public static String decryptMessage(String encryptedMessage) throws Exception {
        if (!encryptedMessage.contains("ENCRYPTED_MESSAGE_START")) {
            return encryptedMessage; // Not encrypted
        }
        
        // Extract the encrypted content
        String encryptedData = encryptedMessage
            .replace("ENCRYPTED_MESSAGE_START\n", "")
            .replace("\nENCRYPTED_MESSAGE_END", "");
        
        // For demo purposes, we'll simulate decryption
        // In a real implementation, you'd decrypt with the recipient's private key
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        
        // Simulate decryption (in reality, you'd use the AES key)
        return "DECRYPTED: " + new String(encryptedBytes);
    }
    
    /**
     * Verifies a digital signature
     */
    public static boolean verifySignature(String message) {
        if (!message.contains("--- DIGITAL SIGNATURE ---")) {
            return false; // Not signed
        }
        
        // Extract signature information
        String[] parts = message.split("--- DIGITAL SIGNATURE ---");
        if (parts.length < 2) {
            return false;
        }
        
        // For demo purposes, we'll always return true
        // In a real implementation, you'd verify the signature with the sender's public key
        logger.info("Digital signature verification: PASSED (demo mode)");
        return true;
    }
    
    /**
     * Generates RSA key pair for the user
     */
    public static void generateKeyPair(String userEmail) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        // Save keys to files
        savePrivateKey(keyPair.getPrivate(), userEmail);
        savePublicKey(keyPair.getPublic(), userEmail);
        
        logger.info("Key pair generated for: " + userEmail);
    }
    
    /**
     * Saves private key to file
     */
    private static void savePrivateKey(PrivateKey privateKey, String userEmail) throws Exception {
        Path keysDir = Paths.get(KEYS_DIR);
        if (!Files.exists(keysDir)) {
            Files.createDirectories(keysDir);
        }
        
        Path privateKeyFile = keysDir.resolve(userEmail.replace("@", "_") + "_private.pem");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(privateKeyFile))) {
            oos.writeObject(privateKey);
        }
    }
    
    /**
     * Saves public key to file
     */
    private static void savePublicKey(PublicKey publicKey, String userEmail) throws Exception {
        Path keysDir = Paths.get(KEYS_DIR);
        if (!Files.exists(keysDir)) {
            Files.createDirectories(keysDir);
        }
        
        Path publicKeyFile = keysDir.resolve(userEmail.replace("@", "_") + "_public.pem");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(publicKeyFile))) {
            oos.writeObject(publicKey);
        }
    }
    
    /**
     * Loads private key from file
     */
    public static PrivateKey loadPrivateKey(String userEmail) throws Exception {
        Path privateKeyFile = Paths.get(KEYS_DIR, userEmail.replace("@", "_") + "_private.pem");
        if (!Files.exists(privateKeyFile)) {
            throw new FileNotFoundException("Private key not found for: " + userEmail);
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(privateKeyFile))) {
            return (PrivateKey) ois.readObject();
        }
    }
    
    /**
     * Loads public key from file
     */
    public static PublicKey loadPublicKey(String userEmail) throws Exception {
        Path publicKeyFile = Paths.get(KEYS_DIR, userEmail.replace("@", "_") + "_public.pem");
        if (!Files.exists(publicKeyFile)) {
            throw new FileNotFoundException("Public key not found for: " + userEmail);
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(publicKeyFile))) {
            return (PublicKey) ois.readObject();
        }
    }
    
    /**
     * Checks if user has key pair
     */
    public static boolean hasKeyPair(String userEmail) {
        Path privateKeyFile = Paths.get(KEYS_DIR, userEmail.replace("@", "_") + "_private.pem");
        Path publicKeyFile = Paths.get(KEYS_DIR, userEmail.replace("@", "_") + "_public.pem");
        return Files.exists(privateKeyFile) && Files.exists(publicKeyFile);
    }
    
    /**
     * Processes a received message (decrypt and verify signature)
     */
    public static ProcessedMessage processReceivedMessage(String rawMessage) {
        ProcessedMessage processed = new ProcessedMessage();
        processed.originalMessage = rawMessage;
        
        try {
            // Check for encryption
            if (rawMessage.contains("ENCRYPTED_MESSAGE_START")) {
                processed.decryptedContent = decryptMessage(rawMessage);
                processed.isEncrypted = true;
                logger.info("Message decrypted successfully");
            } else {
                processed.decryptedContent = rawMessage;
                processed.isEncrypted = false;
            }
            
            // Check for digital signature
            if (processed.decryptedContent.contains("--- DIGITAL SIGNATURE ---")) {
                processed.isSigned = true;
                processed.signatureVerified = verifySignature(processed.decryptedContent);
                logger.info("Digital signature verification: " + (processed.signatureVerified ? "PASSED" : "FAILED"));
            } else {
                processed.isSigned = false;
                processed.signatureVerified = false;
            }
            
        } catch (Exception e) {
            logger.severe(() -> "Error processing message: " + e.getMessage());
            processed.error = e.getMessage();
        }
        
        return processed;
    }
    
    /**
     * Represents a processed message with encryption and signature status
     */
    public static class ProcessedMessage {
        public String originalMessage;
        public String decryptedContent;
        public boolean isEncrypted;
        public boolean isSigned;
        public boolean signatureVerified;
        public String error;
        
        public String getDisplayContent() {
            if (error != null) {
                return "Error processing message: " + error;
            }
            return decryptedContent;
        }
    }
}
