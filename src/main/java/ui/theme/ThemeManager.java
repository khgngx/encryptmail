package ui.theme;

import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Theme manager for dark/light mode
 */
public class ThemeManager {
    private static final String THEME_PREF_KEY = "app.theme";
    private static final String DARK_THEME = "dark";
    private static final String LIGHT_THEME = "light";
    
    private static volatile ThemeManager instance;
    private static volatile boolean isDarkMode = false;
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    // Color schemes
    public static class Colors {
        // Modern SaaS Light theme colors (similar to Linear/Outlook)
        public static final Color LIGHT_BG = new Color(243, 244, 246); // #f3f4f6 - main background
        public static final Color LIGHT_CONTENT_BG = new Color(255, 255, 255); // #ffffff - content areas
        public static final Color LIGHT_FG = new Color(17, 24, 39); // #111827 - primary text
        public static final Color LIGHT_FG_SECONDARY = new Color(107, 114, 128); // #6b7280 - secondary text
        public static final Color LIGHT_ACCENT = new Color(37, 99, 235); // #2563eb - primary blue
        public static final Color LIGHT_ACCENT_HOVER = new Color(29, 78, 216); // #1d4ed8 - darker blue
        public static final Color LIGHT_BORDER = new Color(229, 231, 235); // #e5e7eb - subtle borders
        public static final Color LIGHT_HOVER = new Color(243, 244, 246); // #f3f4f6 - hover state
        public static final Color LIGHT_SELECTED = new Color(219, 234, 254); // #dbeafe - selected state
        public static final Color LIGHT_SUCCESS = new Color(34, 197, 94); // #22c55e - green
        public static final Color LIGHT_WARNING = new Color(245, 158, 11); // #f59e0b - amber
        public static final Color LIGHT_DANGER = new Color(239, 68, 68); // #ef4444 - red
        
        // Modern SaaS Dark theme colors
        public static final Color DARK_BG = new Color(17, 24, 39); // #111827 - main background
        public static final Color DARK_CONTENT_BG = new Color(31, 41, 55); // #1f2937 - content areas
        public static final Color DARK_FG = new Color(243, 244, 246); // #f3f4f6 - primary text
        public static final Color DARK_FG_SECONDARY = new Color(156, 163, 175); // #9ca3af - secondary text
        public static final Color DARK_ACCENT = new Color(59, 130, 246); // #3b82f6 - primary blue
        public static final Color DARK_ACCENT_HOVER = new Color(37, 99, 235); // #2563eb - darker blue
        public static final Color DARK_BORDER = new Color(55, 65, 81); // #374151 - subtle borders
        public static final Color DARK_HOVER = new Color(55, 65, 81); // #374151 - hover state
        public static final Color DARK_SELECTED = new Color(30, 58, 138); // #1e3a8a - selected state
        public static final Color DARK_SUCCESS = new Color(34, 197, 94); // #22c55e - green
        public static final Color DARK_WARNING = new Color(245, 158, 11); // #f59e0b - amber
        public static final Color DARK_DANGER = new Color(239, 68, 68); // #ef4444 - red
        
        // Current theme colors (dynamic)
        public static Color getBgColor() {
            return isDarkMode ? DARK_BG : LIGHT_BG;
        }
        
        public static Color getFgColor() {
            return isDarkMode ? DARK_FG : LIGHT_FG;
        }
        
        public static Color getAccentColor() {
            return isDarkMode ? DARK_ACCENT : LIGHT_ACCENT;
        }
        
        public static Color getSecondaryColor() {
            return isDarkMode ? DARK_FG_SECONDARY : LIGHT_FG_SECONDARY;
        }
        
        public static Color getContentBgColor() {
            return isDarkMode ? DARK_CONTENT_BG : LIGHT_CONTENT_BG;
        }
        
        public static Color getSelectedColor() {
            return isDarkMode ? DARK_SELECTED : LIGHT_SELECTED;
        }
        
        public static Color getAccentHoverColor() {
            return isDarkMode ? DARK_ACCENT_HOVER : LIGHT_ACCENT_HOVER;
        }
        
