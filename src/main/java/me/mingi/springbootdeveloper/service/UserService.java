package me.mingi.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.mingi.springbootdeveloper.domain.User;
import me.mingi.springbootdeveloper.dto.AddUserRequest;
import me.mingi.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    /*private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //AddUserRequest를 통해 들어온 녀석을 param으로..
    //거기서 들어온 ID와 Password를 가지고 로그인을 해본다
    public Long save(AddUserRequest dto){
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))      // 암호화해서 DTO에 전달해준다
                .build()).getId();
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }*/

    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
