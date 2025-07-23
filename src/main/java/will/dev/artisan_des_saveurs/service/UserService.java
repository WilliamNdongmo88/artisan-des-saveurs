package will.dev.artisan_des_saveurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import will.dev.artisan_des_saveurs.entity.User;
import will.dev.artisan_des_saveurs.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user){
        try{
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new RuntimeException("Save error: ", e);
        }
    }
}
