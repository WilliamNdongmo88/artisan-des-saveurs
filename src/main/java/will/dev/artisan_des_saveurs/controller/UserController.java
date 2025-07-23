package will.dev.artisan_des_saveurs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @GetMapping("/")
    public String hello() {
        return "Bravo votre application fonctionne correctement ✅";
    }
}
