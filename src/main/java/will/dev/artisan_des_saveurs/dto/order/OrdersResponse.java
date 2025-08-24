package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class OrdersResponse {
    private Long id;
    private double subtotal;
    private double discount;
    private double total;
    private boolean freeShipping;
    private String delivered;
    //private LocalDateTime createAt;
    private String createAt;
    private Long userid;
    private ProductItemDTO productItem;
}
