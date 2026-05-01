package org.example.Services;

import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavorisService {

    Connection conn;

    public FavorisService() {
        conn = MyDataBase.getInstance().getConnection();
    }

    public void ajouterFavori(int coursId) throws SQLException {
        if (!estFavori(coursId)) {
            String sql = "INSERT INTO `favoris`(`cours_id`) VALUES (?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, coursId);
            statement.executeUpdate();
            System.out.println("Cours ajouté aux favoris.");
        }
    }

    public void supprimerFavori(int coursId) throws SQLException {
        String sql = "DELETE FROM `favoris` WHERE `cours_id` = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, coursId);
        statement.executeUpdate();
        System.out.println("Cours retiré des favoris.");
    }

    public boolean estFavori(int coursId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM `favoris` WHERE `cours_id` = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, coursId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }

    public List<Integer> getTousLesFavoris() throws SQLException {
        List<Integer> favorisIds = new ArrayList<>();
        String sql = "SELECT `cours_id` FROM `favoris`";
        PreparedStatement statement = conn.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            favorisIds.add(resultSet.getInt("cours_id"));
        }
        return favorisIds;
    }
}
