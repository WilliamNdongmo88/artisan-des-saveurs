package will.dev.artisan_des_saveurs.dtoMapper;

import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.entity.ProductItem;

@Component
public class ProductItemMapper {

    public static ProductItemDTO toDTO(ProductItem item) {
        if (item == null) return null;

        ProductItemDTO dto = new ProductItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        //dto.setProduct(ProductMapper.toDTO(item.getProduct()));
        return dto;
    }

    public static ProductItem toEntity(ProductItemDTO dto) {
        if (dto == null) return null;

        ProductItem item = new ProductItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        //item.setProduct(ProductMapper.toEntity(dto.getProduct()));
        return item;
    }
}
