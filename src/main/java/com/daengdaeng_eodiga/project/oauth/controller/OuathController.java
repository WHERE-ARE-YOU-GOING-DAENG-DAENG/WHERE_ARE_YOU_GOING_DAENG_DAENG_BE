package com.daengdaeng_eodiga.project.oauth.controller;

import com.daengdaeng_eodiga.project.global.Security.config.CustomOAuth2User;
import com.daengdaeng_eodiga.project.global.Security.config.JWTUtil;
import com.daengdaeng_eodiga.project.global.Redis.Repository.RedisTokenRepository;
import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.oauth.dto.SignUpForm;
import com.daengdaeng_eodiga.project.oauth.dto.UserOauthDto;
import com.daengdaeng_eodiga.project.oauth.service.OauthUserService;
import com.daengdaeng_eodiga.project.oauth.service.TokenService;
import com.daengdaeng_eodiga.project.user.dto.UserDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import org.springframework.web.bind.annotation.*;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OuathController {

    private final OauthUserService oauthUserService;
    private final TokenService tokenService;
    private final JWTUtil jwtUtil;
    private final RedisTokenRepository redisTokenRepository;

    @Value("${frontend.url}")
    private String frontUrl;


    public void showSignUpForm(String email, String provider, HttpServletResponse response) throws IOException {

        ResponseCookie emailCookie = ResponseCookie.from("email", email)
            .path("/")
            .sameSite("Lax")
            .httpOnly(false)
            .secure(false)
            .maxAge(60 * 10)
            .domain(".daengdaeng-where.link")
            .build();
        response.addHeader("Set-Cookie", emailCookie.toString());

        ResponseCookie provideCookie = ResponseCookie.from("provider", provider)
            .path("/")
            .sameSite("Lax")
            .httpOnly(false)
            .secure(false)
            .maxAge(60 * 10)
            .domain(".daengdaeng-where.link")
            .build();
        response.addHeader("Set-Cookie", provideCookie.toString());
        response.sendRedirect(frontUrl+"/user-register");
    }

    public void deletedUserRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(frontUrl+"/error?status=DELETE_USER");
    }

    @GetMapping("/loginSuccess")
    public void loginSuccess(HttpServletResponse response) throws IOException {
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody SignUpForm signUpForm, HttpServletResponse response) {
        oauthUserService.registerUser(signUpForm);
        tokenService.generateTokensAndSetCookies(signUpForm.getEmail(), signUpForm.getOauthProvider(), response);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("RefreshToken") Cookie RefreshToken ,@AuthenticationPrincipal CustomOAuth2User principal,
                                         HttpServletResponse response) {
        String userEmail = principal.getUserDTO().getEmail();
        tokenService.deleteCookie(userEmail, response,RefreshToken);
        return  ResponseEntity.ok(ApiResponse.success(null));
    }
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@CookieValue("RefreshToken") Cookie RefreshToken,@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                        HttpServletResponse response) {
        int userid = customOAuth2User.getUserDTO().getUserid();
        String userEmail = customOAuth2User.getUserDTO().getEmail();
        oauthUserService.deleteUser(userid);
        tokenService.deleteCookie(userEmail, response,RefreshToken);
        return  ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/user/adjust")
    public ResponseEntity<ApiResponse<Map<String, Object>>> AdjustUserRequest(@AuthenticationPrincipal CustomOAuth2User principal) {
        UserOauthDto userOauthDto = principal.getUserDTO();
        UserDto userDto = oauthUserService.UserToDto(userOauthDto.getEmail(),userOauthDto.getProvider());
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/user/adjust")
    public ResponseEntity<ApiResponse<?>> AdjustUser(@AuthenticationPrincipal CustomOAuth2User principal ,
                                                     @Valid @RequestBody SignUpForm signUpForm, HttpServletResponse response) {
        UserOauthDto userOauthDto = principal.getUserDTO();
        oauthUserService.AdjustUser(signUpForm,userOauthDto.getEmail(),userOauthDto.getProvider());
        return ResponseEntity.ok(ApiResponse.success(oauthUserService.UserToDto(userOauthDto.getEmail(),userOauthDto.getProvider())));
    }
    @GetMapping("/user/duplicateNickname")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNicknameDuplicate( @RequestParam
                                                                                         String nickname) {
        boolean isDuplicate = oauthUserService.isNicknameDuplicate(nickname);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
