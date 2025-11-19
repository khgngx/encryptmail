package infra.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import core.model.Email;
import core.repository.EmailRepository;
import core.service.MailHistoryService;

/**
 * Default implementation of MailHistoryService
 */
public class DefaultMailHistoryService implements MailHistoryService {
    private static final Logger logger = Logger.getLogger(DefaultMailHistoryService.class.getName());
    
    private final EmailRepository emailRepository;
    
    public DefaultMailHistoryService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    
    @Override
    public Email saveEmail(Email email) {
        logger.fine("Saving email: " + email.getSubject());
        return emailRepository.save(email);
    }
    
    @Override
    public List<Email> getEmailsByFolder(Long accountId, String folder) {
        return emailRepository.findByAccountAndFolder(accountId, folder);
    }
    
    @Override
    public int getUnreadCount(Long accountId, String folder) {
        return emailRepository.countUnreadByAccountAndFolder(accountId, folder);
    }
    
    @Override
    public void markAsRead(Long emailId) {
        emailRepository.markAsRead(emailId);
    }
    
    @Override
    public void moveToFolder(Long emailId, String folder) {
        emailRepository.moveToFolder(emailId, folder);
    }
    
    @Override
    public boolean deleteEmail(Long emailId) {
        return emailRepository.deleteById(emailId);
    }
    
    @Override
    public Map<String, Integer> getFolderStats(Long accountId) {
        return emailRepository.getFolderStats(accountId);
    }
    
    @Override
    public Email createSentEmail(Long accountId, String from, String to, String subject, 
                                String body, boolean encrypted, boolean signed) {
        Email email = new Email(accountId, "sent", from, to, subject, body);
        email.setEncrypted(encrypted);
        email.setSigned(signed);
        email.setSignatureOk(signed); // Assume our own signatures are valid
        email.setRead(true); // Sent emails are always read
        
        return saveEmail(email);
    }
    
    @Override
    public Email createDraftEmail(Long accountId, String from, String to, String subject, String body) {
        Email email = new Email(accountId, "drafts", from, to, subject, body);
        email.setRead(true); // Drafts are always read
        
        return saveEmail(email);
    }
}
