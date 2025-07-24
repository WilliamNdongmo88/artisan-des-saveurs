package will.dev.artisan_des_saveurs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactRequestDto {
    private Long id;
    private String subject;
    private String message;
    private Boolean emailSent;
    private Boolean whatsappSent;

    // Pour éviter de retourner toute l'entité User, on peut limiter à des infos essentielles :
    private Long userId;
    private String userFullName;
}

