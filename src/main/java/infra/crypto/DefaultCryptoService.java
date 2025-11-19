package infra.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import core.service.CryptoService;
import crypto.CryptoUtils;

/**
 * Default implementation of CryptoService using existing CryptoUtils
 */
public class DefaultCryptoService implements CryptoService {
    private static final Logger logger = Logger.getLogger(DefaultCryptoService.class.getName());
    
    @Override
    public SecretKey generateAESKey() throws Exception {
        logger.fine("Generating new AES key");
        return CryptoUtils.generateAESKey();
    }
    
    @Override
    public String encryptAES(String plainText, SecretKey key) throws Exception {
        return CryptoUtils.encryptAES(plainText, key);
    }
    
    @Override
    public String decryptAES(String cipherText, SecretKey key) throws Exception {
        return CryptoUtils.decryptAES(cipherText, key);
    }
    
    @Override
    public String wrapKeyRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        return CryptoUtils.wrapKeyRSA(aesKey, publicKey);
    }
    
    @Override
    public SecretKey unwrapKeyRSA(String wrappedKey, PrivateKey privateKey) throws Exception {
        return CryptoUtils.unwrapKeyRSA(wrappedKey, privateKey);
    }
    
    @Override
    public String sign(String message, PrivateKey privateKey) throws Exception {
        return CryptoUtils.sign(message, privateKey);
    }
    
    @Override
    public boolean verify(String message, String signature, PublicKey publicKey) throws Exception {
        return CryptoUtils.verify(message, signature, publicKey);
    }
}
