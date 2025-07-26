package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String category;
    private String description;
    private String image;
    private String origin;
    private double price;
    private String unit;
    private boolean featured;

    // Getters et setters
}
