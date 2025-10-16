package will.dev.artisan_des_saveurs.dtoMapper;

import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.req_resp.dto.FileDTO;

@Component
public class FileDTOMapper {

    public FileDTO map(will.dev.artisan_des_saveurs.entity.Files file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFilePath(file.getFilePath());
        return dto;
    }

    public will.dev.artisan_des_saveurs.entity.Files mapFileDtoToEntity(FileDTO dto) {
        will.dev.artisan_des_saveurs.entity.Files file = new will.dev.artisan_des_saveurs.entity.Files();
        file.setFileName(dto.getFileName());
        file.setFilePath(dto.getFilePath());
        return file;
    }
}
