package will.dev.artisan_des_saveurs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Autorise les appels depuis Angular en local
        config.setAllowCredentials(false);
        config.addAllowedOriginPattern("http://localhost:4200"); // remplace addAllowedOrigin
        //config.addAllowedOrigin("http://localhost:4200");

        // Autorise tous les headers et méthodes HTTP
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // Applique la configuration à tous les chemins
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Enregistre le filtre CORS avec une priorité haute
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
