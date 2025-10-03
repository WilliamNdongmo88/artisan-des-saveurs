package will.dev.artisan_des_saveurs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

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
        Object[][] files = {
                {1L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757466961/upi9t1cv54aiblu6vrlt.jpg"},
                {2L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467535/vrzqn4elyqi4xciru85e.png"},
                {3L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467430/dqwf9z8wnwhtioo7pkna.png"},
                {4L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467324/tszbfdudk1sruivdihz1.jpg"},
                {5L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467492/ail2npf0c309jodigyjj.jpg"},
                {6L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467458/r4wgt8tz2igu2ekfdock.jpg"},
                {7L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467610/zsiahiyyi3wyw77aj402.webp"},
                {8L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467283/d14qmcevjf6xqywhyvyx.jpg"},
                {9L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467380/nul3stym63xgyzhgw4l1.jpg"},
                {10L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467670/flr5fupik6kyba1kdlsc.jpg"},
                {11L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467565/emt3oajyb4xjg2vzyqtv.jpg"},
                {12L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467849/a8runljjzlvhhdmtgdnf.jpg"},
                {13L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467764/ipu7moehgccapnrmjts4.jpg"},
                {14L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757731129/aq02dmgnlxdo4lo8pyso.jpg"},
                {15L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467814/n4ycjpx4tlgjmgmeicqp.jpg"},
                {16L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467732/tkvggngcbglwba9dj5lf.jpg"},
                {17L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467120/epf0iwu5ivcjfu7onh2g.webp"},
                {18L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757731266/jagdfelzidyeb2gvz6z7.webp"},
                {19L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732162/jgpnid1m8qh75lx0ukwb.jpg"},
                {20L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757467053/rxsibdabvvdkp35fblpj.jpg"},
                {21L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757731712/g7iebxpjg7r7kldcgbrh.jpg"},
                {22L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1758055204/qmmavtfpxmkofxadmchz.jpg"},
                {23L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1755976309/mdfh9nvbsw9t94fdsdmo.png"},
                {24L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757984444/tatgkdcofolfv4442chv.jpg"},
                {25L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757468051/q5rvswca7igjzjqaspef.webp"},
                {26L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732926/qzisfheikvln4lgocquj.jpg"},
                {27L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757984725/ooheouoxzp93fblwanmt.jpg"},
                {28L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1758055933/mnw4gtudtugvd5gfxn90.jpg"},
                {29L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1758056120/xjbhllt4viftuzt3v2eu.jpg"},
                {30L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732571/furlulbjkp2d6d9mhkrm.jpg"},
                {31L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732356/ymdhqguksddlwgidqalp.jpg"},
                {32L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757468008/ef3clohcbwsee5c7qqbq.jpg"},
                {33L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1755976309/mdfh9nvbsw9t94fdsdmo.png"},
                {34L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732804/spqz3vb21r9sh73kvwmh.jpg"},
                {35L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757732693/ep24ykngkm4i0rzovf5e.jpg"},
                {36L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757984244/mtgtgqjpawbsuxeofoib.jpg"},
                {37L, "https://res.cloudinary.com/dcjjwjheh/image/upload/v1757984725/ooheouoxzp93fblwanmt.jpg"},

        };

        List<Product> products = List.of(
                new Product(
                        "Chipolatas",
                        "Fine saucisse de porc idéale pour le barbecue.",
                        new BigDecimal(523.88),
                        "saucisses-et-variantes",
                        "France",
                        true,
                        "Griller 10–12 min à feu moyen sans percer, jusqu’à belle coloration uniforme.",
                        "Porc, sel, sucre, épices",
                        9L
                ),
                new Product(
                        "Rouelle de porc",
                        "Tranche épaisse idéale pour un rôti familial.",
                        new BigDecimal(346.50),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Mariner ail-thym-moutarde puis rôtir 1h30 à 180°C, en arrosant régulièrement.",
                        "Porc",
                        7L
                ),
                new Product(
                        "Saucisse douce au Rhum",
                        "Saucisse Dc au Rhum préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(561.00),
                        "saucisses-et-variantes",
                        "Non spécifié",
                        false,
                        "Griller ou poêler 12–15 min à feu moyen en retournant régulièrement.",
                        null,
                        12L
                ),
                new Product(
                        "Escalope de porc",
                        "Escalope de porc préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(363.00),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Assaisonner puis cuire rôti, poêlé ou mijoté selon le morceau, jusqu’à tendreté.",
                        "Porc",
                        8L
                ),
                new Product(
                        "Saucisses Campagne",
                        "Saucisses Campagne préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(532.13),
                        "saucisses-et-variantes",
                        "France",
                        false,
                        "Griller ou poêler 12–15 min à feu moyen en retournant régulièrement.",
                        "Porc, oignons, persil, sel, sucre, épices, œufs, vin blanc",
                        13L
                ),
                new Product(
                        "Poitrine de porc",
                        "Poitrine charnue et savoureuse, parfaite rôtie ou braisée.",
                        new BigDecimal(379.50),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Rôtir 2 h à 170°C, peau vers le haut, puis 15 min à 220°C pour une peau croustillante.",
                        "Porc",
                        10L
                ),
                new Product(
                        "Paupiettes de porc",
                        "Paupiettes de porc ficelées, prêtes à mijoter.",
                        new BigDecimal(495.00),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Dorer sur toutes les faces puis mijoter 35–45 min en sauce tomate ou crème.",
                        "Porc",
                        18L
                ),
                new Product(
                        "Jarret de porc",
                        "Morceau gélatineux idéal pour une cuisson longue.",
                        new BigDecimal(210.00),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Braiser en cocotte 2h30 avec bouquet garni, carottes et oignons.",
                        "Porc",
                        6L
                ),
                new Product(
                        "Boulette porc",
                        "Boulettes de porc assaisonnées, prêtes à cuisiner.",
                        new BigDecimal(329.67),
                        "produits-transformes",
                        "Non spécifié",
                        true,
                        "Saisir 5–6 min à la poêle puis mijoter 15 min dans une sauce tomate-basilic.",
                        "Porc, epices, sel, poivre",
                        4L
                ),
                new Product(
                        "Rillettes de porc",
                        "Préparation fondante de porc effiloché, idéale à tartiner.",
                        new BigDecimal(633.75),
                        "charcuteries-et-terrines",
                        "France",
                        false,
                        "Servir froid sur pain grillé; ajouter cornichons et oignons grelots.",
                        "Porc, sel, sucres, épices",
                        11L
                ),
                new Product(
                        "Tete de porc Roti",
                        "Tete de porc Roti préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(375.00),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Réchauffer 15–20 min à 160°C; servir avec moutarde et pommes de terre.",
                        "Porc, Porc cuit, sel, poivre, poivrons, épices, soja",
                        11L
                ),
                new Product(
                        "Jambon Cuit Torchon",
                        "Jambon Cuit Torchon préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(674.25),
                        "viande-a-la-coupe",
                        "France",
                        true,
                        "Déguster froid en fines tranches, ou tiédir légèrement; parfait en sandwich.",
                        "Porc, saumure, sel, poivre",
                        3L
                ),
                new Product(
                        "Boudin Creole",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(474.38),
                        "boudins",
                        "Non spécifié",
                        true,
                        "Poêler à feu doux 8–10 min; servir avec purée ou salade.",
                        "Porc, sang, pain, épices, sel, sucres, œufs",
                        17L
                ),
                new Product(
                        "Ti Carry porc",
                        "Curry mauricien au porc, parfumé et généreux.",
                        new BigDecimal(180.00),
                        "viande-a-la-coupe",
                        "Île Maurice",
                        false,
                        "Saisir les morceaux, ajouter pâte de cari, tomates et oignons; mijoter 35–45 min jusqu’à tendreté.",
                        "Porc",
                        14L
                ),
                new Product(
                        "Brochette porc Marinée",
                        "Brochettes de porc marinées, prêtes à griller.",
                        new BigDecimal(442.50),
                        "viande-a-la-coupe",
                        "Non spécifié",
                        false,
                        "Griller 8–10 min en retournant souvent; badigeonner de marinade pendant la cuisson.",
                        "Porc, épices, oignons, poivrons, huile, sel, poivre",
                        14L
                ),
                new Product(
                        "Boudin Oignons",
                        "Boudin savoureux à poêler, idéal en entrée ou plat.",
                        new BigDecimal(429.00),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min; servir avec confit d’oignons et salade.",
                        "Porc, sang, pain, oignons, sel, sucres, épices, œufs",
                        19L
                ),
                new Product(
                        "Saucisses Toulouse",
                        "Saucisse traditionnelle de Toulouse, gourmande et généreuse.",
                        new BigDecimal(664.13),
                        "saucisses-et-variantes",
                        "France",
                        true,
                        "Cuire à la poêle ou au barbecue 15–18 min à feu moyen, en piquant légèrement et en retournant régulièrement.",
                        "Porc, oignons, persil, sel, sucre, épices, vin blanc",
                        5L
                ),
                new Product(
                        "Boerewors",
                        "Saucisse sud-africaine en spirale, riche en épices douces.",
                        new BigDecimal(680.00),
                        "saucisses-et-variantes",
                        "Afrique du Sud",
                        false,
                        "Former une spirale, huiler légèrement et griller 18–20 min à feu moyen, en retournant souvent.",
                        "Boeuf, gras mouton, épices",
                        20L
                ),
                new Product(
                        "Saucisses Italiennes",
                        "Saucisse d’inspiration italienne, relevée au fenouil.",
                        new BigDecimal(770.00),
                        "saucisses-et-variantes",
                        "Italie",
                        false,
                        "Griller ou poêler 12–15 min; déglacer au vin blanc et fenouil pour renforcer les arômes.",
                        "Porc, sel, sucre, épices, œufs",
                        16L
                ),
                new Product(
                        "Pâté En Croûte",
                        "Pâté En Croûte préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(1212.75),
                        "charcuteries-et-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches, avec cornichons et salade verte.",
                        "Porc, farine, épices, œufs, beurre, sel, poivre, pistaches",
                        2L
                ),
                new Product(
                        "Pastrami",
                        "Viande assaisonnée et fumée, à trancher finement.",
                        new BigDecimal(900.00),
                        "charcuteries-et-terrines",
                        "Europe de l'Est / États-Unis",
                        false,
                        "Trancher finement; servir chaud dans un sandwich avec moutarde ou réchauffer à la vapeur 2–3 min.",
                        "Bœuf, épices, saumure",
                        34L
                ),
                new Product(
                        "Escalope Panee",
                        "Escalope Panee préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(420.75),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Réchauffer au four jusqu’à cœur chaud et servir avec garniture.",
                        "Porc, chapelure, œufs, chapelure, sel, poivre, lait",
                        25L
                ),
                new Product(
                        "Cordon Bleu Porc",
                        "Escalope garnie de jambon et fromage, panée.",
                        new BigDecimal(495.00),
                        "produits-transformes",
                        "Non spécifié",
                        false,
                        "Cuire au four 18–22 min à 200°C jusqu’à cœur fondant; servir avec salade.",
                        "Porc, jambon, emmental, chapelure, œufs, farine",
                        23L
                ),
                new Product(
                        "Rôti de porc cuit",
                        "Pièce de porc rôtie lentement au four, déjà cuite et prête à être dégustée, offrant une viande tendre et savoureuse.",
                        new BigDecimal(360.00),
                        "produits-transformes",
                        "France",
                        false,
                        "Servir chaud avec de la moutarde et du pain frais, ou réchauffer doucement au four avec un jus ou une sauce pour un repas complet.",
                        "Porc cuit, sel, poivre",
                        24L
                ),
                new Product(
                        "Boudin Mauricien",
                        "Spécialité locale à base de sang et de viande de porc, relevée avec des épices créoles et des herbes aromatiques.",
                        new BigDecimal(165.00),
                        "boudins",
                        "Île Maurice",
                        true,
                        "Faire revenir doucement à la poêle avec un peu d’huile, puis servir chaud accompagné de riz créole, lentilles ou d’une rougaille de tomates.",
                        "Porc, sang, farine, gras de porc, sel, poivre, épices, oignons, persil",
                        28L
                ),
                new Product(
                        "Boudin Mauricien Piment",
                        "Variante épicée du boudin mauricien, préparée avec du sang et de la viande de porc, relevée de piments frais et d’épices créoles.",
                        new BigDecimal(165.00),
                        "boudins",
                        "Île Maurice",
                        false,
                        "Réchauffer doucement à la poêle ou au four. Déguster chaud avec un riz basmati, des haricots rouges ou une salade créole pour adoucir le piquant.",
                        "Porc, sang, gras de porc, sel, poivre, épices, oignons, persil",
                        29L
                ),
                new Product(
                        "Chipolata aux Herbes",
                        "Fine saucisse de porc idéale pour le barbecue.",
                        new BigDecimal(548.63),
                        "saucisses-et-variantes",
                        "France",
                        true,
                        "Griller 10–12 min à feu moyen sans percer, jusqu’à belle coloration uniforme.",
                        "Porc, sel, sucre, épices",
                        33L
                ),
                new Product(
                        "Merguez Agneau",
                        "Saucisse épicée façon merguez, parfaite grillée.",
                        new BigDecimal(709.50),
                        "saucisses-et-variantes",
                        "Maghreb",
                        false,
                        "Griller 8–10 min; servir avec semoule et sauce yaourt-citron.",
                        "Agneau, poulet, sel, sucres, épices",
                        30L
                ),
                new Product(
                        "Rôti Échine",
                        "Morceau persillé, savoureux en rôti ou grillades.",
                        new BigDecimal(478.50),
                        "viande-a-la-coupe",
                        "France",
                        true,
                        "Rôtir 25–30 min/500 g ou griller en tranches marinées (ail, herbes).",
                        "Porc",
                        26L
                ),
                new Product(
                        "Merguez porc",
                        "Saucisse épicée façon merguez, parfaite grillée.",
                        new BigDecimal(519.75),
                        "saucisses-et-variantes",
                        "Maghreb",
                        true,
                        "Griller 8–10 min; servir avec semoule et sauce yaourt-citron.",
                        "Porc, sel, sucres, épices",
                        31L
                ),
                new Product(
                        "Côtes de Porc",
                        "Côtes de porc fraîches, parfaites pour vos grillades et barbecues. Viande tendre et savoureuse, idéale pour les repas en famille ou entre amis.",
                        new BigDecimal(437.25),
                        "viande-a-la-coupe",
                        "Île Maurice",
                        true,
                        "Idéales grillées au barbecue ou à la plancha. Cuisson recommandée : 6-8 minutes de chaque côté à feu moyen.",
                        "Porc",
                        1L
                ),
                new Product(
                        "Pied de porc",
                        "Pièce gélatineuse, parfaite en cuisson lente.",
                        new BigDecimal(180.00),
                        "viande-a-la-coupe",
                        "France",
                        false,
                        "Cuire à frémissement 2–3 h avec aromates; griller ensuite pour caraméliser.",
                        "Porc",
                        35L
                ),
                new Product(
                        "Paté de tête",
                        "Préparation traditionnelle à base de morceaux de tête de porc cuits longuement, pressés et assaisonnés pour offrir une charcuterie savoureuse et rustique.",
                        new BigDecimal(618.75),
                        "charcuteries-et-terrines",
                        "France",
                        false,
                        "Servir frais en tranches, accompagné de pain de campagne, cornichons et moutarde pour un apéritif ou une entrée gourmande.",
                        "Porc, sel, légumes, poivre, gélatine",
                        36L
                ),
                new Product(
                        "Terrine de Campagne",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(633.75),
                        "charcuteries-et-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde.",
                        "Porc,sel, sucre, épices, gélatine, bovine",
                        24L
                ),
                new Product(
                        "Saute de porc",
                        "Saute de porc préparé avec soin, idéal pour vos repas.",
                        new BigDecimal(363.00),
                        "viande-a-la-coupe",
                        "France",
                        true,
                        "Assaisonner puis cuire rôti, poêlé ou mijoté selon le morceau, jusqu’à tendreté.",
                        "Porc",
                        32L
                ),
                new Product(
                        "Boudin Blanc porc",
                        "Boudin blanc délicat, texture fine et gourmande.",
                        new BigDecimal(495.00),
                        "boudins",
                        "Non spécifié",
                        false,
                        "Poêler 8–10 min à feu doux avec une noisette de beurre, sans percer; servir avec purée ou compote de pommes.",
                        "Porc, crème, pain, oignon, lait",
                        22L
                ),
                new Product(
                        "Jambon Persillé",
                        "Terrine rustique artisanale, à savourer sur pain de campagne.",
                        new BigDecimal(562.50),
                        "charcuteries-et-terrines",
                        "France",
                        false,
                        "Servir frais, en tranches épaisses, avec pain de campagne et moutarde.",
                        "Porc, epices, légume, saumure, gélatine",
                        27L
                )
        );


        for (Product product : products) {
            for(Object[] file : files){
                Long id = (Long) file[0];
                String url = (String) file[1];
                if (product.getImgId() == id){
                    product.setAvailable(true);
                    product.setStockQuantity(50);
                    product.setUnit("kg");

                    will.dev.artisan_des_saveurs.entity.Files img = new Files();
                    img.setFilePath((String) file[1]);
                    img.setFileName("");

                    product.setProductImage(img);
                    productRepository.save(product);
                }
            }
        }
        System.out.println("Products :: "+ products);
    }
}
