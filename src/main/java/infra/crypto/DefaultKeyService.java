package infra.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Logger;

import core.service.KeyService;
import crypto.KeyUtil;

/**
 * Default implementation of KeyService using existing KeyUtil and file storage
 */
public class DefaultKeyService implements KeyService {
    private static final Logger logger = Logger.getLogger(DefaultKeyService.class.getName());
    private static final String KEYS_DIR = "keys";
    
    @Override
    public KeyPair generateKeyPair(String email, int keySize) throws Exception {
        KeyPair keyPair = KeyUtil.generateRSAKeyPair(keySize);
        saveKeyPair(email, keyPair);
        logger.info("Generated " + keySize + "-bit RSA key pair for: " + email);
        return keyPair;
    }
    
    @Override
    public boolean hasKeyPair(String email) {
        try {
            String privateKeyFile = getPrivateKeyFileName(email);
            String publicKeyFile = getPublicKeyFileName(email);
            
            return java.nio.file.Files.exists(java.nio.file.Paths.get(privateKeyFile)) &&
                   java.nio.file.Files.exists(java.nio.file.Paths.get(publicKeyFile));
        } catch (Exception e) {
            logger.warning("Error checking key pair existence for " + email + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public PublicKey getPublicKey(String email) throws Exception {
        String publicKeyFile = getPublicKeyFileName(email);
        return KeyUtil.loadPublicKey(publicKeyFile);
    }
    
    @Override
    public PrivateKey getPrivateKey(String email) throws Exception {
        String privateKeyFile = getPrivateKeyFileName(email);
        return KeyUtil.loadPrivateKey(privateKeyFile);
    }
    
    @Override
    public void saveKeyPair(String email, KeyPair keyPair) throws Exception {
        // Ensure keys directory exists
        java.nio.file.Path keysDir = java.nio.file.Paths.get(KEYS_DIR);
        if (!java.nio.file.Files.exists(keysDir)) {
            java.nio.file.Files.createDirectories(keysDir);
        }
        
        String privateKeyFile = getPrivateKeyFileName(email);
        String publicKeyFile = getPublicKeyFileName(email);
        
        KeyUtil.savePrivateKey(keyPair.getPrivate(), privateKeyFile);
        KeyUtil.savePublicKey(keyPair.getPublic(), publicKeyFile);
        
        logger.info("Saved key pair for: " + email);
    }
    
    @Override
    public boolean deleteKeyPair(String email) {
        try {
            String privateKeyFile = getPrivateKeyFileName(email);
            String publicKeyFile = getPublicKeyFileName(email);
            
            boolean deletedPrivate = java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(privateKeyFile));
            boolean deletedPublic = java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(publicKeyFile));
            
            if (deletedPrivate || deletedPublic) {
                logger.info("Deleted key pair for: " + email);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.severe("Error deleting key pair for " + email + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String exportPublicKey(String email) throws Exception {
        PublicKey publicKey = getPublicKey(email);
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    @Override
    public void importPublicKey(String email, String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(keyBytes);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        
        // Save only the public key
        String publicKeyFile = getPublicKeyFileName(email);
        KeyUtil.savePublicKey(publicKey, publicKeyFile);
        
        logger.info("Imported public key for: " + email);
    }
    
    private String getPrivateKeyFileName(String email) {
        String sanitizedEmail = email.replace("@", "_").replace(".", "_");
        return KEYS_DIR + "/" + sanitizedEmail + "_private.key";
    }
    
    private String getPublicKeyFileName(String email) {
        String sanitizedEmail = email.replace("@", "_").replace(".", "_");
        return KEYS_DIR + "/" + sanitizedEmail + "_public.key";
    }
}
