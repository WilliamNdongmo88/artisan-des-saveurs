package will.dev.artisan_des_saveurs.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.entity.Role;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.enums.TypeDeRole;
import will.dev.artisan_des_saveurs.repository.UserRepository;
import will.dev.artisan_des_saveurs.security.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final BrevoService brevoService;

    public String registerUser(will.dev.artisan_des_saveurs.dto.req_resp.dto.@Valid SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Erreur: Le nom d'utilisateur est déjà pris!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }

        // Créer un nouveau compte utilisateur
        Role userRole = new Role();
        userRole.setLibelle(TypeDeRole.USER);
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           encoder.encode(signUpRequest.getPassword()),
                           signUpRequest.getFirstName(),
                           signUpRequest.getLastName());

        user.setPhone(signUpRequest.getPhone());
        user.setAvatar("");
        user.setRole(userRole);
        
        // Générer un token d'activation
        String activationToken = UUID.randomUUID().toString();
        user.setActivationToken(activationToken);
        user.setEnabled(false);

        userRepository.save(user);

        // Envoyer l'email d'activation
        brevoService.sendActivationEmail(user.getEmail(), activationToken);
        System.out.println(":: Notification éffectué :: ");

        return "Utilisateur enregistré avec succès! Veuillez vérifier votre email pour activer votre compte.";
    }

    public String activateUser(String token) {
        Optional<User> userOpt = userRepository.findByActivationToken(token);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Token d'activation invalide!");
        }
        User user = userOpt.get();
        user.setEnabled(true);
        user.setActivationToken(token);
        userRepository.save(user);
        return "Compte activé avec succès!";
    }

    public String resendActivation(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé!");
        }

        User user = userOpt.get();
        if (user.getEnabled()) {
            throw new RuntimeException("Le compte est déjà activé!");
        }

        // Générer un nouveau token d'activation
        String activationToken = UUID.randomUUID().toString();
        user.setActivationToken(activationToken);
        userRepository.save(user);

        // Envoyer l'email d'activation
        brevoService.sendActivationEmail(user.getEmail(), activationToken);

        return "Email d'activation renvoyé avec succès!";
    }

    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé!");
        }

        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(1)); // Token valide 1 heure
        userRepository.save(user);

        // Envoyer l'email de réinitialisation
        brevoService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return "Email de réinitialisation envoyé avec succès!";
    }

    public String updatePassword(String currentPassword, String newPassword) {
        // Récupération de l'utilisateur connecté
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<User> userConnectedOpt = userRepository.findById(userDetails.getId());

        if (userConnectedOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé!");
        }
        User userConnected = userConnectedOpt.get();

        // Vérifie que l'ancien mot de passe correspond
        if (!passwordEncoder.matches(currentPassword, userConnected.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect!");
        }

        // Change le mot de passe
        userConnected.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userConnected);

        return "Mot de passe mis à jour avec succès.";
    }


    public String resetPassword(String token, String newPassword) {
        System.out.println("### token :: " + token + " ### newPassword :: " +  newPassword);
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Token de réinitialisation invalide!");
        }

        User user = userOpt.get();
        if (user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de réinitialisation expiré!");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        userRepository.save(user);

        return "Mot de passe réinitialisé avec succès!";
    }

    public FileDTO getAvatar(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()){
            throw new RuntimeException(":: User not found:: ");
        }
        User user = userOpt.get();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFilePath(user.getAvatar());
        System.out.println("fileDTO :: " + fileDTO);
        return fileDTO;
    }
}

