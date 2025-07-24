package will.dev.artisan_des_saveurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.artisan_des_saveurs.entity.ContactRequest;

import java.util.List;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    List<ContactRequest> findByUserId(Long userId);
}

