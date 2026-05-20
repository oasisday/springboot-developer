package me.mingi.springbootdeveloper.config;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mingi.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

//설정 //WebSecurity //생성자
//보안관련 설정을 해주는 Config Class
@Slf4j
//@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    //서비스를 설정해준다
    private final UserDetailService userDetailService;

    //스프링 시큐리티 비활성화
    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(                   //모두 접근 허용(인가)
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),       //오타내면 여기서 막힘
                                new AntPathRequestMatcher("/user")
                        ).permitAll()
                        .anyRequest().authenticated())      //나머지는 인가x
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")                //로그인 주소
                        .defaultSuccessUrl("/articles")     //로그인 성공시 이동 도메인
                )
                .logout(logout -> logout        //로그아웃시
                        .logoutSuccessUrl("/login")                         //로그아웃하면 이동 페이지
                        .invalidateHttpSession(true)                        //로그아웃하면 세션 정리
                )
                .csrf(AbstractHttpConfigurer::disable)                      //csrf공격 방어 꺼놓음 //rest개발시 꺼놓음
                .build();
    }


    /// 로그인 처리 과정
    /// 사용자가 아이디/비밀번호 입력
    ///         ↓
    /// UserDetailService로 DB에서 유저 조회
    ///         ↓
    /// BCrypt로 비밀번호 일치 여부 확인
    ///         ↓
    /// 성공 → /articles 이동
    /// 실패 → 로그인 페이지로 돌아감
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception{

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);                  //DB에서 User조회
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);                 //비밀번호 암호화 방식

        return new ProviderManager(authProvider);
    }

    /// 평문 비밀번호: "1234"
    ///         ↓ BCrypt 암호화
    /// DB 저장값: "$2a$10$N9qo8uLOickgx2ZMRZo..."
    ///
    /// 요청 들어옴
    ///     ↓
    /// configure()   → 정적파일/H2콘솔이면 그냥 통과
    ///     ↓
    /// filterChain() → 로그인 필요한 페이지인지 확인
    ///     ↓
    /// authenticationManager() → 아이디/비밀번호 검증
    ///     ↓
    /// BCryptPasswordEncoder → 비밀번호 암호화 비교
    ///     ↓
    /// 성공 시 → /articles

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
