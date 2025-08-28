package will.dev.artisan_des_saveurs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.*;

@Service
public class BrevoService {

    //@Value("${app.company.email}")
    public static String companyEmail = "btbimportationservice333@gmail.com";

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    public void sendActivationEmail(String to, String token) {
        try {
            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail);
                    //.name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(to);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("Activation de votre compte - Artisan des Saveurs")
                    .htmlContent("Bonjour,<br/><br/>" +
                            "Merci de vous être inscrit sur Artisan des Saveurs!<br/><br/>" +
                            "Pour activer votre compte, veuillez cliquer sur le lien suivant:<br/>" +
                            "<a href=\"https://artisan-des-saveurs.vercel.app/activate?token=" + token + "\">Activer mon compte</a><br/><br/>" +
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
                    .email(companyEmail);
                    //.name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(to);

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject("Réinitialisation de votre mot de passe - Artisan des Saveurs")
                    .htmlContent("Bonjour,<br><br>" +
                            "Vous avez demandé la réinitialisation de votre mot de passe.<br><br>" +
                            "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant:<br>" +
                            "<a href=\"https://artisan-des-saveurs.vercel.app/reset-password?token=" + token + "\">Réinitialiser mon mot de passe</a><br><br>" +
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

    public void sentToCopany(ContactRequest contactRequest, Boolean isFromCart) {
        System.out.println(":: Sent mail to company :: ");
        System.out.println(":: Message :: " + contactRequest.getMessage());
        try {
            System.out.println(":: Début 1 :: ");
            User user = contactRequest.getUser();

            String messageBody = "";
            if (isFromCart){
                messageBody = contactRequest.getMessage();
            }else {
                messageBody = "Client : "+user.getFullName()+"\n\n"
                        + "Email : "+user.getEmail()+".\n\n"
                        + "Téléphone: "+user.getPhone()+".\n\n"
                        + contactRequest.getMessage() + "\n\n";
            }

            // Initialisation du client
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // ⚡ Initialise bien l’authentification
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(companyEmail);
                    //.name("Artisan des saveurs");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo()
                    .email(user.getEmail());

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(Collections.singletonList(recipient))
                    .subject(contactRequest.getSubject())
                    .htmlContent(messageBody);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
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
                    .email(companyEmail);
                    //.name("Artisan des saveurs");

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
                    .htmlContent(message);

            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            System.out.println("Mail envoyé : " + response);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
        }
    }

    public String customMessage(String clientName) {


        String message = String.format("""
                Bonjour %s,

                Merci pour votre message et pour l’intérêt que vous portez à nos produits.

                Nous serions ravis de vous fournir toutes les informations dont vous avez besoin. N’hésitez pas à me préciser les articles ou catégories qui vous intéressent (ex : produits en promotion, nouveautés, produits personnalisés…).

                En attendant, voici quelques éléments clés à propos de notre offre :
                ✅ Produits de qualité rigoureusement sélectionnés
                🚚 Livraison rapide et fiable
                🤝 Service client à votre écoute avant et après la commande

                Vous pouvez également consulter notre catalogue en ligne ici : https://artisan-des-saveurs.vercel.app/catalogue

                Je reste à votre disposition pour toute question complémentaire, un devis ou un accompagnement personnalisé.

                Cordialement,
                Service Client L'Artisan des saveurs
                +237 6 55 00 23 18
                https://artisan-des-saveurs.vercel.app/
                """, clientName);

        return message;
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


