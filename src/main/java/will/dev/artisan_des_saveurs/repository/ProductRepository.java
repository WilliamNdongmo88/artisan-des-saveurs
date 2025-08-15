package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import will.dev.artisan_des_saveurs.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailableTrue();
    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndAvailableTrue(String category);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.available = true")
    List<Product> findByNameContainingAndAvailableTrue(@Param("name") String name);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findDistinctCategories();
}

