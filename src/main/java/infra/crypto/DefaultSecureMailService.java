package infra.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import core.service.CryptoService;
import core.service.KeyService;
import core.service.MailService;
import core.service.SecureMailService;

/**
 * Default implementation of SecureMailService
 */
public class DefaultSecureMailService implements SecureMailService {
    private static final Logger logger = Logger.getLogger(DefaultSecureMailService.class.getName());
    
    // Message format markers
    private static final String ENCRYPTED_START = "-----BEGIN ENCRYPTED MESSAGE-----";
    private static final String ENCRYPTED_END = "-----END ENCRYPTED MESSAGE-----";
    private static final String SIGNATURE_START = "-----BEGIN DIGITAL SIGNATURE-----";
    private static final String SIGNATURE_END = "-----END DIGITAL SIGNATURE-----";
    
    private final MailService mailService;
    private final CryptoService cryptoService;
    private final KeyService keyService;
    
    public DefaultSecureMailService(MailService mailService, CryptoService cryptoService, KeyService keyService) {
        this.mailService = mailService;
        this.cryptoService = cryptoService;
        this.keyService = keyService;
    }
    
    @Override
    public void sendSecureMail(String from, String password, String to, String subject, 
                              String body, boolean encrypt, boolean sign) throws Exception {
        
        logger.info("Sending secure mail from " + from + " to " + to + 
                   " (encrypt=" + encrypt + ", sign=" + sign + ")");
        
        String processedBody = body;
        
        // Apply encryption if requested
        if (encrypt) {
            if (!keyService.hasKeyPair(to)) {
                throw new IllegalStateException("Recipient " + to + " does not have a public key for encryption");
            }
            processedBody = encryptMessage(processedBody, to);
            logger.info("Message encrypted for recipient: " + to);
        }
        
        // Apply digital signature if requested
        if (sign) {
            if (!keyService.hasKeyPair(from)) {
                throw new IllegalStateException("Sender " + from + " does not have a private key for signing");
            }
            processedBody = signMessage(processedBody, from);
            logger.info("Message digitally signed by: " + from);
        }
        
        // Send the processed message
        mailService.sendMail(from, password, to, subject, processedBody);
        logger.info("Secure mail sent successfully");
    }
    
    @Override
    public ProcessedMessage processReceivedMessage(String rawMessage, String recipientEmail) throws Exception {
        ProcessedMessage processed = new ProcessedMessage();
        processed.setOriginalMessage(rawMessage);
        
        try {
            String currentContent = rawMessage;
            
            // Check for encryption
            if (isEncrypted(currentContent)) {
                processed.setEncrypted(true);
                currentContent = decryptMessage(currentContent, recipientEmail);
                logger.info("Message decrypted for: " + recipientEmail);
            } else {
                processed.setEncrypted(false);
            }
            
            // Check for digital signature
            if (isSigned(currentContent)) {
                processed.setSigned(true);
                SignatureInfo sigInfo = extractSignatureInfo(currentContent);
                processed.setSenderEmail(sigInfo.senderEmail);
                
                // Verify signature
                String messageContent = removeSignature(currentContent);
                processed.setSignatureVerified(verifySignature(messageContent, sigInfo, processed.getSenderEmail()));
                
                // Remove signature from display content
                currentContent = messageContent;
                
                logger.info("Digital signature verification: " + 
                           (processed.isSignatureVerified() ? "PASSED" : "FAILED") + 
                           " for sender: " + processed.getSenderEmail());
            } else {
                processed.setSigned(false);
                processed.setSignatureVerified(false);
            }
            
            processed.setDecryptedContent(currentContent);
            
        } catch (SecurityException e) {
            logger.severe("Security error processing message: " + e.getMessage());
            processed.setError("Security error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid message format: " + e.getMessage());
            processed.setError("Invalid message format: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error processing secure message: " + e.getMessage());
            processed.setError("Processing failed: " + e.getMessage());
        }
        
        return processed;
    }
    
