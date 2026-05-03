package org.example.utils;

import java.sql.Connection;
import java.sql.Statement;

public class AlterDb {
    public static void main(String[] args) {
        try {
            Connection conn = MyDataBase.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            System.out.println("Ajout de la colonne enseignant_id...");
            try {
                stmt.execute("ALTER TABLE cours ADD COLUMN enseignant_id INT");
                System.out.println("Colonne ajoutée.");
            } catch (Exception e) {
                System.out.println("La colonne existe peut-être déjà : " + e.getMessage());
            }

            System.out.println("Ajout de la contrainte de clé étrangère...");
            try {
                stmt.execute("ALTER TABLE cours ADD CONSTRAINT fk_cours_user FOREIGN KEY (enseignant_id) REFERENCES user(id)");
                System.out.println("Contrainte ajoutée.");
            } catch (Exception e) {
                System.out.println("La contrainte existe peut-être déjà : " + e.getMessage());
            }

            System.out.println("Opération terminée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
