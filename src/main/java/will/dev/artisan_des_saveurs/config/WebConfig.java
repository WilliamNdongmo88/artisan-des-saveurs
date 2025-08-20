package will.dev.artisan_des_saveurs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // IMPORTANT : on ne mappe PAS /uploads/** ici,
        // donc Spring Boot ne gérera jamais ce chemin.
        // Il sera laissé libre pour Nginx.
    }
}
