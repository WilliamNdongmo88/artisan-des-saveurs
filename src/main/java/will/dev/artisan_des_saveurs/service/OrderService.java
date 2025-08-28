package will.dev.artisan_des_saveurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.order.OrderDTO;
import will.dev.artisan_des_saveurs.dto.order.OrdersResponse;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.dtoMapper.ProductItemMapper;
import will.dev.artisan_des_saveurs.dtoMapper.ProductMapper;
import will.dev.artisan_des_saveurs.entity.*;
import will.dev.artisan_des_saveurs.repository.*;
import will.dev.artisan_des_saveurs.security.UserDetailsImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final String MESSAGE = "Votre commande a été envoyé avec succès !";
    @Value("${app.company.whatsapp.number:+23059221613}")
    private String company_number;
    private final ProductItemMapper productItemMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BrevoService brevoService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final WhatsappNotification whatsappNotification;
    private final ContactRequestRepository contactRequestRepository;
    private final VonageWhatsappNotificationService vonageWhatsappNotificationService;

    @Transactional
    public ResponseEntity<MessageRetourDto> sendOrder(OrderDTO orderDTO) {
        System.out.println("orderDTO ::: " + orderDTO.getItems());
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println("userDetails isEnabled::: " + userDetails.isEnabled());
//        System.out.println(" principal::: " + principal);
        MessageRetourDto messageRetourDto = new MessageRetourDto();
        String email;

        if (userRepository.existsByEmail(orderDTO.getUser().getEmail())) { //
            email = orderDTO.getUser().getEmail();
            System.out.println("email ::: " + email);

            User userConnected = this.userRepository.findByEmailFromConnectedUser(email);
            String message = generateOrderMessage(orderDTO);

            // Enregistrement de la requête de l'utilisateur
            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setUser(userConnected);
            contactRequest.setSubject("Nouvelle commande client");
            contactRequest.setMessage(message);
            contactRequest.setEmailSent(false);
            contactRequest.setWhatsappSent(false);
            ContactRequest savedContactReq = contactRequestRepository.save(contactRequest);

            //userConnected.setContactRequests(List.of(savedContactReq));
            userConnected.getContactRequests().add(savedContactReq);

            // Création et enregistrement de la commande
            //saveOrderWithItems(orderDTO, userConnected);

            Boolean isFromCart = true;
            String customerMessage = customerOrderMessage(orderDTO);
            brevoService.sentToCopany(savedContactReq, isFromCart);
            brevoService.sentResponseToCustomerFromCartPage(userConnected, customerMessage);
            savedContactReq.markEmailSent();

            whatsappNotification.sendWhatsappMessage(userConnected, company_number, savedContactReq, isFromCart);
            vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, userConnected, savedContactReq);
            savedContactReq.markWhatsappSent();

            messageRetourDto.setSuccess(true);
            messageRetourDto.setMessage(MESSAGE);
        }
        else {
            System.out.println("::: User sans compte ou Nonconnecté ::: ");
            User user = new User();
            user.setFirstName(orderDTO.getUser().getFirstName());
            user.setLastName(orderDTO.getUser().getLastName());
            user.setEmail(orderDTO.getUser().getEmail());
            user.setPhone(orderDTO.getUser().getPhone());
            user.setConsent(true);
            user.setEnabled(false);
            user.setUsername("anonymousUser");
            user.setPassword("anonymousUser123");
            User savedUser = userRepository.save(user);
            System.out.println("savedUser ::: " + savedUser);

            String message = generateOrderMessage(orderDTO);

            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setUser(savedUser);
            contactRequest.setSubject("Nouvelle commande client");
            contactRequest.setMessage(message);
            contactRequest.setEmailSent(false);
            contactRequest.setWhatsappSent(false);
            ContactRequest savedContactReq = contactRequestRepository.save(contactRequest);

            savedUser.setContactRequests(List.of(contactRequest));

            Boolean isFromCart = true;
            brevoService.sentToCopany(contactRequest, isFromCart);
            String customerMessage = customerOrderMessage(orderDTO);
            brevoService.sentResponseToCustomerFromCartPage(savedUser, customerMessage);
            contactRequest.setEmailSent(true);
            contactRequest.setEmailSentAt(LocalDateTime.now());
            whatsappNotification.sendWhatsappMessage(savedUser, company_number, savedContactReq, isFromCart);
            vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, savedUser, savedContactReq);
            contactRequest.setWhatsappSent(true);
            contactRequest.setWhatsappSentAt(LocalDateTime.now());

            messageRetourDto.setSuccess(true);
            messageRetourDto.setMessage(MESSAGE);
        }
        return ResponseEntity.ok(messageRetourDto);
    }

    public String generateOrderMessage(OrderDTO orderDto) {
        List<ProductItemDTO> items = orderDto.getItems();
        double subtotal = orderDto.getSubtotal();
        double discount = orderDto.getDiscount();
        double total = orderDto.getTotal();
        boolean freeShipping = orderDto.isFreeShipping();

        // Vérification du panier
        if (items == null || items.isEmpty()) {
            return "<p>Le panier est vide.</p>";
        }

        // Description des items
        StringBuilder itemsDescription = new StringBuilder("<ul>");
        for (int i = 0; i < items.size(); i++) {
            ProductItemDTO item = items.get(i);
            String name = (item.getProduct().getId() != null && item.getProduct().getName() != null)
                    ? item.getProduct().getName()
                    : "Produit inconnu";
            double quantity = item.getDisplayQuantity();
            String unite = item.getSelectedUnit();

            itemsDescription.append(
                    String.format("<li>%d. %s - Quantité : %.2f %s</li>", i + 1, name, quantity, unite)
            );
        }
        itemsDescription.append("</ul>");

        // Message livraison
        String shippingMessage = freeShipping
                ? "🚚 Livraison gratuite incluse.✅"
                : "🚚 Livraison : À la charge du client";

        // Construction du message HTML
        String message = String.format("""
        <html>
        <body>
            <p>Bonjour,</p>
            <p>Nouvelle commande client reçue :</p>

            <h3>👤 Informations du client :</h3>
            <ul>
                <li><b>Nom :</b> %s</li>
                <li><b>Email :</b> %s</li>
                <li><b>Téléphone :</b> %s</li>
            </ul>

            <h3>🛒 Détail de la commande :</h3>
            %s

            <h3>💰 Résumé :</h3>
            <ul>
                <li><b>Sous-total :</b> %.2f Rs</li>
                <li><b>Remise :</b> %.2f Rs</li>
                <li><b>Total à payer :</b> %.2f Rs</li>
                <li><b>%s</b></li>
            </ul>

            <p>Merci de traiter cette commande rapidement.</p>
            <p>Cordialement,<br/>Votre plateforme <b>L'Artisan-des-saveurs</b>.</p>
        </body>
        </html>
        """,
                orderDto.getUser().getFirstName() + " " + orderDto.getUser().getLastName(),
                orderDto.getUser().getEmail(),
                orderDto.getUser().getPhone(),
                itemsDescription.toString(),
                subtotal,
                discount,
                total,
                shippingMessage
        );

        return message.trim();
    }



    public String customerOrderMessage(OrderDTO orderDto) {
        List<ProductItemDTO> items = orderDto.getItems();
        double total = orderDto.getTotal();
        boolean freeShipping = orderDto.isFreeShipping();

        // Vérification du panier
        if (items == null || items.isEmpty()) {
            return "Le panier est vide.";
        }

        // Description des items
        StringBuilder itemsDescription = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ProductItemDTO item = items.get(i);
            String name = item.getProduct() != null && item.getProduct().getName() != null
                    ? item.getProduct().getName()
                    : "Produit inconnu";
            double quantity = item.getDisplayQuantity();
            String unite = item.getSelectedUnit();
            itemsDescription.append(
                    String.format("%d. %s - Quantité : %.2f %s%n", i + 1, name, quantity, unite)
            );
        }

        // Récupération du nombre de commande
        List<Order> orderList = orderRepository.findAll();
        int sizeOrder = orderList.size();

        // Message livraison
        String shippingMessage = freeShipping
                ? "🚚 Livraison gratuite.✅"
                : "🚚 Livraison : À votre charge\n";

        // Construction du message final
        String message = String.format("""
            Bonjour %s,
            Nous vous remercions pour votre commande n°%s passée le %s.
            
            🧾 Récapitulatif de votre commande :
            %s
            
            💰 Total à payer : %.2f Rs
            %s
            📦 Statut : En cours de préparation
            
            Vous recevrez un e-mail dès que votre commande sera prête à être livrée.

            Merci pour votre confiance !
            Bien cordialement,
            Service Client – L'Artisan-des-saveurs.
            """,
                orderDto.getUser().getFirstName()+" "+orderDto.getUser().getLastName(),
                "CMD-00"+ sizeOrder + 1,
                LocalDate.now(),
                itemsDescription.toString(),
                total,
                shippingMessage
        );

        return message.trim();
    }

    @Transactional
    public void saveOrderWithItems(OrderDTO orderDto, User userConnected) {
        System.out.println("::: saveOrderWithItems ::: ");
        // Étape 1 : enregistrer la commande
        Order order = new Order();
        order.setSubtotal(orderDto.getSubtotal());
        order.setDiscount(orderDto.getDiscount());
        order.setTotal(orderDto.getTotal());
        order.setFreeShipping(orderDto.isFreeShipping());
        order.setUser(userConnected);
        order.setCreatedAt(LocalDateTime.now());
        order.setDelivered("processing");
        Order savedOrder = orderRepository.save(order);
        System.out.println("savedOrder ::: " + savedOrder);

        // Étape 2 : enregistrer les items s’ils existent
        List<ProductItem> productItems = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        System.out.println("Produits en base : ");
        products.forEach(p -> System.out.println(" - " + p.getId() + " : " + p.getName()));

        System.out.println("Produits dans orderDTO : ");
        orderDto.getItems().forEach(i -> System.out.println(" - " + i.getProduct().getId() + " x" + i.getQuantity()));

        for (ProductItemDTO pro : orderDto.getItems()) {
            System.out.println("Processing product ID from DTO: " + pro.getProduct().getId());

            Product product = products.stream()
                    .filter(p -> p.getId().equals(pro.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                System.out.println("Product found in DB: " + product.getName());

                ProductItem productItem = new ProductItem();
                productItem.setProduct(product);
                productItem.setQuantity(pro.getQuantity());
                productItem.setSelectedUnit(pro.getSelectedUnit());
                productItem.setDisplayQuantity(pro.getDisplayQuantity());
                productItem.setOrder(savedOrder);
                productItem.setUserId(savedOrder.getUser().getId());
                productItems.add(productItem);
            } else {
                System.out.println("❌ Aucun produit trouvé en base pour l’ID : " + pro.getId());
            }
        }

        System.out.println("productItems ::: " + productItems);
        productItemRepository.saveAll(productItems);

    }

    @Transactional
    public ResponseEntity<?> getUserOrders(Long userId) {
        try {
            // Récupérer toutes les commandes de l'utilisateur
            List<Order> orders = orderRepository.findAllByUserId(userId);

            if (orders.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<OrdersResponse> ordersResponseList = new ArrayList<>();

            for (Order order : orders) {
                OrdersResponse ordersResponse = new OrdersResponse();
                ordersResponse.setId(order.getId());
                ordersResponse.setCreatedAt(order.getCreatedAt());
                ordersResponse.setDelivered(order.getDelivered());
                ordersResponse.setDiscount(order.getDiscount());
                ordersResponse.setSubtotal(order.getSubtotal());
                ordersResponse.setFreeShipping(order.isFreeShipping());
                ordersResponse.setTotal(order.getTotal());
                ordersResponse.setUserid(userId);

                // Récupérer les productItems de cette commande
                List<ProductItem> productItems = productItemRepository.findByOrderId(order.getId());

                // Mapper en DTO
                List<ProductItemDTO> productItemDTOs = productItems.stream()
                        .map(productItemMapper::toDTO)
                        .toList();

                ordersResponse.setProductItems(productItemDTOs);

                ordersResponseList.add(ordersResponse);
            }

            return ResponseEntity.ok(ordersResponseList);

        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes:: " + e.getMessage(), e);
        }
    }


}
