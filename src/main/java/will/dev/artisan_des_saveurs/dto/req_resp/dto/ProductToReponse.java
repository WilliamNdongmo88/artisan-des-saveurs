package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;

@Data
public class ProductToReponse {
    private FileDTO productImage;
    private ProductResponse productResponse;
}
