package core.service;

import java.util.List;
import jakarta.mail.Message;

/**
 * Mail service interface for sending and receiving emails
 */
public interface MailService {
    
    /**
     * Sends an email
     * @param from sender email address
     * @param password sender password
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @throws Exception if sending fails
     */
    void sendMail(String from, String password, String to, String subject, String body) throws Exception;
    
    /**
     * Fetches emails from inbox
     * @param email user email
     * @param password user password
     * @return list of messages
     * @throws Exception if fetching fails
     */
    List<Message> fetchInbox(String email, String password) throws Exception;
    
    /**
     * Tests connection to mail server
     * @param email user email
     * @param password user password
     * @return true if connection successful
     */
    boolean testConnection(String email, String password);
}
