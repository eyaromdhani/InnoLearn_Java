package org.example.Services;

import org.example.Entities.Categorie_cours;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategorieCoursServiceTest {
    static CategorieCoursService service;

    @BeforeAll
    static void setup() {
        // Initialisation du service (exécutée une seule fois avant tous les tests)
        service = new CategorieCoursService();
    }

    @Test
    @Order(1)
    void testAjouterCategorie() throws SQLException {
        // 1. Création d'une catégorie "Test"
        Categorie_cours cat = new Categorie_cours("TestTitre", "TestDescription", "TestNiveau", LocalDate.now());
        
        // 2. Ajout via le service
        service.ajouter(cat);
        
        // 3. Vérification que la liste n'est pas vide
        List<Categorie_cours> categories = service.afficher();
        assertFalse(categories.isEmpty());
        
        // 4. Vérification que la catégorie ajoutée est bien présente
        assertTrue(
                categories.stream().anyMatch(c -> "TestTitre".equals(c.getTitre()))
        );
    }

    @AfterEach
    void cleanUp() throws SQLException {
        // Nettoyage automatique: "Un bon test ne laisse aucune trace"
        List<Categorie_cours> categories = service.afficher();
        
        if (!categories.isEmpty()) {
            Categorie_cours last = categories.get(categories.size() - 1);
            
            // J'ai ajouté le petit 'if' de sécurité dont nous avons discuté !
            // Cela s'assure qu'on ne supprime QUE si c'est bien notre catégorie "TestTitre"
            if ("TestTitre".equals(last.getTitre())) {
                service.supprimer(last.getId());
                System.out.println("Nettoyage: Catégorie de test supprimée.");
            }
        }
    }
}
