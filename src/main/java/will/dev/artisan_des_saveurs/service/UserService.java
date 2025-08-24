package will.dev.artisan_des_saveurs.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.UserDto;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.dtoMapper.UserDtoMapper;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.ContactRequestRepository;
import will.dev.artisan_des_saveurs.repository.UserRepository;
import will.dev.artisan_des_saveurs.security.UserDetailsImpl;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static will.dev.artisan_des_saveurs.service.ProductService.extractFileName;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    public static final String MESSAGE = "Votre message a été envoyé avec succès ! Nous vous répondrons dans les plus brefs délais.";
    @Value("${app.company.whatsapp.number:+23059221613}")
    private String company_number;

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final NotificationService notificationService;
    private final WhatsappNotification whatsappNotification;
    private final VonageWhatsappNotificationService vonageWhatsappNotificationService;

    @Transactional
    public ResponseEntity<MessageRetourDto> createUser(UserDto userDto) {
        System.out.println("userDto ::: " + userDto);
        try{
            Optional<User> optionalUser = this.userRepository.findByEmail(userDto.getEmail());
            if (optionalUser.isPresent()) {
                String email = userDto.getEmail();
                System.out.println("email ::: " + email);

                User userConnected = this.userRepository.findByEmailFromConnectedUser(email);

                ContactRequest contactRequest = new ContactRequest();
                contactRequest.setUser(userConnected);
                contactRequest.setSubject(userDto.getContactRequests().get(0).getSubject());
                contactRequest.setMessage(userDto.getContactRequests().get(0).getMessage());
                contactRequest.setEmailSent(false);
                contactRequest.setWhatsappSent(false);
                ContactRequest savedContactReq = contactRequestRepository.save(contactRequest);

                //userConnected.setContactRequests(List.of(contactRequest)); // Crée une nouvelle liste et génère l'erreur. Pour utiliser cette logique, mettre orphanRemoval = false dans l'entité User
                userConnected.getContactRequests().clear();
                userConnected.getContactRequests().add(contactRequest); // Modifie la liste

                Boolean isFromCart = false;
                notificationService.sentToCopany(contactRequest, isFromCart);

                notificationService.sentResponseToCustomerFromContactPage(userConnected);
                savedContactReq.markEmailSent();

                whatsappNotification.sendWhatsappMessage(userConnected, company_number, contactRequest, isFromCart);
                vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, userConnected, savedContactReq);
                savedContactReq.markWhatsappSent();

                contactRequestRepository.save(savedContactReq);

                MessageRetourDto messageRetourDto = new MessageRetourDto();
                messageRetourDto.setSuccess(true);
                messageRetourDto.setMessage(MESSAGE);
                //return ResponseEntity.ok(UserDtoMapper.toDto(savedUser));
                return ResponseEntity.ok(messageRetourDto);

            }else {
                User user = new User();
                user.setFirstName(userDto.getFirstName());
                user.setLastName(userDto.getLastName());
                user.setEmail(userDto.getEmail());
                user.setPhone(userDto.getPhone());
                user.setConsent(Boolean.TRUE.equals(userDto.getConsent()));
                user.setEnabled(false);
                user.setUsername("anonymousUser");
                user.setPassword("anonymousUser123");
                User savedUser = userRepository.save(user);
                System.out.println("savedUser ::: " + savedUser);

                ContactRequest contactRequest = new ContactRequest();
                contactRequest.setUser(savedUser);
                contactRequest.setSubject(userDto.getContactRequests().get(0).getSubject());
                contactRequest.setMessage(userDto.getContactRequests().get(0).getMessage());
                contactRequest.setEmailSent(false);
                contactRequest.setWhatsappSent(false);
                ContactRequest savedContactReq = contactRequestRepository.save(contactRequest);

                savedUser.setContactRequests(List.of(contactRequest));

                Boolean isFromCart = false;
                notificationService.sentToCopany(contactRequest, isFromCart);

                notificationService.sentResponseToCustomerFromContactPage(savedUser);
                savedContactReq.markEmailSent();

                whatsappNotification.sendWhatsappMessage(savedUser, company_number, contactRequest, isFromCart);
                vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, savedUser, savedContactReq);
                savedContactReq.markWhatsappSent();

                contactRequestRepository.save(savedContactReq);

                MessageRetourDto messageRetourDto = new MessageRetourDto();
                messageRetourDto.setSuccess(true);
                messageRetourDto.setMessage(MESSAGE);
                //return ResponseEntity.ok(UserDtoMapper.toDto(savedUser));
                return ResponseEntity.ok(messageRetourDto);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("ERROR::: "+ e);
        }
    }


    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toDto)
                .toList();
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserDtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void removeUselessJwt(){
        log.info("Suppresion des users non abonnée a {} %s".formatted(Instant.now()));
        List<User> users = this.userRepository.findByEnabledFalse(false) ;
        if (!users.isEmpty()) {
            userRepository.deleteAll(users);
            log.info("{} Users non abonnée supprimés.", users.size());
        } else {
            log.info("Aucun users à supprimer.");
        }
    }

    public ResponseEntity<MessageRetourDto> updateUser(@Valid UserDto userDto) {
        try {
            User userConnected = this.userRepository.findByEmailFromConnectedUser(userDto.getEmail());
            userConnected.setFirstName(userDto.getFirstName());
            userConnected.setLastName(userDto.getLastName());
            userConnected.setEmail(userDto.getEmail());
            userConnected.setPhone(userDto.getPhone());
            this.userRepository.save(userConnected);
            MessageRetourDto messageRetourDto = new MessageRetourDto();
            messageRetourDto.setSuccess(true);
            messageRetourDto.setMessage("Information personnelles mis a jour avec succès");
            return ResponseEntity.ok(messageRetourDto);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors de la mise a jour des infos personnelles" + e);
        }
    }

    public FileDTO saveAvatar(MultipartFile file) throws IOException {
        System.out.println("✅ Appel du service !");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("userDetails isEnabled::: " + userDetails.isEnabled());
        System.out.println(" principal::: " + principal);
        String imageUrl = cloudinaryService.uploadFile(file);
        FileDTO fileDto = new FileDTO();
        fileDto.setFileName(extractFileName(imageUrl));
        fileDto.setFilePath(imageUrl);
       // userConnected.setAvatar(fileDto.getFilePath());
        System.out.println("✅ fileDto :: "+ fileDto);
        //System.out.println("✅ userConnected :: "+ userConnected);
        //userRepository.save(userConnected);
        return fileDto;
    }
}


