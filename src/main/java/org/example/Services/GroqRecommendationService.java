package org.example.Services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.example.Entities.Cours;

public class GroqRecommendationService {

    private static final String API_KEY = "GROQ_API_KEY";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";

    public String recommanderParCriteres(String motCle, String niveau, String coursAimes, List<Cours> tousLesCours) {

        String disponibles = tousLesCours.stream()
                .map(c -> "[ID: " + c.getId() + "] " + c.getNom() + " (" + c.getNiveau() + ")")
                .collect(Collectors.joining(" | "));

        String prompt = "Voici les cours disponibles : " + disponibles + ". " +
                "Sache que l'étudiant a déjà adoré ces cours dans le passé : ["
                + (coursAimes.isEmpty() ? "Aucun pour l'instant" : coursAimes) + "]. " +
                "Maintenant, il cherche un cours de niveau '" + niveau + "' lié au mot-clé : '" + motCle + "'. " +
                "Trouve 2 cours disponibles qui pourraient lui plaire en te basant sur ses goûts et sa recherche actuelle. "
                +
                "Règle ABSOLUE : Tu dois retourner UNIQUEMENT leurs numéros d'ID séparés par une virgule (exemple exact: 4,12). "
                +
                "N'AJOUTE AUCUN TEXTE, ni point, ni phrase.";

        System.out.println("PROMPT ENVOYÉ À GROQ : " + prompt);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL_NAME);

            JSONArray messages = new JSONArray();
            JSONObject messageContent = new JSONObject();
            messageContent.put("role", "system"); // Un 'system' prompt force le modèle à respecter les règles à 100%
            messageContent.put("content",
                    "Tu es une machine qui renvoie uniquement des chiffres séparés par des virgules.");
            messages.put(messageContent);

            JSONObject messageUser = new JSONObject();
            messageUser.put("role", "user");
            messageUser.put("content", prompt);
            messages.put(messageUser);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.1); // 0.1 = Rendu robotique hyper prévisible

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("RÉPONSE BRUTE DE GROQ : " + response.body());

            JSONObject jsonResponse = new JSONObject(response.body());
            if (jsonResponse.has("error"))
                return null;

            String recommandationTexte = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message").getString("content");

            return recommandationTexte.trim(); // Renverra un truc propre comme "5, 12"

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String resumerTextePDF(String texteBrut) {
        // Pour éviter que la requête soit refusée parce que le PDF fait 1000 pages,
        // on peut limiter le texte envoyé aux 25 000 premiers caractères par sécurité
        String texteLimite = texteBrut.length() > 25000 ? texteBrut.substring(0, 25000) : texteBrut;

        String prompt = "Tu es un professeur expert. Voici le contenu d'un cours en PDF. " +
                "Résume-moi ce cours en français de manière hyper structurée avec des puces (- ) " +
                "pour faire une fiche de révision parfaite. Va directement au but sans phrase d'introduction.\n\n" +
                texteLimite;

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL_NAME); // On utilise toujours llama-3.3-70b-versatile

            JSONArray messages = new JSONArray();
            JSONObject messageContent = new JSONObject();
            messageContent.put("role", "system");
            messageContent.put("content", "Tu es un assistant de synthèse.");
            messages.put(messageContent);

            JSONObject messageUser = new JSONObject();
            messageUser.put("role", "user");
            messageUser.put("content", prompt);
            messages.put(messageUser);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.4); // Un peu de créativité pour bien formuler le résumé

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            if (jsonResponse.has("error"))
                return "Erreur API : " + jsonResponse.getJSONObject("error").getString("message");

            return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Une erreur réseau s'est produite lors du résumé IA.";
        }
    }

}
