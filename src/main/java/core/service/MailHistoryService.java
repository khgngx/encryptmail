package core.service;

import java.util.List;
import java.util.Map;

import core.model.Email;

/**
 * Mail history service interface
 */
public interface MailHistoryService {
    
    /**
     * Save email to history
     * @param email email to save
     * @return saved email
     */
    Email saveEmail(Email email);
    
    /**
     * Get emails by folder
     * @param accountId account ID
     * @param folder folder name
     * @return list of emails
     */
    List<Email> getEmailsByFolder(Long accountId, String folder);
    
    /**
     * Get unread count by folder
     * @param accountId account ID
     * @param folder folder name
     * @return unread count
     */
    int getUnreadCount(Long accountId, String folder);
    
    /**
     * Mark email as read
     * @param emailId email ID
     */
    void markAsRead(Long emailId);
    
    /**
     * Move email to folder
     * @param emailId email ID
     * @param folder new folder
     */
    void moveToFolder(Long emailId, String folder);
    
    /**
     * Delete email
     * @param emailId email ID
     * @return true if deleted
     */
    boolean deleteEmail(Long emailId);
    
    /**
     * Get folder statistics
     * @param accountId account ID
     * @return folder stats
     */
    Map<String, Integer> getFolderStats(Long accountId);
    
    /**
     * Create sent email record
     * @param accountId account ID
     * @param from sender
     * @param to recipient
     * @param subject subject
     * @param body body
     * @param encrypted is encrypted
     * @param signed is signed
     * @return created email
     */
    Email createSentEmail(Long accountId, String from, String to, String subject, 
                         String body, boolean encrypted, boolean signed);
    
    /**
     * Create draft email record
     * @param accountId account ID
     * @param from sender
     * @param to recipient
     * @param subject subject
     * @param body body
     * @return created email
     */
    Email createDraftEmail(Long accountId, String from, String to, String subject, String body);
}
