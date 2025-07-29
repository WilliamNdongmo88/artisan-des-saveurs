package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.order.OrderDTO;
import will.dev.artisan_des_saveurs.dto.order.OrderItemDTO;
import will.dev.artisan_des_saveurs.entity.ContactRequest;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.ContactRequestRepository;
import will.dev.artisan_des_saveurs.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final String MESSAGE = "Votre commande a été envoyé avec succès !";
    @Value("${app.company.whatsapp.number:+23059221613}")
    private String company_number;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final WhatsappNotification whatsappNotification;
    private final ContactRequestRepository contactRequestRepository;

    public ResponseEntity<MessageRetourDto> sendOrder(OrderDTO orderDTO) {
        MessageRetourDto messageRetourDto = new MessageRetourDto();
        Optional<User> optionalUser = this.userRepository.findByEmail(orderDTO.getEmail());
//        if (optionalUser.isPresent()) {
//            // Envoie de la commande et sauvegarde de l'utilisateur
//            throw new RuntimeException("Email déjà existant");
//        }else {
            User user = new User();
            user.setFirstName(orderDTO.getFirstName());
            user.setLastName(orderDTO.getLastName());
            user.setEmail(orderDTO.getEmail());
            user.setPhone(orderDTO.getPhone());
            user.setConsent(true);
            user.setIsActive(false);
            User savedUser = userRepository.save(user);

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
            notificationService.envoyer(contactRequest, isFromCart);
            whatsappNotification.sendWhatsappMessage(savedUser, company_number, savedContactReq, isFromCart);

            messageRetourDto.setSuccess(true);
            messageRetourDto.setMessage(MESSAGE);
//        }
        return ResponseEntity.ok(messageRetourDto);
    }

    public String generateOrderMessage(OrderDTO orderDto) {
        List<OrderItemDTO> items = orderDto.getItems();
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
            OrderItemDTO item = items.get(i);
            String name = item.getProduct() != null && item.getProduct().getName() != null
                    ? item.getProduct().getName()
                    : "Produit inconnu";
            int quantity = item.getQuantity();
            itemsDescription.append(String.format("%d. %s - Quantité : %d\n", i + 1, name, quantity));
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
                orderDto.getFirstName()+" "+orderDto.getLastName(),
                orderDto.getEmail(),
                orderDto.getPhone(),
                itemsDescription.toString(),
                subtotal,
                discount,
                total,
                shippingMessage
        );

        return message.trim();
    }


    public String buildOrderMessage(OrderDTO order) {
        StringBuilder message = new StringBuilder();

        message.append("Nouvelle commande client :\n\n");

        // Parcours des articles commandés
        message.append("🛒 Produits commandés:\n");
        for (OrderItemDTO item : order.getItems()) {
            String productName = item.getProduct().getName();
            int quantity = item.getQuantity();
            message.append("- ").append(productName)
                    .append(" x").append(quantity).append("\n");
        }

        // Détails financiers
        message.append("\n💰 Détails de la commande:\n");
        message.append("Sous-total : ").append(order.getSubtotal()).append(" Rs\n");
        message.append("Remise : ").append(order.getDiscount()).append(" Rs\n");
        message.append("Total à payer : ").append(order.getTotal()).append(" Rs\n");

        // Livraison
        if (order.isFreeShipping()) {
            message.append("🚚 Livraison : Gratuite\n");
        } else {
            message.append("🚚 Livraison : À la charge du client\n");
        }

        return message.toString();
    }
}
