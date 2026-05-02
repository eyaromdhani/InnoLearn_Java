package org.example.Services;

import org.example.Entities.Cours;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoursServiceTest {

    static CoursService service;

    @BeforeAll
    static void setup() {
        // Initialisation de l'instance permettant les tests
        service = new CoursService();
    }

    @Test
    @Order(1)
    void testAjouterCours() throws SQLException {
        // 1. Initialiser le cours à insérer
        Cours cours = new Cours(
                "TestNom",
                "TestDescription",
                "test-slug",
                "Vidéo",
                "http://test.url",
                10,
                "Débutant",
                LocalDateTime.now(),
                "ProfTest",
                1 // ID catégorie simulée
        );

        // 2. Action (Ajout)
        service.ajouter(cours);

        // 3. Vérification que la liste n'est pas vide (assertFalse)
        List<Cours> tousLesCours = service.afficher();
        assertFalse(tousLesCours.isEmpty(), "La base de données ne devrait pas être vide");

        // 4. Vérification que notre nouveau cours y figure bien (assertTrue)
        assertTrue(
                tousLesCours.stream().anyMatch(c -> "TestNom".equals(c.getNom())),
                "Le cours 'TestNom' devrait être présent dans la base."
        );
    }

    @AfterEach
    void cleanUp() throws SQLException {
        // Après chaque test, on efface le cours créé pour ne pas polluer la base.
        List<Cours> tousLesCours = service.afficher();

        if (!tousLesCours.isEmpty()) {
            Cours last = tousLesCours.get(tousLesCours.size() - 1);
            
            // Sécurité : On supprime UNIQUEMENT si le nom correspond à notre test
            if ("TestNom".equals(last.getNom())) {
                service.supprimer(last.getId());
                System.out.println("Nettoyage: Cours de test supprimé.");
            }
        }
    }
}
