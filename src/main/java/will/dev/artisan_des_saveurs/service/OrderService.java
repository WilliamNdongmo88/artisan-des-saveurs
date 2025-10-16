package will.dev.artisan_des_saveurs.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.order.OrderDTO;
import will.dev.artisan_des_saveurs.dto.order.OrdersResponse;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.dtoMapper.OrderMapper;
import will.dev.artisan_des_saveurs.dtoMapper.ProductItemMapper;
import will.dev.artisan_des_saveurs.entity.*;
import will.dev.artisan_des_saveurs.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final String MESSAGE = "Votre commande a été envoyé avec succès !";
    @Value("${app.company.whatsapp.number}")
    private String company_number;
    private final OrderMapper orderMapper;
    private final ProductItemMapper productItemMapper;
    private final UserRepository userRepository;
    private final BrevoService brevoService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final VonageWhatsappNotificationService vonageWhatsappNotificationService;

    @Transactional
    public ResponseEntity<MessageRetourDto> sendOrder(OrderDTO orderDTO) {
        System.out.println("orderDTO ::: " + orderDTO.getItems());
        MessageRetourDto messageRetourDto = new MessageRetourDto();
        String email;

        try{
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

                userConnected.getContactRequests().add(savedContactReq);

                // Création et enregistrement de la commande
                saveOrderWithItems(orderDTO, userConnected);

                Boolean isFromCart = true;
                String customerMessage = customerOrderMessage(orderDTO);
                brevoService.sentToCompany(savedContactReq, isFromCart);
                brevoService.sentResponseToCustomerFromCartPage(userConnected, customerMessage);
                savedContactReq.markEmailSent();

                //whatsappNotification.sendWhatsappMessage(userConnected, company_number, savedContactReq, isFromCart);
                //vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, userConnected, savedContactReq);
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
                brevoService.sentToCompany(contactRequest, isFromCart);
                String customerMessage = customerOrderMessage(orderDTO);
                brevoService.sentResponseToCustomerFromCartPage(savedUser, customerMessage);
                contactRequest.setEmailSent(true);
                contactRequest.setEmailSentAt(LocalDateTime.now());
                //whatsappNotification.sendWhatsappMessage(savedUser, company_number, savedContactReq, isFromCart);
                //vonageWhatsappNotificationService.sendWhatsappMessageToCustomer(isFromCart, savedUser, savedContactReq);
                contactRequest.setWhatsappSent(true);
                contactRequest.setWhatsappSentAt(LocalDateTime.now());

                messageRetourDto.setSuccess(true);
                messageRetourDto.setMessage(MESSAGE);
            }
            return ResponseEntity.ok(messageRetourDto);
        } catch (RuntimeException e) {
            throw new RuntimeException("SendOrder_ERROR :: " + e.getMessage());
        }
    }

    public String generateOrderMessage(OrderDTO orderDto) {
        try{
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
            String deliveryMethod = Objects.equals(orderDto.getDeliveryMethod(), "expedier") ?
                    "🚚 Livraison : Le client souhaite être livré." :
                    "📦 Retrait : Le client viendra récupérer sa commande.";

            // Message paiement
            String paymentMethod;
            if (Objects.equals(orderDto.getPaymentMethod(), "bank")) { // "mips","bank","delivery"
                paymentMethod = "💳 Paiement : Effectué par Juice (banque). Merci de vérifier votre compte.";
            } else if (Objects.equals(orderDto.getPaymentMethod(), "delivery")) {
                paymentMethod = "💵 Paiement : À effectuer lors de la livraison.";
            } else {
                paymentMethod = "💳 Paiement : Service de paiement en ligne.";
            }

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
            </ul>
    
            <h3>📦 Livraison & Paiement :</h3>
            <ul>
                <li>%s</li>
                <li>%s</li>
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
                    deliveryMethod,
                    paymentMethod
            );

            return message.trim();
        } catch (RuntimeException e) {
            throw new RuntimeException("GenerateOrderMessage_ERROR :: " + e.getMessage());
        }
    }

    public String customerOrderMessage(OrderDTO orderDto) {
        try{
            List<ProductItemDTO> items = orderDto.getItems();
            double total = orderDto.getTotal();

            // Vérification du panier
            if (items == null || items.isEmpty()) {
                return "<p>Le panier est vide.</p>";
            }

            // Description des items
            StringBuilder itemsDescription = new StringBuilder("<ul>");
            for (ProductItemDTO item : items) {
                String name = (item.getProduct() != null && item.getProduct().getName() != null)
                        ? item.getProduct().getName()
                        : "Produit inconnu";
                double quantity = item.getDisplayQuantity();
                String unite = item.getSelectedUnit();

                itemsDescription.append(
                        String.format("<li>%s - Quantité : %.2f %s</li>", name, quantity, unite)
                );
            }
            itemsDescription.append("</ul>");

            // Numéro de commande
            int sizeOrder = orderRepository.findAll().size();
            String orderNumber = "CMD-00" + (sizeOrder);

            // Date formatée
            String orderDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Message livraison
            String deliveryMethod = "expedier".equalsIgnoreCase(orderDto.getDeliveryMethod()) ?
                    "🚚 Livraison : Une notification vous sera envoyée avant que le livreur ne vienne." :
                    "🏬 Retrait : Dès que votre commande sera prête, vous recevrez une notification pour venir la récupérer.";

            // Construction du message final
            String message = String.format("""
                <html>
                <body>
                    Bonjour %s,<br/><br/>
                    Nous vous remercions pour votre commande <b>%s</b> passée le %s.<br/><br/>
                    
                    🧾 <b>Récapitulatif de votre commande :</b><br/>
                    %s<br/>
                    
                    💰 <b>Total à payer :</b> %.2f Rs<br/>
                    %s<br/><br/>
                    
                    📦 <b>Statut :</b> En cours de préparation<br/><br/>
                    
                    Merci pour votre confiance !<br/><br/>
                    
                    Bien cordialement,<br/>
                    Service Client – <i>L'Artisan-des-saveurs</i>
                </body>
                </html>
                """,
                    orderDto.getUser().getFirstName() + " " + orderDto.getUser().getLastName(),
                    orderNumber,
                    orderDate,
                    itemsDescription.toString(),
                    total,
                    deliveryMethod
            );
            //Vous serez également notifié dès que votre commande sera prête.<br/><br/>

            return message.trim();
        } catch (RuntimeException e) {
            throw new RuntimeException("CustomerOrderMessage_ERROR :: " + e.getMessage());
        }
    }

    public String sendDeliveryCustomerMessage(Order order){
        try{
            // Construire le contenu du mail
            String subject = "📦 Votre commande est en cours de livraison";
            List<ProductItem> items = order.getItems();
            // Vérification du panier
            if (items == null || items.isEmpty()) {
                return "<p>Le panier est vide.</p>";
            }

            // Description des items
            StringBuilder itemsDescription = new StringBuilder("<ul>");
            for (int i = 0; i < items.size(); i++) {
                ProductItem item = items.get(i);
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
            String message = String.format("""
                <html>
                <body>
                    <p>Bonjour %s %s,</p>
                    <p>Votre commande <b>CMD-%d</b> est en cours de livraison 🚚.</p>
                    
                    <h3>🧾 Détails de votre commande :</h3>
                    %s
                    
                    <ul>
                        <li>Total payé : <b>%.2f Rs</b></li>
                        <li>Date de commande : %s</li>
                    </ul>

                    <p>⏳ Le livreur vous contactera prochainement.</p>

                    <p>Merci pour votre confiance,<br/>
                    L’équipe <b>L’Artisan-des-saveurs</b>.</p>
                </body>
                </html>
                """,
                    order.getUser().getFirstName(),
                    order.getUser().getLastName(),
                    order.getId(),
                    itemsDescription.toString(),
                    order.getTotal(),
                    order.getCreatedAt().toLocalDate()
            );
            return message;
        } catch (RuntimeException e) {
            throw new RuntimeException("SendDeliveryCustomerMessage_ERROR :: " +e.getMessage());
        }
    }

    public String sendRecoveryCustomerMessage(Order order){
        try{
            String subject = "📢 Votre commande est prête à être récupérée";
            List<ProductItem> items = order.getItems();
            // Vérification du panier
            if (items == null || items.isEmpty()) {
                return "<p>Le panier est vide.</p>";
            }

            // Description des items
            StringBuilder itemsDescription = new StringBuilder("<ul>");
            for (int i = 0; i < items.size(); i++) {
                ProductItem item = items.get(i);
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

            String message = String.format("""
                <html>
                <body>
                    <p>Bonjour %s %s,</p>
                    <p>Votre commande <b>CMD-%d</b> est désormais prête ✅.</p>
                    
                    <h3>🧾 Détails de votre commande :</h3>
                    %s
                    
                    <ul>
                        <li>Total à régler / payé : <b>%.2f Rs</b></li>
                        <li>Date de commande : %s</li>
                    </ul>

                    <p>📍 Merci de bien vouloir venir la récupérer:</p>
                     <p>
                         <a href="https://www.google.com/maps/dir/?api=1&destination=Aspenvillas%%2C%%20XHJF%%2BM6W%%2C%%20Topize%%20Rd%%2C%%20Grand%%20Baie" target="_blank">
                            📌 Voir la localisation sur Google Maps
                         </a>
                     </p>
                    <p>Nous vous remercions pour votre confiance,<br/>
                    L’équipe <b>L’Artisan-des-saveurs</b>.</p>
                </body>
                </html>
                """,
                    order.getUser().getFirstName(),
                    order.getUser().getLastName(),
                    order.getId(),
                    itemsDescription.toString(),
                    order.getTotal(),
                    order.getCreatedAt().toLocalDate()
            );

            return message;
        } catch (RuntimeException e) {
            throw new RuntimeException("SendRecoveryCustomerMessage_ERROR :: " + e.getMessage());
        }
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
        order.setDeliveryMethod(orderDto.getDeliveryMethod());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setUser(userConnected);
        System.out.println("Local date :: " + LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setDelivered("En attente");
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

    @Transactional
    public ResponseEntity<?> getAllUserOrders() {
        try {
            // Récupérer toutes les commandes des utilisateurs
            List<Order> orders = orderRepository.findAll();
            System.out.println("::: orders ::: " + orders);
            if (orders.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<OrderDTO> orderDtoList = new ArrayList<>();
            for(Order order : orders) {
                OrderDTO orderDto = orderMapper.toDTO(order);
                orderDtoList.add(orderDto);
            }
            System.out.println("::: orderDtoList ::: " + orderDtoList);
            return ResponseEntity.ok(orderDtoList);

        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes:: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseEntity<?> updateStatusOrder(Map<String, String> body) {
        Long orderId = Long.valueOf(body.get("orderId"));
        Optional<User> user = Optional.ofNullable(orderRepository.findUserByOrderId(orderId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur trouvé pour cette commande")));
        var ref = new Object() {
            String status = "";
        };
        System.out.println("Status :: " + body.get("status"));
        try{
            switch (body.get("status")) {
                case "pending":
                    ref.status = "En attente";
                    break;
                case "processing":
                    ref.status = "En cours";
                    break;
                case "recovery":
                    ref.status = "Récupération";
                    break;
                case "shipped":
                    ref.status = "Expédiée";
                    break;
                case "delivered":
                    ref.status = "Livrée";
                    break;
                default:
                    ref.status = "Annulée";
            }
            Map<String, OrderDTO> map = new HashMap<>();
            return orderRepository.findById(orderId)
                    .map(order -> {
                        order.setDelivered(ref.status);
                        if(ref.status.equals("Expédiée")){
                            String customerMessage = sendDeliveryCustomerMessage(order);
                            brevoService.sentResponseToCustomerFromCartPage(user.get(), customerMessage);
                        }else if(ref.status.equals(("Récupération"))){
                            String customerMessage = sendRecoveryCustomerMessage(order);
                            brevoService.sentResponseToCustomerFromCartPage(user.get(), customerMessage);
                        }
                        order.setUpdatedAt(LocalDateTime.now());
                        orderRepository.save(order);
                        return ResponseEntity.ok(map.put("order",orderMapper.toDTO(order)));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            throw new RuntimeException("UpdateStatusOrder_ERROR :: " + e.getMessage());
        }
    }
}
