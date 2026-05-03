package org.example.Services;

import org.example.Entities.StageCondidature;
import org.example.utils.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceStageCondidature implements ServiceStageCondidatureInterface {
    private Connection conn;

    public ServiceStageCondidature(Connection conn) {
        this.conn = conn;
    }

    public void ajouter(StageCondidature sc) throws SQLException {
        // Set student ID automatically from Session if not already set
        if (sc.getId_etudiant() == null || sc.getId_etudiant() == 0) {
            sc.setId_etudiant(Session.getUserId());
        }

        String req = "INSERT INTO stagecondidature (type_request, titre, description, domaine, competences, cv, lettre_motivation, date_publication, statut, id_etudiant, id_offre) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, sc.getType_request());
            pst.setString(2, sc.getTitre());
            pst.setString(3, sc.getDescription());
            pst.setString(4, sc.getDomaine());
            pst.setString(5, sc.getCompetences());
            pst.setString(6, sc.getCv());
            pst.setString(7, sc.getLettre_motivation());
            pst.setDate(8, sc.getDate_publication());
            pst.setString(9, sc.getStatut());
            pst.setObject(10, sc.getId_etudiant());
            pst.setObject(11, sc.getId_offre());
            pst.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM stagecondidature WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public void modifier(StageCondidature sc) throws SQLException {
        String req = "UPDATE stagecondidature SET type_request = ?, titre = ?, description = ?, "
                + "domaine = ?, competences = ?, cv = ?, lettre_motivation = ?, "
                + "date_publication = ?, statut = ?, id_etudiant = ?, id_offre = ? "
                + "WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, sc.getType_request());
            pst.setString(2, sc.getTitre());
            pst.setString(3, sc.getDescription());
            pst.setString(4, sc.getDomaine());
            pst.setString(5, sc.getCompetences());
            pst.setString(6, sc.getCv());
            pst.setString(7, sc.getLettre_motivation());
            pst.setDate(8, sc.getDate_publication());
            pst.setString(9, sc.getStatut());
            pst.setObject(10, sc.getId_etudiant());
            pst.setObject(11, sc.getId_offre());
            pst.setInt(12, sc.getId());
            pst.executeUpdate();

            // Notify if Status is Accepted
            if ("Acceptée".equalsIgnoreCase(sc.getStatut())) {
                try {
                    String[] info = getStudentContactInfo(sc.getId_etudiant());
                    if (info != null && info[1] != null) {
                        org.example.utils.EmailSender.sendAcceptanceEmail(info[1], info[0], sc.getTitre());
                    } else {
                        System.err.println("Aucune adresse email trouvée pour l'étudiant ID: " + sc.getId_etudiant());
                    }
                } catch (Exception e) {
                    System.err.println("Échec de l'envoi de l'email : " + e.getMessage());
                }
            }
        }
    }

    public List<StageCondidature> afficherAll() throws SQLException {
        List<StageCondidature> liste = new ArrayList<>();
        String req = "SELECT * FROM stagecondidature";
        try (Statement ste = conn.createStatement(); ResultSet rs = ste.executeQuery(req)) {
            while (rs.next()) liste.add(extractFromRS(rs));
        }
        return liste;
    }

    @Override
    public List<StageCondidature> afficherParRecruteur() throws SQLException {
        int idRecruteur = Session.getUserId();
        List<StageCondidature> liste = new ArrayList<>();
        String req = "SELECT sc.* FROM stagecondidature sc "
                   + "JOIN offrestage o ON sc.id_offre = o.id "
                   + "WHERE o.id_recruteur = ? AND UPPER(sc.type_request) = 'CANDIDATURE'";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idRecruteur);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) liste.add(extractFromRS(rs));
            }
        }
        return liste;
    }

    public List<StageCondidature> afficherDemandes() throws SQLException {
        List<StageCondidature> liste = new ArrayList<>();
        String req = "SELECT * FROM stagecondidature WHERE type_request = 'DEMANDE'";
        try (Statement ste = conn.createStatement(); ResultSet rs = ste.executeQuery(req)) {
            while (rs.next()) liste.add(extractFromRS(rs));
        }
        return liste;
    }

    public StageCondidature getById(int id) throws SQLException {
        String req = "SELECT * FROM stagecondidature WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return extractFromRS(rs);
            }
        }
        return null;
    }

    @Override
    public StageCondidature getProfileEtudiant() throws SQLException {
        int idEtudiant = org.example.utils.Session.getUserId();
        String req = "SELECT * FROM stagecondidature WHERE id_etudiant = ? AND type_request = 'DEMANDE' LIMIT 1";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idEtudiant);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return extractFromRS(rs);
            }
        }
        return null;
    }

    @Override
    public java.util.Map<String, Integer> getStatsCandidatures() throws SQLException {
        int idEtudiant = Session.getUserId();
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String req = "SELECT statut, COUNT(*) as count FROM stagecondidature WHERE id_etudiant = ? AND type_request = 'CANDIDATURE' GROUP BY statut";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idEtudiant);
            try (ResultSet rs = pst.executeQuery()) {
                int total = 0, accepted = 0, pending = 0;
                while (rs.next()) {
                    String status = rs.getString("statut");
                    int count = rs.getInt("count");
                    total += count;
                    if (status != null) {
                        String s = status.toUpperCase();
                        if (s.contains("ACC") || s.contains("CONF")) accepted += count;
                        else if (s.contains("ATT") || s.contains("PEND")) pending += count;
                    }
                }
                stats.put("total", total);
                stats.put("accepted", accepted);
                stats.put("pending", pending);
            }
        }
        return stats;
    }

    @Override
    public java.util.Map<String, Integer> getStatsCandidaturesForRecruiter() throws SQLException {
        int idRecruteur = Session.getUserId();
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String req = "SELECT sc.statut, COUNT(*) as count FROM stagecondidature sc "
                   + "JOIN offrestage o ON sc.id_offre = o.id "
                   + "WHERE o.id_recruteur = ? AND UPPER(sc.type_request) = 'CANDIDATURE' "
                   + "GROUP BY sc.statut";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idRecruteur);
            try (ResultSet rs = pst.executeQuery()) {
                int total = 0, accepted = 0, pending = 0;
                while (rs.next()) {
                    String status = rs.getString("statut");
                    int count = rs.getInt("count");
                    total += count;
                    if (status != null) {
                        String s = status.toUpperCase();
                        if (s.contains("ACC") || s.contains("CONF")) accepted += count;
                        else if (s.contains("ATT") || s.contains("PEND")) pending += count;
                    }
                }
                stats.put("total", total);
                stats.put("accepted", accepted);
                stats.put("pending", pending);
            }
        }
        return stats;
    }

    public String[] getStudentContactInfo(int idEtudiant) throws SQLException {
        String req = "SELECT name, email FROM user WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idEtudiant);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new String[]{name != null ? name.trim() : "Étudiant", rs.getString("email")};
                }
            }
        }
        return null;
    }

    @Override
    public List<StageCondidature> afficherMesCandidatures() throws SQLException {
        int idEtudiant = Session.getUserId();
        List<StageCondidature> liste = new ArrayList<>();
        String req = "SELECT * FROM stagecondidature WHERE id_etudiant = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setInt(1, idEtudiant);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) liste.add(extractFromRS(rs));
            }
        }
        return liste;
    }

    private StageCondidature extractFromRS(ResultSet rs) throws SQLException {
        StageCondidature sc = new StageCondidature();
        sc.setId(rs.getInt("id"));
        sc.setType_request(rs.getString("type_request"));
        sc.setTitre(rs.getString("titre"));
        sc.setDescription(rs.getString("description"));
        sc.setDomaine(rs.getString("domaine"));
        sc.setCompetences(rs.getString("competences"));
        sc.setCv(rs.getString("cv"));
        sc.setLettre_motivation(rs.getString("lettre_motivation"));
        sc.setDate_publication(rs.getDate("date_publication"));
        sc.setStatut(rs.getString("statut"));
        sc.setId_etudiant(rs.getObject("id_etudiant", Integer.class));
        sc.setId_offre(rs.getObject("id_offre", Integer.class));
        return sc;
    }
}