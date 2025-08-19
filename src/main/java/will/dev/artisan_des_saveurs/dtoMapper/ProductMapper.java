package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductRequest;
import will.dev.artisan_des_saveurs.entity.Product;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final FileDTOMapper fileDTOMapper;

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setPreparation(product.getPreparation());
        dto.setCategory(product.getCategory());

        if (product.getProductImage() != null) {
            dto.setMainImage(fileDTOMapper.map(product.getProductImage()));
        }

        dto.setAvailable(product.isAvailable());
        dto.setOrigin(product.getOrigin());
        dto.setPrice(product.getPrice());
        dto.setUnit(product.getUnit());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setFeatured(product.isFeatured());

        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setPreparation(dto.getPreparation());
        product.setCategory(dto.getCategory());

        if (dto.getMainImage() != null) {
            product.setProductImage(fileDTOMapper.mapFileDtoToEntity(dto.getMainImage()));
        }

        product.setAvailable(dto.isAvailable());
        product.setOrigin(dto.getOrigin());
        product.setUnit(dto.getUnit());
        product.setStockQuantity(dto.getStockQuantity());
        product.setFeatured(dto.isFeatured());

        return product;
    }

}

