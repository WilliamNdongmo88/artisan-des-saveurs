package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import will.dev.artisan_des_saveurs.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Récupère tous les produits qui sont disponibles (available = true)
     * en chargeant immédiatement ("FETCH") leurs images associées.
     *
     * @return Une liste des produits disponibles avec leurs images déjà chargées.
     */
    @Query("SELECT p FROM Product p JOIN FETCH p.productImage WHERE p.available = true")
    List<Product> findByAvailableTrueWithImage();

    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndAvailableTrue(String category);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.available = true")
    List<Product> findByNameContainingAndAvailableTrue(@Param("name") String name);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findDistinctCategories();

    @Query("SELECT p FROM Product p JOIN FETCH p.productImage WHERE p.id = :id")
    Optional<Product> findByIdWithProductImage(@Param("id") Long id);

}

