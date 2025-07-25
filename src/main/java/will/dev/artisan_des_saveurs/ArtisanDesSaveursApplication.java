package will.dev.artisan_des_saveurs;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArtisanDesSaveursApplication {

	private final Environment environment;

	public ArtisanDesSaveursApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		String port = System.getenv("PORT");
		if (port != null) {
			System.setProperty("server.port", port);
		}
		// On configure Dotenv pour qu'il charge le fichier .env s'il existe,
		// mais qu'il n'échoue PAS s'il ne le trouve pas.
		// C'est parfait pour passer de l'environnement local à la production.
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // L'option magique !
				.load();

//		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(ArtisanDesSaveursApplication.class, args);
	}

	@PostConstruct
	public void testDBEnvVars() {
		System.out.println("===================================");
		System.out.println("DB URL: " + System.getProperty("DATABASE_URL"));
		System.out.println("DB USER: " + System.getProperty("DATABASE_USERNAME"));
		System.out.println("MAIL_USERNAME = " + System.getenv("MAIL_USERNAME"));
		System.out.println("MAIL_PASSWORD = " + System.getenv("MAIL_PASSWORD"));
		System.out.println("===================================");
//		System.out.println("accountSid = " + System.getProperty("TWILIO_ACCOUNT_SID"));
//		System.out.println("authToken = " + System.getProperty("TWILIO_AUTH_TOKEN"));
//		System.out.println("fromPhoneNumber = " + System.getProperty("TWILIO_WHATSAPP_FROM"));
	}


	// C'est parfait pour passer de l'environnement local à la production.

}
