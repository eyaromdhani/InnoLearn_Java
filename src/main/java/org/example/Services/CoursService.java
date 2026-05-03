package org.example.Services;

import org.example.Entities.Cours;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService implements IService<Cours> {

    Connection conn;

    public CoursService() {
        conn = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Cours c) throws SQLException {
        String sql = "INSERT INTO `cours`(`nom`, `description`, `slug`, `type_media`, `media_url`, `duree`, `niveau`, `date_creation`, `enseignant_id`, `categorie_cours_id`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, c.getNom());
        statement.setString(2, c.getDescription());
        statement.setString(3, c.getSlug());
        statement.setString(4, c.getTypeMedia());
        statement.setString(5, c.getMediaUrl());
        statement.setInt(6, c.getDuree());
        statement.setString(7, c.getNiveau());
        statement.setTimestamp(8, Timestamp.valueOf(c.getDateCreation()));
        statement.setInt(9, c.getEnseignantId());
        statement.setInt(10, c.getCategorieCourId());
        statement.executeUpdate();
        System.out.println("Ajouter avec succes");
    }

    @Override
    public List<Cours> afficher() throws SQLException {
        List<Cours> cours = new ArrayList<>();
        String sql = "SELECT * FROM `cours`";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Cours c = new Cours();
            c.setId(resultSet.getInt("id"));
            c.setNom(resultSet.getString("nom"));
            c.setDescription(resultSet.getString("description"));
            c.setSlug(resultSet.getString("slug"));
            c.setTypeMedia(resultSet.getString("type_media"));
            c.setMediaUrl(resultSet.getString("media_url"));
            c.setDuree(resultSet.getInt("duree"));
            c.setNiveau(resultSet.getString("niveau"));
            c.setDateCreation(resultSet.getTimestamp("date_creation").toLocalDateTime());
            c.setEnseignantId(resultSet.getInt("enseignant_id"));
            c.setCategorieCourId(resultSet.getInt("categorie_cours_id"));
            cours.add(c);
        }
        return cours;
    }

    @Override
    public void modifier(Cours c) throws SQLException {
        String sql = "UPDATE `cours` SET `nom`=?, `description`=?, `slug`=?, `type_media`=?, `media_url`=?, `duree`=?, `niveau`=?, `date_creation`=?, `enseignant_id`=?, `categorie_cours_id`=? WHERE id=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, c.getNom());
        statement.setString(2, c.getDescription());
        statement.setString(3, c.getSlug());
        statement.setString(4, c.getTypeMedia());
        statement.setString(5, c.getMediaUrl());
        statement.setInt(6, c.getDuree());
        statement.setString(7, c.getNiveau());
        statement.setTimestamp(8, Timestamp.valueOf(c.getDateCreation()));
        statement.setInt(9, c.getEnseignantId());
        statement.setInt(10, c.getCategorieCourId());
        statement.setInt(11, c.getId());
        statement.executeUpdate();
        System.out.println("Modifier avec succes");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `cours` WHERE `id`=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();
        System.out.println("Supprimer avec succes");
    }

    public List<Cours> afficherParCategorie(int categorieId) throws SQLException {
        List<Cours> cours = new ArrayList<>();
        String sql = "SELECT * FROM `cours` WHERE `categorie_cours_id` = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, categorieId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Cours c = new Cours();
            c.setId(rs.getInt("id"));
            c.setNom(rs.getString("nom"));
            c.setDescription(rs.getString("description"));
            c.setSlug(rs.getString("slug"));
            c.setTypeMedia(rs.getString("type_media"));
            c.setMediaUrl(rs.getString("media_url"));
            c.setDuree(rs.getInt("duree"));
            c.setNiveau(rs.getString("niveau"));
            c.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            c.setEnseignantId(rs.getInt("enseignant_id"));
            c.setCategorieCourId(rs.getInt("categorie_cours_id"));
            cours.add(c);
        }
        return cours;
    }

    public List<Cours> afficherParEnseignant(int enseignantId) throws SQLException {
        List<Cours> cours = new ArrayList<>();
        String sql = "SELECT * FROM `cours` WHERE `enseignant_id` = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, enseignantId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Cours c = new Cours();
            c.setId(rs.getInt("id"));
            c.setNom(rs.getString("nom"));
            c.setDescription(rs.getString("description"));
            c.setSlug(rs.getString("slug"));
            c.setTypeMedia(rs.getString("type_media"));
            c.setMediaUrl(rs.getString("media_url"));
            c.setDuree(rs.getInt("duree"));
            c.setNiveau(rs.getString("niveau"));
            c.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            c.setEnseignantId(rs.getInt("enseignant_id"));
            c.setCategorieCourId(rs.getInt("categorie_cours_id"));
            cours.add(c);
        }
        return cours;
    }
}