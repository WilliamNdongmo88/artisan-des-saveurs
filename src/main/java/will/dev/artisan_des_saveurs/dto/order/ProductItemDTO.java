package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;

@Data
public class ProductItemDTO {
    private Long id;
    private int quantity;
    private ProductDTO product;

    // Getters et setters
}

