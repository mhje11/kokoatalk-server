package org.kokoatalkserver.global.util.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.global.util.jwt.entity.RefreshToken;
import org.kokoatalkserver.global.util.jwt.filter.JwtAuthenticationFilter;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.kokoatalkserver.global.util.jwt.util.JwtTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    String[] allAllowPage = new String[] {
            "/api/auth/signup", "/api/auth/signin",
            "/api/auth/signout", "/api/auth/refresh"
    };

    String[] authPage = new String[] {
            "/api/member/upload/profileImage", "/api/member/upload/backgroundImage",
            "/api/member/delete/backgroundImage", "/api/member/delete/profileImage",
            "/api/member/update/bio", "/api/friend/search",
            "/api/friend/add", "/api/friend/friendList",
            "/api/chatRoom/create", "/api/chatRoom/list", "/api/chatRoom/leave",
            "/ws/**", "/api/chat/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allAllowPage).permitAll()
                        .requestMatchers(authPage).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, refreshTokenService),
                        UsernamePasswordAuthenticationFilter.class);

        http.logout(logout -> logout
                .logoutUrl("/api/auth/signout") // 로그아웃 URL
                .logoutSuccessHandler((request, response, authentication) -> {
                    // 쿠키에서 refreshToken 가져오기
                    String refreshToken = null;
                    if (request.getCookies() != null) {
                        for (var cookie : request.getCookies()) {
                            if ("refreshToken".equals(cookie.getName())) {
                                refreshToken = cookie.getValue();
                                break;
                            }
                        }
                    }

                    // 리프레시 토큰 삭제 로직
                    if (refreshToken != null) {
                        refreshTokenService.deleteAllRefreshTokenData(refreshToken);
                    }

                    // 응답 처리
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json; charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("로그아웃 성공");
                    response.getWriter().flush();
                })
                .deleteCookies("accessToken", "refreshToken") // 쿠키 삭제
                .permitAll()
        );
        http
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public CorsConfigurationSource configurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://www.kokoatalk.shop");
        config.addAllowedOrigin("https://api.kokoatalk.shop");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://kokoatalk-server-c794b03f124a.herokuapp.com");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // 쿠키 허용을 위해 필요
        config.setAllowedMethods(List.of("GET","POST","DELETE", "PUT"));
        source.registerCorsConfiguration("/**",config);
        return source;
    }
}
