package will.dev.artisan_des_saveurs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.*;
import will.dev.artisan_des_saveurs.service.FileService;
import will.dev.artisan_des_saveurs.service.FileStorageService;
import will.dev.artisan_des_saveurs.service.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final FileService fileService;

    private static final String UPLOAD_DIR = "/app/uploads/";

//    @PostMapping("/files-upload")
//    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // Vérifie que le dossier existe, sinon crée-le
//            Path uploadPath = Paths.get(UPLOAD_DIR);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            // Génère un nom unique
//            String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
//
//            // Sauvegarde le fichier sur disque
//            Path filePath = uploadPath.resolve(uniqueFileName);
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            // Construit l’URL publique qui sera servie par Nginx
//            String publicUrl = "https://artisan-des-saveurs-production.up.railway.app/api/uploads/" + uniqueFileName;
//
//            // Retourne les infos au frontend
//            FileDTO dto = new FileDTO();
//            dto.setFileName(uniqueFileName);
//            dto.setFilePath(publicUrl);
//            dto.setTemp("ok"); // champ libre si tu veux marquer l’état
//
//            return ResponseEntity.ok(dto);
//
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new FileDTO(null, null, null));
//        }
//    }

    @PostMapping(value = "/files-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileDTO dto = fileService.save(file);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProductDTO>> getAvailableProducts() {
        List<ProductDTO> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        List<ProductResponse> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

//    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
//    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductToSend productToSend) {
//        System.out.println("productToSend :: "+ productToSend);
//        try {
//            ProductResponse createdProduct = productService.createProduct(productToSend);
//            return ResponseEntity.ok(createdProduct);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDto) {
        System.out.println("productToSend :: "+ productDto);
        try {
            ProductDTO createdProduct = productService.createProduct(productDto);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
//    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
//                                                        @Valid @RequestBody ProductToSend productToSend) throws IOException {
//        Optional<ProductResponse> updatedProduct = productService.updateProduct(id, productToSend);
//        return updatedProduct.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductDTO productDto) throws IOException {
        ProductDTO updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER_DELETE')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("Produit supprimé avec succès"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/toggle-availability")
    @PreAuthorize("hasAuthority('ADMIN_PATCH')")
    public ResponseEntity<ProductResponse> toggleProductAvailability(@PathVariable Long id) {
        Optional<ProductResponse> updatedProduct = productService.toggleProductAvailability(id);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

