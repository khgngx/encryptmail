package core.service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Key management service interface
 */
public interface KeyService {
    
    /**
     * Generate RSA key pair for user
     * @param email user email
     * @param keySize key size in bits (2048, 4096)
     * @return generated key pair
     */
    KeyPair generateKeyPair(String email, int keySize) throws Exception;
    
    /**
     * Check if user has key pair
     * @param email user email
     * @return true if key pair exists
     */
    boolean hasKeyPair(String email);
    
    /**
     * Get public key for user
     * @param email user email
     * @return public key if exists
     */
    PublicKey getPublicKey(String email) throws Exception;
    
    /**
     * Get private key for user
     * @param email user email
     * @return private key if exists
     */
    PrivateKey getPrivateKey(String email) throws Exception;
    
    /**
     * Save key pair for user
     * @param email user email
     * @param keyPair key pair to save
     */
    void saveKeyPair(String email, KeyPair keyPair) throws Exception;
    
    /**
     * Delete key pair for user
     * @param email user email
     * @return true if deleted
     */
    boolean deleteKeyPair(String email);
    
    /**
     * Export public key as string
     * @param email user email
     * @return public key as Base64 string
     */
    String exportPublicKey(String email) throws Exception;
    
    /**
     * Import public key from string
     * @param email user email
     * @param publicKeyString public key as Base64 string
     */
    void importPublicKey(String email, String publicKeyString) throws Exception;
}
