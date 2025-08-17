package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductRequest;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductResponse;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductToSend;
import will.dev.artisan_des_saveurs.dtoMapper.FileDTOMapper;
import will.dev.artisan_des_saveurs.dtoMapper.ProductMapper;
import will.dev.artisan_des_saveurs.entity.Product;
import will.dev.artisan_des_saveurs.repository.FilesRepository;
import will.dev.artisan_des_saveurs.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public ProductService(
            @Value("${application.files.base-path}") final String basePath,
            ProductRepository productRepository, ProductMapper productMapper, FileStorageService fileStorageService,
            FilesRepository filesRepository, FileDTOMapper fileDTOMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.fileDTOMapper = fileDTOMapper;
        this.fileStorageService = fileStorageService;
        this.filesRepository = filesRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAvailableProducts() {
        List<Product> products = (List<Product>) this.productRepository.findByAvailableTrueWithImage();
        System.out.println("products ::"+ products);
        List<ProductDTO> productDTOList = new java.util.ArrayList<>(List.of());
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
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse createProduct(ProductToSend productToSend) {
        Product product = new Product();
        Product savedProduct;
        try {
            product.setName(productToSend.getProductRequest().getName());
            product.setDescription(productToSend.getProductRequest().getDescription());
            product.setPrice(productToSend.getProductRequest().getPrice());
            product.setCategory(productToSend.getProductRequest().getCategory());
            product.setAvailable(productToSend.getProductRequest().isAvailable());
            product.setStockQuantity(productToSend.getProductRequest().getStockQuantity());
            product.setUnit(productToSend.getProductRequest().getUnit());
            product.setFeatured(productToSend.getProductRequest().isFeatured());
            product.setOrigin(productToSend.getProductRequest().getOrigin());
            product.setPreparation(productToSend.getProductRequest().getPreparation());
            savedProduct = productRepository.save(product);

            // Traitement de l’image principale
            will.dev.artisan_des_saveurs.entity.Files imagePrincipale = fileDTOMapper.mapFileDtoToEntity(productToSend.getProductImage());
            System.out.println("imagePrincipale :: "+ imagePrincipale);
            if (imagePrincipale != null && imagePrincipale.getName() != null && imagePrincipale.getTemp() == null) {
            String extension = FilenameUtils.getExtension(imagePrincipale.getName());
            String temp = System.currentTimeMillis() + "." + extension;
            imagePrincipale.setTemp(temp);
            imagePrincipale.setProduct(savedProduct);
            product.setProductImage(imagePrincipale);
            fileStorageService.writeOnDisk(imagePrincipale);
        }else {
            throw new RuntimeException("Image principale manquante ou invalide");
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProductResponse productResponse = new ProductResponse(savedProduct);
        System.out.println("productResponse :: "+ productResponse);
        return productResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<ProductResponse> updateProduct(Long id, ProductToSend productToSend) throws IOException {
        // Vérifier que le produit existe
        Product productInBd = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Mise à jour des champs simples
        productInBd.setName(productToSend.getProductRequest().getName());
        productInBd.setDescription(productToSend.getProductRequest().getDescription());
        productInBd.setPrice(productToSend.getProductRequest().getPrice());
        productInBd.setCategory(productToSend.getProductRequest().getCategory());
        productInBd.setAvailable(productToSend.getProductRequest().isAvailable());
        productInBd.setStockQuantity(productToSend.getProductRequest().getStockQuantity());
        productInBd.setUnit(productToSend.getProductRequest().getUnit());
        productInBd.setFeatured(productToSend.getProductRequest().isFeatured());
        productInBd.setOrigin(productToSend.getProductRequest().getOrigin());
        productInBd.setPreparation(productToSend.getProductRequest().getPreparation());

        // Gestion de l'image principale
        if (productToSend.getProductImage() != null) {
            // Cas 1: Une nouvelle image est envoyée (pas d'ID ni de temp)
            if (productToSend.getProductImage().getId() == null && productToSend.getProductImage().getTemp() == null) {
                will.dev.artisan_des_saveurs.entity.Files nouvelleImage = fileDTOMapper.mapFileDtoToEntity(productToSend.getProductImage());
                String extension = FilenameUtils.getExtension(nouvelleImage.getName());
                String temp = System.currentTimeMillis() + "." + extension;
                nouvelleImage.setTemp(temp);
                nouvelleImage.setProduct(productInBd);

                // Si une ancienne image existait, on peut la supprimer si nécessaire
                if (productInBd.getProductImage() != null) {
                    // Logique pour supprimer l'ancienne image du disque et de la base de données
                    filesRepository.delete(productInBd.getProductImage());
                    fileStorageService.deleteFromDisk(productInBd.getProductImage());
                }

                filesRepository.save(nouvelleImage);
                productInBd.setProductImage(nouvelleImage);
                fileStorageService.writeOnDisk(nouvelleImage);
                System.out.println("✅ Nouvelle image enregistrée.");
            } else if (productToSend.getProductImage().getId() != null) {
                // Cas 2: L'image envoyée est une image existante (elle a un ID)
                // On vérifie si c'est la même image que celle déjà associée au produit
                if (productInBd.getProductImage() == null || !productInBd.getProductImage().getId().equals(productToSend.getProductImage().getId())) {
                    // L'image envoyée est différente de l'image actuelle du produit
                    // On charge l'image existante par son ID
                    Optional<will.dev.artisan_des_saveurs.entity.Files> existingImageOpt = filesRepository.findById(productToSend.getProductImage().getId());
                    if (existingImageOpt.isPresent()) {
                        will.dev.artisan_des_saveurs.entity.Files existingImage = existingImageOpt.get();
                        // Si une ancienne image existait, on peut la supprimer si nécessaire
                        if (productInBd.getProductImage() != null) {
                            // Logique pour supprimer l'ancienne image du disque et de la base de données
                            filesRepository.delete(productInBd.getProductImage());
                            fileStorageService.deleteFromDisk(productInBd.getProductImage());
                        }
                        productInBd.setProductImage(existingImage);
                        System.out.println("✅ Image existante associée au produit.");
                    } else {
                        // L'image avec cet ID n'existe pas, cela peut indiquer une erreur ou une tentative de suppression
                        System.err.println("⚠️ L'image avec l'ID " + productToSend.getProductImage().getId() + " n'a pas été trouvée.");
                        // Gérer ce cas, par exemple en ne mettant pas à jour l'image ou en lançant une exception
                    }
                } else {
                    System.out.println("✅ Conservation de l'ancienne image (même image renvoyée).");
                }
            }
        } else {
            // Cas 3: Aucune image n'est envoyée dans productToSend (productImage est null)
            // Si une image était associée au produit, on peut la supprimer si c'est l'intention
            if (productInBd.getProductImage() != null) {
                // Logique pour supprimer l'ancienne image du disque et de la base de données
                filesRepository.delete(productInBd.getProductImage());
                fileStorageService.deleteFromDisk(productInBd.getProductImage());
                productInBd.setProductImage(null);
                System.out.println("✅ Image du produit supprimée.");
            }
        }
        // ... (fin de la méthode updateProduct)

        Product updatedProductInBd = productRepository.save(productInBd);
        return Optional.of(new ProductResponse(updatedProductInBd));
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<ProductResponse> toggleProductAvailability(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setAvailable(!product.isAvailable());
                    Product updatedProduct = productRepository.save(product);
                    return new ProductResponse(updatedProduct);
                });
    }
}


