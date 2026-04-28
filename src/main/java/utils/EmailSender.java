package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT_TLS = "587";
    private static final String SMTP_PORT_SSL = "465";
    private static final String SENDER_EMAIL = "benazzoun.myriam@gmail.com";
    private static final String SENDER_PASSWORD = "auac ozai dlpi eoxl";

    public static void sendAcceptanceEmail(String recipientEmail, String studentName, String offerTitle) {
        // Try with Port 587 (STARTTLS) first
        if (!sendEmail(recipientEmail, studentName, offerTitle, SMTP_PORT_TLS, true)) {
            System.out.println("Échec avec le port 587, tentative avec le port 465...");
            // Fallback to Port 465 (SSL)
            sendEmail(recipientEmail, studentName, offerTitle, SMTP_PORT_SSL, false);
        }
    }

    private static boolean sendEmail(String recipientEmail, String studentName, String offerTitle, String port, boolean useStartTLS) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", port);
        
        // Timeouts to prevent hanging
        prop.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
        prop.put("mail.smtp.timeout", "10000");
        prop.put("mail.smtp.writetimeout", "10000");

        if (useStartTLS) {
            prop.put("mail.smtp.starttls.enable", "true");
        } else {
            prop.put("mail.smtp.socketFactory.port", port);
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            prop.put("mail.smtp.ssl.enable", "true");
        }
        
        prop.put("mail.smtp.ssl.trust", SMTP_HOST);
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Félicitations ! Votre candidature a été acceptée - InnoLearn");

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; border: 1px solid #ddd; border-radius: 10px; overflow: hidden;'>"
                    + "<div style='background: linear-gradient(to right, #1b004a, #27ae60); padding: 20px; text-align: center; color: white;'>"
                    + "<h1 style='margin: 0;'>InnoLearn</h1>"
                    + "</div>"
                    + "<div style='padding: 30px;'>"
                    + "<h2>Bonjour " + studentName + ",</h2>"
                    + "<p>Nous avons le plaisir de vous informer que votre candidature pour l'offre : <strong>" + offerTitle + "</strong> a été <strong>acceptée</strong> !</p>"
                    + "<div style='background-color: #f8f9fa; border-left: 4px solid #27ae60; padding: 15px; margin: 20px 0;'>"
                    + "<p style='margin: 0;'>Vous êtes invité à un <strong>entretien</strong> prochainement. L'équipe de recrutement vous contactera dès que possible pour fixer une date et une heure.</p>"
                    + "</div>"
                    + "<p>En attendant, n'hésitez pas à préparer vos questions et à revoir les détails de l'offre sur votre tableau de bord InnoLearn.</p>"
                    + "<p>Félicitations encore pour cette étape importante !</p>"
                    + "<br>"
                    + "<p>Cordialement,<br><strong>L'équipe InnoLearn</strong></p>"
                    + "</div>"
                    + "<div style='background-color: #f1f3f9; padding: 15px; text-align: center; font-size: 12px; color: #7f8c8d;'>"
                    + "<p>Ceci est un message automatique, merci de ne pas y répondre directement.</p>"
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email envoyé avec succès à : " + recipientEmail + " via port " + port);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erreur d'envoi sur le port " + port + " : " + e.getMessage());
            return false;
        }
    }
}

