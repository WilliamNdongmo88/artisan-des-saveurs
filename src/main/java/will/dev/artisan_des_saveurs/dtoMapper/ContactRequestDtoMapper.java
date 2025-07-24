package will.dev.artisan_des_saveurs.dtoMapper;

import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.ContactRequestDto;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;

@Component
public class ContactRequestDtoMapper {

    public static ContactRequestDto toDto(ContactRequest entity) {
        if (entity == null) return null;

        ContactRequestDto dto = new ContactRequestDto();
        dto.setId(entity.getId());
        dto.setSubject(entity.getSubject());
        dto.setMessage(entity.getMessage());
        dto.setEmailSent(entity.getEmailSent());
        dto.setWhatsappSent(entity.getWhatsappSent());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
        }

        return dto;
    }

    public static ContactRequest toEntity(ContactRequestDto dto) {

        return ContactRequest.builder()
                .id(dto.getId())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .emailSent(Boolean.TRUE.equals(dto.getEmailSent()))
                .whatsappSent(Boolean.TRUE.equals(dto.getWhatsappSent()))
                .build();
    }
}
