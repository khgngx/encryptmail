package infra.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import core.model.Account;
import core.repository.AccountRepository;

/**
 * PostgreSQL implementation of AccountRepository
 */
public class PgAccountRepository implements AccountRepository {
    private static final Logger logger = Logger.getLogger(PgAccountRepository.class.getName());
    
    @Override
    public Account save(Account account) {
        if (account.getId() == null) {
            return insert(account);
        } else {
            return update(account);
        }
    }
    
    private Account insert(Account account) {
        // Kiểm tra xem table có column plain_password không
        boolean hasPlainPasswordColumn = checkPlainPasswordColumn();
        
        String sql;
        if (hasPlainPasswordColumn) {
            sql = """
                INSERT INTO accounts (email, password_hash, plain_password, smtp_host, smtp_port, imap_host, imap_port, active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id, created_at
                """;
        } else {
            sql = """
                INSERT INTO accounts (email, password_hash, smtp_host, smtp_port, imap_host, imap_port, active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id, created_at
                """;
        }
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getEmail());
            stmt.setString(2, account.getPasswordHash());
            
            if (hasPlainPasswordColumn) {
                stmt.setString(3, account.getPlainPassword());
                stmt.setString(4, account.getSmtpHost());
                stmt.setInt(5, account.getSmtpPort());
                stmt.setString(6, account.getImapHost());
                stmt.setInt(7, account.getImapPort());
                stmt.setBoolean(8, account.isActive());
            } else {
                stmt.setString(3, account.getSmtpHost());
                stmt.setInt(4, account.getSmtpPort());
                stmt.setString(5, account.getImapHost());
                stmt.setInt(6, account.getImapPort());
                stmt.setBoolean(7, account.isActive());
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    account.setId(rs.getLong("id"));
                    account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            
            logger.info("Account created: " + account.getEmail());
            return account;
            
        } catch (SQLException e) {
            logger.severe("Failed to insert account: " + e.getMessage());
            throw new RuntimeException("Failed to save account", e);
        }
    }
    
    private Account update(Account account) {
        String sql = """
            UPDATE accounts 
            SET email = ?, password_hash = ?, smtp_host = ?, smtp_port = ?, 
                imap_host = ?, imap_port = ?, active = ?, last_login_at = ?
            WHERE id = ?
            """;
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getEmail());
            stmt.setString(2, account.getPasswordHash());
            stmt.setString(3, account.getSmtpHost());
            stmt.setInt(4, account.getSmtpPort());
            stmt.setString(5, account.getImapHost());
            stmt.setInt(6, account.getImapPort());
            stmt.setBoolean(7, account.isActive());
            stmt.setTimestamp(8, account.getLastLoginAt() != null ? 
                Timestamp.valueOf(account.getLastLoginAt()) : null);
            stmt.setLong(9, account.getId());
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Account not found for update: " + account.getId());
            }
            
            logger.info("Account updated: " + account.getEmail());
            return account;
            
        } catch (SQLException e) {
            logger.severe("Failed to update account: " + e.getMessage());
            throw new RuntimeException("Failed to update account", e);
        }
    }
    
    @Override
    public Optional<Account> findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.severe("Failed to find account by ID: " + e.getMessage());
            throw new RuntimeException("Failed to find account", e);
        }
    }
    
    @Override
    public Optional<Account> findByEmail(String email) {
        String sql = "SELECT * FROM accounts WHERE email = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.severe("Failed to find account by email: " + e.getMessage());
            throw new RuntimeException("Failed to find account", e);
        }
    }
    
    @Override
    public List<Account> findAllActive() {
        String sql = "SELECT * FROM accounts WHERE active = true ORDER BY email";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
            
            return accounts;
            
        } catch (SQLException e) {
            logger.severe("Failed to find active accounts: " + e.getMessage());
            throw new RuntimeException("Failed to find accounts", e);
        }
    }
    
    @Override
    public void updateLastLogin(Long accountId) {
        String sql = "UPDATE accounts SET last_login_at = NOW() WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.executeUpdate();
            
            logger.info("Updated last login for account ID: " + accountId);
            
        } catch (SQLException e) {
            logger.severe("Failed to update last login: " + e.getMessage());
            throw new RuntimeException("Failed to update last login", e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();
            
            if (deleted > 0) {
                logger.info("Account deleted: " + id);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.severe("Failed to delete account: " + e.getMessage());
            throw new RuntimeException("Failed to delete account", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM accounts WHERE email = ?";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to check email existence: " + e.getMessage());
            throw new RuntimeException("Failed to check email", e);
        }
    }
    
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setEmail(rs.getString("email"));
        account.setPasswordHash(rs.getString("password_hash"));
        
        // Safely get plain_password (có thể column chưa tồn tại)
        try {
            account.setPlainPassword(rs.getString("plain_password"));
        } catch (SQLException e) {
            // Column plain_password chưa tồn tại, set null
            account.setPlainPassword(null);
        }
        
        account.setSmtpHost(rs.getString("smtp_host"));
        account.setSmtpPort(rs.getInt("smtp_port"));
        account.setImapHost(rs.getString("imap_host"));
        account.setImapPort(rs.getInt("imap_port"));
        account.setActive(rs.getBoolean("active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            account.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            account.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        
        return account;
    }
    
    private boolean checkPlainPasswordColumn() {
        String sql = """
            SELECT column_name 
            FROM information_schema.columns 
            WHERE table_name = 'accounts' AND column_name = 'plain_password'
            """;
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next(); // Trả về true nếu có column plain_password
            
        } catch (SQLException e) {
            logger.warning("Failed to check plain_password column: " + e.getMessage());
            return false; // Nếu lỗi, giả sử không có column
        }
    }
}
