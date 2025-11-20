package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import infra.db.DbConnectionManager;

/**
 * Utility class to automatically fix database schema issues
 */
public class DatabaseFixer {
    private static final Logger logger = Logger.getLogger(DatabaseFixer.class.getName());
    
    public static void main(String[] args) {
        logger.info("Starting DatabaseFixer utility");
        System.out.println("========================================");
        System.out.println("AUTO FIX DATABASE - SECURE MAIL CLIENT");
        System.out.println("========================================");
        
        DatabaseFixer fixer = new DatabaseFixer();
        
        try {
            System.out.println("Step 1: Checking database connection...");
            fixer.testConnection();
            
            System.out.println("Step 2: Adding plain_password column...");
            fixer.addPlainPasswordColumn();
            
            System.out.println("Step 3: Cleaning old accounts...");
            fixer.cleanOldAccounts();
            
            System.out.println("Step 4: Resetting ID sequence...");
            fixer.resetSequence();
            
            System.out.println("Step 5: Verifying schema...");
            fixer.verifySchema();
            
            System.out.println("\n========================================");
            System.out.println("DATABASE FIX COMPLETED SUCCESSFULLY!");
            System.out.println("========================================");
            System.out.println("Now you can run the main application.");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testConnection() throws SQLException {
        try (Connection conn = DbConnectionManager.getConnection()) {
            System.out.println("✓ Database connection successful");
        }
    }
    
    private void addPlainPasswordColumn() throws SQLException {
        String sql = "ALTER TABLE accounts ADD COLUMN IF NOT EXISTS plain_password VARCHAR(255)";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            System.out.println("✓ plain_password column added");
        }
    }
    
    private void cleanOldAccounts() throws SQLException {
        // Force clean all tables with TRUNCATE to reset sequences properly
        String[] sqls = {
            "TRUNCATE TABLE accounts RESTART IDENTITY CASCADE",
            "TRUNCATE TABLE login_history RESTART IDENTITY CASCADE", 
            "TRUNCATE TABLE emails RESTART IDENTITY CASCADE"
        };
        
        try (Connection conn = DbConnectionManager.getConnection()) {
            for (String sql : sqls) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
            System.out.println("✓ All tables truncated and sequences reset");
        }
    }
    
    private void resetSequence() throws SQLException {
        String sql = "ALTER SEQUENCE accounts_id_seq RESTART WITH 1";
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            System.out.println("✓ ID sequence reset");
        }
    }
    
    private void verifySchema() throws SQLException {
        String sql = """
            SELECT column_name, data_type 
            FROM information_schema.columns 
            WHERE table_name = 'accounts' 
            ORDER BY ordinal_position
            """;
        
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("✓ Database schema:");
            while (rs.next()) {
                String columnName = rs.getString("column_name");
                String dataType = rs.getString("data_type");
                System.out.println("  - " + columnName + " (" + dataType + ")");
            }
        }
    }
}
