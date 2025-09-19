package will.dev.artisan_des_saveurs.qrcode.repository;

import will.dev.artisan_des_saveurs.qrcode.entity.PdfMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfMetadataRepository extends JpaRepository<PdfMetadata, Long> {
    
    Optional<PdfMetadata> findByUniqueId(String uniqueId);
    
    boolean existsByUniqueId(String uniqueId);
}

