package will.dev.artisan_des_saveurs.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import will.dev.artisan_des_saveurs.dto.order.ProductDTO;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.MessageResponse;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductRequest;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductResponse;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.ProductToSend;
import will.dev.artisan_des_saveurs.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

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

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductToSend productToSend) {
        System.out.println("productToSend :: "+ productToSend);
        try {
            ProductResponse createdProduct = productService.createProduct(productToSend);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, 
                                                        @Valid @RequestBody ProductToSend productToSend) throws IOException {
        Optional<ProductResponse> updatedProduct = productService.updateProduct(id, productToSend);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

