package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(max = 50)
    private String category;

    private FileDTO mainImage;

    private boolean available = true;

    private Integer stockQuantity = 0;

    @Size(max = 50)
    private String unit;

    private boolean featured = false;

    @Size(max = 50)
    private String origin;

    @Size(max = 3000)
    private String preparation;

}

