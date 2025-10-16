package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.entity.Product;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final FileDTOMapper fileDTOMapper;

    public ProductDTO toDTO(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setPreparation(product.getPreparation());
        dto.setRecette(product.getRecette());
        dto.setCategory(product.getCategory());

        if (product.getProductImage() != null) {
            dto.setFileId(product.getProductImage().getId());
            dto.setMainImage(fileDTOMapper.map(product.getProductImage()));
            dto.setImageUrl(product.getProductImage().getFilePath());
        }
        dto.setAvailable(product.isAvailable());
        dto.setOrigin(product.getOrigin());
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
        product.setRecette(dto.getRecette());
        product.setCategory(dto.getCategory());
        product.setAvailable(dto.isAvailable());
        product.setOrigin(dto.getOrigin());
        product.setUnit(dto.getUnit());
        product.setStockQuantity(dto.getStockQuantity());
        product.setFeatured(dto.isFeatured());

        if (dto.getMainImage() != null) {
            product.setProductImage(fileDTOMapper.mapFileDtoToEntity(dto.getMainImage()));
        }

        return product;
    }
}
