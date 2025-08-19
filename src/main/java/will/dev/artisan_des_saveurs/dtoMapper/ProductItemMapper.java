package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.entity.ProductItem;

@Component
@RequiredArgsConstructor
public class ProductItemMapper {
    private static final FileDTOMapper fileDTOMapper = new FileDTOMapper();

    public static ProductItemDTO toDTO(ProductItem item) {
        if (item == null) return null;

        ProductItemDTO dto = new ProductItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        ProductDTO productDto = new ProductDTO();
        productDto.setId(item.getProduct().getId());
        productDto.setName(item.getProduct().getName());
        productDto.setPrice(item.getProduct().getPrice());
        productDto.setDescription(item.getProduct().getDescription());
        productDto.setPreparation(item.getProduct().getPreparation());
        productDto.setCategory(item.getProduct().getCategory());

        if (item.getProduct().getProductImage() != null) {
            productDto.setMainImage(fileDTOMapper.map(item.getProduct().getProductImage()));
        }

        productDto.setAvailable(item.getProduct().isAvailable());
        productDto.setOrigin(item.getProduct().getOrigin());
        productDto.setPrice(item.getProduct().getPrice());
        productDto.setUnit(item.getProduct().getUnit());
        productDto.setStockQuantity(item.getProduct().getStockQuantity());
        productDto.setFeatured(item.getProduct().isFeatured());

        dto.setProduct(productDto);
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
