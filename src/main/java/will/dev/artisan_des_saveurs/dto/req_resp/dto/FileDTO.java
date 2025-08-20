package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;

@Data
public class FileDTO {
    private Long id;
    private String name;
    private String temp;
    private String content;
    private String fileName;
    private String filePath; // URL publique nginx

    public FileDTO() {
    }

    public FileDTO(Long id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }
    // getters, setters
}
