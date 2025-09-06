package will.dev.artisan_des_saveurs.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private Boolean consent;
    private Boolean enabled;
    private String currency;
    private Boolean emailNewProducts;
    private Boolean emailOrderUpdates;
    private Boolean emailPromotions;
    private String language;

    private List<ContactRequestDto> contactRequests;
}