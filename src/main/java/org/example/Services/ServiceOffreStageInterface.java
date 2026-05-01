package org.example.Services;

import org.example.Entities.OffreStage;

import java.sql.SQLException;
import java.util.List;

public interface ServiceOffreStageInterface {
    void ajouter(OffreStage os) throws SQLException;
    List<OffreStage> afficherAll() throws SQLException;
    OffreStage getById(int id) throws SQLException;
    void modifier(OffreStage os) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<OffreStage> afficherParRecruteur(int idRecruteur) throws SQLException;
}
