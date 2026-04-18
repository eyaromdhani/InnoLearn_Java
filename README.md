# 🎓 InnoLearn

Une application développée en **JavaFX** .
## 🚀 Fonctionnalités Principales

Ce projet intègre les fonctionnalités liées à différents rôles utilisateurs (Enseignants et Étudiants) :

*   **👨‍🏫 Espace Enseignant :
*   **🎓 Espace Étudiant (Système de Favoris) :

## 🛠️ Stack Technique

*   **Langage :** Java
*   **Interface Graphique :** JavaFX (fichiers `.fxml` et `Controllers`)
*   **Base de Données :** MySQL
*   **Communication BDD :** JDBC
*   **Gestionnaire de dépendances :** Maven
*   **Architecture :** Architecture en Couches séparées (Vue -> Contrôleur -> Service -> Base de Données)

## 📁 Architecture du Projet

L'architecture est structurée pour séparer l'interface de la logique métier de la façon suivante :
 (`Cours`, `Categorie_cours`, `Favoris`).
*   `Services/` : Gère la logique métier et l'accès à la base de données via SQL .
*   `Controllers/` : Relie l'interface JavaFX à la logique métier (gestion des clics, récupération des champs).
*   `utiles/` : Classes utilitaires, notamment le Singleton de connexion à la base de données (`MyDataBase`).
*   `resources/` : Contient toutes les vues interactives (`.fxml`) et la charte graphique (`.css`).

## ⚙️ Installation & Lancement

1.  **Prérequis environnementaux :**
    *   JDK (Java Development Kit) installé.
    *   Un serveur MySQL opérationnel (via XAMPP, WAMP, ou autre).
2.  **Configuration de la Base de Données :**
    *   Créer une base de données.
    *   Importer la structure des tables .
    *   Les identifiants de connexion modifiables se trouvent dans `src/main/java/org/example/utiles/MyDataBase.java` .
3.  **Lancement :**
    *   Ouvrir le projet dans IntelliJ IDEA ou Eclipse.
    *   Recharger le projet Maven pour télécharger les dépendances (JavaFX / connecteur MySQL).
    *   Exécuter le fichier principal `MainFX.java` pour lancer l'application graphique.

## Contributors
Eya Allah Romdhani
Myriam ben Azzoun
Alae Naoui
Rayen Sboui
Mohamed Aziz Mesalmani
Zied Ibrahim
