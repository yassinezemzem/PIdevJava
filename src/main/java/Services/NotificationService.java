package Services;

import Models.Notification;
import Entities.User;
import utils.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final Connection connection;

    public NotificationService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    public boolean isEligibleForDonation(User user) {
        String query = "SELECT MAX(date_donation) FROM donations WHERE user_id = ? AND status = 'accepted'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Date lastDonationDate = rs.getDate(1);
                if (lastDonationDate == null) {
                    return true; // No previous donations
                }
                
                // Check if 3 months have passed since last donation
                LocalDateTime lastDonation = lastDonationDate.toLocalDate().atStartOfDay();
                LocalDateTime now = LocalDateTime.now();
                return lastDonation.plusMonths(3).isBefore(now);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createNotification(User user, String message) {
        String query = "INSERT INTO notification (user_id, message, is_read, created_at) VALUES (?, ?, false, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, message);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getUserNotifications(User user) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessage(rs.getString("message"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }

    public void markNotificationAsRead(int notificationId) {
        String query = "UPDATE notification SET is_read = true WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUnreadNotificationCount(User user) {
        String query = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = false";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 