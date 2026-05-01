package org.example.Controllers;

import org.example.Entities.Question;
import org.example.Services.ServiceQuestion;

import java.sql.SQLException;
import java.util.List;

public class QuestionController {

    private final ServiceQuestion service = new ServiceQuestion();

    public void ajouter(Question question) throws SQLException {
        service.ajouter(question);
    }

    public void modifier(Question question) throws SQLException {
        service.modifier(question);
    }

    public void supprimer(int id) throws SQLException {
        service.supprimer(id);
    }

    public List<Question> afficher() throws SQLException {
        return service.afficher();
    }
}
