package Services;

import Entities.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pidevjava";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // CREATE
    public void saveAppointment(Appointment appointment) {
        String query = "INSERT INTO appointments (name, email, phone, service, date, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, appointment.getName());
            stmt.setString(2, appointment.getEmail());
            stmt.setString(3, appointment.getPhone());
            stmt.setString(4, appointment.getService());
            stmt.setDate(5, appointment.getDate());
            stmt.setDouble(6, appointment.getLatitude());
            stmt.setDouble(7, appointment.getLongitude());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("service"),
                        rs.getDate("date"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                );
                appointment.setId(rs.getInt("id"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // UPDATE
    public void updateAppointment(Appointment appointment) {
        String query = "UPDATE appointments SET name = ?, email = ?, phone = ?, service = ?, date = ?, latitude = ?, longitude = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, appointment.getName());
            stmt.setString(2, appointment.getEmail());
            stmt.setString(3, appointment.getPhone());
            stmt.setString(4, appointment.getService());
            stmt.setDate(5, appointment.getDate());
            stmt.setDouble(6, appointment.getLatitude());
            stmt.setDouble(7, appointment.getLongitude());
            stmt.setInt(8, appointment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void deleteAppointment(int id) {
        String query = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 