package core.model;

import java.time.LocalDateTime;

/**
 * Email domain model
 */
public class Email {
    private Long id;
    private Long accountId;
    private String folder; // inbox, sent, drafts, trash
    private String fromAddr;
    private String toAddr;
    private String subject;
    private String body;
    private String rawMessage;
    private boolean encrypted;
    private boolean signed;
    private boolean signatureOk;
    private boolean read;
    private boolean important;
    private LocalDateTime createdAt;
    private String serverMessageId;
    
    public Email() {}
    
    public Email(Long accountId, String folder, String fromAddr, String toAddr, 
                 String subject, String body) {
        this.accountId = accountId;
        this.folder = folder;
        this.fromAddr = fromAddr;
        this.toAddr = toAddr;
        this.subject = subject;
        this.body = body;
        this.createdAt = LocalDateTime.now();
        this.read = false;
        this.important = false;
        this.encrypted = false;
        this.signed = false;
        this.signatureOk = false;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    
    public String getFromAddr() { return fromAddr; }
    public void setFromAddr(String fromAddr) { this.fromAddr = fromAddr; }
    
    public String getToAddr() { return toAddr; }
    public void setToAddr(String toAddr) { this.toAddr = toAddr; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    
    public String getRawMessage() { return rawMessage; }
    public void setRawMessage(String rawMessage) { this.rawMessage = rawMessage; }
    
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    
    public boolean isSigned() { return signed; }
    public void setSigned(boolean signed) { this.signed = signed; }
    
    public boolean isSignatureOk() { return signatureOk; }
    public void setSignatureOk(boolean signatureOk) { this.signatureOk = signatureOk; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    
    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getServerMessageId() { return serverMessageId; }
    public void setServerMessageId(String serverMessageId) { this.serverMessageId = serverMessageId; }
    
    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", folder='" + folder + '\'' +
                ", fromAddr='" + fromAddr + '\'' +
                ", toAddr='" + toAddr + '\'' +
                ", subject='" + subject + '\'' +
                ", encrypted=" + encrypted +
                ", signed=" + signed +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
}
