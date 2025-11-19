package ui.modern;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import config.AppConfig;
import core.ServiceRegistry;
import core.service.AuthService;
import ui.theme.ThemeManager;

/**
 * Modern login panel with registration support
 */
public class ModernLoginPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ModernLoginPanel.class.getName());
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton toggleModeButton;
    private JButton themeToggleButton;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JCheckBox rememberMeCheck;
    private JPanel confirmPasswordPanel;
    
    private boolean isRegisterMode = false;
    private LoginCallback callback;
    private ThemeManager themeManager;
    private AuthService authService;
    
    public interface LoginCallback {
        void onLoginSuccess(String email, String password);
        void onLoginError(String message);
    }
    
    public ModernLoginPanel(LoginCallback callback) {
        this.callback = callback;
        this.themeManager = ThemeManager.getInstance();
        
        // Initialize auth service if available
        try {
            ServiceRegistry registry = ServiceRegistry.getInstance();
            if (!registry.getConfig().isDemoMode()) {
                this.authService = registry.getAuthService();
            }
        } catch (Exception e) {
            logger.info("Auth service not available, using demo mode");
        }
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyTheme();
    }
    
    private void initializeComponents() {
        // Title
        titleLabel = new JLabel("Secure Mail Client", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.Fonts.TITLE);
        
        // Input fields
        emailField = createStyledTextField("Email address");
        passwordField = createStyledPasswordField("Password");
        confirmPasswordField = createStyledPasswordField("Confirm password");
        
        // Buttons
        loginButton = createStyledButton("Sign In", ThemeManager.Colors.getAccentColor());
        registerButton = createStyledButton("Create Account", ThemeManager.Colors.getSuccessColor());
        registerButton.setVisible(false);
        
        toggleModeButton = createLinkButton("Don't have an account? Sign up");
        themeToggleButton = createIconButton(themeManager.isDarkMode() ? "☀" : "🌙");
        
        // Other components
        rememberMeCheck = new JCheckBox("Remember me");
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(ThemeManager.Fonts.SMALL);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Header with theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(themeToggleButton, BorderLayout.EAST);
        headerPanel.setOpaque(false);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(titleLabel, gbc);
        
        // Email field
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        contentPanel.add(createFieldPanel("Email", emailField), gbc);
        
        // Password field
        gbc.gridy = 2;
        contentPanel.add(createFieldPanel("Password", passwordField), gbc);
        
        // Confirm password field (shown only in register mode)
        gbc.gridy = 3;
        confirmPasswordPanel = createFieldPanel("Confirm Password", confirmPasswordField);
        contentPanel.add(confirmPasswordPanel, gbc);
        confirmPasswordPanel.setVisible(false);
        
        // Remember me checkbox
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 15, 0);
        contentPanel.add(rememberMeCheck, gbc);
        
        // Login button
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 10, 0);
        contentPanel.add(loginButton, gbc);
        
        // Register button (hidden initially)
        gbc.gridy = 6;
        contentPanel.add(registerButton, gbc);
        
        // Toggle mode button
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 0, 10, 0);
        contentPanel.add(toggleModeButton, gbc);
        
        // Status label
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0);
        contentPanel.add(statusLabel, gbc);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Set preferred size
        setPreferredSize(new Dimension(400, 600));
    }
    
    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(ThemeManager.Fonts.BODY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(ThemeManager.Fonts.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.Colors.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setPreferredSize(new Dimension(0, 45));
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(ThemeManager.Fonts.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.Colors.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setPreferredSize(new Dimension(0, 45));
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(ThemeManager.Fonts.BUTTON);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(0, 45));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(ThemeManager.Fonts.SMALL);
        button.setForeground(ThemeManager.Colors.getAccentColor());
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setText("<html><u>" + text + "</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setText(text);
            }
        });
        
        return button;
    }
    
    private JButton createIconButton(String icon) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        
        return button;
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
        toggleModeButton.addActionListener(e -> toggleMode());
        themeToggleButton.addActionListener(e -> toggleTheme());
        
        // Enter key handling
        ActionListener enterAction = e -> {
            if (isRegisterMode) {
                handleRegister();
            } else {
                handleLogin();
            }
        };
        
        emailField.addActionListener(enterAction);
        passwordField.addActionListener(enterAction);
        confirmPasswordField.addActionListener(enterAction);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (authService != null) {
                    // Real authentication
                    var account = authService.login(email, password);
                    if (account.isPresent()) {
                        showStatus("Login successful!", ThemeManager.Colors.getSuccessColor());
                        callback.onLoginSuccess(email, password);
                    } else {
                        showStatus("Invalid email or password", ThemeManager.Colors.getDangerColor());
                    }
                } else {
                    // Demo mode - accept any credentials
                    showStatus("Demo login successful!", ThemeManager.Colors.getSuccessColor());
                    callback.onLoginSuccess(email, password);
                }
            } catch (Exception ex) {
                showStatus("Login failed: " + ex.getMessage(), ThemeManager.Colors.getDangerColor());
                callback.onLoginError(ex.getMessage());
            } finally {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
            }
        });
    }
    
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showStatus("Please fill in all fields", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Please enter a valid email address", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters", ThemeManager.Colors.getDangerColor());
            return;
        }
        
        registerButton.setEnabled(false);
        registerButton.setText("Creating account...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (authService != null) {
                    // Real registration - use default SMTP/IMAP settings
                    AppConfig config = AppConfig.getInstance();
                    var account = authService.register(
                        email, password,
                        config.getSmtpHost(), config.getSmtpPort(),
                        config.getImapHost(), config.getImapPort()
                    );
                    if (account != null) {
                        showStatus("Account created successfully!", ThemeManager.Colors.getSuccessColor());
                        // Switch to login mode
                        toggleMode();
                    } else {
                        showStatus("Registration failed - email may already exist", ThemeManager.Colors.getDangerColor());
                    }
                } else {
                    // Demo mode
                    showStatus("Demo registration successful!", ThemeManager.Colors.getSuccessColor());
                    toggleMode();
                }
            } catch (Exception ex) {
                showStatus("Registration failed: " + ex.getMessage(), ThemeManager.Colors.getDangerColor());
            } finally {
                registerButton.setEnabled(true);
                registerButton.setText("Create Account");
            }
        });
    }
    
    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        
        if (isRegisterMode) {
            titleLabel.setText("Create Account");
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            toggleModeButton.setText("Already have an account? Sign in");
            rememberMeCheck.setVisible(false);
            if (confirmPasswordPanel != null) {
                confirmPasswordPanel.setVisible(true);
            }
        } else {
            titleLabel.setText("Secure Mail Client");
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            toggleModeButton.setText("Don't have an account? Sign up");
            rememberMeCheck.setVisible(true);
            if (confirmPasswordPanel != null) {
                confirmPasswordPanel.setVisible(false);
            }
        }
        
        // Clear status
        statusLabel.setText(" ");
        
        revalidate();
        repaint();
    }
    
    private void toggleTheme() {
        themeManager.toggleTheme();
        themeToggleButton.setText(themeManager.isDarkMode() ? "☀" : "🌙");
        applyTheme();
        
        // Notify parent to refresh
        SwingUtilities.getWindowAncestor(this).repaint();
    }
    
    private void applyTheme() {
        themeManager.applyTheme();
        
        setBackground(ThemeManager.Colors.getBgColor());
        titleLabel.setForeground(ThemeManager.Colors.getFgColor());
        statusLabel.setForeground(ThemeManager.Colors.getFgColor());
        rememberMeCheck.setForeground(ThemeManager.Colors.getFgColor());
        
        // Update field colors
        emailField.setBackground(ThemeManager.Colors.getBgColor());
        emailField.setForeground(ThemeManager.Colors.getFgColor());
        passwordField.setBackground(ThemeManager.Colors.getBgColor());
        passwordField.setForeground(ThemeManager.Colors.getFgColor());
        if (confirmPasswordField != null) {
            confirmPasswordField.setBackground(ThemeManager.Colors.getBgColor());
            confirmPasswordField.setForeground(ThemeManager.Colors.getFgColor());
        }
        
        repaint();
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gradient background
        Color bg1 = ThemeManager.Colors.getBgColor();
        Color bg2 = themeManager.isDarkMode() ? 
            new Color(bg1.getRed() + 10, bg1.getGreen() + 10, bg1.getBlue() + 10) :
            new Color(Math.max(0, bg1.getRed() - 10), Math.max(0, bg1.getGreen() - 10), Math.max(0, bg1.getBlue() - 10));
            
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
            0, 0, bg1,
            0, getHeight(), bg2
        );
        
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        
        super.paintComponent(g);
    }
}
