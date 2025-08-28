package will.dev.artisan_des_saveurs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.*;

@Service
public class BrevoService {

    @Value("${app.company.email}")
    private String companyEmail;

    public void sendMail(User user, String subject, String msg) throws Exception {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication(System.getenv("BREVO_USERNAME"));
        apiKey.setApiKey(System.getenv("BREVO_API_KEY"));

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
        SendSmtpEmailSender sender = new SendSmtpEmailSender().email(companyEmail).name("Artisan des saveurs");
        SendSmtpEmailTo recipient = new SendSmtpEmailTo().email(user.getEmail());

        SendSmtpEmail email = new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(recipient))
                .subject(subject)
                .htmlContent(msg);

        CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
        System.out.println(response);
    }
}

