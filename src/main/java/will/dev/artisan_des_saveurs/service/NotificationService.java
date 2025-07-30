package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
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

    @Value("${app.company.email}")
    private String companyEmail;

    private final JavaMailSender javaMailSender;

    public void sentToCopany(ContactRequest contactRequest, Boolean isFromCart) {
        try {
            User user = contactRequest.getUser();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(contactRequest.getSubject());

            String messageBody = "";
            if (isFromCart){
                messageBody = contactRequest.getMessage();
            }else {
                messageBody = "Client : "+user.getFullName()+"\n\n"
                        + "Email : "+user.getEmail()+".\n\n"
                        + "Téléphone: "+user.getPhone()+".\n\n"
                        + contactRequest.getMessage() + "\n\n";
            }


            message.setFrom("no-reply@will.dev");
            message.setTo(companyEmail);

            message.setText(messageBody);

            javaMailSender.send(message);
        } catch (RuntimeException e) {
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
            javaMailSender.send(mail);
        } catch (RuntimeException e) {
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
            javaMailSender.send(mail);
        } catch (RuntimeException e) {
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
