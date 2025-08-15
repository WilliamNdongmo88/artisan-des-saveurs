package will.dev.artisan_des_saveurs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.entity.Role;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.enums.TypeDeRole;
import will.dev.artisan_des_saveurs.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.company.username}")
    private String companyUsername;
    @Value("${app.company.pass}")
    private String companyPass;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Créer un utilisateur admin par défaut
        if (!userRepository.existsByUsername("admin")) {
            Role userRole = new Role();
            userRole.setLibelle(TypeDeRole.ADMIN);

            User admin = new User();
            admin.setUsername(companyUsername);
            admin.setEmail("btbimportationservice333@gmail.com");
            admin.setPassword(passwordEncoder.encode(companyPass));
            admin.setFirstName("William");
            admin.setLastName("Ndongmo");
            admin.setPhone("+23059221613");
            admin.setEnabled(true);
            //admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            admin.setRole(userRole);
            userRepository.save(admin);
            System.out.println("Utilisateur admin créé: admin / admin123");
        }

    }

}


