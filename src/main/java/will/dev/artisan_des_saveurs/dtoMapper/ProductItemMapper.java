package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.order.ProductItemDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.entity.ProductItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ProductItemMapper {
    @Value("${application.files.base-path}")
    private static String basePath;
    private final FileDTOMapper fileDTOMapper;

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
            will.dev.artisan_des_saveurs.entity.Files file = item.getProduct().getProductImage();
            FileDTO fileDto = new FileDTO();
            fileDto.setId(file.getId());
            fileDto.setName(file.getName());
            fileDto.setTemp(file.getTemp());

            if (file.getTemp() != null) {
                try {
                    Path path = Paths.get(basePath, file.getTemp());
                    if (Files.exists(path)) {
                        byte[] bytes = Files.readAllBytes(path);
                        String base64 = Base64.getEncoder().encodeToString(bytes);
                        String extension = FilenameUtils.getExtension(file.getName());
                        fileDto.setContent("data:image/" + extension + ";base64," + base64);
                    } else {
                        fileDto.setContent(null);
                    }
                } catch (IOException e) {
                    fileDto.setContent(null);
                }
            }
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
