package org.example.service;
import org.example.entity.InscriptionEvent;
import org.example.utils.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscriptionEventService {
    private final Connection connection;

    public InscriptionEventService() {
        this.connection = Mydatabase.getInstance().getConnection();
    }

    public void addInscription(InscriptionEvent inscription) {
        String query = "INSERT INTO inscrit_event (name, email, date_inscrit, status, event_id, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, inscription.getName());
            ps.setString(2, inscription.getEmail());

            if (inscription.getDateInscrit() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(inscription.getDateInscrit()));
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }

            ps.setString(4, inscription.getStatus());
            ps.setInt(5, inscription.getEventId());

            if (inscription.getUserId() != null) {
                ps.setInt(6, inscription.getUserId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                inscription.setId(rs.getInt(1));
            }

            System.out.println("Inscription ajoutée avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'inscription : " + e.getMessage(), e);
        }
    }
    public void updateInscription(InscriptionEvent inscription) {
        String query = "UPDATE inscrit_event SET name = ?, email = ?, date_inscrit = ?, status = ?, event_id = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, inscription.getName());
            ps.setString(2, inscription.getEmail());

            if (inscription.getDateInscrit() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(inscription.getDateInscrit()));
            } else {
                ps.setTimestamp(3, null);
            }

            ps.setString(4, inscription.getStatus());
            ps.setInt(5, inscription.getEventId());

            if (inscription.getUserId() != null) {
                ps.setInt(6, inscription.getUserId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, inscription.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Aucune inscription trouvée avec l'id : " + inscription.getId());
            }

            System.out.println("Inscription modifiée avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage(), e);
        }
    }
    public void deleteInscription(int id) {
        String query = "DELETE FROM inscrit_event WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Aucune inscription trouvée avec l'id : " + id);
            }

            System.out.println("Inscription supprimée avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage(), e);
        }
    }
    public InscriptionEvent getInscriptionById(int id) {
        String query = "SELECT * FROM inscrit_event WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInscription(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération par id : " + e.getMessage(), e);
        }

        return null;
    }
    public List<InscriptionEvent> getAllInscriptions() {
        List<InscriptionEvent> inscriptions = new ArrayList<>();
        String query = "SELECT * FROM inscrit_event ORDER BY id DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                inscriptions.add(mapResultSetToInscription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des inscriptions : " + e.getMessage(), e);
        }

        return inscriptions;
    }
    private InscriptionEvent mapResultSetToInscription(ResultSet rs) throws SQLException {
        InscriptionEvent inscription = new InscriptionEvent();

        inscription.setId(rs.getInt("id"));
        inscription.setName(rs.getString("name"));
        inscription.setEmail(rs.getString("email"));

        Timestamp ts = rs.getTimestamp("date_inscrit");
        if (ts != null) {
            inscription.setDateInscrit(ts.toLocalDateTime());
        }

        inscription.setStatus(rs.getString("status"));
        inscription.setEventId(rs.getInt("event_id"));

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            inscription.setUserId(userId);
        }

        return inscription;
    }
}
