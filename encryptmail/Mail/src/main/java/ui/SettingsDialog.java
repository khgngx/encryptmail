package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.UICustomizationManager;

/**
 * Settings dialog for UI customization
 */
public class SettingsDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(SettingsDialog.class.getName());
    
    private JComboBox<String> themeCombo;
    private JButton primaryColorBtn;
    private JButton secondaryColorBtn;
    private JButton successColorBtn;
    private JButton dangerColorBtn;
    private JButton warningColorBtn;
    private JButton composeColorBtn;
    private JButton resetBtn;
    private JButton applyBtn;
    private JButton cancelBtn;
    
    private Color selectedPrimaryColor;
    private Color selectedSecondaryColor;
    private Color selectedSuccessColor;
    private Color selectedDangerColor;
    private Color selectedWarningColor;
    private Color selectedComposeColor;

    public SettingsDialog(java.awt.Frame parent) {
        super(parent, "UI Settings", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCurrentSettings();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Theme selection
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        themeCombo.setPreferredSize(new Dimension(150, 30));
        
        // Color buttons
        primaryColorBtn = createColorButton("Primary", Color.BLUE);
        secondaryColorBtn = createColorButton("Secondary", Color.GRAY);
        successColorBtn = createColorButton("Success", Color.GREEN);
        dangerColorBtn = createColorButton("Danger", Color.RED);
        warningColorBtn = createColorButton("Warning", Color.ORANGE);
        composeColorBtn = createColorButton("Compose", Color.BLUE);
        
        // Action buttons
        resetBtn = new JButton("Reset to Defaults");
        resetBtn.setPreferredSize(new Dimension(120, 30));
        
        applyBtn = new JButton("Apply");
        applyBtn.setPreferredSize(new Dimension(80, 30));
        applyBtn.setBackground(new Color(40, 167, 69));
        applyBtn.setForeground(Color.WHITE);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(80, 30));
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setForeground(Color.WHITE);
    }
    
    private JButton createColorButton(String text, Color defaultColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30));
        button.setBackground(defaultColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("UI Customization Settings");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Theme selection
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(themeCombo, gbc);
        
        // Color customization section
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel colorLabel = new JLabel("Button Colors:");
        colorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        contentPanel.add(colorLabel, gbc);
        
        // Color buttons in grid
        gbc.gridwidth = 1; gbc.gridy = 2;
        gbc.gridx = 0; contentPanel.add(primaryColorBtn, gbc);
        gbc.gridx = 1; contentPanel.add(secondaryColorBtn, gbc);
        gbc.gridx = 2; contentPanel.add(successColorBtn, gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0; contentPanel.add(dangerColorBtn, gbc);
        gbc.gridx = 1; contentPanel.add(warningColorBtn, gbc);
        gbc.gridx = 2; contentPanel.add(composeColorBtn, gbc);
        
        // Preset colors
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 3;
        JLabel presetLabel = new JLabel("Quick Color Presets:");
        presetLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        contentPanel.add(presetLabel, gbc);
        
        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Map<String, Color> presets = UICustomizationManager.getColorPresets();
        for (Map.Entry<String, Color> entry : presets.entrySet()) {
            JButton presetBtn = new JButton(entry.getKey());
            presetBtn.setBackground(entry.getValue());
            presetBtn.setForeground(Color.WHITE);
            presetBtn.setPreferredSize(new Dimension(80, 25));
            presetBtn.addActionListener(e -> applyPresetColors(entry.getValue()));
            presetPanel.add(presetBtn);
        }
        gbc.gridy = 5; gbc.gridwidth = 3;
        contentPanel.add(presetPanel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        buttonPanel.add(resetBtn);
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Color button handlers
        primaryColorBtn.addActionListener(e -> selectColor("Primary", primaryColorBtn));
        secondaryColorBtn.addActionListener(e -> selectColor("Secondary", secondaryColorBtn));
        successColorBtn.addActionListener(e -> selectColor("Success", successColorBtn));
        dangerColorBtn.addActionListener(e -> selectColor("Danger", dangerColorBtn));
        warningColorBtn.addActionListener(e -> selectColor("Warning", warningColorBtn));
        composeColorBtn.addActionListener(e -> selectColor("Compose", composeColorBtn));
        
        // Action button handlers
        resetBtn.addActionListener(e -> resetToDefaults());
        applyBtn.addActionListener(e -> applySettings());
        cancelBtn.addActionListener(e -> dispose());
    }
    
    private void selectColor(String colorType, JButton button) {
        Color currentColor = button.getBackground();
        Color newColor = JColorChooser.showDialog(this, "Choose " + colorType + " Color", currentColor);
        
        if (newColor != null) {
            button.setBackground(newColor);
            updateSelectedColors(colorType, newColor);
        }
    }
    
    private void updateSelectedColors(String colorType, Color color) {
        switch (colorType) {
            case "Primary":
                selectedPrimaryColor = color;
                break;
            case "Secondary":
                selectedSecondaryColor = color;
                break;
            case "Success":
                selectedSuccessColor = color;
                break;
            case "Danger":
                selectedDangerColor = color;
                break;
            case "Warning":
                selectedWarningColor = color;
                break;
            case "Compose":
                selectedComposeColor = color;
                break;
        }
    }
    
    private void applyPresetColors(Color color) {
        primaryColorBtn.setBackground(color);
        secondaryColorBtn.setBackground(color);
        successColorBtn.setBackground(color);
        dangerColorBtn.setBackground(color);
        warningColorBtn.setBackground(color);
        composeColorBtn.setBackground(color);
        
        selectedPrimaryColor = color;
        selectedSecondaryColor = color;
        selectedSuccessColor = color;
        selectedDangerColor = color;
        selectedWarningColor = color;
        selectedComposeColor = color;
    }
    
    private void loadCurrentSettings() {
        UICustomizationManager.UIConfig config = UICustomizationManager.getConfig();
        
        // Load theme
        themeCombo.setSelectedItem(config.getTheme().substring(0, 1).toUpperCase() + 
                                  config.getTheme().substring(1));
        
        // Load colors
        primaryColorBtn.setBackground(config.getPrimaryButtonColor());
        secondaryColorBtn.setBackground(config.getSecondaryButtonColor());
        successColorBtn.setBackground(config.getSuccessButtonColor());
        dangerColorBtn.setBackground(config.getDangerButtonColor());
        warningColorBtn.setBackground(config.getWarningButtonColor());
        composeColorBtn.setBackground(config.getComposeButtonColor());
        
        // Store selected colors
        selectedPrimaryColor = config.getPrimaryButtonColor();
        selectedSecondaryColor = config.getSecondaryButtonColor();
        selectedSuccessColor = config.getSuccessButtonColor();
        selectedDangerColor = config.getDangerButtonColor();
        selectedWarningColor = config.getWarningButtonColor();
        selectedComposeColor = config.getComposeButtonColor();
    }
    
    private void resetToDefaults() {
        UICustomizationManager.resetToDefaults();
        loadCurrentSettings();
        logger.info("UI settings reset to defaults");
    }
    
    private void applySettings() {
        try {
            // Update colors
            UICustomizationManager.setButtonColor("primary", selectedPrimaryColor);
            UICustomizationManager.setButtonColor("secondary", selectedSecondaryColor);
            UICustomizationManager.setButtonColor("success", selectedSuccessColor);
            UICustomizationManager.setButtonColor("danger", selectedDangerColor);
            UICustomizationManager.setButtonColor("warning", selectedWarningColor);
            UICustomizationManager.setButtonColor("compose", selectedComposeColor);
            
            // Update theme
            String selectedTheme = (String) themeCombo.getSelectedItem();
            UICustomizationManager.UIConfig config = UICustomizationManager.getConfig();
            config.setTheme(selectedTheme.toLowerCase());
            UICustomizationManager.updateConfig(config);
            
            JOptionPane.showMessageDialog(this, 
                "Settings applied successfully! Restart the application to see all changes.", 
                "Settings Applied", 
                JOptionPane.INFORMATION_MESSAGE);
            
            logger.info("UI settings applied successfully");
            dispose();
            
        } catch (Exception ex) {
            logger.severe(() -> "Failed to apply settings: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to apply settings: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}