package will.dev.artisan_des_saveurs.dtoMapper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

//@Component
//public class FileDTOMapper {
//
//    @Value("${application.files.base-path}")
//    private String basePath;
//
//    public FileDTO map(will.dev.artisan_des_saveurs.entity.Files file) {
//        FileDTO dto = new FileDTO();
//        dto.setId(file.getId());
//        dto.setName(file.getName());
//        dto.setTemp(file.getTemp());
//
//        if (file.getTemp() != null) {
//            try {
//                Path path = Paths.get(basePath, file.getTemp());
//                if (Files.exists(path)) {
//                    byte[] bytes = Files.readAllBytes(path);
//                    String base64 = Base64.getEncoder().encodeToString(bytes);
//                    String extension = FilenameUtils.getExtension(file.getName());
//                    dto.setContent("data:image/" + extension + ";base64," + base64);
//                } else {
//                    dto.setContent(null);
//                }
//            } catch (IOException e) {
//                dto.setContent(null);
//            }
//        }
//
//        return dto;
//    }
//
//    public will.dev.artisan_des_saveurs.entity.Files mapFileDtoToEntity(FileDTO dto) {
//        will.dev.artisan_des_saveurs.entity.Files file = new will.dev.artisan_des_saveurs.entity.Files();
//        file.setName(dto.getName());
//        file.setContent(dto.getContent());
//        return file;
//    }
//}

@Component
public class FileDTOMapper {

    @Value("${application.files.public-url}") // exemple : https://cdn.monsite.com/uploads
    private String publicUrl;

    public FileDTO map(will.dev.artisan_des_saveurs.entity.Files file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setTemp(file.getTemp());
        dto.setFileName(file.getFileName());
        dto.setFilePath(file.getFilePath());


        return dto;
    }

    public will.dev.artisan_des_saveurs.entity.Files mapFileDtoToEntity(FileDTO dto) {
        will.dev.artisan_des_saveurs.entity.Files file = new will.dev.artisan_des_saveurs.entity.Files();
        file.setName(dto.getName());
        file.setTemp(dto.getTemp());
        file.setFileName(dto.getFileName());
        file.setFilePath(dto.getFilePath());
        // ⚠️ ne mets pas le content en DB, garde uniquement le chemin/filename
        return file;
    }
}
