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

    @Query("SELECT p FROM Product p JOIN FETCH p.productImage")
    List<Product> findByAvailableTrue();

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

    /**
     * Récupère TOUS les produits en chargeant immédiatement ("FETCH")
     * leurs relations 'image' associées pour éviter les LazyInitializationException.
     *
     * @return Une liste de produits avec leurs images déjà chargées.
     */
//    @Query("SELECT p FROM Product p JOIN FETCH p.image")
//    List<Product> findAllWithImage();
}

