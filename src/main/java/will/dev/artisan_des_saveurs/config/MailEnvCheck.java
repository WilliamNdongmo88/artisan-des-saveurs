package will.dev.artisan_des_saveurs.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailEnvCheck {

    @Value("${MAIL_USERNAME:defaultUsername}")
    private String mailUser;

    @Value("${MAIL_PASSWORD:defaultPassword}")
    private String mailPass;

    @PostConstruct
    public void check() {
        System.out.println("MAIL_USERNAME from @Value: " + mailUser);
        System.out.println("MAIL_PASSWORD from @Value: " + mailPass);
    }
}
