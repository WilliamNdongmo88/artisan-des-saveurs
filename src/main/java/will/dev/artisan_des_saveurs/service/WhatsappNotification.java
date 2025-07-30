package will.dev.artisan_des_saveurs.service;//package will.dev.Artisan_des_saveurs.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
        try {
            String messages = "";
            if (isFromCart) {
                messages = contactRequest.getMessage();
            }else {
                messages = "Client : "+user.getFullName()+"\n"
                        + "Email : "+user.getEmail()+".\n"
                        + "Téléphone: "+user.getPhone()+".\n\n"
                        + "Sujet: "+contactRequest.getSubject()+".\n\n"
                        + contactRequest.getMessage() + "\n\n";
            }
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                            new PhoneNumber("whatsapp:" + to),
                            new PhoneNumber("whatsapp:" + fromPhoneNumber),
                            messages)
                    .create();

            System.out.println("Message SID: " + message.getSid());
        } catch (RuntimeException e) {
            throw new RuntimeException("NOTIFICATION_WHATSAPP_EXCEPTION: " + e);
        }
    }
}
