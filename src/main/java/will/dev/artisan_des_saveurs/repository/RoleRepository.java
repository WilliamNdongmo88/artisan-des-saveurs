package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.artisan_des_saveurs.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