    private String encryptMessage(String message, String recipientEmail) throws Exception {
        // Generate AES key
        SecretKey aesKey = cryptoService.generateAESKey();
        
        // Encrypt message with AES
        String encryptedContent = cryptoService.encryptAES(message, aesKey);
        
        // Wrap AES key with recipient's public key
        PublicKey recipientPublicKey = keyService.getPublicKey(recipientEmail);
        String wrappedKey = cryptoService.wrapKeyRSA(aesKey, recipientPublicKey);
        
        // Format encrypted message
        StringBuilder encrypted = new StringBuilder();
        encrypted.append(ENCRYPTED_START).append("\n");
        encrypted.append("Recipient: ").append(recipientEmail).append("\n");
        encrypted.append("Wrapped-Key: ").append(wrappedKey).append("\n");
        encrypted.append("Content: ").append(encryptedContent).append("\n");
        encrypted.append(ENCRYPTED_END);
        
        return encrypted.toString();
    }
    
    private String decryptMessage(String encryptedMessage, String recipientEmail) throws Exception {
        // Extract wrapped key and content
        Pattern keyPattern = Pattern.compile("Wrapped-Key: (.+)");
        Pattern contentPattern = Pattern.compile("Content: (.+)");
        
        Matcher keyMatcher = keyPattern.matcher(encryptedMessage);
        Matcher contentMatcher = contentPattern.matcher(encryptedMessage);
        
        if (!keyMatcher.find() || !contentMatcher.find()) {
            throw new IllegalArgumentException("Invalid encrypted message format");
        }
        
        String wrappedKey = keyMatcher.group(1);
        String encryptedContent = contentMatcher.group(1);
        
        // Unwrap AES key with recipient's private key
        PrivateKey recipientPrivateKey = keyService.getPrivateKey(recipientEmail);
        SecretKey aesKey = cryptoService.unwrapKeyRSA(wrappedKey, recipientPrivateKey);
        
        // Decrypt content
        return cryptoService.decryptAES(encryptedContent, aesKey);
    }
    
    private String signMessage(String message, String senderEmail) throws Exception {
        // Sign the message
        PrivateKey senderPrivateKey = keyService.getPrivateKey(senderEmail);
        String signature = cryptoService.sign(message, senderPrivateKey);
        
        // Format signed message
        StringBuilder signed = new StringBuilder();
        signed.append(message).append("\n\n");
        signed.append(SIGNATURE_START).append("\n");
        signed.append("Sender: ").append(senderEmail).append("\n");
        signed.append("Signature: ").append(signature).append("\n");
        signed.append("Timestamp: ").append(new java.util.Date().toString()).append("\n");
        signed.append(SIGNATURE_END);
        
        return signed.toString();
    }
    
    private boolean isEncrypted(String message) {
        return message.contains(ENCRYPTED_START) && message.contains(ENCRYPTED_END);
    }
    
    private boolean isSigned(String message) {
        return message.contains(SIGNATURE_START) && message.contains(SIGNATURE_END);
    }
    
    private SignatureInfo extractSignatureInfo(String message) {
        Pattern senderPattern = Pattern.compile("Sender: (.+)");
        Pattern signaturePattern = Pattern.compile("Signature: (.+)");
        Pattern timestampPattern = Pattern.compile("Timestamp: (.+)");
        
        Matcher senderMatcher = senderPattern.matcher(message);
        Matcher signatureMatcher = signaturePattern.matcher(message);
        Matcher timestampMatcher = timestampPattern.matcher(message);
        
        SignatureInfo info = new SignatureInfo();
        if (senderMatcher.find()) info.senderEmail = senderMatcher.group(1);
        if (signatureMatcher.find()) info.signature = signatureMatcher.group(1);
        if (timestampMatcher.find()) {
            info.timestamp = timestampMatcher.group(1);
            logger.fine("Extracted signature timestamp: " + info.timestamp);
        }
        
        return info;
    }
    
    private String removeSignature(String message) {
        int signatureStart = message.indexOf(SIGNATURE_START);
        if (signatureStart > 0) {
            return message.substring(0, signatureStart).trim();
        }
        return message;
    }
    
    private boolean verifySignature(String message, SignatureInfo sigInfo, String senderEmail) throws Exception {
        if (sigInfo.signature == null || senderEmail == null) {
            return false;
        }
        
        try {
            PublicKey senderPublicKey = keyService.getPublicKey(senderEmail);
            return cryptoService.verify(message, sigInfo.signature, senderPublicKey);
        } catch (Exception e) {
            logger.warning("Signature verification failed: " + e.getMessage());
            return false;
        }
    }
    
    private static class SignatureInfo {
        String senderEmail;
        String signature;
        String timestamp;
    }
}
