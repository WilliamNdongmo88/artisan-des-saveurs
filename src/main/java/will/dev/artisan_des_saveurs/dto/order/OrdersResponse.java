package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.UserDto;

@Data
public class OrdersResponse {
    private Long id;
    private double subtotal;
    private double discount;
    private double total;
    private boolean freeShipping;
    private Long userid;
    private ProductItemDTO productItem;
}
