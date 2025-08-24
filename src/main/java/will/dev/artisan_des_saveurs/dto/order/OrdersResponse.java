package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersResponse {
    private Long id;
    private double subtotal;
    private double discount;
    private double total;
    private boolean freeShipping;
    private String delivered;
    private LocalDateTime createdAt;
    private Long userid;
    private List<ProductItemDTO> productItems;
}
