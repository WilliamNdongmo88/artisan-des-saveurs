package will.dev.artisan_des_saveurs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.*;

@Service
public class BrevoService {

    @Value("${app.company.email}")
    private String companyEmail;

    @Value("${BREVO_API_KEY}") // ⚡ récupère directement dans application.properties / Railway env
    private String brevoApiKey;

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


