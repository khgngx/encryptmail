-- Database schema for Secure Mail Client
-- PostgreSQL version

-- Create database (run this manually)
-- CREATE DATABASE securemail;
-- CREATE USER securemail WITH PASSWORD 'secret';
-- GRANT ALL PRIVILEGES ON DATABASE securemail TO securemail;

-- Connect to securemail database and run the following:

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    smtp_host VARCHAR(255) NOT NULL DEFAULT 'localhost',
    smtp_port INTEGER NOT NULL DEFAULT 25,
    imap_host VARCHAR(255) NOT NULL DEFAULT 'localhost',
    imap_port INTEGER NOT NULL DEFAULT 143,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    last_login_at TIMESTAMPTZ,
    active BOOLEAN DEFAULT TRUE
);

-- Login history table
CREATE TABLE IF NOT EXISTS login_history (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    login_time TIMESTAMPTZ DEFAULT NOW(),
    ip_address VARCHAR(45), -- IPv6 compatible
    user_agent VARCHAR(500),
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message TEXT
);

-- Emails table
CREATE TABLE IF NOT EXISTS emails (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    folder VARCHAR(50) NOT NULL DEFAULT 'inbox', -- inbox, sent, drafts, trash
    from_addr VARCHAR(255) NOT NULL,
    to_addr VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    body TEXT,
    raw_message TEXT, -- Original message for debugging
    is_encrypted BOOLEAN DEFAULT FALSE,
    is_signed BOOLEAN DEFAULT FALSE,
    signature_ok BOOLEAN DEFAULT FALSE,
    is_read BOOLEAN DEFAULT FALSE,
    is_important BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    server_message_id VARCHAR(255) -- Message-ID from SMTP/IMAP
);

-- Keys table for RSA key pairs
CREATE TABLE IF NOT EXISTS keys (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    key_type VARCHAR(20) NOT NULL DEFAULT 'RSA', -- RSA, AES, etc.
    public_key TEXT, -- Base64 encoded public key
    private_key TEXT, -- Base64 encoded private key (encrypted)
    key_size INTEGER DEFAULT 2048,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    active BOOLEAN DEFAULT TRUE,
    
    UNIQUE(account_id, key_type, active) -- Only one active key per type per account
);

-- UI customization settings
CREATE TABLE IF NOT EXISTS ui_settings (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id) ON DELETE CASCADE,
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    
    UNIQUE(account_id, setting_key)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_emails_account_folder ON emails(account_id, folder);
CREATE INDEX IF NOT EXISTS idx_emails_created_at ON emails(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_emails_unread ON emails(account_id, is_read) WHERE is_read = FALSE;
CREATE INDEX IF NOT EXISTS idx_login_history_account_time ON login_history(account_id, login_time DESC);
CREATE INDEX IF NOT EXISTS idx_keys_account_active ON keys(account_id, active) WHERE active = TRUE;
CREATE INDEX IF NOT EXISTS idx_ui_settings_account_key ON ui_settings(account_id, setting_key);

-- Insert some default data for testing
INSERT INTO accounts (email, password_hash, smtp_host, smtp_port, imap_host, imap_port) 
VALUES 
    ('testuser@localhost', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'localhost', 3025, 'localhost', 3143),
    ('demo@localhost', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'localhost', 3025, 'localhost', 3143)
ON CONFLICT (email) DO NOTHING;

-- Create a function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for ui_settings
CREATE TRIGGER update_ui_settings_updated_at 
    BEFORE UPDATE ON ui_settings 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
