package will.dev.artisan_des_saveurs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

//    private final Environment environment;
//
//    public MailConfig(Environment environment) {
//        this.environment = environment;
//    }
//
//
//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        // Lire depuis les variables d'environnement système (Railway)
//        mailSender.setUsername(System.getenv("MAIL_USERNAME"));
//        mailSender.setPassword(System.getenv("MAIL_PASSWORD"));
//
//        // Lire depuis les variables d'environnement système (Local)
//        mailSender.setUsername(System.getProperty("MAIL_USERNAME"));
//        mailSender.setPassword(System.getProperty("MAIL_PASSWORD"));
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true");
//        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//
//        return mailSender;
//    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(System.getProperty("MAIL_USERNAME"));
        mailSender.setPassword(System.getProperty("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}

