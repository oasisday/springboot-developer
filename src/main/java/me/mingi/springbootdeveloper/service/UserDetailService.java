package me.mingi.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.mingi.springbootdeveloper.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import me.mingi.springbootdeveloper.domain.User;


@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

    //DI로 주입
    private final UserRepository userRepository;

    //e-mail로 사용자 정보를 받아오는 메서드
    @Override
    public User loadUserByUsername(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }





}
