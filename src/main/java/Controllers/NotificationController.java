package Controllers;

import Models.Notification;
import Entities.User;
import Services.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    @FXML private ListView<Notification> notificationListView;
    @FXML private Button markAllReadBtn;
    
    private NotificationService notificationService;
    private User currentUser;
    private ObservableList<Notification> notifications;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notificationService = new NotificationService();
        notifications = FXCollections.observableArrayList();
        notificationListView.setItems(notifications);
        
        // Set up custom cell factory for notifications
        notificationListView.setCellFactory(param -> new ListCell<Notification>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create the main card container
                    VBox card = new VBox(10);
                    card.setPadding(new Insets(15));
                    card.setMaxWidth(Double.MAX_VALUE);
                    
                    // Add shadow and rounded corners
                    card.setStyle("-fx-background-color: white; " +
                                "-fx-background-radius: 5; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-width: 1; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                    
                    // Create header with timestamp
                    HBox header = new HBox();
                    header.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                    Text dateText = new Text(notification.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                    dateText.setFill(Color.GRAY);
                    dateText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                    header.getChildren().add(dateText);
                    
                    // Create message text
                    Text messageText = new Text(notification.getMessage());
                    messageText.setWrappingWidth(notificationListView.getWidth() - 40);
                    messageText.setFont(Font.font("System", FontWeight.NORMAL, 14));
                    
                    // Add unread indicator if needed
                    if (!notification.isRead()) {
                        HBox indicator = new HBox(5);
                        indicator.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        Circle dot = new Circle(5, Color.BLUE);
                        Text unreadText = new Text("New");
                        unreadText.setFill(Color.BLUE);
                        unreadText.setFont(Font.font("System", FontWeight.BOLD, 12));
                        indicator.getChildren().addAll(dot, unreadText);
                        
                        // Add indicator to header
                        header.getChildren().add(0, indicator);
                        HBox.setHgrow(indicator, Priority.ALWAYS);
                        
                        // Add highlight to card
                        card.setStyle(card.getStyle() + "; -fx-background-color: #f8f9fa;");
                    }
                    
                    // Add all elements to card
                    card.getChildren().addAll(header, messageText);
                    
                    // Set the card as the cell's graphic
                    setGraphic(card);
                    setPadding(new Insets(5, 10, 5, 10));
                }
            }
        });
        
        // Load notifications when a notification is clicked
        notificationListView.setOnMouseClicked(event -> {
            Notification selected = notificationListView.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isRead()) {
                notificationService.markNotificationAsRead(selected.getId());
                selected.setRead(true);
                notificationListView.refresh();
            }
        });
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadNotifications();
    }
    
    private void loadNotifications() {
        if (currentUser != null) {
            List<Notification> userNotifications = notificationService.getUserNotifications(currentUser);
            notifications.clear();
            notifications.addAll(userNotifications);
        }
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
            notificationListView.refresh();
        }
    }
} 