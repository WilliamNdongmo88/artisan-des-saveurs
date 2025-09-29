package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private double subtotal;
    private double discount;
    private double total;
    private String status;
    private boolean freeShipping;
    private String deliveryMethod;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto user;
    private List<ProductItemDTO> items;
}
