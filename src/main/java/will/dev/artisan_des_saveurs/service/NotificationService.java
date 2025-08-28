package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Value("${app.company.email}")
    private String companyEmail;

    private final JavaMailSender javaMailSender;
    private final BrevoService brevoService;

    public void sendActivationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(companyEmail);
            message.setTo(to);
            message.setSubject("Activation de votre compte - Artisan des Saveurs");
            String msg = "Bonjour,\n\n" +
                    "Merci de vous être inscrit sur Artisan des Saveurs!\n\n" +
                    "Pour activer votre compte, veuillez cliquer sur le lien suivant:\n" +
                    "https://artisan-des-saveurs.vercel.app/activate?token=" + token + "\n\n" +
                    "Ce lien est valide pendant 24 heures.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Artisan des Saveurs";
            message.setText(msg);

            //javaMailSender.send(message);
            User user = new User();
            user.setEmail(to);
            brevoService.sendMail(user, "Activation de votre compte - Artisan des Saveurs", msg);
            logger.info("Email d'activation envoyé à: {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email d'activation à {}: {}", to, e.getMessage());
            // En mode développement, on log le token pour pouvoir tester
            logger.info("Token d'activation pour {}: {}", to, token);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(companyEmail);
            message.setTo(to);
            message.setSubject("Réinitialisation de votre mot de passe - Artisan des Saveurs");
            String msg = "Bonjour,\n\n" +
                    "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
                    "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant:\n" +
                    "https://artisan-des-saveurs.vercel.app/reset-password?token=" + token + "\n\n" + // http://localhost:4200/reset-password?token=
                    "Ce lien est valide pendant 1 heure.\n\n" +
                    "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe Artisan des Saveurs";
            message.setText(msg);

            //javaMailSender.send(message);
            User user = new User();
            user.setEmail(to);
            brevoService.sendMail(user, "Activation de votre compte - Artisan des Saveurs", msg);
            logger.info("Email de réinitialisation envoyé à: {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de réinitialisation à {}: {}", to, e.getMessage());
            // En mode développement, on log le token pour pouvoir tester
            logger.info("Token de réinitialisation pour {}: {}", to, token);
        }
    }

    public void sentToCopany(ContactRequest contactRequest, Boolean isFromCart) {
        System.out.println(":: Sent mail to company :: ");
        System.out.println(":: Message :: " + contactRequest.getMessage());
        try {
            System.out.println(":: Début 1 :: ");
            User user = contactRequest.getUser();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(contactRequest.getSubject());

            String messageBody = "";
            if (isFromCart){
                System.out.println(":: Début 2 :: ");
                messageBody = contactRequest.getMessage();
                System.out.println(":: Fin 2 :: ");
            }else {
                messageBody = "Client : "+user.getFullName()+"\n\n"
                        + "Email : "+user.getEmail()+".\n\n"
                        + "Téléphone: "+user.getPhone()+".\n\n"
                        + contactRequest.getMessage() + "\n\n";
            }


            message.setFrom(companyEmail);
            message.setTo(companyEmail);

            message.setText(messageBody);
            System.out.println(":: Fin 2 :: ");
            //javaMailSender.send(message);
            User newUser = new User();
            newUser.setEmail(companyEmail);
            brevoService.sendMail(newUser, contactRequest.getSubject(), messageBody);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_EXCEPTION: " + e);
        }
    }

    public void sentResponseToCustomerFromCartPage(User savedUser, String customerMessage) {
        try {
            String subject = "Merci pour votre commande – L'Artisan des saveurs";
            String message = customerMessage;

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(companyEmail);
            mail.setTo(savedUser.getEmail());
            mail.setSubject(subject);
            mail.setText(message);
            //javaMailSender.send(mail);
            brevoService.sendMail(savedUser, subject, message);
        } catch (Exception e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
        }
    }

    public void sentResponseToCustomerFromContactPage(User savedUser) {
        try {
            String subject = "Merci pour votre intérêt – Informations sur nos produits";
            String message = customMessage(savedUser.getFullName());

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(companyEmail);
            mail.setTo(savedUser.getEmail());
            mail.setSubject(subject);
            mail.setText(message);
            //javaMailSender.send(mail);
            User newUser = new User();
            newUser.setEmail(companyEmail);
            brevoService.sendMail(newUser, subject, message);
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
}
