package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
