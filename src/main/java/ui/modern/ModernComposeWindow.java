package ui.modern;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import core.ServiceRegistry;
import core.service.MailHistoryService;
import core.service.MailService;
import core.service.SecureMailService;
import ui.theme.ThemeManager;

/**
 * Modern compose window for sending emails
 */
public class ModernComposeWindow extends JDialog {
    private static final Logger logger = Logger.getLogger(ModernComposeWindow.class.getName());
    
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea bodyArea;
    private JCheckBox encryptCheck;
    private JCheckBox signCheck;
    private JButton sendButton;
    private JButton draftButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private String currentUser;
    private JFrame parentApp;
    private ServiceRegistry serviceRegistry;
    private MailService mailService;
    private SecureMailService secureMailService;
    private MailHistoryService mailHistoryService;
    private ThemeManager themeManager;
    
    public ModernComposeWindow(ModernMainApplication parent, String userEmail) {
        super(parent, "Compose Message", true);
        this.parentApp = parent;
        this.currentUser = userEmail;
        this.serviceRegistry = ServiceRegistry.getInstance();
        this.mailService = serviceRegistry.getMailService();
        this.themeManager = ThemeManager.getInstance();
        
        // Initialize services if available
        try {
            if (!serviceRegistry.getConfig().isDemoMode()) {
                this.secureMailService = serviceRegistry.getSecureMailService();
                this.mailHistoryService = serviceRegistry.getMailHistoryService();
            }
        } catch (Exception e) {
            logger.info("Secure services not available: " + e.getMessage());
        }
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyTheme();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Input fields
        toField = createStyledTextField();
        subjectField = createStyledTextField();
        bodyArea = new JTextArea();
        bodyArea.setFont(ThemeManager.Fonts.BODY);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Checkboxes
        encryptCheck = new JCheckBox("🔒 Encrypt message");
        encryptCheck.setFont(ThemeManager.Fonts.BODY);
        encryptCheck.setEnabled(secureMailService != null);
        
        signCheck = new JCheckBox("✍️ Sign message");
        signCheck.setFont(ThemeManager.Fonts.BODY);
        signCheck.setEnabled(secureMailService != null);
        
        // Buttons
        sendButton = createStyledButton("Send", ThemeManager.Colors.getAccentColor());
        draftButton = createStyledButton("Save Draft", ThemeManager.Colors.getSecondaryColor());
        cancelButton = createStyledButton("Cancel", ThemeManager.Colors.getDangerColor());
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(ThemeManager.Fonts.SMALL);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // To field
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createFieldPanel("To:", toField), gbc);
        
        // Subject field
        gbc.gridy = 1;
        contentPanel.add(createFieldPanel("Subject:", subjectField), gbc);
        
        // Body area
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane bodyScrollPane = new JScrollPane(bodyArea);
        bodyScrollPane.setBorder(BorderFactory.createTitledBorder("Message"));
        bodyScrollPane.setPreferredSize(new Dimension(0, 250));
        contentPanel.add(bodyScrollPane, gbc);
        
        // Security options
        gbc.gridy = 3;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel securityPanel = createSecurityPanel();
        contentPanel.add(securityPanel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(draftButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(sendButton);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Add to dialog
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }
    
    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(ThemeManager.Fonts.BODY);
        label.setPreferredSize(new Dimension(80, 0));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Security Options"));
        
        panel.add(encryptCheck);
        panel.add(signCheck);
        
        if (secureMailService == null) {
            JLabel infoLabel = new JLabel("(Available in non-demo mode)");
            infoLabel.setFont(ThemeManager.Fonts.SMALL);
            infoLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
            panel.add(infoLabel);
        }
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(ThemeManager.Fonts.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.Colors.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(0, 35));
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(ThemeManager.Fonts.BUTTON);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }
    
