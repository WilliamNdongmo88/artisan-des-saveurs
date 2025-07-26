package will.dev.artisan_des_saveurs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/users/**,/order/**") // Applique la configuration à toutes les routes sous /api/
                        .allowedOrigins("http://localhost:4200", "https://artisan-des-saveurs-app-will.vercel.app" ) // Autorise votre Angular local ET votre déploiement Vercel
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                        .allowedHeaders("*") // Tous les en-têtes autorisés
                        .allowCredentials(true); // Autorise les cookies et les informations d'authentification
            }
        };
    }
}

