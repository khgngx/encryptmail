package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Keys management dialog for encryption keys
 */
public class KeysDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(KeysDialog.class.getName());
    
    private JTable keysTable;
    private DefaultTableModel tableModel;
    private JTextArea keyDetailsArea;
    private JButton btnGenerateKey;
    private JButton btnImportKey;
    private JButton btnExportKey;
    private JButton btnDeleteKey;
    private JButton btnClose;

    public KeysDialog(java.awt.Frame parent) {
        super(parent, "Key Management", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Keys table
        String[] columnNames = {"Key ID", "Type", "Status", "Created", "Expires"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        keysTable = new JTable(tableModel);
        keysTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        keysTable.setRowHeight(25);
        keysTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Key details area
        keyDetailsArea = new JTextArea(8, 40);
        keyDetailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        keyDetailsArea.setEditable(false);
        keyDetailsArea.setBackground(new Color(248, 249, 250));
        keyDetailsArea.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        
        // Buttons
        btnGenerateKey = new JButton("Generate New Key");
        btnGenerateKey.setPreferredSize(new Dimension(140, 30));
        btnGenerateKey.setBackground(new Color(40, 167, 69));
        btnGenerateKey.setForeground(java.awt.Color.WHITE);
        btnGenerateKey.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnGenerateKey.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnGenerateKey.setFocusPainted(false);
        
        btnImportKey = new JButton("Import Key");
        btnImportKey.setPreferredSize(new Dimension(100, 30));
        btnImportKey.setBackground(new Color(0, 123, 255));
        btnImportKey.setForeground(java.awt.Color.WHITE);
        btnImportKey.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnImportKey.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnImportKey.setFocusPainted(false);
        
        btnExportKey = new JButton("Export Key");
        btnExportKey.setPreferredSize(new Dimension(100, 30));
        btnExportKey.setBackground(new Color(255, 193, 7));
        btnExportKey.setForeground(java.awt.Color.BLACK);
        btnExportKey.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnExportKey.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnExportKey.setFocusPainted(false);
        
        btnDeleteKey = new JButton("Delete Key");
        btnDeleteKey.setPreferredSize(new Dimension(100, 30));
        btnDeleteKey.setBackground(new Color(220, 53, 69));
        btnDeleteKey.setForeground(java.awt.Color.WHITE);
        btnDeleteKey.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnDeleteKey.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnDeleteKey.setFocusPainted(false);
        
        btnClose = new JButton("Close");
        btnClose.setPreferredSize(new Dimension(80, 30));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(java.awt.Color.WHITE);
        btnClose.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnClose.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("🔑 Key Management");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Keys table
        gbc.gridx = 0; gbc.gridy = 0;
        JScrollPane tableScrollPane = new JScrollPane(keysTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 200));
        contentPanel.add(tableScrollPane, gbc);
        
        // Key details
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel detailsLabel = new JLabel("Key Details:");
        detailsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        contentPanel.add(detailsLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JScrollPane detailsScrollPane = new JScrollPane(keyDetailsArea);
        detailsScrollPane.setPreferredSize(new Dimension(600, 120));
        contentPanel.add(detailsScrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        
        gbc.gridx = 0;
        buttonPanel.add(btnGenerateKey, gbc);
        gbc.gridx = 1;
        buttonPanel.add(btnImportKey, gbc);
        gbc.gridx = 2;
        buttonPanel.add(btnExportKey, gbc);
        gbc.gridx = 3;
        buttonPanel.add(btnDeleteKey, gbc);
        gbc.gridx = 4;
        buttonPanel.add(btnClose, gbc);
        
        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load sample data
        loadSampleKeys();
    }

    private void setupEventHandlers() {
        btnGenerateKey.addActionListener(e -> handleGenerateKey());
        btnImportKey.addActionListener(e -> handleImportKey());
        btnExportKey.addActionListener(e -> handleExportKey());
        btnDeleteKey.addActionListener(e -> handleDeleteKey());
        btnClose.addActionListener(e -> dispose());
        
        keysTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateKeyDetails();
            }
        });
    }
    
    private void loadSampleKeys() {
        // Add sample keys
        tableModel.addRow(new Object[]{"0x12345678", "RSA-2048", "Active", "2024-01-15", "2025-01-15"});
        tableModel.addRow(new Object[]{"0x87654321", "RSA-4096", "Active", "2024-02-01", "2026-02-01"});
        tableModel.addRow(new Object[]{"0xABCDEF12", "ECC-P256", "Expired", "2023-06-01", "2024-06-01"});
    }
    
    private void updateKeyDetails() {
        int selectedRow = keysTable.getSelectedRow();
        if (selectedRow >= 0) {
            String keyId = (String) tableModel.getValueAt(selectedRow, 0);
            String type = (String) tableModel.getValueAt(selectedRow, 1);
            String status = (String) tableModel.getValueAt(selectedRow, 2);
            
            keyDetailsArea.setText(
                "Key ID: " + keyId + "\n" +
                "Type: " + type + "\n" +
                "Status: " + status + "\n" +
                "Fingerprint: " + generateFingerprint(keyId) + "\n" +
                "Public Key: -----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: GnuPG v2.0.22 (GNU/Linux)\n" +
                "mQENBFqB...\n" +
                "-----END PGP PUBLIC KEY BLOCK-----"
            );
        }
    }
    
    private String generateFingerprint(String keyId) {
        // Generate a mock fingerprint based on the keyId
        logger.info(() -> "Generating fingerprint for key: " + keyId);
        return "A1B2 C3D4 E5F6 7890 1234 5678 9ABC DEF0 1234 5678";
    }
    
    private void handleGenerateKey() {
        logger.info("Generating new key...");
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Key generation started. This may take a few minutes.", 
            "Generating Key", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleImportKey() {
        logger.info("Importing key...");
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Key import functionality would be implemented here.", 
            "Import Key", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleExportKey() {
        logger.info("Exporting key...");
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Key export functionality would be implemented here.", 
            "Export Key", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleDeleteKey() {
        int selectedRow = keysTable.getSelectedRow();
        if (selectedRow >= 0) {
            int result = javax.swing.JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this key?", 
                "Delete Key", 
                javax.swing.JOptionPane.YES_NO_OPTION);
            
            if (result == javax.swing.JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                keyDetailsArea.setText("");
                logger.info("Key deleted");
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Please select a key to delete.", 
                "No Selection", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }
}
