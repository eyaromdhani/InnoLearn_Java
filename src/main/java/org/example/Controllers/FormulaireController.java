package org.example.Controllers;

import org.example.Entities.Formulaire;
import org.example.Services.ServiceFormulaire;

import java.sql.SQLException;
import java.util.List;

public class FormulaireController {

    private final ServiceFormulaire service = new ServiceFormulaire();

    public void ajouter(Formulaire formulaire) throws SQLException {
        service.ajouter(formulaire);
    }

    public void modifier(Formulaire formulaire) throws SQLException {
        service.modifier(formulaire);
    }

    public void supprimer(int id) throws SQLException {
        service.supprimer(id);
    }

    public List<Formulaire> afficher() throws SQLException {
        return service.afficher();
    }

    public List<Formulaire> rechercherParTitre(String titre) throws SQLException {
        return service.rechercherParTitre(titre);
    }
}
