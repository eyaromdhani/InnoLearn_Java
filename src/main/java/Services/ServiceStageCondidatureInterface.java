package Services;

import Entities.StageCondidature;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ServiceStageCondidatureInterface {
    void ajouter(StageCondidature sc) throws SQLException;
    void supprimer(int id) throws SQLException;
    void modifier(StageCondidature sc) throws SQLException;
    List<StageCondidature> afficherAll() throws SQLException;
    StageCondidature getById(int id) throws SQLException;
    StageCondidature getProfileEtudiant(int idEtudiant) throws SQLException;
    Map<String, Integer> getStatsCandidaturesForRecruiter(int idRecruteur) throws SQLException;
}
