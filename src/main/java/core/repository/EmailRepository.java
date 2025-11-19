package core.repository;

import java.util.List;
import java.util.Optional;

import core.model.Email;

/**
 * Repository interface for Email operations
 */
public interface EmailRepository {
    
    /**
     * Save or update an email
     * @param email email to save
     * @return saved email with ID
     */
    Email save(Email email);
    
    /**
     * Find email by ID
     * @param id email ID
     * @return email if found
     */
    Optional<Email> findById(Long id);
    
    /**
     * Find emails by account and folder
     * @param accountId account ID
     * @param folder folder name (inbox, sent, drafts, trash)
     * @return list of emails
     */
    List<Email> findByAccountAndFolder(Long accountId, String folder);
    
    /**
     * Find unread emails count
     * @param accountId account ID
     * @param folder folder name
     * @return count of unread emails
     */
    int countUnreadByAccountAndFolder(Long accountId, String folder);
    
    /**
     * Mark email as read
     * @param emailId email ID
     */
    void markAsRead(Long emailId);
    
    /**
     * Mark email as important
     * @param emailId email ID
     * @param important important flag
     */
    void markAsImportant(Long emailId, boolean important);
    
    /**
     * Move email to different folder
     * @param emailId email ID
     * @param newFolder new folder name
     */
    void moveToFolder(Long emailId, String newFolder);
    
    /**
     * Delete email by ID
     * @param id email ID
     * @return true if deleted
     */
    boolean deleteById(Long id);
    
    /**
     * Find emails by server message ID
     * @param accountId account ID
     * @param serverMessageId server message ID
     * @return email if found
     */
    Optional<Email> findByServerMessageId(Long accountId, String serverMessageId);
    
    /**
     * Get folder statistics
     * @param accountId account ID
     * @return map of folder name to email count
     */
    java.util.Map<String, Integer> getFolderStats(Long accountId);
}
