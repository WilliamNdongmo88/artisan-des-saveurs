package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;

@Data
public class FileDTO {
    private Long id;
    private String name;
    private String temp;
    private String content;

    // getters, setters
}
