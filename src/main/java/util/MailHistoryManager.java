package util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages mail history for different folders (Inbox, Sent, Drafts, Trash)
 */
public class MailHistoryManager {
    private static final Logger logger = Logger.getLogger(MailHistoryManager.class.getName());
    private static final String HISTORY_FILE = "data/mail_history.dat";
    
    private static final Map<String, List<MailRecord>> folderHistory = new HashMap<>();
    
    public static class MailRecord implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final String id;
        private final String from;
        private final String to;
        private final String subject;
        private final String body;
        private final Date timestamp;
        private final String folder;
        private final boolean isEncrypted;
        private final boolean isSigned;
        private final boolean isRead;
        private final boolean isImportant;
        
        public MailRecord(String id, String from, String to, String subject, String body, 
                         Date timestamp, String folder, boolean isEncrypted, boolean isSigned, 
                         boolean isRead, boolean isImportant) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
            this.timestamp = timestamp;
            this.folder = folder;
            this.isEncrypted = isEncrypted;
            this.isSigned = isSigned;
            this.isRead = isRead;
            this.isImportant = isImportant;
        }
        
        // Getters
        public String getId() { return id; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getBody() { return body; }
        public Date getTimestamp() { return timestamp; }
        public String getFolder() { return folder; }
        public boolean isEncrypted() { return isEncrypted; }
        public boolean isSigned() { return isSigned; }
        public boolean isRead() { return isRead; }
        public boolean isImportant() { return isImportant; }
        
        public MailRecord markAsRead() {
            return new MailRecord(id, from, to, subject, body, timestamp, folder, 
                                isEncrypted, isSigned, true, isImportant);
        }
    }
    
    static {
        loadHistory();
    }
    
    /**
     * Adds a mail record to the specified folder
     */
    public static void addMailRecord(String folder, MailRecord record) {
        folderHistory.computeIfAbsent(folder, k -> new ArrayList<>()).add(record);
        saveHistory();
        logger.info("Added mail record to " + folder + ": " + record.getSubject());
    }
    
    /**
     * Gets all mail records for a specific folder
     */
    public static List<MailRecord> getFolderMails(String folder) {
        return new ArrayList<>(folderHistory.getOrDefault(folder, new ArrayList<>()));
    }
    
    /**
     * Gets unread mail count for a folder
     */
    public static int getUnreadCount(String folder) {
        return (int) folderHistory.getOrDefault(folder, new ArrayList<>())
                .stream()
                .filter(record -> !record.isRead())
                .count();
    }
    
    /**
     * Marks a mail as read
     */
    public static void markAsRead(String folder, String mailId) {
        List<MailRecord> mails = folderHistory.get(folder);
        if (mails != null) {
            for (int i = 0; i < mails.size(); i++) {
                MailRecord record = mails.get(i);
                if (record.getId().equals(mailId)) {
                    mails.set(i, record.markAsRead());
                    saveHistory();
                    logger.info("Marked mail as read: " + mailId);
                    break;
                }
            }
        }
    }
    
    /**
     * Moves a mail from one folder to another
     */
    public static boolean moveMail(String fromFolder, String toFolder, String mailId) {
        List<MailRecord> sourceMails = folderHistory.get(fromFolder);
        if (sourceMails == null) return false;
        
        MailRecord recordToMove = null;
        for (MailRecord record : sourceMails) {
            if (record.getId().equals(mailId)) {
                recordToMove = record;
                break;
            }
        }
        
        if (recordToMove != null) {
            // Remove from source folder
            sourceMails.removeIf(record -> record.getId().equals(mailId));
            
            // Add to destination folder with updated folder name
            MailRecord movedRecord = new MailRecord(
                recordToMove.getId(),
                recordToMove.getFrom(),
                recordToMove.getTo(),
                recordToMove.getSubject(),
                recordToMove.getBody(),
                recordToMove.getTimestamp(),
                toFolder,
                recordToMove.isEncrypted(),
                recordToMove.isSigned(),
                recordToMove.isRead(),
                recordToMove.isImportant()
            );
            
            addMailRecord(toFolder, movedRecord);
            logger.info("Moved mail from " + fromFolder + " to " + toFolder + ": " + mailId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Deletes a mail from a folder
     */
    public static boolean deleteMail(String folder, String mailId) {
        List<MailRecord> mails = folderHistory.get(folder);
        if (mails != null) {
            boolean removed = mails.removeIf(record -> record.getId().equals(mailId));
            if (removed) {
                saveHistory();
                logger.info("Deleted mail from " + folder + ": " + mailId);
            }
            return removed;
        }
        return false;
    }
    
    /**
     * Creates a new mail record for sending
     */
    public static MailRecord createSentMail(String from, String to, String subject, 
                                          String body, boolean isEncrypted, boolean isSigned) {
        String id = "sent_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        return new MailRecord(id, from, to, subject, body, new Date(), "sent", 
                            isEncrypted, isSigned, true, false);
    }
    
    /**
     * Creates a new draft mail record
     */
    public static MailRecord createDraftMail(String from, String to, String subject, String body) {
        String id = "draft_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        return new MailRecord(id, from, to, subject, body, new Date(), "drafts", 
                            false, false, true, false);
    }
    
    /**
     * Loads mail history from file
     */
    private static void loadHistory() {
        try {
            Path filePath = Paths.get(HISTORY_FILE);
            if (!Files.exists(filePath)) {
                logger.info("No mail history file found, starting with empty history");
                return;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
                @SuppressWarnings("unchecked")
                Map<String, List<MailRecord>> loadedHistory = (Map<String, List<MailRecord>>) ois.readObject();
                folderHistory.putAll(loadedHistory);
                logger.info("Loaded mail history for " + folderHistory.size() + " folders");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to load mail history: " + e.getMessage());
        }
    }
    
    /**
     * Saves mail history to file
     */
    private static void saveHistory() {
        try {
            Path filePath = Paths.get(HISTORY_FILE);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(folderHistory);
                logger.info("Mail history saved to file");
            }
        } catch (Exception e) {
            logger.severe("Failed to save mail history: " + e.getMessage());
        }
    }
    
    /**
     * Clears all history (for testing purposes)
     */
    public static void clearAllHistory() {
        folderHistory.clear();
        saveHistory();
        logger.info("All mail history cleared");
    }
    
    /**
     * Gets folder statistics
     */
    public static Map<String, Integer> getFolderStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, List<MailRecord>> entry : folderHistory.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}
