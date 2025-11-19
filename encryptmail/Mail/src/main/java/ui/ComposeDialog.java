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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Compose email dialog
 */
public class ComposeDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(ComposeDialog.class.getName());
    
    private JTextField txtTo;
    private JTextField txtSubject;
    private JTextArea txtBody;
    private JButton btnSend;
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnEncrypt;
    private JButton btnSign;

    public ComposeDialog(java.awt.Frame parent) {
        super(parent, "Compose Email", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        txtTo = new JTextField(40);
        txtTo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        txtSubject = new JTextField(40);
        txtSubject.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        txtBody = new JTextArea(15, 40);
        txtBody.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        
        btnSend = new JButton("Send");
        btnSend.setPreferredSize(new Dimension(80, 30));
        btnSend.setBackground(new Color(40, 167, 69));
        btnSend.setForeground(java.awt.Color.WHITE);
        btnSend.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnSend.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSend.setFocusPainted(false);
        
        btnSave = new JButton("Save Draft");
        btnSave.setPreferredSize(new Dimension(100, 30));
        btnSave.setBackground(new Color(108, 117, 125));
        btnSave.setForeground(java.awt.Color.WHITE);
        btnSave.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSave.setFocusPainted(false);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(80, 30));
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(java.awt.Color.WHITE);
        btnCancel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnCancel.setFocusPainted(false);
        
        btnEncrypt = new JButton("🔒 Encrypt");
        btnEncrypt.setPreferredSize(new Dimension(100, 30));
        btnEncrypt.setBackground(new Color(0, 123, 255));
        btnEncrypt.setForeground(java.awt.Color.WHITE);
        btnEncrypt.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnEncrypt.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnEncrypt.setFocusPainted(false);
        
        btnSign = new JButton("✓ Sign");
        btnSign.setPreferredSize(new Dimension(80, 30));
        btnSign.setBackground(new Color(255, 193, 7));
        btnSign.setForeground(java.awt.Color.BLACK);
        btnSign.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnSign.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSign.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        headerPanel.add(txtTo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        headerPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        headerPanel.add(txtSubject, gbc);
        
        // Body panel
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel bodyLabel = new JLabel("Message:");
        bodyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        bodyLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JScrollPane bodyScrollPane = new JScrollPane(txtBody);
        bodyScrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        
        bodyPanel.add(bodyLabel, BorderLayout.NORTH);
        bodyPanel.add(bodyScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        
        gbc.gridx = 0;
        buttonPanel.add(btnEncrypt, gbc);
        gbc.gridx = 1;
        buttonPanel.add(btnSign, gbc);
        gbc.gridx = 2;
        buttonPanel.add(btnSave, gbc);
        gbc.gridx = 3;
        buttonPanel.add(btnSend, gbc);
        gbc.gridx = 4;
        buttonPanel.add(btnCancel, gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        btnSend.addActionListener(e -> handleSend());
        btnSave.addActionListener(e -> handleSaveDraft());
        btnCancel.addActionListener(e -> dispose());
        btnEncrypt.addActionListener(e -> handleEncrypt());
        btnSign.addActionListener(e -> handleSign());
    }
    
    private void handleSend() {
        String to = txtTo.getText().trim();
        String subject = txtSubject.getText().trim();
        String body = txtBody.getText().trim();
        
        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Please fill in all fields.", 
                "Validation Error", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate recipient email
        String emailError = util.AccountManager.getEmailValidationError(to);
        if (emailError != null) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Invalid recipient email: " + emailError, 
                "Validation Error", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Check if encryption or signing is requested
            boolean isEncrypted = btnEncrypt.getText().contains("Encrypted");
            boolean isSigned = btnSign.getText().contains("Signed");
            
            // Send the email using secure mail client
            if (isEncrypted || isSigned) {
                mail.SecureMailClient.sendSecureMail(
                    MainController.getEmail(),
                    to,
                    subject,
                    body,
                    isEncrypted,
                    isSigned
                );
            } else {
                mail.MailClient.sendMail(
                    MainController.getEmail(),
                    to,
                    subject,
                    body
                );
            }
            
            // Create mail record for sent folder
            util.MailHistoryManager.MailRecord sentMail = util.MailHistoryManager.createSentMail(
                MainController.getEmail(),
                to,
                subject,
                body,
                isEncrypted,
                isSigned
            );
            
            util.MailHistoryManager.addMailRecord("sent", sentMail);

            javax.swing.JOptionPane.showMessageDialog(this, 
                "Email sent successfully!", 
                "Success", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (Exception ex) {
            logger.severe(() -> "Failed to send email: " + ex.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Failed to send email: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSaveDraft() {
        String to = txtTo.getText().trim();
        String subject = txtSubject.getText().trim();
        String body = txtBody.getText().trim();
        
        if (subject.isEmpty() && body.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Please enter at least a subject or message body.", 
                "Validation Error", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Create draft mail record
            util.MailHistoryManager.MailRecord draftMail = util.MailHistoryManager.createDraftMail(
                MainController.getEmail(),
                to,
                subject,
                body
            );
            
            util.MailHistoryManager.addMailRecord("drafts", draftMail);
            
            logger.info("Draft saved successfully");
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Draft saved successfully!", 
                "Draft Saved", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            logger.severe(() -> "Failed to save draft: " + ex.getMessage());
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Failed to save draft: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleEncrypt() {
        logger.info("Encrypting email...");
        btnEncrypt.setText("🔒 Encrypted");
        btnEncrypt.setBackground(new Color(40, 167, 69));
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Email will be encrypted before sending.", 
            "Encryption Enabled", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleSign() {
        logger.info("Signing email...");
        btnSign.setText("✓ Signed");
        btnSign.setBackground(new Color(40, 167, 69));
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Email will be digitally signed before sending.", 
            "Digital Signature Enabled", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
