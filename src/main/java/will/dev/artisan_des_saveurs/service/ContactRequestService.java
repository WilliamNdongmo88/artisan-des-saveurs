package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.dto.ContactRequestDto;
import will.dev.artisan_des_saveurs.dtoMapper.ContactRequestDtoMapper;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.ContactRequestRepository;
import will.dev.artisan_des_saveurs.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final UserRepository userRepository;

    public ContactRequestDto save(ContactRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ContactRequest entity = ContactRequest.builder()
                .user(user)
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .emailSent(false)
                .whatsappSent(false)
                .build();

        ContactRequest saved = contactRequestRepository.save(entity);
        return ContactRequestDtoMapper.toDto(saved);
    }

    public List<ContactRequestDto> getAll() {
        return contactRequestRepository.findAll().stream()
                .map(ContactRequestDtoMapper::toDto)
                .toList();
    }

    public List<ContactRequestDto> getByUser(Long userId) {
        return contactRequestRepository.findByUserId(userId).stream()
                .map(ContactRequestDtoMapper::toDto)
                .toList();
    }
}


