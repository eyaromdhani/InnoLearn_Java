package org.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class EmailSender {

    // IMPORTANT: Use the API Key (v3) starting with "xkeysib-", NOT the SMTP Key "xsmtpsib-".
    // Find it at: Brevo Dashboard -> SMTP & API -> API Keys

    //private static final String API_KEY = "";
    private static final String SENDER_EMAIL = "chzied16@gmail.com";
    private static final String SENDER_NAME = "InnoLearn Team";

    public static void sendConfirmationEmail(String recipientEmail, String userName, String eventTitle) {
        
        // Use a thread or CompletableFuture to not block the UI
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                // Prepare JSON payload for Brevo API (v3)
                JSONObject payload = new JSONObject();
                
                // Sender
                JSONObject sender = new JSONObject();
                sender.put("name", SENDER_NAME);
                sender.put("email", SENDER_EMAIL);
                payload.put("sender", sender);

                // Recipient
                JSONObject to = new JSONObject();
                to.put("email", recipientEmail);
                to.put("name", userName);
                JSONArray toArray = new JSONArray();
                toArray.put(to);
                payload.put("to", toArray);

                // Content
                payload.put("subject", "Confirmation de Participation : " + eventTitle);
                
                String content = "Bonjour " + userName + ",\n\n"
                        + "Nous avons le plaisir de vous confirmer que votre inscription à l'événement \"" + eventTitle + "\" a été validée par l'administration !\n\n"
                        + "Détails de l'événement :\n"
                        + "- Événement : " + eventTitle + "\n\n"
                        + "Nous avons hâte de vous y voir.\n\n"
                        + "Cordialement,\n"
                        + "L'équipe InnoLearn";
                
                payload.put("textContent", content);

                // Build Request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                        .header("accept", "application/json")
                        //.header("api-key",API_KEY)
                        .header("content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                // Send Request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    System.out.println("✅ Email sent successfully via API to " + recipientEmail);
                } else {
                    System.err.println("❌ Failed to send email via API. Status code: " + response.statusCode());
                    System.err.println("Response Body: " + response.body());
                    
                    if (response.statusCode() == 401) {
                        System.err.println("HINT: Your API key seems invalid. Ensure you are using an API Key (xkeysib-), not an SMTP Key (xsmtpsib-).");
                    } else if (response.statusCode() == 400) {
                        System.err.println("HINT: Check if the sender email (" + SENDER_EMAIL + ") is verified in your Brevo account.");
                    }
                }

            } catch (Exception e) {
                System.err.println("Error while sending email via API: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
