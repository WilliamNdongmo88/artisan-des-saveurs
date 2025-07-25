package will.dev.artisan_des_saveurs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {
    @GetMapping("/env")
    public Map<String, String> showEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("MAIL_USERNAME", System.getenv("MAIL_USERNAME"));
        env.put("MAIL_PASSWORD", System.getenv("MAIL_PASSWORD"));
        return env;
    }
}

