package will.dev.artisan_des_saveurs.dto.req_resp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequest {
    @NotBlank
    @Email
    private String email;

    public EmailRequest() {}

    public EmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

