package crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * CryptoUtils: hỗ trợ mã hóa + chữ ký số.
 * - AES: mã hóa/giải mã nội dung mail.
 * - RSA: bọc/giải bọc AES key, ký/verify.
 */
public class CryptoUtils {

    // Tạo AES key 256-bit
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        return gen.generateKey();
    }

    // Mã hóa AES (Base64 output)
    public static String encryptAES(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Giải mã AES
    public static String decryptAES(String cipherText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }

    // Bọc AES key bằng RSA PublicKey
    public static String wrapKeyRSA(SecretKey key, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        byte[] wrapped = cipher.wrap(key);
        return Base64.getEncoder().encodeToString(wrapped);
    }

    // Giải bọc AES key bằng RSA PrivateKey
    public static SecretKey unwrapKeyRSA(String wrappedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.UNWRAP_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(wrappedKey);
        return (SecretKey) cipher.unwrap(decoded, "AES", Cipher.SECRET_KEY);
    }

    // Tạo chữ ký số bằng RSA
    public static String sign(String message, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = sig.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    // Xác minh chữ ký số
    public static boolean verify(String message, String signatureBase64, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(message.getBytes("UTF-8"));
        byte[] signature = Base64.getDecoder().decode(signatureBase64);
        return sig.verify(signature);
    }
}
