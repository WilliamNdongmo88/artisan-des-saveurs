package will.dev.artisan_des_saveurs.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.controller.advice.exception.InvalidCredentialsException;
import will.dev.artisan_des_saveurs.controller.advice.exception.UserNotFoundException;
import will.dev.artisan_des_saveurs.dto.DeleteAccountRequest;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.UserDto;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.dtoMapper.UserDtoMapper;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.Order;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.ContactRequestRepository;
import will.dev.artisan_des_saveurs.repository.OrderRepository;
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
    @Value("${app.company.whatsapp.number}")
    private String company_number;

    private  final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ContactRequestRepository contactRequestRepository;
    //private final NotificationService notificationService;
    private final BrevoService brevoService;
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
                //System.out.println("savedContactReq :: " + savedContactReq);
                brevoService.sentToCompany(savedContactReq, isFromCart);
                System.out.println(":: Debut ::");

                brevoService.sentResponseToCustomerFromContactPage(userConnected);
                savedContactReq.markEmailSent();
                System.out.println(":: Fin ::");
                //whatsappNotification.sendWhatsappMessage(userConnected, company_number, contactRequest, isFromCart);
                //vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, userConnected, savedContactReq);
                savedContactReq.markWhatsappSent();

                contactRequestRepository.save(savedContactReq);

                MessageRetourDto messageRetourDto = new MessageRetourDto();
                messageRetourDto.setSuccess(true);
                messageRetourDto.setMessage(MESSAGE);
                //return ResponseEntity.ok(UserDtoMapper.toDto(savedUser));
                return ResponseEntity.ok(messageRetourDto);

            }
            else {
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
                brevoService.sentToCompany(contactRequest, isFromCart);

                brevoService.sentResponseToCustomerFromContactPage(savedUser);
                savedContactReq.markEmailSent();

                //whatsappNotification.sendWhatsappMessage(savedUser, company_number, contactRequest, isFromCart);
                //vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, savedUser, savedContactReq);
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

    @Scheduled(cron = "0 */1440 * * * *")
    public void removeUselessJwt(){
        log.info("Suppresion des users non abonnée a {} %s".formatted(Instant.now()));
        List<User> users = this.userRepository.findByEnabledFalse(false);
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
        System.out.println("✅ Appel du service saveAvatar !");

        // Récupération de l'utilisateur connecté
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<User> userConnectedOpt = userRepository.findById(userDetails.getId());

        if (userConnectedOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + userDetails.getId());
        }

        User userConnected = userConnectedOpt.get();
        System.out.println("👤 Utilisateur connecté : " + userConnected.getUsername());

        // Upload du fichier sur Cloudinary
        String imageUrl = cloudinaryService.uploadFile(file);
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IOException("Échec de l'upload de l'image sur Cloudinary !");
        }

        // Construction du DTO
        FileDTO fileDto = new FileDTO();
        fileDto.setFileName(extractFileName(imageUrl)); // Vérifie ton extractFileName
        fileDto.setFilePath(imageUrl);

        // Mise à jour de l'utilisateur avec l'avatar
        userConnected.setAvatar(fileDto.getFilePath());
        userRepository.save(userConnected);

        System.out.println("✅ Avatar mis à jour pour l'utilisateur : " + userConnected.getUsername());

        return fileDto;
    }

    public ResponseEntity<MessageRetourDto> updatePreferenceUser(UserDto userDto) {
        System.out.println("✅ Appel du service préférences !");

        // Récupération de l'utilisateur connecté
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<User> userConnectedOpt = userRepository.findById(userDetails.getId());

        if (userConnectedOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + userDetails.getId());
        }

        User userConnected = userConnectedOpt.get();
        System.out.println("👤 Utilisateur connecté : " + userConnected.getUsername());

        userConnected.setReceive_order_updates(userDto.getEmailOrderUpdates());
        userConnected.setReceive_promotional_offers(userDto.getEmailPromotions());
        userConnected.setBe_notified_of_new_products(userDto.getEmailNewProducts());
        userConnected.setCurrency(userDto.getCurrency());
        userConnected.setLanguage(userDto.getLanguage());

        userRepository.save(userConnected);
        MessageRetourDto messageRetourDto = new MessageRetourDto();
        messageRetourDto.setMessage("Préférences mises à jour avec succès");

        return ResponseEntity.ok(messageRetourDto);
    }

    /**
     * Supprime définitivement le compte d'un utilisateur après vérification des credentials
     *
     * @param userId L'ID de l'utilisateur à supprimer
     * @param deleteRequest Les données de confirmation (mot de passe et texte de confirmation)
     * @throws UserNotFoundException Si l'utilisateur n'existe pas
     * @throws InvalidCredentialsException Si le mot de passe est incorrect ou la confirmation invalide
     */
    @Transactional
    public void deleteUserAccount(Long userId, DeleteAccountRequest deleteRequest) {
        log.info("Tentative de suppression du compte utilisateur avec l'ID: {}", userId);

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Tentative de suppression d'un utilisateur inexistant avec l'ID: {}", userId);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
                });

        // Vérifier le texte de confirmation
        if (deleteRequest.getConfirmationText() == null ||
                !deleteRequest.getConfirmationText().trim().equalsIgnoreCase("SUPPRIMER")) {
            log.warn("Texte de confirmation invalide pour l'utilisateur ID: {}", userId);
            throw new InvalidCredentialsException("Le texte de confirmation doit être exactement 'SUPPRIMER'");
        }

        // Vérifier le mot de passe
        if (deleteRequest.getPassword() == null ||
                !passwordEncoder.matches(deleteRequest.getPassword(), user.getPassword())) {
            log.warn("Mot de passe incorrect lors de la tentative de suppression pour l'utilisateur ID: {}", userId);
            throw new InvalidCredentialsException("Mot de passe incorrect");
        }

        // Actions de nettoyage avant la suppression
        performPreDeletionCleanup(user);

        // Supprimer l'utilisateur
        userRepository.delete(user);

        log.info("Compte utilisateur supprimé avec succès - ID: {}, Email: {}",
                userId, user.getEmail());
    }

    /**
     * Effectue des actions de nettoyage avant la suppression du compte
     * (par exemple, supprimer les données associées, envoyer des notifications, etc.)
     *
     * @param user L'utilisateur à supprimer
     */
    private void performPreDeletionCleanup(User user) {
        log.info("Début du nettoyage pré-suppression pour l'utilisateur: {}", user.getEmail());

        // Logique à ajouter pour la version future:
        // - Supprimer les commandes associées
        // - Supprimer les fichiers uploadés par l'utilisateur
        // - Envoyer des notifications aux administrateurs
        // - Archiver certaines données si nécessaire
        // - Supprimer les sessions actives

        // Nettoyage des données associées:

        try{
            Order order = orderRepository.findByUserId(user.getId());
            System.out.println("order :: " + order);
            orderRepository.deleteById(order.getId());
            brevoService.notifyAdminUserDeleted(user);
        } catch (RuntimeException e) {
            throw new RuntimeException("DELETE_ORDER_ERROR"+ e.getMessage());
        }

        log.info("Nettoyage pré-suppression terminé pour l'utilisateur: {}", user.getEmail());
    }

    /**
     * Vérifie si un utilisateur existe par son ID
     *
     * @param userId L'ID de l'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Récupère un utilisateur par son ID
     *
     * @param userId L'ID de l'utilisateur
     * @return L'utilisateur trouvé
     * @throws UserNotFoundException Si l'utilisateur n'existe pas
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    /**
     * Vérifie si le mot de passe fourni correspond au mot de passe de l'utilisateur
     *
     * @param user L'utilisateur
     * @param rawPassword Le mot de passe en clair à vérifier
     * @return true si le mot de passe correspond, false sinon
     */
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}


