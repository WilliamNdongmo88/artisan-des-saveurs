package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;

@Data
public class ProductItemDTO {
    private Long id;
    private int quantity;
    private ProductDTO product;
    private String selectedUnit;
    private double displayQuantity;

    // Getters et setters
}

