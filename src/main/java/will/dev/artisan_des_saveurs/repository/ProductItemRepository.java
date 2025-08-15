package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import will.dev.artisan_des_saveurs.entity.ProductItem;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.List;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

//    @Query("SELECT p FROM ProductItem p WHERE p.userid =:id")
//    List<ProductItem> findByUserid(Long id);
}
