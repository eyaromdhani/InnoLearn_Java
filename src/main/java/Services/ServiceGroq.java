package Services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServiceGroq {

    private final String API_KEY;
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public ServiceGroq() {
        String key = System.getenv("GROQ_API_KEY");
        if (key == null || key.isEmpty()) {
            // MOCK/Fallback key or alert
            this.API_KEY = "mock_key"; // Replace with your actual Groq key or Env var
        } else {
            this.API_KEY = key;
        }
    }

    public String generateCvIntro(String domaine, String niveau, String competences) {
        if (API_KEY.equals("mock_key")) {
            return String.format("Étudiant(e) passionné(e) en %s de niveau %s. Je maîtrise : %s. " +
                    "Je suis prêt(e) à relever de nouveaux défis !", domaine, niveau, competences);
        }

        String prompt = String.format(
                "Tu es un expert en rédaction de CV. Génère une accroche professionnelle courte (3 phrases maximum) pour un(e) étudiant(e) :\n" +
                "- Domaine : %s\n" +
                "- Niveau académique : %s\n" +
                "- Compétences : %s\n" +
                "Rédige une accroche concise, impactante et professionnelle en français.",
                domaine, niveau, competences
        );

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Tu es un assistant expert en rédaction de CV professionnels.");
            
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            messages.put(systemMessage);
            messages.put(userMessage);

            jsonBody.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                JSONObject responseObject = new JSONObject(response.toString());
                return responseObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                return "Je suis un(e) étudiant(e) très motivé(e) cherchant une opportunité de stage stimulante dans mon domaine d'étude.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Étudiant(e) passionné(e) cherchant de nouvelles expériences.";
        }
    }

    public JSONObject generateCareerAdvice(String domaine, String niveau, String competences) {
        if (API_KEY.equals("mock_key")) {
            JSONObject mock = new JSONObject();
            mock.put("standing", 75);
            mock.put("skillGaps", new JSONArray().put("Maîtrise avancée de " + domaine).put("Expérience pratique en entreprise"));
            mock.put("actionPlan", new JSONArray().put("Suivre une formation certifiante en " + domaine).put("Chercher un stage de fin d'études"));
            return mock;
        }

        String prompt = String.format(
                "Analyse ce profil étudiant :\n" +
                "- Domaine: %s\n" +
                "- Niveau: %s\n" +
                "- Compétences: %s\n" +
                "\n" +
                "Réponds UNIQUEMENT en JSON avec les clés :\n" +
                "- standing (int 0-100)\n" +
                "- skillGaps (array de strings)\n" +
                "- actionPlan (array de strings)\n" +
                "La réponse doit être uniquement le JSON, sans texte autour.",
                domaine, niveau, competences
        );

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Tu es un conseiller carrière expert. Réponds UNIQUEMENT en JSON.");
            
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            messages.put(systemMessage);
            messages.put(userMessage);

            jsonBody.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                JSONObject responseObject = new JSONObject(response.toString());
                String content = responseObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
                
                // Extract JSON using substring to find first '{' and last '}'
                int firstBrace = content.indexOf("{");
                int lastBrace = content.lastIndexOf("}");
                if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                    content = content.substring(firstBrace, lastBrace + 1);
                }
                
                return new JSONObject(content);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public JSONObject calculateMatchingScore(String offerDetails, String studentProfile) {
        if (API_KEY.equals("mock_key")) {
            JSONObject mock = new JSONObject();
            mock.put("score", 85);
            mock.put("justification", "Le profil de l'étudiant correspond bien a la demande");
            return mock;
        }

        String prompt = String.format(
                "Analyse l'adéquation entre cette offre de stage et ce profil candidat :\n\n" +
                "OFFRE :\n%s\n\n" +
                "CANDIDAT :\n%s\n\n" +
                "Réponds UNIQUEMENT en JSON avec les clés :\n" +
                "- score (int 0-100)\n" +
                "- justification (string courte en français)\n" +
                "La réponse doit être uniquement le JSON.",
                offerDetails, studentProfile
        );

        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.5);

            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Tu es un expert en recrutement. Réponds UNIQUEMENT en JSON.");
            
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            messages.put(systemMessage);
            messages.put(userMessage);

            jsonBody.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                JSONObject responseObject = new JSONObject(response.toString());
                String content = responseObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
                
                int firstBrace = content.indexOf("{");
                int lastBrace = content.lastIndexOf("}");
                if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                    content = content.substring(firstBrace, lastBrace + 1);
                }
                
                return new JSONObject(content);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
