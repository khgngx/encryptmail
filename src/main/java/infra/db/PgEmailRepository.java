package infra.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import core.model.Email;
import core.repository.EmailRepository;

/**
 * PostgreSQL implementation of EmailRepository
 */
public class PgEmailRepository implements EmailRepository {
    private static final Logger logger = Logger.getLogger(PgEmailRepository.class.getName());
    
    @Override
    public Email save(Email email) {
        if (email.getId() == null) {
            return insert(email);
        } else {
            return update(email);
        }
    }
    
    private Email insert(Email email) {
        String sql = """
            INSERT INTO emails (account_id, folder, from_addr, to_addr, subject, body, raw_message,
                               is_encrypted, is_signed, signature_ok, is_read, is_important, server_message_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, email.getAccountId());
            stmt.setString(2, email.getFolder());
            stmt.setString(3, email.getFromAddr());
            stmt.setString(4, email.getToAddr());
            stmt.setString(5, email.getSubject());
            stmt.setString(6, email.getBody());
            stmt.setString(7, email.getRawMessage());
            stmt.setBoolean(8, email.isEncrypted());
            stmt.setBoolean(9, email.isSigned());
            stmt.setBoolean(10, email.isSignatureOk());
            stmt.setBoolean(11, email.isRead());
            stmt.setBoolean(12, email.isImportant());
            stmt.setString(13, email.getServerMessageId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    email.setId(rs.getLong("id"));
                    email.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            
            logger.info("Email saved: " + email.getSubject());
            return email;
            
        } catch (SQLException e) {
            logger.severe("Failed to insert email: " + e.getMessage());
            throw new RuntimeException("Failed to save email", e);
        }
    }
    
    private Email update(Email email) {
        String sql = """
            UPDATE emails 
            SET folder = ?, from_addr = ?, to_addr = ?, subject = ?, body = ?, raw_message = ?,
                is_encrypted = ?, is_signed = ?, signature_ok = ?, is_read = ?, is_important = ?,
                server_message_id = ?
            WHERE id = ?
            """;
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email.getFolder());
            stmt.setString(2, email.getFromAddr());
            stmt.setString(3, email.getToAddr());
            stmt.setString(4, email.getSubject());
            stmt.setString(5, email.getBody());
            stmt.setString(6, email.getRawMessage());
            stmt.setBoolean(7, email.isEncrypted());
            stmt.setBoolean(8, email.isSigned());
            stmt.setBoolean(9, email.isSignatureOk());
            stmt.setBoolean(10, email.isRead());
            stmt.setBoolean(11, email.isImportant());
            stmt.setString(12, email.getServerMessageId());
            stmt.setLong(13, email.getId());
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Email not found for update: " + email.getId());
            }
            
            logger.info("Email updated: " + email.getSubject());
            return email;
            
        } catch (SQLException e) {
            logger.severe("Failed to update email: " + e.getMessage());
            throw new RuntimeException("Failed to update email", e);
        }
    }
    
    @Override
    public Optional<Email> findById(Long id) {
        String sql = "SELECT * FROM emails WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmail(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.severe("Failed to find email by ID: " + e.getMessage());
            throw new RuntimeException("Failed to find email", e);
        }
    }
    
    @Override
    public List<Email> findByAccountAndFolder(Long accountId, String folder) {
        String sql = "SELECT * FROM emails WHERE account_id = ? AND folder = ? ORDER BY created_at DESC";
        List<Email> emails = new ArrayList<>();
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setString(2, folder);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEmail(rs));
                }
            }
            
            return emails;
            
        } catch (SQLException e) {
            logger.severe("Failed to find emails by account and folder: " + e.getMessage());
            throw new RuntimeException("Failed to find emails", e);
        }
    }
    
    @Override
    public int countUnreadByAccountAndFolder(Long accountId, String folder) {
        String sql = "SELECT COUNT(*) FROM emails WHERE account_id = ? AND folder = ? AND is_read = false";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setString(2, folder);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.severe("Failed to count unread emails: " + e.getMessage());
            throw new RuntimeException("Failed to count unread emails", e);
        }
    }
    
    @Override
    public void markAsRead(Long emailId) {
        String sql = "UPDATE emails SET is_read = true WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, emailId);
            stmt.executeUpdate();
            
            logger.info("Email marked as read: " + emailId);
            
        } catch (SQLException e) {
            logger.severe("Failed to mark email as read: " + e.getMessage());
            throw new RuntimeException("Failed to mark email as read", e);
        }
    }
    
    @Override
    public void markAsImportant(Long emailId, boolean important) {
        String sql = "UPDATE emails SET is_important = ? WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, important);
            stmt.setLong(2, emailId);
            stmt.executeUpdate();
            
            logger.info("Email marked as important=" + important + ": " + emailId);
            
        } catch (SQLException e) {
            logger.severe("Failed to mark email as important: " + e.getMessage());
            throw new RuntimeException("Failed to mark email as important", e);
        }
    }
    
    @Override
    public void moveToFolder(Long emailId, String newFolder) {
        String sql = "UPDATE emails SET folder = ? WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newFolder);
            stmt.setLong(2, emailId);
            stmt.executeUpdate();
            
            logger.info("Email moved to folder " + newFolder + ": " + emailId);
            
        } catch (SQLException e) {
            logger.severe("Failed to move email to folder: " + e.getMessage());
            throw new RuntimeException("Failed to move email", e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM emails WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();
            
            if (deleted > 0) {
                logger.info("Email deleted: " + id);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.severe("Failed to delete email: " + e.getMessage());
            throw new RuntimeException("Failed to delete email", e);
        }
    }
    
    @Override
    public Optional<Email> findByServerMessageId(Long accountId, String serverMessageId) {
        String sql = "SELECT * FROM emails WHERE account_id = ? AND server_message_id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setString(2, serverMessageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmail(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.severe("Failed to find email by server message ID: " + e.getMessage());
            throw new RuntimeException("Failed to find email", e);
        }
    }
    
    @Override
    public Map<String, Integer> getFolderStats(Long accountId) {
        String sql = "SELECT folder, COUNT(*) as count FROM emails WHERE account_id = ? GROUP BY folder";
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("folder"), rs.getInt("count"));
                }
            }
            
            return stats;
            
        } catch (SQLException e) {
            logger.severe("Failed to get folder stats: " + e.getMessage());
            throw new RuntimeException("Failed to get folder stats", e);
        }
    }
    
    private Email mapResultSetToEmail(ResultSet rs) throws SQLException {
        Email email = new Email();
        email.setId(rs.getLong("id"));
        email.setAccountId(rs.getLong("account_id"));
        email.setFolder(rs.getString("folder"));
        email.setFromAddr(rs.getString("from_addr"));
        email.setToAddr(rs.getString("to_addr"));
        email.setSubject(rs.getString("subject"));
        email.setBody(rs.getString("body"));
        email.setRawMessage(rs.getString("raw_message"));
        email.setEncrypted(rs.getBoolean("is_encrypted"));
        email.setSigned(rs.getBoolean("is_signed"));
        email.setSignatureOk(rs.getBoolean("signature_ok"));
        email.setRead(rs.getBoolean("is_read"));
        email.setImportant(rs.getBoolean("is_important"));
        email.setServerMessageId(rs.getString("server_message_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            email.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return email;
    }
}
