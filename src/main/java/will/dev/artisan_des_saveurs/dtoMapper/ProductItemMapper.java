package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.entity.ProductItem;

@Component
@RequiredArgsConstructor
public class ProductItemMapper {
    private final ProductMapper productMapper;

    public ProductItemDTO toDTO(ProductItem item) {
        if (item == null) return null;

        ProductItemDTO dto = new ProductItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setProduct(productMapper.toDTO(item.getProduct()));
        return dto;
    }

    public ProductItem toEntity(ProductItemDTO dto) {
        if (dto == null) return null;

        ProductItem item = new ProductItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setProduct(productMapper.toEntity(dto.getProduct()));
        return item;
    }
}
