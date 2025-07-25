package will.dev.artisan_des_saveurs.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailEnvCheck {

    @Value("${MAIL_USERNAME:undefined}")
    private String mailUsername;

    @PostConstruct
    public void show() {
        System.out.println("MAIL_USERNAME from Spring = " + mailUsername);
        System.out.println("MAIL_USERNAME from system property = " + System.getProperty("MAIL_USERNAME"));
    }
}
