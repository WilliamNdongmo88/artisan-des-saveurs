package will.dev.artisan_des_saveurs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http ) throws Exception {
        http
            .cors(withDefaults( ))

            //Désactivation CSRF si vous utilisation JWT ou des tokens
            .csrf(AbstractHttpConfigurer::disable)

            // Configuration des autorisations de requêtes
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/users/**").permitAll()
                    .requestMatchers("/orders/**").permitAll()
                    //PERMETTRE EXPLICITEMENT LES REQUÊTES OPTIONS SANS AUTHENTIFICATION
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/actuator/health").permitAll()

                    //définition des autres règles de sécurité
                    .requestMatchers("/api/auth/**").permitAll() // Ex: routes pour s'inscrire/se connecter
                    .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
            );
        // ... le reste de la configuration (gestion de session, filtres JWT, etc.)

        return http.build( );
    }
}

