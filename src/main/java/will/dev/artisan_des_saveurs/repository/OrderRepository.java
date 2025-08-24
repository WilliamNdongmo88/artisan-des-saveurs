package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import will.dev.artisan_des_saveurs.entity.Order;
import will.dev.artisan_des_saveurs.entity.ProductItem;

import java.util.List;

public  interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("Select o From Order o Where o.userId=:id")
    List<Order> findAllByUserId(Long id);
}