        public static Color getBorderColor() {
            return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
        }
        
        public static Color getHoverColor() {
            return isDarkMode ? DARK_HOVER : LIGHT_HOVER;
        }
        
        public static Color getSuccessColor() {
            return isDarkMode ? DARK_SUCCESS : LIGHT_SUCCESS;
        }
        
        public static Color getWarningColor() {
            return isDarkMode ? DARK_WARNING : LIGHT_WARNING;
        }
        
        public static Color getDangerColor() {
            return isDarkMode ? DARK_DANGER : LIGHT_DANGER;
        }
    }
    
    public static class Fonts {
        // Modern font stack: Inter -> SF Pro -> Segoe UI -> system-ui
        private static final String[] FONT_FAMILIES = {
            "Inter", "SF Pro Display", "Segoe UI", "system-ui", "sans-serif"
        };
        
        private static Font createFont(int style, int size) {
            for (String fontFamily : FONT_FAMILIES) {
                Font font = new Font(fontFamily, style, size);
                if (!font.getFamily().equals(Font.DIALOG)) {
                    return font;
                }
            }
            // Fallback to system default
            return new Font(Font.SANS_SERIF, style, size);
        }
        
        public static final Font TITLE = createFont(Font.BOLD, 28);
        public static final Font HEADING = createFont(Font.BOLD, 20);
        public static final Font SUBHEADING = createFont(Font.BOLD, 16);
        public static final Font BODY = createFont(Font.PLAIN, 14);
        public static final Font BODY_MEDIUM = createFont(Font.BOLD, 14);
        public static final Font SMALL = createFont(Font.PLAIN, 12);
        public static final Font BUTTON = createFont(Font.BOLD, 14);
        public static final Font CAPTION = createFont(Font.PLAIN, 11);
    }
    
    private ThemeManager() {
        loadThemePreference();
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveThemePreference();
        applyTheme();
    }
    
    public void setDarkMode(boolean dark) {
        isDarkMode = dark;
        saveThemePreference();
        applyTheme();
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    private void loadThemePreference() {
        String theme = prefs.get(THEME_PREF_KEY, LIGHT_THEME);
        isDarkMode = DARK_THEME.equals(theme);
    }
    
    private void saveThemePreference() {
        prefs.put(THEME_PREF_KEY, isDarkMode ? DARK_THEME : LIGHT_THEME);
    }
    
    public void applyTheme() {
        try {
            // Set Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // Override UI defaults
            UIManager.put("Panel.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("Panel.foreground", new ColorUIResource(Colors.getFgColor()));
            
            UIManager.put("Button.background", new ColorUIResource(Colors.getAccentColor()));
            UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
            UIManager.put("Button.font", Fonts.BUTTON);
            
            UIManager.put("TextField.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("TextField.foreground", new ColorUIResource(Colors.getFgColor()));
            UIManager.put("TextField.border", javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
            
            UIManager.put("PasswordField.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("PasswordField.foreground", new ColorUIResource(Colors.getFgColor()));
            UIManager.put("PasswordField.border", javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
            
            UIManager.put("Label.foreground", new ColorUIResource(Colors.getFgColor()));
            UIManager.put("Label.font", Fonts.BODY);
            
            UIManager.put("List.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("List.foreground", new ColorUIResource(Colors.getFgColor()));
            UIManager.put("List.selectionBackground", new ColorUIResource(Colors.getAccentColor()));
            UIManager.put("List.selectionForeground", new ColorUIResource(Color.WHITE));
            
            UIManager.put("Table.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("Table.foreground", new ColorUIResource(Colors.getFgColor()));
            UIManager.put("Table.selectionBackground", new ColorUIResource(Colors.getAccentColor()));
            UIManager.put("Table.selectionForeground", new ColorUIResource(Color.WHITE));
            UIManager.put("Table.gridColor", new ColorUIResource(Colors.getBorderColor()));
            
            UIManager.put("ScrollPane.background", new ColorUIResource(Colors.getBgColor()));
            UIManager.put("ScrollBar.background", new ColorUIResource(Colors.getBgColor()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
