package crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.*;

/**
 * KeyUtil: tiện ích quản lý RSA KeyPair (tạo, lưu, đọc).
 */
public class KeyUtil {

    // Sinh cặp khóa RSA (2048-bit hoặc 4096-bit)
    public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(keySize);
        return gen.generateKeyPair();
    }

    // Lưu PrivateKey ra file
    public static void savePrivateKey(PrivateKey privateKey, String filename) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(spec.getEncoded());
        }
    }

    // Lưu PublicKey ra file
    public static void savePublicKey(PublicKey publicKey, String filename) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(spec.getEncoded());
        }
    }

    // Đọc PrivateKey từ file
    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] bytes = fis.readAllBytes();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    // Đọc PublicKey từ file
    public static PublicKey loadPublicKey(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] bytes = fis.readAllBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }
}
