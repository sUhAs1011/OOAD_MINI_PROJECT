package com.example.voting;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages language internationalization for the application
 */
public class LanguageManager {
    private static final String BUNDLE_NAME = "resources.messages";
    private static LanguageManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;
    
    // Available languages
    public static final Locale ENGLISH = new Locale("en");
    public static final Locale HINDI = new Locale("hi");
    public static final Locale KANNADA = new Locale("kn");
    
    private LanguageManager() {
        // Default to English
        setLocale(ENGLISH);
    }
    
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    /**
     * Set the application locale
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.messages = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
    
    /**
     * Get the current locale
     * @return the current locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Get a message from the resource bundle
     * @param key the message key
     * @return the localized message
     */
    public String getMessage(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            System.out.println("Warning: Missing translation key: " + key);
            return key;
        }
    }
    
    /**
     * Shows available languages
     * @return array of locales
     */
    public Locale[] getAvailableLocales() {
        return new Locale[]{ENGLISH, HINDI, KANNADA};
    }
    
    /**
     * Get language name for a locale
     * @param locale the locale
     * @return the language name
     */
    public String getLanguageName(Locale locale) {
        if (locale.equals(ENGLISH)) {
            return "English";
        } else if (locale.equals(HINDI)) {
            return "Hindi (हिन्दी)";
        } else if (locale.equals(KANNADA)) {
            return "Kannada (ಕನ್ನಡ)";
        }
        return locale.getDisplayLanguage();
    }
} 