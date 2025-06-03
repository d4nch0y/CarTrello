package org.dkn.cartrello.service;



import org.dkn.cartrello.repository.UserRepository;
import org.dkn.cartrello.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Регистрира нов потребител: криптира паролата и записва потребителя в базата
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // криптиране на паролата
        userRepository.save(user); // запис в базата данни
    }

    // Зарежда потребител по потребителско име за Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username); // взима потребителя от репозитория
    }
}

