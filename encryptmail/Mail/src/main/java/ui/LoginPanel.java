package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Swing login panel to replace LoginController
 */
public class LoginPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(LoginPanel.class.getName());
    
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final LoginCallback callback;

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginError(String message);
    }

    public LoginPanel(LoginCallback callback) {
        this.callback = callback;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        txtEmail = new JTextField(20);
        txtEmail.setToolTipText("Email (ví dụ: user@gmail.com)");
        
        txtPassword = new JPasswordField(20);
        txtPassword.setToolTipText("App Password (16 ký tự)");
        
        btnLogin = new JButton("Login");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("Secure Mail Login");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(txtEmail, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(txtPassword, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(btnLogin, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        btnLogin.addActionListener(e -> handleLogin());
        
        // Allow login on Enter key press
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Please enter both email and password.", 
                     JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        String emailError = util.AccountManager.getEmailValidationError(email);
        if (emailError != null) {
            showAlert(emailError, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save account information
        boolean accountSaved = util.AccountManager.saveAccount(
                email, password, "localhost", "localhost", 3025, 3143);
        
        if (!accountSaved) {
            showAlert("Failed to save account information.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set session data
        MainController.setSession(
                email,
                password,
                "localhost",
                "localhost",
                3025,
                3143
        );

        try {
            logger.info(() -> "Login successful: " + email);
            callback.onLoginSuccess();
        } catch (Exception ex) {
            logger.severe(() -> "Error switching screens: " + ex.getMessage());
            showAlert("Error switching screens: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            callback.onLoginError(ex.getMessage());
        }
    }

    private void showAlert(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
    }
}
