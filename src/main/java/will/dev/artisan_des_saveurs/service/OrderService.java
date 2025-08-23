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

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final String MESSAGE = "Votre commande a été envoyé avec succès !";
    @Value("${app.company.whatsapp.number:+23059221613}")
    private String company_number;
    private final ProductItemMapper productItemMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final WhatsappNotification whatsappNotification;
    private final ContactRequestRepository contactRequestRepository;
    private final VonageWhatsappNotificationService vonageWhatsappNotificationService;

    @Transactional
    public ResponseEntity<MessageRetourDto> sendOrder(OrderDTO orderDTO) {
        System.out.println("orderDTO ::: " + orderDTO.getItems());
        //Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //System.out.println("userDetails isEnabled::: " + userDetails.isEnabled());
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
            saveOrderWithItems(orderDTO, userConnected);

            Boolean isFromCart = true;
            String customerMessage = customerOrderMessage(orderDTO);
            notificationService.sentToCopany(savedContactReq, isFromCart);
            notificationService.sentResponseToCustomerFromCartPage(userConnected, customerMessage);
            savedContactReq.markEmailSent();

            whatsappNotification.sendWhatsappMessage(userConnected, company_number, savedContactReq, isFromCart);
            vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, userConnected, savedContactReq);
            savedContactReq.markWhatsappSent();

            messageRetourDto.setSuccess(true);
            messageRetourDto.setMessage(MESSAGE);
        }
        else {
            //User sans compte ou Nonconnecté
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
            notificationService.sentToCopany(contactRequest, isFromCart);
            String customerMessage = customerOrderMessage(orderDTO);
            notificationService.sentResponseToCustomerFromCartPage(savedUser, customerMessage);
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
            return "Le panier est vide.";
        }

        // Description des items
        StringBuilder itemsDescription = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            ProductItemDTO item = items.get(i);
            String name = item.getProduct().getId() != null && item.getProduct().getName() != null
                    ? item.getProduct().getName()
                    : "Produit inconnu";
            int quantity = item.getQuantity();
            itemsDescription.append(String.format("%d. %s - Quantité : %dKg\n", i + 1, name, quantity));
        }

        // Message livraison
        String shippingMessage = freeShipping
                ? "🚚 Livraison gratuite incluse.✅"
                : "🚚 Livraison : À la charge du client\n";

        // Construction du message final
        String message = String.format("""
            Bonjour,

            Nouvelle commande client reçue :

            👤 Informations du client :
            - Nom : %s
            - Email : %s
            - Téléphone : %s

            🛒 Détail de la commande :
            %s
            💰 Résumé :
            - Sous-total : %.2f Rs
            - Remise : %.2f Rs
            - Total à payer : %.2f Rs
            - %s

            Merci de traiter cette commande rapidement.

            Cordialement,
            Votre plateforme L'Artisan-des-saveurs.
            """,
                orderDto.getUser().getFirstName()+" "+orderDto.getUser().getLastName(),
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
            int quantity = item.getQuantity();
            itemsDescription.append(String.format("%d. %s - Quantité : %dKg\n", i + 1, name, quantity));
        }

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
                "CMD000001",
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
    public ResponseEntity<?> getUserOrders(Long id) {
        try {
            List<ProductItem> productItems = productItemRepository.findByUserId(id);
            System.out.println("productItems ::: " + productItems);
            List<OrdersResponse> ordersResponses = new ArrayList<>();
            for (ProductItem productItem : productItems){
                OrdersResponse ordersResponse = new OrdersResponse();
                ordersResponse.setDiscount(productItem.getOrder().getDiscount());
                ordersResponse.setSubtotal(productItem.getOrder().getSubtotal());
                ordersResponse.setFreeShipping(productItem.getOrder().isFreeShipping());
                ordersResponse.setTotal(productItem.getOrder().getTotal());
                ordersResponse.setUserid(productItem.getUserId());
                ordersResponse.setCreateAt(productItem.getOrder().getCreatedAt());
                ordersResponse.setProductItem(productItemMapper.toDTO(productItem));
                ordersResponses.add(ordersResponse);
            }
            System.out.println("#### productItems ::: " + productItems);
            System.out.println("#### ordersResponses ::: " + ordersResponses);
            return ResponseEntity.ok(ordersResponses);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes:: " + e);
        }
    }
}
