package util;

import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages UI customization settings including button colors and themes
 */
public class UICustomizationManager {
    private static final Logger logger = Logger.getLogger(UICustomizationManager.class.getName());
    private static final String UI_CONFIG_FILE = "ui_config.dat";
    
    private static UIConfig currentConfig = new UIConfig();
    
    public static class UIConfig implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Color primaryButtonColor = new Color(0, 123, 255);
        private Color secondaryButtonColor = new Color(108, 117, 125);
        private Color successButtonColor = new Color(40, 167, 69);
        private Color dangerButtonColor = new Color(220, 53, 69);
        private Color warningButtonColor = new Color(255, 193, 7);
        private Color composeButtonColor = new Color(0, 123, 255);
        private String theme = "light";
        private boolean customColorsEnabled = false;
        
        // Getters and setters
        public Color getPrimaryButtonColor() { return primaryButtonColor; }
        public void setPrimaryButtonColor(Color color) { this.primaryButtonColor = color; }
        
        public Color getSecondaryButtonColor() { return secondaryButtonColor; }
        public void setSecondaryButtonColor(Color color) { this.secondaryButtonColor = color; }
        
        public Color getSuccessButtonColor() { return successButtonColor; }
        public void setSuccessButtonColor(Color color) { this.successButtonColor = color; }
        
        public Color getDangerButtonColor() { return dangerButtonColor; }
        public void setDangerButtonColor(Color color) { this.dangerButtonColor = color; }
        
        public Color getWarningButtonColor() { return warningButtonColor; }
        public void setWarningButtonColor(Color color) { this.warningButtonColor = color; }
        
        public Color getComposeButtonColor() { return composeButtonColor; }
        public void setComposeButtonColor(Color color) { this.composeButtonColor = color; }
        
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        
        public boolean isCustomColorsEnabled() { return customColorsEnabled; }
        public void setCustomColorsEnabled(boolean enabled) { this.customColorsEnabled = enabled; }
    }
    
    static {
        loadConfig();
    }
    
    /**
     * Gets the current UI configuration
     */
    public static UIConfig getConfig() {
        return currentConfig;
    }
    
    /**
     * Updates the UI configuration
     */
    public static void updateConfig(UIConfig config) {
        currentConfig = config;
        saveConfig();
        logger.info("UI configuration updated");
    }
    
    /**
     * Sets button text color for a specific button type
     */
    public static void setButtonColor(String buttonType, Color color) {
        switch (buttonType.toLowerCase()) {
            case "primary":
                currentConfig.setPrimaryButtonColor(color);
                break;
            case "secondary":
                currentConfig.setSecondaryButtonColor(color);
                break;
            case "success":
                currentConfig.setSuccessButtonColor(color);
                break;
            case "danger":
                currentConfig.setDangerButtonColor(color);
                break;
            case "warning":
                currentConfig.setWarningButtonColor(color);
                break;
            case "compose":
                currentConfig.setComposeButtonColor(color);
                break;
            default:
                logger.warning("Unknown button type: " + buttonType);
                return;
        }
        
        currentConfig.setCustomColorsEnabled(true);
        saveConfig();
        logger.info("Button color updated for " + buttonType + ": " + color);
    }
    
    /**
     * Gets button color for a specific button type
     */
    public static Color getButtonColor(String buttonType) {
        switch (buttonType.toLowerCase()) {
            case "primary":
                return currentConfig.getPrimaryButtonColor();
            case "secondary":
                return currentConfig.getSecondaryButtonColor();
            case "success":
                return currentConfig.getSuccessButtonColor();
            case "danger":
                return currentConfig.getDangerButtonColor();
            case "warning":
                return currentConfig.getWarningButtonColor();
            case "compose":
                return currentConfig.getComposeButtonColor();
            default:
                logger.warning("Unknown button type: " + buttonType);
                return Color.BLACK;
        }
    }
    
    /**
     * Resets all colors to default
     */
    public static void resetToDefaults() {
        currentConfig = new UIConfig();
        saveConfig();
        logger.info("UI colors reset to defaults");
    }
    
    /**
     * Applies color to a button based on its type
     */
    public static void applyButtonColor(javax.swing.JButton button, String buttonType) {
        if (!currentConfig.isCustomColorsEnabled()) {
            return;
        }
        
        Color color = getButtonColor(buttonType);
        button.setBackground(color);
        
        // Set appropriate text color based on background
        if (isLightColor(color)) {
            button.setForeground(Color.BLACK);
        } else {
            button.setForeground(Color.WHITE);
        }
    }
    
    /**
     * Checks if a color is light (for text color selection)
     */
    private static boolean isLightColor(Color color) {
        // Calculate luminance
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5;
    }
    
    /**
     * Gets available color presets
     */
    public static Map<String, Color> getColorPresets() {
        Map<String, Color> presets = new HashMap<>();
        presets.put("Blue", new Color(0, 123, 255));
        presets.put("Green", new Color(40, 167, 69));
        presets.put("Red", new Color(220, 53, 69));
        presets.put("Orange", new Color(255, 193, 7));
        presets.put("Purple", new Color(111, 66, 193));
        presets.put("Teal", new Color(32, 201, 151));
        presets.put("Pink", new Color(232, 62, 140));
        presets.put("Gray", new Color(108, 117, 125));
        return presets;
    }
    
    /**
     * Loads configuration from file
     */
    private static void loadConfig() {
        try {
            Path filePath = Paths.get(UI_CONFIG_FILE);
            if (!Files.exists(filePath)) {
                logger.info("No UI config file found, using defaults");
                return;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
                currentConfig = (UIConfig) ois.readObject();
                logger.info("UI configuration loaded from file");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to load UI configuration: " + e.getMessage());
            currentConfig = new UIConfig();
        }
    }
    
    /**
     * Saves configuration to file
     */
    private static void saveConfig() {
        try {
            Path filePath = Paths.get(UI_CONFIG_FILE);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(currentConfig);
                logger.info("UI configuration saved to file");
            }
        } catch (Exception e) {
            logger.severe("Failed to save UI configuration: " + e.getMessage());
        }
    }
    
    /**
     * Gets theme-specific colors
     */
    public static Map<String, Color> getThemeColors(String theme) {
        Map<String, Color> colors = new HashMap<>();
        
        switch (theme.toLowerCase()) {
            case "dark":
                colors.put("background", new Color(33, 37, 41));
                colors.put("foreground", Color.WHITE);
                colors.put("panel", new Color(52, 58, 64));
                colors.put("border", new Color(73, 80, 87));
                break;
            case "light":
            default:
                colors.put("background", Color.WHITE);
                colors.put("foreground", Color.BLACK);
                colors.put("panel", new Color(248, 249, 250));
                colors.put("border", new Color(222, 226, 230));
                break;
        }
        
        return colors;
    }
}
