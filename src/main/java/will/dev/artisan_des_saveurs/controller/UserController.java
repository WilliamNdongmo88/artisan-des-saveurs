package will.dev.artisan_des_saveurs.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.controller.advice.exception.InvalidCredentialsException;
import will.dev.artisan_des_saveurs.controller.advice.exception.UserNotFoundException;
import will.dev.artisan_des_saveurs.dto.DeleteAccountRequest;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.UserDto;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.dtoMapper.UserDtoMapper;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.UserRepository;
import will.dev.artisan_des_saveurs.security.UserDetailsImpl;
import will.dev.artisan_des_saveurs.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${app.company.account.number}")
    private String accountNumber;

    @Value("${app.company.whatsapp.number}")
    private String whatsappNumber;

    @Value("${app.company.email}")
    private String companyEmail;

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String hello() {
        System.out.println("/api/users/test atteint !");
        return "Bravo votre application fonctionne correctement";
    }

    /**
     * Endpoint pour vérifier si l'utilisateur connecté existe
     *
     * @return ResponseEntity avec les informations de l'utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            String username = user.getUsername();

            if (userId != null && userService.userExists(userId)) {
                response.put("success", true);
                response.put("userId", userId);
                response.put("username", username);
                response.put("message", "Utilisateur trouvé");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur connecté", e);
            response.put("success", false);
            response.put("message", "Erreur interne");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/place-order")
    public ResponseEntity<MessageRetourDto> createUser(@RequestBody @Valid UserDto userDto) {
        System.out.println("Requête reçue pour créer un utilisateur : " + userDto.getEmail());
        return this.userService.createUser(userDto);
    }

    @RequestMapping(value = "/place-order", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> corsCheck() {
        System.out.println("Préflight OPTIONS reçu !");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN_CREATE', 'USER_CREATE')")
    @PutMapping("personal-info")
    public ResponseEntity<MessageRetourDto> updateInfoUser(@RequestBody @Valid UserDto userDto){
        System.out.println("Requête reçue pour modifier un utilisateur : " + userDto.getEmail());
        return this.userService.updateUser(userDto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN_UPDATE', 'USER_UPDATE')")
    @PutMapping("preferences")
    public ResponseEntity<MessageRetourDto> updatePreferenceUser(@RequestBody UserDto userDto){
        System.out.println("Requête reçue pour modifier les préférences de 'utilisateur : ");
        return this.userService.updatePreferenceUser(userDto);
    }

    // GET /users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDto> userDtos = users.stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserDtoMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("### Appel réussi !");
        try {
            FileDTO dto = userService.saveAvatar(file);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("account-number")
    public ResponseEntity<Map<String, String>> getCompanyAccountNumber(){
        Map<String, String> map = new HashMap<>();
        map.put("accountNumber", accountNumber);
        map.put("whatsappNumber", whatsappNumber);
        map.put("email", companyEmail);
        return ResponseEntity.ok(map);
    }

    /**
     * Endpoint pour supprimer le compte de l'utilisateur connecté
     *
     * @param deleteRequest Les données de confirmation (mot de passe et texte de confirmation)
     * @return ResponseEntity avec le statut de l'opération
     */
    @DeleteMapping("/account")
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @Valid @RequestBody DeleteAccountRequest deleteRequest) {

        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Tentative de suppression de compte sans authentification");
                response.put("success", false);
                response.put("message", "Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Récupération de l'utilisateur depuis l'authentification
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            if (userDetails.getId() == null) {
                log.warn("Impossible de récupérer l'ID utilisateur depuis l'authentification pour: {}", userDetails.getUsername());
                response.put("success", false);
                response.put("message", "Erreur d'authentification");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            log.info("Demande de suppression de compte pour l'utilisateur ID: {} (username: {})", userDetails.getId(), userDetails.getUsername());

            // Appeler le service pour supprimer le compte
            userService.deleteUserAccount(userDetails.getId(), deleteRequest);

            // Réponse de succès
            response.put("success", true);
            response.put("message", "Compte supprimé avec succès");

            log.info("Compte utilisateur supprimé avec succès - ID: {}, Username: {}", userDetails.getId(), userDetails.getUsername());

            return ResponseEntity.ok(response);

        }catch (UserNotFoundException e) {
            log.warn("Utilisateur non trouvé lors de la suppression: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        }catch (InvalidCredentialsException e) {
            log.warn("Credentials invalides lors de la suppression: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression du compte", e);
            response.put("success", false);
            response.put("message", "Une erreur interne est survenue");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
