package will.dev.artisan_des_saveurs.dto.order;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String preparation;
    private boolean available;
    private FileDTO mainImage;
    private String imageUrl;
    private String origin;
    private BigDecimal price;
    private String unit;
    private Integer stockQuantity;
    private boolean featured;

    // Getters et setters
}
