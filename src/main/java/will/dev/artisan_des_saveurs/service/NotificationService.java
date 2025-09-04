//package will.dev.artisan_des_saveurs.service;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//import will.dev.artisan_des_saveurs.entity.ContactRequest;
//import will.dev.artisan_des_saveurs.entity.User;
//
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
//
//    @Value("${app.company.email}")
//    private String companyEmail;
//
//    @Value("${app.company.whatsapp.number}")
//    private String companyNumber;
//
//    @Value("app.env.apiUrl")
//    private String apiUrl;
//
//    private final JavaMailSender javaMailSender;
//
//    public void sendActivationEmail(String to, String token) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(companyEmail);
//            message.setTo(to);
//            message.setSubject("Activation de votre compte - Artisan des Saveurs");
//            message.setText("Bonjour,\n\n" +
//                    "Merci de vous être inscrit sur Artisan des Saveurs!\n\n" +
//                    "Pour activer votre compte, veuillez cliquer sur le lien suivant:\n" +
//                    apiUrl+"activate?token=" + token + "\n\n" +
//                    "Ce lien est valide pendant 24 heures.\n\n" +
//                    "Cordialement,\n" +
//                    "L'équipe Artisan des Saveurs");
//
//            javaMailSender.send(message);
//            logger.info("Email d'activation envoyé à: {}", to);
//        } catch (Exception e) {
//            logger.error("Erreur lors de l'envoi de l'email d'activation à {}: {}", to, e.getMessage());
//            // En mode développement, on log le token pour pouvoir tester
//            logger.info("Token d'activation pour {}: {}", to, token);
//        }
//    }
//
//    public void sendPasswordResetEmail(String to, String token) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(companyEmail);
//            message.setTo(to);
//            message.setSubject("Réinitialisation de votre mot de passe - Artisan des Saveurs");
//            message.setText("Bonjour,\n\n" +
//                    "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
//                    "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant:\n" +
//                    apiUrl+"reset-password?token=" + token + "\n\n" + // http://localhost:4200/reset-password?token=
//                    "Ce lien est valide pendant 1 heure.\n\n" +
//                    "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
//                    "Cordialement,\n" +
//                    "L'équipe Artisan des Saveurs");
//
//            javaMailSender.send(message);
//            logger.info("Email de réinitialisation envoyé à: {}", to);
//        } catch (Exception e) {
//            logger.error("Erreur lors de l'envoi de l'email de réinitialisation à {}: {}", to, e.getMessage());
//            // En mode développement, on log le token pour pouvoir tester
//            logger.info("Token de réinitialisation pour {}: {}", to, token);
//        }
//    }
//
//    public void sentToCopany(ContactRequest contactRequest, Boolean isFromCart) {
//        System.out.println(":: Sent mail to company :: ");
//        System.out.println(":: Message :: " + contactRequest.getMessage());
//        try {
//            System.out.println(":: Début 1 :: ");
//            User user = contactRequest.getUser();
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setSubject(contactRequest.getSubject());
//
//            String messageBody = "";
//            if (isFromCart){
//                System.out.println(":: Début 2 :: ");
//                messageBody = contactRequest.getMessage();
//                System.out.println(":: Fin 2 :: ");
//            }else {
//                messageBody = "Client : "+user.getFullName()+"\n\n"
//                        + "Email : "+user.getEmail()+".\n\n"
//                        + "Téléphone: "+user.getPhone()+".\n\n"
//                        + contactRequest.getMessage() + "\n\n";
//            }
//
//
//            message.setFrom(companyEmail);
//            message.setTo(companyEmail);
//
//            message.setText(messageBody);
//            System.out.println(":: Fin 2 :: ");
//            javaMailSender.send(message);
//        } catch (RuntimeException e) {
//            throw new RuntimeException("NOTIFICATION_EMAIL_EXCEPTION: " + e);
//        }
//    }
//
//    public void sentResponseToCustomerFromCartPage(User savedUser, String customerMessage) {
//        try {
//            String subject = "Merci pour votre commande – L'Artisan des saveurs";
//            String message = customerMessage;
//
//            SimpleMailMessage mail = new SimpleMailMessage();
//            mail.setFrom(companyEmail);
//            mail.setTo(savedUser.getEmail());
//            mail.setSubject(subject);
//            mail.setText(message);
//            javaMailSender.send(mail);
//        } catch (RuntimeException e) {
//            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
//        }
//    }
//
//    public void sentResponseToCustomerFromContactPage(User savedUser) {
//        try {
//            String subject = "Merci pour votre intérêt – Informations sur nos produits";
//            String message = customMessage(savedUser.getFullName());
//
//            SimpleMailMessage mail = new SimpleMailMessage();
//            mail.setFrom(companyEmail);
//            mail.setTo(savedUser.getEmail());
//            mail.setSubject(subject);
//            mail.setText(message);
//            javaMailSender.send(mail);
//        } catch (RuntimeException e) {
//            throw new RuntimeException("NOTIFICATION_EMAIL_FOR_CUSTOMER__EXCEPTION: " + e);
//        }
//    }
//
//    public String customMessage(String clientName) {
//        return String.format("""
//        <html>
//          <body style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">
//            <p>Bonjour <strong>%s</strong>,</p>
//            <p>Merci pour votre message et pour l’intérêt que vous portez à nos produits.</p>
//            <p>
//              Notre équipe service client prendra contact avec vous rapidement
//              afin de répondre à vos questions.
//            </p>
//            <p>
//              En attendant, vous pouvez consulter notre site officiel pour plus d’informations :<br/>
//              <a href="https://artisan-des-saveurs.vercel.app/">artisan-des-saveurs.vercel.app</a>
//            </p>
//            <br/>
//            <p>Cordialement,</p>
//            <p>
//              <strong>Service Client – L'Artisan des saveurs</strong><br/>
//              📞 %s
//            </p>
//          </body>
//        </html>
//        """, clientName, companyNumber);
//    }
//}
