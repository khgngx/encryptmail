package core.service;

/**
 * Secure mail service interface for encrypted and signed emails
 */
public interface SecureMailService {
    
    /**
     * Send secure mail with encryption and/or signing
     * @param from sender email
     * @param password sender password
     * @param to recipient email
     * @param subject email subject
     * @param body email body
     * @param encrypt whether to encrypt
     * @param sign whether to sign
     */
    void sendSecureMail(String from, String password, String to, String subject, 
                       String body, boolean encrypt, boolean sign) throws Exception;
    
    /**
     * Process received message (decrypt and verify)
     * @param rawMessage raw message content
     * @param recipientEmail recipient email (for decryption)
     * @return processed message
     */
    ProcessedMessage processReceivedMessage(String rawMessage, String recipientEmail) throws Exception;
    
    /**
     * Represents a processed secure message
     */
    class ProcessedMessage {
        private String originalMessage;
        private String decryptedContent;
        private boolean isEncrypted;
        private boolean isSigned;
        private boolean signatureVerified;
        private String senderEmail;
        private String error;
        
        public ProcessedMessage() {}
        
        public ProcessedMessage(String originalMessage, String decryptedContent, 
                              boolean isEncrypted, boolean isSigned, boolean signatureVerified,
                              String senderEmail) {
            this.originalMessage = originalMessage;
            this.decryptedContent = decryptedContent;
            this.isEncrypted = isEncrypted;
            this.isSigned = isSigned;
            this.signatureVerified = signatureVerified;
            this.senderEmail = senderEmail;
        }
        
        // Getters and setters
        public String getOriginalMessage() { return originalMessage; }
        public void setOriginalMessage(String originalMessage) { this.originalMessage = originalMessage; }
        
        public String getDecryptedContent() { return decryptedContent; }
        public void setDecryptedContent(String decryptedContent) { this.decryptedContent = decryptedContent; }
        
        public boolean isEncrypted() { return isEncrypted; }
        public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }
        
        public boolean isSigned() { return isSigned; }
        public void setSigned(boolean signed) { isSigned = signed; }
        
        public boolean isSignatureVerified() { return signatureVerified; }
        public void setSignatureVerified(boolean signatureVerified) { this.signatureVerified = signatureVerified; }
        
        public String getSenderEmail() { return senderEmail; }
        public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getDisplayContent() {
            if (error != null) {
                return "Error processing message: " + error;
            }
            return decryptedContent != null ? decryptedContent : originalMessage;
        }
        
        @Override
        public String toString() {
            return "ProcessedMessage{" +
                    "isEncrypted=" + isEncrypted +
                    ", isSigned=" + isSigned +
                    ", signatureVerified=" + signatureVerified +
                    ", senderEmail='" + senderEmail + '\'' +
                    ", hasError=" + (error != null) +
                    '}';
        }
    }
}
