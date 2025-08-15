package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import will.dev.artisan_des_saveurs.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email =:email")
    User findByEmailFromConnectedUser(String email);

//    @Query("SELECT u FROM User u WHERE u.isActive =:is_active")
//    List<User> findByIsActiveFalse(Boolean is_active);

    @Query("SELECT u FROM User u WHERE u.enabled =:enabled")
    List<User> findByEnabledFalse(boolean enabled);

    Optional<User> findByUsername(String username);
    Optional<User> findByActivationToken(String activationToken);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
