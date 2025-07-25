package will.dev.artisan_des_saveurs.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailEnvCheck {

    private final MailConfig mailConfig;

    public MailEnvCheck(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
    }

    @PostConstruct
    public void logMail() {
        System.out.println("===================================");
        System.out.println("HELLO_WORLD from System.getenv = " + System.getenv("HELLO_WORLD"));
        System.out.println("MAIL_USERNAME = " + mailConfig.getMailUsername());
        System.out.println("MAIL_PASSWORD = " + mailConfig.getMailPassword());
        System.out.println("===================================");
    }
}
