package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<OrderItemDTO> items;
    private double subtotal;
    private double discount;
    private double total;
    private boolean freeShipping;

    // Getters et setters
}

