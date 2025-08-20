package will.dev.artisan_des_saveurs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;
//import will.dev.artisan_des_saveurs.entity.Files;
import will.dev.artisan_des_saveurs.repository.FileRepository;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    // Chemin local où stocker les images (monté par Nginx dans /uploads par exemple)
    @Value("${app.upload.dir:/app/uploads}")
    private String uploadDir;

    // URL publique de Nginx (pour exposer les fichiers uploadés)
    @Value("${app.upload.url:https://artisan-des-saveurs-production.up.railway.app/uploads}")
    private String uploadUrl;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileDTO save(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new RuntimeException("Fichier vide !");
        }

        // Générer un nom unique
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Créer le dossier si inexistant
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(fileName);
        multipartFile.transferTo(filePath.toFile());

        // Construire l’URL publique
        String fileUrl = uploadUrl + "/" + fileName;

        // Sauvegarder en DB
        will.dev.artisan_des_saveurs.entity.Files fileEntity = new will.dev.artisan_des_saveurs.entity.Files();
        fileEntity.setFileName(fileName);
        fileEntity.setFilePath(fileUrl); // stocke l’URL nginx
        fileRepository.save(fileEntity);

        // Retourner un DTO
        FileDTO dto = new FileDTO();
        dto.setId(fileEntity.getId());
        dto.setFileName(fileEntity.getFileName());
        dto.setFilePath(fileEntity.getFilePath());

        return dto;
    }
}
