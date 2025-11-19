package core.service;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

/**
 * Cryptographic service interface
 */
public interface CryptoService {
    
    /**
     * Generate AES key for symmetric encryption
     * @return AES secret key
     */
    SecretKey generateAESKey() throws Exception;
    
    /**
     * Encrypt text using AES
     * @param plainText text to encrypt
     * @param key AES key
     * @return encrypted text (Base64)
     */
    String encryptAES(String plainText, SecretKey key) throws Exception;
    
    /**
     * Decrypt text using AES
     * @param cipherText encrypted text (Base64)
     * @param key AES key
     * @return decrypted text
     */
    String decryptAES(String cipherText, SecretKey key) throws Exception;
    
    /**
     * Wrap AES key using RSA public key
     * @param aesKey AES key to wrap
     * @param publicKey RSA public key
     * @return wrapped key (Base64)
     */
    String wrapKeyRSA(SecretKey aesKey, PublicKey publicKey) throws Exception;
    
    /**
     * Unwrap AES key using RSA private key
     * @param wrappedKey wrapped key (Base64)
     * @param privateKey RSA private key
     * @return unwrapped AES key
     */
    SecretKey unwrapKeyRSA(String wrappedKey, PrivateKey privateKey) throws Exception;
    
    /**
     * Sign message using RSA private key
     * @param message message to sign
     * @param privateKey RSA private key
     * @return signature (Base64)
     */
    String sign(String message, PrivateKey privateKey) throws Exception;
    
    /**
     * Verify signature using RSA public key
     * @param message original message
     * @param signature signature (Base64)
     * @param publicKey RSA public key
     * @return true if signature is valid
     */
    boolean verify(String message, String signature, PublicKey publicKey) throws Exception;
}
