package org.example.Services;

import org.example.Entities.Categorie_cours;
import org.example.utiles.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieCoursService implements IService<Categorie_cours> {

    Connection conn;
    public CategorieCoursService (){
        conn = MyDataBase.getInstance().getConnection();

    }

    @Override
    public void ajouter(Categorie_cours c) throws SQLException {
        String sql = "INSERT INTO `categorie_cours`(`titre`, `description`, `niveau`, `datepublication`) " +
                "VALUES ('" + c.getTitre() + "','" + c.getDescription() + "','" + c.getNiveau() + "','" + c.getDatepublication() + "')";

        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);
        System.out.println("Ajouter avec succes");
    }

    @Override
    public List<Categorie_cours> afficher() throws SQLException{
        List<Categorie_cours>  categorie_cours=new ArrayList<>();
        String sql = "SELECT * FROM `categorie_cours`";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            Categorie_cours c = new Categorie_cours();
            c.setId(resultSet.getInt("id"));
            c.setTitre(resultSet.getString("titre"));
            c.setDescription(resultSet.getString("description"));
            c.setNiveau(resultSet.getString("niveau"));
            c.setDatepublication(resultSet.getDate("datepublication").toLocalDate());
            categorie_cours.add(c);
        }
        return categorie_cours;
    }

    @Override
    public void modifier(Categorie_cours c) throws SQLException {
        String sql = "UPDATE `categorie_cours` SET `titre`=?,`description`=?,`niveau`=?,`datepublication`=? WHERE id=?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, c.getTitre());
        statement.setString(2, c.getDescription());
        statement.setString(3, c.getNiveau());
        statement.setDate(4, Date.valueOf(c.getDatepublication()));
        statement.setInt(5, c.getId());
        statement.executeUpdate();
        System.out.println("Modifier avec succes");

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `categorie_cours` WHERE `id`= ?";
        PreparedStatement  statement = conn.prepareStatement(sql);
        statement.setInt(1,id);
        statement.executeUpdate();
        System.out.println("Supprimer avec succes");
    }




}