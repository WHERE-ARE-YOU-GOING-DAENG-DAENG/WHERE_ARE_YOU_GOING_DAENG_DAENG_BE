package com.daengdaeng_eodiga.project.global.Security.config;

import java.util.Arrays;
import java.util.List;

import com.daengdaeng_eodiga.project.global.Redis.Repository.RedisTokenRepository;
import com.daengdaeng_eodiga.project.oauth.controller.OuathController;
import com.daengdaeng_eodiga.project.user.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RedisTokenRepository redisTokenRepository;
    private final UserService userService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final Boolean testMode;
    private final OuathController ouathController;
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil,
    RedisTokenRepository redisTokenRepository,UserService userService,CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler,@Value("${frontend.test}") Boolean testMode,
        OuathController ouathController) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.redisTokenRepository = redisTokenRepository;
        this.userService = userService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.testMode = testMode;

        this.ouathController = ouathController;
    }
    /**
     * CORS 설정을 위한 {@link CorsConfiguration} Bean을 생성합니다.
     * @author 김승환
     * 이 메소드는 CORS 정책을 설정하고, 여러 도메인에서의 요청을 허용합니다.
     * @return CORS 설정을 위한 {@link CorsConfiguration} 객체
     */
    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:5173","https://pet.daengdaeng-where.link","https://daengdaeng-where-git-test-wldusdns-projects.vercel.app","https://fronttest.daengdaeng-where.link"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Set-Cookie","Access-Control-Allow-Origin"));
        configuration.setExposedHeaders(List.of("Set-Cookie","Access-Control-Allow-Origin"));

        return configuration;
    }
    /**
     * Spring Security 설정을 위한 {@link SecurityFilterChain} Bean을 생성합니다.
     *  @author 김승환
     * 이 메소드는 HTTP 보안 설정을 구성하며, CORS 정책, CSRF 보호 비활성화, HTTP 기본 인증 비활성화 등을 포함합니다.
     * 또한, JWT 필터를 추가하여 인증 처리를 하고, OAuth2 로그인 설정도 구성합니다.
     *
     * @param http {@link HttpSecurity} 객체
     * @return 구성된 {@link SecurityFilterChain} 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());
        http.formLogin((form) -> form.disable());
        http.cors(cors -> cors.configurationSource(request -> corsConfiguration()));

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/v1/loginSuccess","/login", "/favicon.ico","https://pet.daengdaeng-where.link/login","/api/v1/places/**","/login/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/signup").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/user/duplicateNickname").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/banners/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/place/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/visit/place/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v2/region/owners").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v2/story").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v2/story/detail/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/prometheus/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/metrics**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(new JWTFilter(jwtUtil,redisTokenRepository,userService,testMode), UsernamePasswordAuthenticationFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureHandler(new CustomAuthenticationFailureHandler(ouathController))
                );


        http
            .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(authenticationEntryPoint));


        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}