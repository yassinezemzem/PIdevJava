package org.example.utils;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Utility class for form validation
 */
public class ValidationUtils {

    /**
     * Validates if an email address has valid format
     * 
     * @param email Email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        // Email must contain @ and end with a domain (.com, .org, etc.)
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
    
    /**
     * Validates if a string has minimum length
     * 
     * @param text String to validate
     * @param minLength Minimum required length
     * @return true if string has at least minLength characters, false otherwise
     */
    public static boolean hasMinimumLength(String text, int minLength) {
        return text != null && text.trim().length() >= minLength;
    }
    
    /**
     * Validates if a string represents a valid number
     * 
     * @param text String to validate
     * @return true if string is a valid number, false otherwise
     */
    public static boolean isNumeric(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a number is within specified range
     * 
     * @param value Number to validate
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Sets a validation tooltip on a control
     * 
     * @param control Control to add tooltip to
     * @param message Tooltip message
     */
    public static void setTooltip(Control control, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-font-size: 12px;");
        control.setTooltip(tooltip);
    }
    
    /**
     * Sets success style (green border) on a control
     * 
     * @param control Control to style
     */
    public static void setSuccessStyle(Control control) {
        control.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
    }
    
    /**
     * Sets error style (red border) on a control
     * 
     * @param control Control to style
     */
    public static void setErrorStyle(Control control) {
        control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }
    
    /**
     * Removes all styling from a control
     * 
     * @param control Control to clear styling from
     */
    public static void clearStyle(Control control) {
        control.setStyle("");
        control.setTooltip(null);
    }
    
    /**
     * Sets up numeric validation for a text field (allowing decimal numbers)
     * 
     * @param field Text field to validate
     * @param allowNegative Whether to allow negative numbers
     */
    public static void setupNumericValidation(TextField field, boolean allowNegative) {
        String pattern = allowNegative ? "-?\\d*(\\.\\d*)?" : "\\d*(\\.\\d*)?";
        
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(pattern)) {
                field.setText(oldValue);
            }
        });
    }
    
    /**
     * Sets up length validation for a text field
     * 
     * @param field Text field to validate
     * @param maxLength Maximum allowed length
     */
    public static void setupLengthValidation(TextField field, int maxLength) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                field.setText(oldValue);
                setTooltip(field, "Maximum length is " + maxLength + " characters");
            }
        });
    }
} 