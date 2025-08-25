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
                        "Côtes de Porc Première",
                        "Côtes de porc fraîches, parfaites pour vos grillades et barbecues. Viande tendre et savoureuse, idéale pour les repas en famille ou entre amis.",
                        new BigDecimal("583"),
                        "cotes-travers",
                        "Île Maurice",
                        true,
                        "Idéales grillées au barbecue ou à la plancha. Cuisson recommandée : 6-8 minutes de chaque côté à feu moyen."
                ),
                new Product(
                        "Rouelle de Porc",
                        "Morceau généreux découpé dans la cuisse du porc, avec os et gras, idéal pour les cuissons longues. Sa chair moelleuse et savoureuse en fait un plat familial traditionnel apprécié.",
                        new BigDecimal("462"),
                        "cotes-travers",
                        "Île Maurice",
                        false,
                        "Cuisson lente au four à 180°C pendant 2h à 2h30, arrosée régulièrement de son jus. Peut aussi être mijotée en cocotte avec des légumes et des aromates."
                ),
                new Product(
                        "Rôti de Porc dans l'Échine",
                        "Rôti de porc tendre et juteux, parfait pour vos repas dominicaux. Préparé avec soin par nos bouchers expérimentés.",
                        new BigDecimal("550"),
                        "rotis-filets",
                        "Île Maurice",
                        true,
                        "Cuisson au four à 180°C, compter 25 minutes par 500g. Laisser reposer 10 minutes avant de découper."
                ),
                new Product(
                        "Filet de Porc",
                        "Morceau noble et tendre, le filet de porc se cuisine rapidement et offre une chair délicate et savoureuse.",
                        new BigDecimal("290"),
                        "rotis-filets",
                        "Île Maurice",
                        false,
                        "Cuisson rapide à la poêle ou au four. 15-20 minutes à 200°C. Ne pas trop cuire pour garder la tendreté."
                ),
                new Product(
                        "Rôti de Porc Farci",
                        "Rôti de porc farci aux herbes de Provence et aux champignons. Une spécialité de notre boucherie pour les grandes occasions.",
                        new BigDecimal("390"),
                        "rotis-filets",
                        "Île Maurice",
                        true,
                        "Cuisson au four à 170°C pendant 45 minutes. Arroser régulièrement avec le jus de cuisson."
                ),
                new Product(
                        "Chipolata Normale",
                        "Saucisses artisanales préparées selon nos recettes traditionnelles. 100% viande de porc, sans colorants ni conservateurs.",
                        new BigDecimal("698"),
                        "saucisses-charcuterie",
                        "Fabrication Maison",
                        true,
                        "Cuisson à la poêle à feu moyen pendant 12-15 minutes en les retournant régulièrement."
                ),
                new Product(
                        "Chipolata aux Herbes",
                        "Saucisses parfumées aux herbes de Provence, idéales pour apporter une touche méditerranéenne à vos repas.",
                        new BigDecimal("731.50"),
                        "saucisses-charcuterie",
                        "Fabrication Maison",
                        false,
                        "Parfaites grillées au barbecue ou cuites à la poêle avec un peu d'huile d'olive."
                ),
                new Product(
                        "Jambon Cuit Torchon",
                        "Jambon blanc cuit à l'ancienne, sans polyphosphates. Goût authentique et texture fondante garantis.",
                        new BigDecimal("885.50"),
                        "saucisses-charcuterie",
                        "Fabrication Maison",
                        true,
                        "Prêt à consommer. Parfait en sandwich, salade ou plat chaud. Se conserve 5 jours au réfrigérateur."
                ),
                new Product(
                        "Jarret de Porc",
                        "Jarret de porc parfait pour vos plats mijotés et potées. Viande gélatineuse qui apporte du moelleux à vos préparations.",
                        new BigDecimal("220"),
                        "morceaux-braiser",
                        "Île Maurice",
                        false,
                        "Idéal pour les plats mijotés. Cuisson lente 2-3h dans un bouillon avec légumes et aromates."
                ),
                new Product(
                        "Palette de Porc",
                        "Morceau savoureux idéal pour les cuissons lentes. Parfait pour les rôtis braisés et les plats en sauce.",
                        new BigDecimal("430"),
                        "morceaux-braiser",
                        "Île Maurice",
                        false,
                        "Braiser au four à 160°C pendant 2h30 avec légumes et vin blanc. Viande fondante garantie."
                ),
                new Product(
                        "Poitrine de Porc",
                        "Poitrine de porc fraîche, parfaite pour les lardons maison ou les plats traditionnels comme le petit salé.",
                        new BigDecimal("506"),
                        "morceaux-braiser",
                        "Île Maurice",
                        false,
                        "Pour petit salé : cuire 1h30 dans l'eau bouillante avec légumes. Pour lardons : découper et faire revenir."
                ),
                new Product(
                        "Lardons Fumés",
                        "Lardons fumés au bois de hêtre, découpés à la demande. Parfaits pour vos quiches, salades et plats cuisinés.",
                        new BigDecimal("250"),
                        "produits-transformes",
                        "Fabrication Maison",
                        false,
                        "Faire revenir à sec dans une poêle chaude jusqu'à ce qu'ils soient dorés et croustillants."
                ),
                new Product(
                        "Bacon Artisanal",
                        "Bacon préparé selon la méthode traditionnelle, fumé lentement pour développer tous ses arômes.",
                        new BigDecimal("590"),
                        "produits-transformes",
                        "Fabrication Maison",
                        false,
                        "Cuire à la poêle 2-3 minutes de chaque côté jusqu'à obtenir la croustillance désirée."
                ),
                new Product(
                        "Pâté de Campagne",
                        "Pâté de campagne traditionnel préparé avec du porc fermier. Recette familiale transmise de génération en génération.",
                        new BigDecimal("390"),
                        "produits-transformes",
                        "Fabrication Maison",
                        false,
                        "Prêt à consommer. Servir à température ambiante avec du pain frais et des cornichons."
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


