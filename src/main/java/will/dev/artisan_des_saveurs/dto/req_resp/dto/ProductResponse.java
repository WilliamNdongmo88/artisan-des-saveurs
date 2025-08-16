package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;
import will.dev.artisan_des_saveurs.entity.Files;
import will.dev.artisan_des_saveurs.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    //private FileDTO productImage;
    private boolean available;
    private boolean featured;
    private String origin;
    private String preparation;
    private Integer stockQuantity;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {}

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.category = product.getCategory();
        //this.productImage = product.getProductImage();
        this.available = product.isAvailable();
        this.featured = product.isFeatured();
        this.origin = product.getOrigin();
        this.preparation = product.getPreparation();
        this.stockQuantity = product.getStockQuantity();
        this.unit = product.getUnit();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

    // Getters and Setters
}


