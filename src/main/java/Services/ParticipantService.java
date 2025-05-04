package Services;

import Entities.Participation;
import Entities.Evenement;
import utils.MyConnection;
import utils.PDFGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantService {
    private final Connection connection;

    public ParticipantService() {
        connection = MyConnection.getInstance().getConnection();
    }

    /**
     * Ajout simple d'un participant, utilisé si tu veux juste savoir si l'opération a réussi ou pas.
     */
    public boolean addParticipant(Participation participant) {
        if (hasAlreadyParticipated(participant.getUserId(), participant.getEvenementId())) {
            System.out.println("Le client a déjà participé à cet événement.");
            return false;
        }

        String query = "INSERT INTO participation (date_participation, nombre_personnes, client_id, evenement_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, participant.getDateParticipation());
            ps.setInt(2, participant.getNombrePersonnes());
            ps.setInt(3, participant.getUserId());
            ps.setInt(4, participant.getEvenementId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajout d'un participant avec génération automatique du PDF de confirmation
     */
    public int addParticipantWithPDF(Participation participant, Evenement evenement) {
        if (hasAlreadyParticipated(participant.getUserId(), participant.getEvenementId())) {
            System.out.println("Le client a déjà participé à cet événement.");
            return -1;
        }

        String query = "INSERT INTO participation (date_participation, nombre_personnes, client_id, evenement_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, participant.getDateParticipation());
            ps.setInt(2, participant.getNombrePersonnes());
            ps.setInt(3, participant.getUserId());
            ps.setInt(4, participant.getEvenementId());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    participant.setId(generatedKeys.getInt(1));
                    return PDFGenerator.generateConfirmationPDF(evenement, participant);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Vérifie si un client a déjà participé à un événement donné
     */
    private boolean hasAlreadyParticipated(int clientId, int evenementId) {
        String query = "SELECT COUNT(*) FROM participation WHERE client_id = ? AND evenement_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, clientId);
            ps.setInt(2, evenementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Récupère la liste de tous les participants
     */
    public List<Participation> getAllParticipants() {
        List<Participation> participants = new ArrayList<>();
        String query = "SELECT * FROM participation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Participation p = new Participation();
                p.setId(rs.getInt("id"));
                p.setDateParticipation(rs.getTimestamp("date_participation").toLocalDateTime());
                p.setNombrePersonnes(rs.getInt("nombre_personnes"));
                p.setUserId(rs.getInt("client_id"));
                p.setEvenementId(rs.getInt("evenement_id"));
                participants.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
} 