package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.artisan_des_saveurs.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
