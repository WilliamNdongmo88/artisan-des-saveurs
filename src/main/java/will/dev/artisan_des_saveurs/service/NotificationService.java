package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NotificationService {
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
            message.setTo(user.getEmail());

            message.setText(messageBody);

            javaMailSender.send(message);
        } catch (RuntimeException e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_EXCEPTION: " + e);
        }
    }

    public void sentToCustomer(User savedUser, String customerMessage) {
        try {
            String subject = "Merci pour votre commande – L'Artisan des saveurs";
            String message = customerMessage;

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(savedUser.getEmail());
            mail.setSubject(subject);
            mail.setText(message);
            javaMailSender.send(mail);
        } catch (RuntimeException e) {
            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
        }
    }
}
