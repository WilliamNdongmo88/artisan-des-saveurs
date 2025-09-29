package will.dev.artisan_des_saveurs.dto;

import lombok.Data;

@Data
public class DeleteAccountRequest {
    private  String confirmationText;
    private  String password;
}
