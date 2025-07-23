package will.dev.artisan_des_saveurs;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
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
		System.out.println("DB URL: " + System.getenv("DATABASE_URL"));
		System.out.println("DB USER: " + System.getenv("DATABASE_USERNAME"));
//		System.out.println("accountSid = " + System.getenv("TWILIO_ACCOUNT_SID"));
//		System.out.println("authToken = " + System.getenv("TWILIO_AUTH_TOKEN"));
//		System.out.println("fromPhoneNumber = " + System.getenv("TWILIO_WHATSAPP_FROM"));
	}
}
