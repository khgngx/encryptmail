package infra.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import config.AppConfig;

/**
 * Database connection manager using HikariCP connection pooling
 */
public class DbConnectionManager {
    private static final Logger logger = Logger.getLogger(DbConnectionManager.class.getName());
    private static HikariDataSource dataSource;
    private static boolean initialized = false;
    
    private DbConnectionManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initialize the connection pool
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            AppConfig config = AppConfig.getInstance();
            
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getDbUrl());
            hikariConfig.setUsername(config.getDbUser());
            hikariConfig.setPassword(config.getDbPassword());
            hikariConfig.setDriverClassName("org.postgresql.Driver");
            
            // Connection pool settings
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setMinimumIdle(2);
            hikariConfig.setConnectionTimeout(30000); // 30 seconds
            hikariConfig.setIdleTimeout(600000); // 10 minutes
            hikariConfig.setMaxLifetime(1800000); // 30 minutes
            
            // Connection validation
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariConfig.setValidationTimeout(5000);
            
            dataSource = new HikariDataSource(hikariConfig);
            initialized = true;
            
            logger.info("Database connection pool initialized successfully");
            
            // Test connection
            try (Connection testConn = getConnection()) {
                if (testConn.isValid(5)) {
                    logger.info("Database connection test successful");
                }
            }
            
        } catch (Exception e) {
            logger.severe("Failed to initialize database connection pool: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Get a connection from the pool
     * @return database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        
        return dataSource.getConnection();
    }
    
    /**
     * Check if database is available
     * @return true if database is accessible
     */
    public static boolean isAvailable() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (Exception e) {
            logger.warning("Database availability check failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the connection pool
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
        initialized = false;
    }
    
    /**
     * Get connection pool statistics
     * @return formatted string with pool stats
     */
    public static String getPoolStats() {
        if (dataSource == null) {
            return "Connection pool not initialized";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
}
