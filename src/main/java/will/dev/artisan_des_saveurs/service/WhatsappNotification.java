package will.dev.artisan_des_saveurs.service;//package will.dev.Artisan_des_saveurs.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

@Service
@RequiredArgsConstructor
public class WhatsappNotification {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromPhoneNumber;

    public void sendWhatsappMessage(User user, String to, ContactRequest contactRequest, Boolean isFromCart) {

    }

    public void sendWhatsappMessageToCustomer(User savedUser, String companyNumber, String customerMessage) {
    }
}
