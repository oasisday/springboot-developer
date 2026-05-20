package me.mingi.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.mingi.springbootdeveloper.domain.User;
import me.mingi.springbootdeveloper.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다")
    @Test
    void generateToken(){
        // given - 테스트용 유저를 DB에 저장
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when - 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then - 토큰 안에 유저 ID가 제대로 들어갔는지 검증
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );

        Long userId = Jwts.parser()
                .verifyWith(secretKey)              // ✅ setSigningKey() → verifyWith()
                .build()                            // ✅ build() 추가
                .parseSignedClaims(token)           // ✅ parseClaimsJws() → parseSignedClaims()
                .getPayload()                       // ✅ getBody() → getPayload()
                .get("id", Long.class);

        Assertions.assertThat(userId).isEqualTo(testUser.getId());
    }

/*
    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        //given
        String token = JwtFactory.builder()
                .expireation(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        Assertions.assertThat(result).isFalse();

    }
*/


    @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );

        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties, secretKey);  // ✅ secretKey 추가

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다")
    @Test
    void validToken_validToken(){
        // given
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties, secretKey);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다")
    @Test
    void getAuthentication(){
        //given
        String userEmail = "user@email.com";
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties, secretKey);

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        Assertions.assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId: 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId(){
        //given
        Long userId = 1L;
        SecretKey secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties, secretKey);

        //when
        Long userIdByToken = tokenProvider.getUserId(token);

        //then
        Assertions.assertThat(userIdByToken).isEqualTo(userId);

    }

}