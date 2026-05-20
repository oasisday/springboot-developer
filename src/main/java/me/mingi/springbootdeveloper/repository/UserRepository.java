package me.mingi.springbootdeveloper.repository;

import me.mingi.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//interface로 선언하기..!!!!
//Query Method 사용!!
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
