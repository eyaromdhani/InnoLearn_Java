package org.example.Services;

import org.example.Entities.Event;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private Connection connection;

    public EventService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    public void addEvent(Event e) {
        String query = "INSERT INTO event (titre, description, type_evenement, date_debut, date_fin, lieu, capacite, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, e.getTitre());
            statement.setString(2, e.getDescription());
            statement.setString(3, e.getTypeEvenement());
            
            // Convert LocalDateTime to Timestamp for MySQL
            statement.setTimestamp(4, e.getDateDebut() != null ? Timestamp.valueOf(e.getDateDebut()) : null);
            statement.setTimestamp(5, e.getDateFin() != null ? Timestamp.valueOf(e.getDateFin()) : null);
            
            statement.setString(6, e.getLieu());
            statement.setInt(7, e.getCapacite());
            statement.setString(8, e.getStatut());
            
            statement.executeUpdate();
            System.out.println("Event successfully added!");
        } catch (SQLException ex) {
            System.out.println("Error adding event: " + ex.getMessage());
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("id"));
                e.setTitre(rs.getString("titre"));
                e.setDescription(rs.getString("description"));
                e.setTypeEvenement(rs.getString("type_evenement"));
                
                Timestamp dateDebutRs = rs.getTimestamp("date_debut");
                if (dateDebutRs != null) e.setDateDebut(dateDebutRs.toLocalDateTime());
                
                Timestamp dateFinRs = rs.getTimestamp("date_fin");
                if (dateFinRs != null) e.setDateFin(dateFinRs.toLocalDateTime());
                
                e.setLieu(rs.getString("lieu"));
                e.setCapacite(rs.getInt("capacite"));
                e.setStatut(rs.getString("statut"));
                
                events.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting events: " + ex.getMessage());
        }
        return events;
    }

    public void updateEvent(Event e) {
        String query = "UPDATE event SET titre=?, description=?, type_evenement=?, date_debut=?, date_fin=?, lieu=?, capacite=?, statut=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, e.getTitre());
            statement.setString(2, e.getDescription());
            statement.setString(3, e.getTypeEvenement());
            
            statement.setTimestamp(4, e.getDateDebut() != null ? Timestamp.valueOf(e.getDateDebut()) : null);
            statement.setTimestamp(5, e.getDateFin() != null ? Timestamp.valueOf(e.getDateFin()) : null);
            
            statement.setString(6, e.getLieu());
            statement.setInt(7, e.getCapacite());
            statement.setString(8, e.getStatut());
            statement.setInt(9, e.getId());
            
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Event successfully updated!");
            } else {
                System.out.println("Event not found with ID: " + e.getId());
            }
        } catch (SQLException ex) {
            System.out.println("Error updating event: " + ex.getMessage());
        }
    }

    public void deleteEvent(int id) {
        String query = "DELETE FROM event WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Event successfully deleted!");
            } else {
                System.out.println("Event not found with ID: " + id);
            }
        } catch (SQLException ex) {
            System.out.println("Error deleting event: " + ex.getMessage());
        }
    }
}
