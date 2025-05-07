package controllers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {

    private final String username = "smtp@mailtrap.io"; // from Mailtrap SMTP settings
    private final String password = "6c422fdd37b0c6ed397f31fc293602aa"; // from Mailtrap SMTP settings
    private final String host = "live.smtp.mailtrap.io"; // not send.api.mailtrap.io
    private final String port = "587";

    public void sendPurchaseNotification(String productName, int quantity, String clientEmail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.debug", "true"); // Optional: for debug

        System.out.println("Tentative d'envoi d'email à: " + clientEmail);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("hello@demomailtrap.co", "Covoituni - Système d'inventaire"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(clientEmail)
            );
            message.setSubject("Notification d'achat: " + productName);
            message.setText("Cher administrateur,\n\n" +
                    "Un achat a été effectué:\n" +
                    "Produit: " + productName + "\n" +
                    "Quantité: " + quantity + "\n" +
                    "Client: " + clientEmail + "\n\n" +
                    "Cordialement,\n" +
                    "Système de gestion d'inventaire");

            Transport.send(message);

            System.out.println("Email notification sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Failed to set email sender name: " + e.getMessage());
        }
    }
}
