package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import will.dev.artisan_des_saveurs.entity.Files;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Long> {
    @Modifying
    @Query("DELETE FROM Files f WHERE f.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);

    @Query("SELECT f FROM Files f WHERE f.product.id = :productId")
    List<Files> findByProductId(@Param("productId") Long productId);


    @Query("SELECT f FROM Files f WHERE f.product.id = :productId")
    List<Files> findAllByProductId(@Param("productId") Long productId);

//    @Query("SELECT f FROM Files f WHERE f.temp = :temp")
//    Files findByTemp(String temp);
}
