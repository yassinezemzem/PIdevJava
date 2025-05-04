package org.example.service;

import org.example.entities.User;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DonationEligibilityChecker {
    private final Connection connection;
    private final NotificationService notificationService;
    private Timer timer;

    public DonationEligibilityChecker() {
        this.connection = MyDatabase.getInstance().getConn();
        this.notificationService = new NotificationService();
    }

    public void startChecking() {
        // Check immediately when started
        checkAllUsers();
        
        // Then check every 24 hours
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAllUsers();
            }
        }, 24 * 60 * 60 * 1000, 24 * 60 * 60 * 1000); // 24 hours in milliseconds
    }

    public void stopChecking() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void checkAllUsers() {
        List<User> users = getAllUsers();
        for (User user : users) {
            checkUserEligibility(user);
        }
    }

    private List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'DONOR'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRoles(Arrays.asList(rs.getString("role")));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    private void checkUserEligibility(User user) {
        String query = "SELECT MAX(date_donation) FROM donations WHERE user_id = ? AND status = 'accepted'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Date lastDonationDate = rs.getDate(1);
                if (lastDonationDate == null) {
                    // No previous donations, create notification
                    notificationService.createNotification(user, 
                        "You are eligible to donate blood! You haven't donated before. Please visit a donation center.");
                    return;
                }
                
                // Check if 3 months have passed since last donation
                LocalDateTime lastDonation = lastDonationDate.toLocalDate().atStartOfDay();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eligibilityDate = lastDonation.plusMonths(3);
                
                if (eligibilityDate.isBefore(now)) {
                    // User is eligible, create notification
                    notificationService.createNotification(user, 
                        "You are now eligible to donate blood again! It has been 3 months since your last donation.");
                } else {
                    // User is not eligible yet, create notification with next eligible date
                    notificationService.createNotification(user, 
                        "You will be eligible to donate blood on " + eligibilityDate.toLocalDate() + 
                        ". Please wait until then to ensure your safety.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 