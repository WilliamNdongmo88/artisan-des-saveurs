package will.dev.artisan_des_saveurs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.entity.Files;
import will.dev.artisan_des_saveurs.entity.Product;
import will.dev.artisan_des_saveurs.entity.Role;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.enums.TypeDeRole;
import will.dev.artisan_des_saveurs.repository.ProductRepository;
import will.dev.artisan_des_saveurs.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    @Value("${app.company.username}")
    private String companyUsername;
    @Value("${app.company.pass}")
    private String companyPass;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Créer un utilisateur admin par défaut
        if (!userRepository.existsByEmail("btbimportationservice333@gmail.com")) {
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
            admin.setAvatar("https://res.cloudinary.com/dcjjwjheh/image/upload/v1756025207/ko5cyz724dqdgujlckn9.jpg");
            //admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            admin.setRole(userRole);
            userRepository.save(admin);
            System.out.println("Utilisateur admin créé: admin / admin123");
        }

        // Créer quelques produits de démonstration
        System.out.println("###### productRepository.count() :" + productRepository.count());
        if (productRepository.count() == 0) {
            createSampleProducts();
            System.out.println("Produits de démonstration créés");
        }
    }

    private void createSampleProducts() {
        Product[] products = {
                new Product(
                        "Côtes de Porc",
                        "Côtes de porc fraîches, parfaites pour vos grillades et barbecues. Viande tendre et savoureuse, idéale pour les repas en famille ou entre amis.",
                        new BigDecimal("583"),
                        "cotes-travers",
                        "Île Maurice",
                        true,
                        "Idéales grillées au barbecue ou à la plancha. Cuisson recommandée : 6-8 minutes de chaque côté à feu moyen."
                ),
                new Product(
                        "Escalope de porc",
                        "Escalope de porc préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(464.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Assaisonner puis cuire rôti, poêlé ou mijoté selon le morceau, jusqu’à tendreté."
                ),
                new Product(
                        "Boerewors",
                        "Saucisse sud-africaine en spirale, riche en épices douces.",
                        new BigDecimal(680.0),
                        "Saucisses et variantes",
                        "Afrique du Sud",
                        false,
                        "Former une spirale, huiler légèrement et griller 18–20 min à feu moyen, en retournant souvent."
                ),
                new Product(
                        "Saucisses Toulouse",
                        "Saucisse traditionnelle de Toulouse, gourmande et généreuse.",
                        new BigDecimal(805.0),
                        "Saucisses et variantes",
                        "France",
                        false,
                        "Cuire à la poêle ou au barbecue 15–18 min à feu moyen, en piquant légèrement et en retournant régulièrement."
                ),
                new Product(
                        "Pâté En Croûte",
                        "Pâté En Croûte préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(1470.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches, avec cornichons et salade verte."
                ),
                new Product(
                        "Rillettes de porc",
                        "Préparation fondante de porc effiloché, idéale à tartiner.",
                        new BigDecimal(1100.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir froid sur pain grillé; ajouter cornichons et oignons grelots."
                ),
                new Product(
                        "Sciss porc Chinois",
                        "Préparation parfumée aux notes asiatiques, sucrées-salées.",
                        new BigDecimal(680.0),
                        "produits-cuisines",
                        "Asie",
                        false,
                        "Mariner 2 h (soja, miel, 5-épices), puis rôtir 30–40 min à 200°C ou sauter au wok 8–10 min."
                ),
                new Product(
                        "Boudin Creole",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(190.0),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler à feu doux 8–10 min; servir avec purée ou salade."
                ),
                new Product(
                        "Saucisses Italiennes",
                        "Saucisse d’inspiration italienne, relevée au fenouil.",
                        new BigDecimal(770.0),
                        "Saucisses et variantes",
                        "Italie",
                        false,
                        "Griller ou poêler 12–15 min; déglacer au vin blanc et fenouil pour renforcer les arômes."
                ),
                new Product(
                        "Scs porc Feta Epnrd",
                        "Scs porc Feta Epnrd préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(750.0),
                        "decoupes-porc-classiques",
                        "Méditerranée",
                        false,
                        "Poêler 12–14 min à feu moyen; servir avec salade grecque."
                ),
                new Product(
                        "Boulette porc",
                        "Boulettes de porc assaisonnées, prêtes à cuisiner.",
                        new BigDecimal(550.0),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Saisir 5–6 min à la poêle puis mijoter 15 min dans une sauce tomate-basilic."
                ),
                new Product(
                        "Jarret de porc",
                        "Morceau gélatineux idéal pour une cuisson longue.",
                        new BigDecimal(320.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Braiser en cocotte 2h30 avec bouquet garni, carottes et oignons."
                ),
                new Product(
                        "Saucisses Campagne",
                        "Saucisses Campagne préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(635.0),
                        "Saucisses et variantes",
                        "France",
                        false,
                        "Griller ou poêler 12–15 min à feu moyen en retournant régulièrement."
                ),
                new Product(
                        "Ti Carry porc",
                        "Curry mauricien au porc, parfumé et généreux.",
                        new BigDecimal(265.0),
                        "produits-cuisines",
                        "Île Maurice",
                        false,
                        "Saisir les morceaux, ajouter pâte de cari, tomates et oignons; mijoter 35–45 min jusqu’à tendreté."
                ),
                new Product(
                        "Saucisse Dc au Rhum",
                        "Saucisse Dc au Rhum préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(680.0),
                        "Saucisses et variantes",
                        "Non spécifié",
                        false,
                        "Griller ou poêler 12–15 min à feu moyen en retournant régulièrement."
                ),
                new Product(
                        "Sciss porc Rougaille",
                        "Saucisse de porc cuisinée à la sauce rougaille, spécialité mauricienne.",
                        new BigDecimal(635.0),
                        "produits-cuisines",
                        "Île Maurice",
                        false,
                        "Saisir la saucisse puis mijoter 20–25 min dans une rougaille de tomates, oignons, thym et piment."
                ),
                new Product(
                        "Rouelle de porc",
                        "Tranche épaisse idéale pour un rôti familial.",
                        new BigDecimal(462.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Mariner ail-thym-moutarde puis rôtir 1h30 à 180°C, en arrosant régulièrement."
                ),
                new Product(
                        "Poitrine de porc",
                        "Poitrine charnue et savoureuse, parfaite rôtie ou braisée.",
                        new BigDecimal(460.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Rôtir 2 h à 170°C, peau vers le haut, puis 15 min à 220°C pour une peau croustillante."
                ),
                new Product(
                        "Jambon Cuit Torchon",
                        "Jambon Cuit Torchon préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(1100.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Déguster froid en fines tranches, ou tiédir légèrement; parfait en sandwich."
                ),
                new Product(
                        "Brochette porc Marinée",
                        "Brochettes de porc marinées, prêtes à griller.",
                        new BigDecimal(605.0),
                        "produits-cuisines",
                        "Non spécifié",
                        false,
                        "Griller 8–10 min en retournant souvent; badigeonner de marinade pendant la cuisson."
                ),
                new Product(
                        "Chipolatas",
                        "Fine saucisse de porc idéale pour le barbecue.",
                        new BigDecimal(630.0),
                        "Saucisses et variantes",
                        "France",
                        false,
                        "Griller 10–12 min à feu moyen sans percer, jusqu’à belle coloration uniforme."
                ),
                new Product(
                        "Paupiettes de porc",
                        "Paupiettes de porc ficelées, prêtes à mijoter.",
                        new BigDecimal(600.0),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Dorer sur toutes les faces puis mijoter 35–45 min en sauce tomate ou crème."
                ),
                new Product(
                        "Porc Chasive",
                        "Préparation parfumée aux notes asiatiques, sucrées-salées.",
                        new BigDecimal(460.0),
                        "produits-cuisines",
                        "Asie",
                        false,
                        "Mariner 2 h (soja, miel, 5-épices), puis rôtir 30–40 min à 200°C ou sauter au wok 8–10 min."
                ),
                new Product(
                        "Sciss Texmex porc",
                        "Sciss Texmex porc préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(825.0),
                        "produits-cuisines",
                        "Mexique / États-Unis",
                        false,
                        "Réchauffer ou cuisiner doucement en sauce; servir avec riz ou légumes."
                ),
                new Product(
                        "Tete de porc Roti",
                        "Tete de porc Roti préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(1500.0),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Réchauffer 15–20 min à 160°C; servir avec moutarde et pommes de terre."
                ),
                new Product(
                        "Terrine Canard Og",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(1050.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde."
                ),
                new Product(
                        "Boudin Bl Foie Gras",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(1200.0),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min à feu doux avec une noisette de beurre, sans percer; servir avec purée ou compote de pommes."
                ),
                new Product(
                        "Boudin Oignons",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(380.0),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min; servir avec confit d’oignons et salade."
                ),
                new Product(
                        "Merguez Agneau",
                        "Saucisse épicée façon merguez, parfaite grillée.",
                        new BigDecimal(850.0),
                        "Saucisses et variantes",
                        "Maghreb",
                        false,
                        "Griller 8–10 min; servir avec semoule et sauce yaourt-citron."
                ),
                new Product(
                        "Rôti Longe",
                        "Rôti de longe tendre, à la chair maigre.",
                        new BigDecimal(560.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Rôtir 25 min/500 g à 180°C; laisser reposer 10 min avant de trancher."
                ),
                new Product(
                        "Porc Crepinettes",
                        "Porc Crepinettes préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(715.0),
                        "Saucisses et variantes",
                        "Non spécifié",
                        false,
                        "Poêler 10–12 min à feu moyen, en retournant à mi-cuisson."
                ),
                new Product(
                        "Pack Bbq",
                        "Pack Bbq préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(750.0),
                        "produits-cuisines",
                        "Non spécifié",
                        false,
                        "Réchauffer ou cuisiner doucement en sauce; servir avec riz ou légumes."
                ),
                new Product(
                        "Boudin Bln Cps Trfs",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(1400.0),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min à feu doux avec une noisette de beurre, sans percer; servir avec purée ou compote de pommes."
                ),
                new Product(
                        "Chipolata aux Herbes",
                        "Fine saucisse de porc idéale pour le barbecue.",
                        new BigDecimal(465.0),
                        "Saucisses et variantes",
                        "France",
                        false,
                        "Griller 10–12 min à feu moyen sans percer, jusqu’à belle coloration uniforme."
                ),
                new Product(
                        "Saute de porc",
                        "Saute de porc préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(600.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Assaisonner puis cuire rôti, poêlé ou mijoté selon le morceau, jusqu’à tendreté."
                ),
                new Product(
                        "Rôti D'épaule",
                        "Rôti D'épaule préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(580.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Rôtir 1h30–2h selon poids avec ail et herbes; arroser en cours de cuisson."
                ),
                new Product(
                        "Pied de porc",
                        "Pièce gélatineuse, parfaite en cuisson lente.",
                        new BigDecimal(180.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Cuire à frémissement 2–3 h avec aromates; griller ensuite pour caraméliser."
                ),
                new Product(
                        "Merguez porc",
                        "Saucisse épicée façon merguez, parfaite grillée.",
                        new BigDecimal(680.0),
                        "Saucisses et variantes",
                        "Maghreb",
                        false,
                        "Griller 8–10 min; servir avec semoule et sauce yaourt-citron."
                ),
                new Product(
                        "Boudin Blanc porc",
                        "Boudin blanc délicat, texture fine et gourmande.",
                        new BigDecimal(680.0),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min à feu doux avec une noisette de beurre, sans percer; servir avec purée ou compote de pommes."
                ),
                new Product(
                        "Sciss porc Cocktail",
                        "Sciss porc Cocktail préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(1200.0),
                        "decoupes-porc-classiques",
                        "Non spécifié",
                        false,
                        "Assaisonner puis cuire rôti, poêlé ou mijoté selon le morceau, jusqu’à tendreté."
                ),
                new Product(
                        "Pastrami",
                        "Viande assaisonnée et fumée, à trancher finement.",
                        new BigDecimal(1400.0),
                        "produits-transformes",
                        "Europe de l'Est / États-Unis",
                        false,
                        "Trancher finement; servir chaud dans un sandwich avec moutarde ou réchauffer à la vapeur 2–3 min."
                ),
                new Product(
                        "Terrine Jmb porc Prs",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(600.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde."
                ),
                new Product(
                        "Scs porc Prun Frmg",
                        "Scs porc Prun Frmg préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(600.0),
                        "decoupes-porc-classiques",
                        "Non spécifié",
                        false,
                        "Poêler 12–14 min à feu moyen-doux; accompagner d’une compotée d’oignons."
                ),
                new Product(
                        "Rôti Échine",
                        "Morceau persillé, savoureux en rôti ou grillades.",
                        new BigDecimal(800.0),
                        "decoupes-porc-classiques",
                        "France",
                        false,
                        "Rôtir 25–30 min/500 g ou griller en tranches marinées (ail, herbes)."
                ),
                new Product(
                        "Terrine Jambon Prsl",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(520.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde."
                ),
                new Product(
                        "Cordon Bleu Plt",
                        "Escalope garnie de jambon et fromage, panée.",
                        new BigDecimal(600.0),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Cuire au four 18–22 min à 200°C jusqu’à cœur fondant; servir avec salade."
                ),
                new Product(
                        "Boudin Ananas & Gingembre",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(630.0),
                        "boudins",
                        "Île Maurice",
                        false,
                        "Dorer à la poêle 8–10 min; déglacer avec un trait de jus d’ananas."
                ),
                new Product(
                        "Terrine Rustiq",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(1600.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde."
                ),
                new Product(
                        "Escalope Panee",
                        "Escalope Panee préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(561.0),
                        "produits-transformes",
                        "France",
                        false,
                        "Réchauffer au four jusqu’à cœur chaud et servir avec garniture."
                ),
                new Product(
                        "Terrine de Campagne",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(1140.0),
                        "charcuteries-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde."
                )
        };


        for (Product product : products) {
            product.setAvailable(true);
            product.setStockQuantity(50);
            product.setUnit("kg");

            will.dev.artisan_des_saveurs.entity.Files img = new Files();
            img.setFilePath("""
                    https://res.cloudinary.com/dcjjwjheh/image/upload/v1755976309/mdfh9nvbsw9t94fdsdmo.png""");
            img.setFileName("chipo_aux_herbes.png");

            product.setProductImage(img);
            productRepository.save(product);
        }
        System.out.println("Products :: "+ products);
    }
}


