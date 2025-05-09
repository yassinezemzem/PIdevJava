package utils;

import javafx.scene.control.Alert;

/**
 * Utility class for common application functions
 */
public class Utils {
    
    /**
     * Shows an alert dialog with the specified type, title, and message
     * 
     * @param alertType Type of alert (e.g., ERROR, INFORMATION)
     * @param title Title of the alert dialog
     * @param message Content message to display
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 