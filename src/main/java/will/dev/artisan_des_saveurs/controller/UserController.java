package will.dev.artisan_des_saveurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.UserRepository;
import will.dev.artisan_des_saveurs.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/home")
    public String hello() {
        return "Bravo votre application fonctionne correctement ✅";
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
