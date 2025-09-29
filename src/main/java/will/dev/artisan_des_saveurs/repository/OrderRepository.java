package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import will.dev.artisan_des_saveurs.entity.Order;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.List;
import java.util.Optional;

public  interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("Select o From Order o Where o.user=:user")
    List<Order> findAllByUser(User user);

    List<Order> findAllByUserId(Long userId);

    @Query("SELECT o.user FROM Order o WHERE o.id = :orderId")
    Optional<User> findUserByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE o.user.id=:id")
    Order findByUserId(@Param("id") Long id);
}
