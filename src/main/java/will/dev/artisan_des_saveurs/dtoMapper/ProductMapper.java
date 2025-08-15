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

    public  Product toEntity(ProductDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
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
        return product;
    }

    public  Product toEntities(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        request.setName(product.getName());
        request.setPrice(product.getPrice());
        request.setDescription(product.getDescription());
        request.setPreparation(product.getPreparation());
        request.setCategory(product.getCategory());
        if (product.getProductImage() != null) {
            request.setMainImage(fileDTOMapper.map(product.getProductImage()));
        }
        request.setAvailable(product.isAvailable());
        request.setOrigin(product.getOrigin());
        request.setPrice(product.getPrice());
        request.setUnit(product.getUnit());
        request.setStockQuantity(product.getStockQuantity());
        request.setFeatured(product.isFeatured());
        return product;
    }
}

