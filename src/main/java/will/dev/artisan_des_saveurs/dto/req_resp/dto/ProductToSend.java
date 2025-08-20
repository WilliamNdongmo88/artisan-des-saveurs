package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;

@Data
public class ProductToSend {
    private FileDTO productImage;
    private ProductDTO productDto;
}
