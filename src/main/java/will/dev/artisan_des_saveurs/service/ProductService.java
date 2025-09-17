package will.dev.artisan_des_saveurs.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductRequest;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductResponse;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductToSend;
import will.dev.artisan_des_saveurs.dtoMapper.FileDTOMapper;
import will.dev.artisan_des_saveurs.dtoMapper.ProductMapper;
import will.dev.artisan_des_saveurs.entity.Files;
import will.dev.artisan_des_saveurs.entity.Product;
import will.dev.artisan_des_saveurs.repository.FilesRepository;
import will.dev.artisan_des_saveurs.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final FilesRepository filesRepository;
    private final ProductMapper productMapper;
    private final FileDTOMapper fileDTOMapper;
    private final FileStorageService fileStorageService;
    private final FileService fileService;
    private final CloudinaryService cloudinaryService;

    public ProductService(
            @Value("${application.files.base-path}") final String basePath,
            ProductRepository productRepository, ProductMapper productMapper, FileStorageService fileStorageService,
            FilesRepository filesRepository, FileDTOMapper fileDTOMapper, FileService fileService,
            CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.fileDTOMapper = fileDTOMapper;
        this.fileStorageService = fileStorageService;
        this.fileService = fileService;
        this.filesRepository = filesRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public List<ProductDTO> getAllProducts() {
        List<Product> products = (List<Product>) this.productRepository.findAll();
        System.out.println("products ::"+ products);
        List<ProductDTO> productDTOList = new ArrayList<>(List.of());
        for (Product product: products){
            productDTOList.add(productMapper.toDTO(product));
        }
        return productDTOList;
    }

    public List<ProductDTO> getAvailableProducts() {
        List<Product> products = (List<Product>) this.productRepository.findByAvailableTrueWithImage();
        System.out.println("products ::"+ products);
        List<ProductDTO> productDTOList = new ArrayList<>(List.of());
        for (Product product: products){
            productDTOList.add(productMapper.toDTO(product));
        }
        return productDTOList;
    }

    public Optional<ProductDTO> getProductById(Long id) {
        Optional<Product> product = productRepository.findByIdWithProductImage(id);
        return Optional.ofNullable(productMapper.toDTO(product.get()));
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name).stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    //Create
    @Transactional
    public ProductDTO createProduct(@Valid ProductDTO productDto, MultipartFile file) throws IOException {
        Product product = productMapper.toEntity(productDto);

        //FileDTO fileDto = fileService.uploadFile(file).getBody();
        String imageUrl = cloudinaryService.uploadFile(file);
        FileDTO fileDto = new FileDTO();
        fileDto.setFileName(extractFileName(imageUrl));
        fileDto.setFilePath(imageUrl);
        will.dev.artisan_des_saveurs.entity.Files newFile = fileDTOMapper.mapFileDtoToEntity(fileDto);
        filesRepository.save(newFile);
        product.setProductImage(newFile);
        Product savedProd = productRepository.save(product);


        return productMapper.toDTO(savedProd);
    }

    //Update
    @Transactional(rollbackFor = Exception.class)
    public ProductDTO updateProduct(Long id, ProductDTO dto, MultipartFile file) throws IOException {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setDescription(dto.getDescription());
        existing.setPreparation(dto.getPreparation());
        existing.setRecette(dto.getRecette());
        existing.setCategory(dto.getCategory());
        existing.setAvailable(dto.isAvailable());
        existing.setOrigin(dto.getOrigin());
        existing.setUnit(dto.getUnit());
        existing.setStockQuantity(dto.getStockQuantity());
        existing.setFeatured(dto.isFeatured());

        Product savedProd = new Product();
        //FileDTO fileDto = fileService.uploadFile(file).getBody();
        if (dto.getMainImage().getContent() != null) {
            String imageUrl = cloudinaryService.uploadFile(file);
            FileDTO fileDto = new FileDTO();
            fileDto.setFileName(extractFileName(imageUrl));
            fileDto.setFilePath(imageUrl);
            will.dev.artisan_des_saveurs.entity.Files newFile = fileDTOMapper.mapFileDtoToEntity(fileDto);
            filesRepository.save(newFile);
            existing.setProductImage(newFile);
            savedProd = productRepository.save(existing);
        }else if (dto.getMainImage().getContent() == null){
            System.out.println("L'image n'a pas changé ::: " + dto.getMainImage());
            savedProd = productRepository.save(existing);
        }

        return productMapper.toDTO(savedProd);
    }

    public static String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == url.length() - 1) {
            return null; // aucun fichier trouvé
        }
        return url.substring(lastSlashIndex + 1);
    }

    //Delete
    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {

            // ✅ Supprimer image principale
            Files mainImage = product.getProductImage();
            if (mainImage != null) {
//                try {
//                    fileStorageService.deleteFromDisk(mainImage); // chemin du fichier
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                filesRepository.delete(mainImage);
            }

            // ✅ Supprimer le produit
            productRepository.delete(product);
            return true;

        }).orElse(false);
    }

    @Transactional
    public Optional<ProductDTO> toggleProductAvailability(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setAvailable(!product.isAvailable());
                    Product updatedProduct = productRepository.save(product);
                    return productMapper.toDTO(updatedProduct);
                });
    }
}


