package will.dev.artisan_des_saveurs.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class FileStorageService {

    @Value("${application.files.base-path}")
    private String basePath;

//    public void writeOnDisk(will.dev.artisan_des_saveurs.entity.Files file) throws IOException {
//        String fullPath = String.format("%s/%s", basePath, file.getTemp());
//        Path folder = Paths.get(fullPath).getParent();
//        Files.createDirectories(folder);
//
//        String base64Data = file.getContent();
//        if (base64Data.contains(",")) {
//            base64Data = base64Data.split(",")[1]; // Supprime le préfixe base64
//        }
//
//        byte[] decodedFile = Base64.getDecoder().decode(base64Data);
//        File destinationFile = new File(fullPath);
//
//        if (destinationFile.exists()) {
//            FileUtils.deleteQuietly(destinationFile);
//        }
//
//        FileUtils.writeByteArrayToFile(destinationFile, decodedFile);
//    }

//    public void deleteFromDisk(will.dev.artisan_des_saveurs.entity.Files file) throws IOException {
//        if (file != null && file.getTemp() != null) {
//            Path filePath = Paths.get(basePath, file.getTemp());
//            if (Files.exists(filePath)) {
//                try {
//                    Files.delete(filePath);
//                    System.out.println("#### Fichier " + file.getTemp() + " supprimé du disque.");
//                } catch (IOException e) {
//                    System.err.println("#### Erreur lors de la suppression du fichier " + file.getTemp() + ": " + e.getMessage());
//                    throw e;
//                }
//            } else {
//                System.out.println("#### Le fichier " + file.getTemp() + " n'existe pas sur le disque.");
//            }
//        }
//    }
}
