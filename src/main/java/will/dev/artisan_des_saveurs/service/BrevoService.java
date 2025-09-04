package will.dev.artisan_des_saveurs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BrevoService {

    @Value("${app.company.email}")
    private String companyEmail;

    @Value("${app.company.whatsapp.number}")
    private String companyNumber;

    @Value("${app.env.apiUrl}")
    private String apiUrl;

    @Value("${app.env.apiKey}")
    private String brevoApiKey;

    public void sendActivationEmail(String to, String token) {
        try {
            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(to);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("Activation de votre compte - Artisan des Saveurs")
                    .htmlContent("Bonjour,<br/><br/>" +
                            "Merci de vous être inscrit sur Artisan des Saveurs!<br/><br/>" +
                            "Pour activer votre compte, veuillez cliquer sur le lien suivant:<br/>" +
                            "<a href=\""+apiUrl+"/activate?token=" + token + "\">Activer mon compte</a><br/><br/>" +
                            "Ce lien est valide pendant 24 heures.<br/><br/>" +
                            "Cordialement,<br/>" +
                            "L'équipe Artisan des Saveurs");


            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);

        } catch (Exception e) {
            throw new RuntimeException("ACTIVATION_EMAIL_EXCEPTION: " + e);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(to);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("Réinitialisation de votre mot de passe - Artisan des Saveurs")
                    .htmlContent("Bonjour,<br><br>" +
                            "Vous avez demandé la réinitialisation de votre mot de passe.<br><br>" +
                            "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant:<br>" +
                            "<a href=\""+apiUrl+"/reset-password?token=" + token + "\">Réinitialiser mon mot de passe</a><br><br>" +
                            "Ce lien est valide pendant 1 heure.<br><br>" +
                            "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.<br><br>" +
                            "Cordialement,<br>" +
                            "L'équipe Artisan des Saveurs");


            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("RESET_PASSWORD_EMAIL_EXCEPTION: " + e);
        }
    }

    public void sentToCompany(ContactRequest contactRequest, Boolean isFromCart) {
        System.out.println(":: Sent mail to company :: ");
        System.out.println(":: Message :: " + contactRequest.getMessage());
        try {
            System.out.println(":: Début 1 :: ");
            User user = contactRequest.getUser();

            String messageBody = "";
            String textContent = "";
            if (isFromCart){
                messageBody = contactRequest.getMessage();
                textContent ="Bonjour " + user.getFullName() + ",\n\n" +
                        "Merci pour votre commande. Notre équipe vous contactera bientôt.\n\n" +
                        "Service Client – L'Artisan des saveurs";
            }else {
                messageBody =
                        "<html><body>" +
                            "<p><strong>Client :</strong> " + user.getFullName() + "</p>" +
                            "<p><strong>Email :</strong> " + user.getEmail() + "</p>" +
                            "<p><strong>Téléphone :</strong> " + (user.getPhone() != null ? user.getPhone() : "N/A") + "</p>" +
                            "<p>" + contactRequest.getMessage() + "</p>" +
                        "</body></html>";
                textContent =
                        "Client: " + user.getFullName() +
                        "\nEmail: " + user.getEmail() +
                        "\nTéléphone: " + (user.getPhone() != null ? user.getPhone() : "N/A") +
                        "\nMessage: " + contactRequest.getMessage();
            }

            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(companyEmail);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("[Demande client] : " + contactRequest.getSubject())
                    .htmlContent(messageBody) // version HTML
                    .textContent(textContent);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (ApiException e) {
            System.err.println("BREVO_API_EXCEPTION : " + e.getResponseBody());
            throw new RuntimeException("COMPANY_EMAIL_EXCEPTION: " + e);
        }
    }

    public void sentResponseToCustomerFromCartPage(User savedUser, String customerMessage) {
        try {
            String subject = "Merci pour votre commande – L'Artisan des saveurs";
            String message = customerMessage;

            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(savedUser.getEmail());

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject(subject)
                    .htmlContent(message);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
        }
    }

    public void sentResponseToCustomerFromContactPage(User savedUser) {
        try {
            String subject = "Merci pour votre intérêt – Informations sur nos produits";
            String message = customMessage(savedUser.getFullName());

            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(savedUser.getEmail());

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject(subject)
                    .htmlContent(message)
                    .textContent("Bonjour " + savedUser.getFullName() + ",\n\n" +
                    "Merci pour votre message. Notre équipe vous contactera bientôt.\n\n" +
                    "Service Client – L'Artisan des saveurs");


            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
        }
    }

    public String customMessage(String clientName) {
        return String.format("""
        <html>
          <body style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">
            <p>Bonjour <strong>%s</strong>,</p>
            <p>Merci pour votre message et pour l’intérêt que vous portez à nos produits.</p>
            <p>
              Notre équipe service client prendra contact avec vous rapidement 
              afin de répondre à vos questions.
            </p>
            <p>
              En attendant, vous pouvez consulter notre site officiel pour plus d’informations :<br/>
              <a href="%s">artisan-des-saveurs.vercel.app</a>
            </p>
            <br/>
            <p>Cordialement,</p>
            <p>
              <strong>Service Client – L'Artisan des saveurs</strong><br/>
              📞 %s
            </p>
          </body>
        </html>
        """, clientName, apiUrl, companyNumber);
    }

    public void notifyAdminNewUser(User user) {
        try{
            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            // Date formatée
            String creationDate = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String body = "Bonjour,<br><br>" +
                    "Un nouvel utilisateur vient de s’inscrire sur la plateforme Artisan des Saveurs.<br><br>" +
                    "Voici les informations du compte :<br>" +
                    "- Nom : " + user.getFullName() + "<br>" +
                    "- Email : " + user.getEmail() + "<br>" +
                    "- Date de création : " + creationDate + "<br><br>" +
                    "Vous pouvez consulter son profil dans l’espace d’administration.<br><br>" +
                    "Cordialement,<br>" +
                    "L’équipe Artisan des Saveurs";

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(companyEmail);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("🆕 Nouveau compte créé sur Artisan des Saveurs")
                    .htmlContent(body);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_TO_ADMIN_EXCEPTION: " + e);
        }
    }

    public void notifyAdminUserDeleted(User user) {
        try{
            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            // Date formatée
            String creationDate = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String body = "Bonjour,<br><br>" +
                    "Un utilisateur a supprimé son compte de la plateforme Artisan des Saveurs.<br><br>" +
                    "Voici les informations du compte supprimé :<br>" +
                    "- Nom : " + user.getFullName() + "<br>" +
                    "- Email : " + user.getEmail() + "<br>" +
                    "- Date de création : " + creationDate + "<br><br>" +
                    "Ceci est une notification automatique à titre informatif.<br><br>" +
                    "Cordialement,<br>" +
                    "L’équipe Artisan des Saveurs";

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail)
                    .name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(companyEmail);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("🗑️ Compte supprimé sur Artisan des Saveurs")
                    .htmlContent(body);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_TO_ADMIN_EXCEPTION: " + e);
        }
    }

    public void sendMail(User user, String subject, String msg) throws Exception {
        // Initialisation du client
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // ⚡ Initialise bien l’authentification
        defaultClient.setApiKey(brevoApiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

        SendSmtpEmailSender sender = new SendSmtpEmailSender()
                .email(companyEmail)
                .name("Artisan des saveurs");

        SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                .email(user.getEmail());

        SendSmtpEmail email = new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(recipient))
                .subject(subject)
                .htmlContent(msg);

        CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
        System.out.println("Mail envoyé : " + response);
    }
}


