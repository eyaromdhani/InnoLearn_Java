package org.example;

import org.example.Entities.Categorie_cours;
import org.example.Entities.Cours;
import org.example.Services.CategorieCoursService;
import org.example.Services.CoursService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CategorieCoursService cs = new CategorieCoursService();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse("10-04-2026", formatter);
            //cs.ajouter(new Categorie_cours("JAVA", "pratiquer code Java", "Débutant", date));
            //cs.supprimer(8);
            //Categorie_cours c =new Categorie_cours("Cybersécurité & Ethical Hacking", "Apprenez à identifier et contrer les vulnérabilités systèmes avec des outils professionnels", "Avancé", date);
            Categorie_cours c =new Categorie_cours("Data Science avec Python", "Analysez et visualisez des données réelles avec Pandas, NumPy et Matplotlib", "Débutant", date);
            c.setId(1);
            cs.modifier(c);
            System.out.println(cs.afficher());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



    }
}