    private void setupEventHandlers() {
        sendButton.addActionListener(e -> sendEmail());
        draftButton.addActionListener(e -> saveDraft());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void sendEmail() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();
        boolean encrypt = encryptCheck.isSelected();
        boolean sign = signCheck.isSelected();
        
        // Validation
        if (to.isEmpty()) {
            showStatus("Please enter recipient email", ThemeManager.Colors.getDangerColor());
            toField.requestFocus();
            return;
        }
        
        if (subject.isEmpty()) {
            showStatus("Please enter subject", ThemeManager.Colors.getDangerColor());
            subjectField.requestFocus();
            return;
        }
        
        if (body.trim().isEmpty()) {
            showStatus("Please enter message body", ThemeManager.Colors.getDangerColor());
            bodyArea.requestFocus();
            return;
        }
        
        // Disable send button
        sendButton.setEnabled(false);
        sendButton.setText("Sending...");
        showStatus("Sending email...", ThemeManager.Colors.getAccentColor());
        
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = false;
                
                // Lấy plainPassword từ database để authenticate với hMailServer
                String userPassword = "";
                var accountOpt = serviceRegistry.getAccountRepository().findByEmail(currentUser);
                if (accountOpt.isPresent()) {
                    userPassword = accountOpt.get().getPlainPassword();
                    if (userPassword == null) {
                        userPassword = ""; // Fallback nếu không có plainPassword
                    }
                }
                
                if ((encrypt || sign) && secureMailService != null) {
                    // Send secure email
                    secureMailService.sendSecureMail(currentUser, userPassword, to, subject, body, encrypt, sign);
                    success = true;
                    
                    String securityInfo = "";
                    if (encrypt) securityInfo += "encrypted ";
                    if (sign) securityInfo += "signed ";
                    showStatus("Secure email sent successfully! (" + securityInfo.trim() + ")", 
                             ThemeManager.Colors.getSuccessColor());
                } else {
                    // Send regular email
                    mailService.sendMail(currentUser, userPassword, to, subject, body);
                    success = true;
                    showStatus("Email sent successfully!", ThemeManager.Colors.getSuccessColor());
                }
                
                if (success && mailHistoryService != null) {
                    // Save to sent folder
                    try {
                        var account = serviceRegistry.getAccountRepository().findByEmail(currentUser);
                        if (account.isPresent()) {
                            mailHistoryService.createSentEmail(account.get().getId(), currentUser, to, 
                                                             subject, body, encrypt, sign);
                        }
                    } catch (Exception e) {
                        logger.warning("Failed to save to sent folder: " + e.getMessage());
                    }
                }
                
                // Close window after successful send
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(1500); // Show success message briefly
                        dispose();
                        if (parentApp instanceof ModernMainApplication) {
                            ((ModernMainApplication) parentApp).refreshEmails();
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                });
                
            } catch (Exception ex) {
                logger.severe("Failed to send email: " + ex.getMessage());
                showStatus("Failed to send email: " + ex.getMessage(), ThemeManager.Colors.getDangerColor());
            } finally {
                sendButton.setEnabled(true);
                sendButton.setText("Send");
            }
        });
    }
    
    private void saveDraft() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();
        
        if (subject.isEmpty() && body.trim().isEmpty()) {
            showStatus("Nothing to save", ThemeManager.Colors.getWarningColor());
            return;
        }
        
        draftButton.setEnabled(false);
        draftButton.setText("Saving...");
        showStatus("Saving draft...", ThemeManager.Colors.getAccentColor());
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (mailHistoryService != null) {
                    var account = serviceRegistry.getAccountRepository().findByEmail(currentUser);
                    if (account.isPresent()) {
                        mailHistoryService.createDraftEmail(account.get().getId(), currentUser, to, subject, body);
                        showStatus("Draft saved successfully!", ThemeManager.Colors.getSuccessColor());
                    } else {
                        showStatus("Failed to save draft - account not found", ThemeManager.Colors.getDangerColor());
                    }
                } else {
                    // Demo mode - just show success
                    showStatus("Draft saved! (demo mode)", ThemeManager.Colors.getSuccessColor());
                }
            } catch (Exception ex) {
                logger.severe("Failed to save draft: " + ex.getMessage());
                showStatus("Failed to save draft: " + ex.getMessage(), ThemeManager.Colors.getDangerColor());
            } finally {
                draftButton.setEnabled(true);
                draftButton.setText("Save Draft");
            }
        });
    }
    
    private void applyTheme() {
        themeManager.applyTheme();
        
        getContentPane().setBackground(ThemeManager.Colors.getBgColor());
        
        toField.setBackground(ThemeManager.Colors.getBgColor());
        toField.setForeground(ThemeManager.Colors.getFgColor());
        
        subjectField.setBackground(ThemeManager.Colors.getBgColor());
        subjectField.setForeground(ThemeManager.Colors.getFgColor());
        
        bodyArea.setBackground(ThemeManager.Colors.getBgColor());
        bodyArea.setForeground(ThemeManager.Colors.getFgColor());
        
        encryptCheck.setForeground(ThemeManager.Colors.getFgColor());
        signCheck.setForeground(ThemeManager.Colors.getFgColor());
        
        statusLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        repaint();
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        logger.info(message);
    }
}
