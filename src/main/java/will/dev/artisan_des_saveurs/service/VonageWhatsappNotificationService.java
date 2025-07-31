package will.dev.artisan_des_saveurs.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VonageWhatsappNotificationService {
    @Value("${vonage.apiKey}")
    private String apiKey;

    @Value("${vonage.apiSecret}")
    private String apiSecret;

    @Value("${vonage.whatsapp.from}")
    private String fromNumber;

    @Value("${app.company.whatsappNumber}")
    private String companyNumber;

    public void sendWhatsappMessageToCustomer(Boolean isFromCart, User user, ContactRequest contactRequest) {
        String url = "https://messages-sandbox.nexmo.com/v0.1/messages";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (isFromCart){
            Map<String, Object> body = Map.of(
                    "from", Map.of("type", "whatsapp", "number", fromNumber),
                    "to", Map.of("type", "whatsapp", "number", companyNumber),
                    "message", Map.of("content", Map.of("type", "text", "text", contactRequest.getMessage()))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        }else {
            String messages = "Client : "+user.getFullName()+"\n"
                    + "Email : "+user.getEmail()+".\n"
                    + "Téléphone: "+user.getPhone()+".\n\n"
                    + "Sujet: "+contactRequest.getSubject()+".\n\n"
                    + contactRequest.getMessage() + "\n\n";
            Map<String, Object> body = Map.of(
                    "from", Map.of("type", "whatsapp", "number", fromNumber),
                    "to", Map.of("type", "whatsapp", "number", companyNumber),
                    "message", Map.of("content", Map.of("type", "text", "text", messages))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        }
    }
}






