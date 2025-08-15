package will.dev.artisan_des_saveurs.dtoMapper;


import lombok.Data;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.UserDto;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getFormattedPhone());
        dto.setConsent(user.getConsent());
        dto.setEnabled(user.getEnabled());

        if (user.getContactRequests() != null) {
            dto.setContactRequests(
                    user.getContactRequests().stream()
                            .map(ContactRequestDtoMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setConsent(dto.getConsent() != null ? dto.getConsent() : false);
        user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);

        return user;
    }
}

