package Controllers;

import Entities.User;
import Models.Notification;
import Services.NotificationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    @FXML
    private ListView<Notification> notificationsListView;
    
    @FXML
    private Button markAllReadBtn;
    
    private User currentUser;
    private NotificationService notificationService;
    private ObservableList<Notification> notifications;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadNotifications();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        notificationService = new NotificationService();
        notifications = FXCollections.observableArrayList();
        
        // Configure the ListView
        notificationsListView.setItems(notifications);
        notificationsListView.setCellFactory(lv -> new NotificationCell());
        notificationsListView.setStyle("-fx-background-color: transparent;");
        
        // Add click handler to mark notifications as read
        notificationsListView.setOnMouseClicked(event -> {
            Notification selected = notificationsListView.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isRead()) {
                notificationService.markNotificationAsRead(selected.getId());
                selected.setRead(true);
                notificationsListView.refresh();
            }
        });
    }
    
    @FXML
    private void handleMarkAllAsRead() {
        if (currentUser != null) {
            for (Notification notification : notifications) {
                if (!notification.isRead()) {
                    notificationService.markNotificationAsRead(notification.getId());
                    notification.setRead(true);
                }
            }
            notificationsListView.refresh();
        }
    }
    
    private void loadNotifications() {
        if (currentUser != null) {
            List<Notification> userNotifications = notificationService.getUserNotifications(currentUser);
            notifications.setAll(userNotifications);
        }
    }
    
    private class NotificationCell extends ListCell<Notification> {
        private final VBox card;
        private final TextFlow messageText;
        private final Label dateLabel;
        private final Circle statusDot;
        private final DateTimeFormatter dateFormat;
        
        public NotificationCell() {
            card = new VBox(10);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
            card.setPadding(new Insets(15));
            
            // Create header with status dot and date
            HBox header = new HBox(10);
            header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            statusDot = new Circle(5);
            statusDot.setFill(Color.TRANSPARENT);
            
            dateLabel = new Label();
            dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            header.getChildren().addAll(statusDot, dateLabel, spacer);
            
            // Create message text
            messageText = new TextFlow();
            messageText.setMaxWidth(notificationsListView.getWidth() - 40); // Account for padding
            
            card.getChildren().addAll(header, messageText);
            
            // Set the card as the cell's graphic
            setGraphic(card);
            setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            
            dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        }
        
        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);
            
            if (empty || notification == null) {
                setGraphic(null);
            } else {
                // Update status dot
                statusDot.setFill(notification.isRead() ? Color.TRANSPARENT : Color.DODGERBLUE);
                
                // Update date
                dateLabel.setText(notification.getCreatedAt().format(dateFormat));
                
                // Update message
                Text text = new Text(notification.getMessage());
                text.setStyle("-fx-font-size: 14px;");
                messageText.getChildren().setAll(text);
                
                // Update card width to match ListView width
                card.setPrefWidth(notificationsListView.getWidth() - 20);
                messageText.setMaxWidth(card.getPrefWidth() - 30);
                
                setGraphic(card);
            }
        }
    }
} 