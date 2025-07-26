package will.dev.artisan_des_saveurs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.UserDto;
import will.dev.artisan_des_saveurs.dtoMapper.UserDtoMapper;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.UserRepository;
import will.dev.artisan_des_saveurs.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String hello() {
        System.out.println("✅ /api/users/test atteint !");
        return "Bravo votre application fonctionne correctement ✅";
    }

    @PostMapping("/place-order")
    public ResponseEntity<MessageRetourDto> createUser(@RequestBody @Valid UserDto userDto) {
        System.out.println("📦 Requête reçue pour créer un utilisateur : " + userDto.getEmail());
        return this.userService.createUser(userDto);
    }

    @RequestMapping(value = "/place-order", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> corsCheck() {
        System.out.println("🛰️ Préflight OPTIONS reçu !");
        return ResponseEntity.ok().build();
    }

    // GET /users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDto> userDtos = users.stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserDtoMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
