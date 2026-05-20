package me.mingi.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder
    public User(String email, String password, String auth, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    //권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    //비밀번호 반환
    @Override
    public String getPassword() {
        return password;
    }

    //아이디 반환
    @Override
    public String getUsername() {
        return email;
    }

    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 잠금여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 사용여부 반환
    @Override
    public boolean isEnabled() {
        return true;
    }

    //사용자 이름 변경
    public User update(String nickname){
        this.nickname = nickname;

        return this;
    }

}

